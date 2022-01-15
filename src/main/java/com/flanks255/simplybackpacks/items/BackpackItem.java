package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.inventory.FilterItemHandler;
import com.flanks255.simplybackpacks.inventory.SBItemHandler;
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
import net.minecraft.util.text.TranslationTextComponent;
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

public class BackpackItem extends Item {
    public BackpackItem(String name, Backpack tier) {
        super(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TOOLS).fireResistant());
        this.name = name;
        this.tier = tier;
    }

    final String name;
    final Backpack tier;

    public static Backpack getTier(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BackpackItem)
            return ((BackpackItem) stack.getItem()).tier;
        else
        return Backpack.COMMON;
    }

    public static BackpackData getData(ItemStack stack) {
        if (!(stack.getItem() instanceof BackpackItem))
            return null;
        UUID uuid;
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.contains("UUID")) {
            uuid = UUID.randomUUID();
            tag.putUUID("UUID", uuid);
        } else
            uuid = tag.getUUID("UUID");
        return BackpackManager.get().getOrCreateBackpack(uuid, ((BackpackItem) stack.getItem()).tier);
    }

    public static boolean isBackpack(ItemStack stack) {
        return stack.getItem() instanceof BackpackItem;
    }

    @Override
    @Nonnull
    public Rarity getRarity(@Nonnull ItemStack stack) {
        return tier.rarity;
    }

    @Override
    @Nonnull
    public ITextComponent getName(@Nonnull ItemStack stack) {
        return new TranslationTextComponent(this.getDescriptionId(stack)).withStyle(this.tier == Backpack.ULTIMATE?TextFormatting.DARK_AQUA:TextFormatting.RESET);
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.isIn(SimplyBackpacks.SOULBOUND);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn,@Nonnull Hand handIn) {
        ItemStack backpack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayerEntity && backpack.getItem() instanceof BackpackItem) {
            BackpackData data = BackpackItem.getData(backpack);

            //Old backpack, lets migrate
            if (backpack.getOrCreateTag().contains("Inventory")) {
                // Fixes FTBTeam/FTB-Modpack-Issues #478
                if (backpack.getTag().getCompound("Inventory").contains("Size"))
                    backpack.getTag().getCompound("Inventory").remove("Size");
                ((SBItemHandler) data.getHandler()).deserializeNBT(backpack.getTag().getCompound("Inventory"));
                if (backpack.getTag().contains("Filter")) {
                    data.getFilter().deserializeNBT(backpack.getTag().getCompound("Filter"));
                    backpack.getTag().remove("Filter");
                }
                playerIn.sendMessage(new StringTextComponent("Backpack Migrated"), Util.NIL_UUID);

                backpack.getTag().remove("Inventory");
            }
            Backpack itemTier = ((BackpackItem) backpack.getItem()).tier;
            UUID uuid = data.getUuid();

            data.updateAccessRecords(playerIn.getName().getString(), System.currentTimeMillis());

            if (data.getTier().ordinal() < itemTier.ordinal()) {
                data.upgrade(itemTier);
                playerIn.sendMessage(new StringTextComponent("Backpack upgraded to " + itemTier.name), Util.NIL_UUID);
            }

            if (playerIn.isShiftKeyDown()) {
                //filter
                NetworkHooks.openGui(((ServerPlayerEntity) playerIn), new SimpleNamedContainerProvider( (windowId, playerInventory, playerEntity) -> new FilterContainer(windowId, playerInventory, data.getFilter()), backpack.getHoverName()), (buffer -> buffer.writeNbt(data.getFilter().serializeNBT())));
            } else {
                //open
                NetworkHooks.openGui(((ServerPlayerEntity) playerIn), new SimpleNamedContainerProvider( (windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, uuid, data.getTier(), data.getHandler()), backpack.getHoverName()), (buffer -> buffer.writeUUID(uuid).writeInt(data.getTier().ordinal())));
            }
        }
        return ActionResult.success(playerIn.getItemInHand(handIn));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new BackpackCaps(stack);
    }

    static class BackpackCaps implements ICapabilityProvider {
        private final ItemStack stack;

        public BackpackCaps(ItemStack stack) {
            this.stack = stack;
        }

        private LazyOptional<IItemHandler> optional = LazyOptional.empty();

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                if(!optional.isPresent())
                    optional = BackpackManager.get().getCapability(stack);
                return optional.cast();
            }
            else
                return LazyOptional.empty();
        }
    }

    public static void togglePickup(PlayerEntity playerEntity, ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();

        boolean Pickup = !nbt.getBoolean("Pickup");

        nbt.putBoolean("Pickup", Pickup);
        if (playerEntity instanceof ServerPlayerEntity)
            SimplyBackpacks.NETWORK.send(PacketDistributor.PLAYER.with(()-> (ServerPlayerEntity) playerEntity), new ToggleMessageMessage(Pickup));
        else
            playerEntity.displayClientMessage(new StringTextComponent(I18n.get(Pickup?"simplybackpacks.autopickupenabled":"simplybackpacks.autopickupdisabled")), true);

    }


    public static boolean applyFilter(ItemStack item, ItemStack packItem) {
        LazyOptional<IItemHandler> handlerOptional = packItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

        if (handlerOptional.isPresent() && handlerOptional.resolve().get() instanceof SBItemHandler) {
            BackpackData data = BackpackItem.getData(packItem);
            if (data == null)
                return false;

            FilterItemHandler filterHandler = data.getFilter();

            int filterOpts = packItem.getOrCreateTag().getInt("Filter-OPT");
            boolean whitelist = (filterOpts & 1) > 0;
            boolean nbtMatch = (filterOpts & 2) > 0;

            for (int i = 0; i < 16; i++) {
                ItemStack fStack = filterHandler.getStackInSlot(i);
                if (!fStack.isEmpty()) {
                    if (fStack.sameItem(item)) {
                        if (nbtMatch)
                            return ItemStack.tagMatches(fStack, item) == whitelist;
                        else
                            return whitelist;
                    }
                }
            }

            return !whitelist;
        }
        return false;
    }

    public static boolean pickupEvent(EntityItemPickupEvent event, ItemStack stack) {
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

            if (!applyFilter(event.getItem().getItem(), stack))
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
        return !I18n.get(key).equals(key);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World worldIn,@Nonnull List<ITextComponent> tooltip,@Nonnull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        String translationKey = getDescriptionId();

        boolean pickupEnabled = stack.getOrCreateTag().getBoolean("Pickup");
        if (pickupEnabled)
            tooltip.add(new TranslationTextComponent("simplybackpacks.autopickupenabled"));
        else
            tooltip.add(new TranslationTextComponent("simplybackpacks.autopickupdisabled"));

        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent( translationKey + ".info"));
            if (hasTranslation(translationKey + ".info2"))
                tooltip.add(new TranslationTextComponent( translationKey + ".info2"));
            if (hasTranslation(translationKey + ".info3"))
                tooltip.add(new TranslationTextComponent( translationKey + ".info3"));
        }
        else {
            tooltip.add(new TranslationTextComponent( "simplybackpacks.shift" ));
        }

        if (flagIn.isAdvanced() && stack.getTag() != null && stack.getTag().contains("UUID")) {
            UUID uuid = stack.getTag().getUUID("UUID");
            tooltip.add(new StringTextComponent("ID: " + uuid.toString().substring(0,8)).withStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
        }
    }
}
