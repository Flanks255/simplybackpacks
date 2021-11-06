package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.inventory.FilterItemHandler;
import com.flanks255.simplybackpacks.inventory.SBItemHandler;
import com.flanks255.simplybackpacks.network.FilterSyncMessage;
import com.flanks255.simplybackpacks.network.ToggleMessageMessage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemBackpackBase extends Item {
    public ItemBackpackBase(String name, Backpack tier) {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS));
        this.name = name;
        this.tier = tier;
    }

    String name;
    Backpack tier;

    public static Backpack getTier(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemBackpackBase)
            return ((ItemBackpackBase) stack.getItem()).tier;
        else
        return Backpack.COMMON;
    }

    public static BackpackData getData(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemBackpackBase))
            return null;
        UUID uuid;
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.contains("UUID")) {
            uuid = UUID.randomUUID();
            tag.putUniqueId("UUID", uuid);
        } else
            uuid = tag.getUniqueId("UUID");
        return BackpackManager.get().getOrCreateBackpack(uuid, ((ItemBackpackBase) stack.getItem()).tier);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return tier.rarity;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.isIn(SimplyBackpacks.SOULBOUND);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack backpack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && playerIn instanceof ServerPlayerEntity && backpack.getItem() instanceof ItemBackpackBase) {
            BackpackData data = ItemBackpackBase.getData(backpack);
            //Old backpack, lets migrate
            if (backpack.getOrCreateTag().contains("Inventory")) {
                ((SBItemHandler) data.getHandler()).deserializeNBT(backpack.getTag().getCompound("Inventory"));
                if (backpack.getTag().contains("Filter")) {
                    data.getFilter().deserializeNBT(backpack.getTag().getCompound("Filter"));
                    backpack.getTag().remove("Filter");
                }
                playerIn.sendMessage(new StringTextComponent("Backpack Migrated"), Util.DUMMY_UUID);

                backpack.getTag().remove("Inventory");
            }
            Backpack itemTier = ((ItemBackpackBase) backpack.getItem()).tier;
            UUID uuid = data.getUuid();

            if (data.getTier().ordinal() < itemTier.ordinal())
                data.upgrade(itemTier);

            if (playerIn.isSneaking()) {
                //filter
                SimplyBackpacks.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new FilterSyncMessage(data.getUuid(), data.getFilter()));
                NetworkHooks.openGui(((ServerPlayerEntity) playerIn), new SimpleNamedContainerProvider( (windowId, playerInventory, playerEntity) -> new FilterContainer(windowId, playerInventory, uuid, data.getFilter()), backpack.getDisplayName()), (buffer -> buffer.writeUniqueId(uuid)));
            } else {
                //open
                NetworkHooks.openGui(((ServerPlayerEntity) playerIn), new SimpleNamedContainerProvider( (windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, uuid, data.getHandler()), backpack.getDisplayName()), (buffer -> buffer.writeUniqueId(uuid).writeInt(ItemBackpackBase.getTier(backpack).slots)));
            }
        }
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new BackpackCaps(stack);
    }

    class BackpackCaps implements ICapabilityProvider {
        private final ItemStack stack;

        public BackpackCaps(ItemStack stack) {
            this.stack = stack;
        }

        private LazyOptional<IItemHandler> optional = LazyOptional.empty();

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                if(optional.isPresent())
                    return optional.cast();
                else {
                    return BackpackManager.get().getCapability(stack).cast();
                }
            }
            else
                return LazyOptional.empty();
        }
    }

    public void togglePickup(PlayerEntity playerEntity, ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();

        boolean Pickup = !nbt.getBoolean("Pickup");

        nbt.putBoolean("Pickup", Pickup);
        if (playerEntity instanceof ServerPlayerEntity)
            SimplyBackpacks.NETWORK.send(PacketDistributor.PLAYER.with(()-> (ServerPlayerEntity) playerEntity), new ToggleMessageMessage(Pickup));
        else
            playerEntity.sendStatusMessage(new StringTextComponent(I18n.format(Pickup?"simplybackpacks.autopickupenabled":"simplybackpacks.autopickupdisabled")), true);

    }


    public boolean filterItem(ItemStack item, ItemStack packItem) {
        LazyOptional<IItemHandler> handlerOptional = packItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

        if (handlerOptional.isPresent() && handlerOptional.resolve().get() instanceof SBItemHandler) {
            BackpackData data = ItemBackpackBase.getData(packItem);
            if (data == null)
                return false;

            FilterItemHandler filterHandler = data.getFilter();

            int filterOpts = packItem.getOrCreateTag().getInt("Filter-OPT");
            boolean whitelist = (filterOpts & 1) > 0;
            boolean nbtMatch = (filterOpts & 2) > 0;

            for (int i = 0; i < 16; i++) {
                ItemStack fStack = filterHandler.getStackInSlot(i);
                if (!fStack.isEmpty()) {
                    if (fStack.isItemEqual(item)) {
                        if (nbtMatch)
                            return ItemStack.areItemStackTagsEqual(fStack, item) == whitelist;
                        else
                            return whitelist;
                    }
                }
            }

            return !whitelist;
        }
        return false;
    }

    public boolean pickupEvent(EntityItemPickupEvent event, ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        if (nbt == null)
            return false;

        if (!nbt.getBoolean("Pickup"))
                return false;

        LazyOptional<IItemHandler> optional = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (optional.isPresent()) {
            IItemHandler handler = optional.resolve().get();

            if (!(handler instanceof SBItemHandler))
                return false;

            if (!filterItem(event.getItem().getItem(), stack))
                return false;

            ItemStack pickedUp = event.getItem().getItem();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack slot = handler.getStackInSlot(i);
                if (slot.isEmpty() || (ItemHandlerHelper.canItemStacksStack(slot, pickedUp) && slot.getCount() < slot.getMaxStackSize() && slot.getCount() < handler.getSlotLimit(i))) {
                    int remainder = handler.insertItem(i, pickedUp.copy(), false).getCount();
                    pickedUp.setCount(remainder);
                    if (remainder == 0)
                        break;
                }
            }
            return pickedUp.isEmpty();
        }
        else
            return false;
    }


    private boolean hasTranslation(String key) {
        return !I18n.format(key).equals(key);
    }

    private String fallbackString(String key, String fallback) {
        String tmp = I18n.format(key);
        return tmp.equals(key)?fallback:tmp;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        String translationKey = getTranslationKey();

        boolean pickupEnabled = stack.getOrCreateTag().getBoolean("Pickup");
        if (pickupEnabled)
            tooltip.add(new StringTextComponent(I18n.format("simplybackpacks.autopickupenabled")));
        else
            tooltip.add(new StringTextComponent(I18n.format("simplybackpacks.autopickupdisabled")));

        if (Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent( I18n.format( translationKey + ".info") ));
            if (hasTranslation(translationKey + ".info2"))
                tooltip.add(new StringTextComponent( I18n.format(translationKey + ".info2")));
            if (hasTranslation(translationKey + ".info3"))
                tooltip.add(new StringTextComponent( I18n.format(translationKey + ".info3")));
        }
        else {
            tooltip.add(new StringTextComponent( fallbackString("simplybackpacks.shift", "Press <§6§oShift§r> for info.") ));
        }

        if (flagIn.isAdvanced() && stack.getTag() != null && stack.getTag().contains("UUID")) {
            UUID uuid = stack.getTag().getUniqueId("UUID");
            tooltip.add(new StringTextComponent("ID: " + uuid.toString().substring(0,8)).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
        }
    }
}
