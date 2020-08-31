package com.flanks255.simplybackpacks.items;

import com.flanks255.simplybackpacks.BackpackItemHandler;
import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.network.ToggleMessageMessage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BackpackItem extends Item {
    public Backpack backpack;

    public BackpackItem(Backpack backpack) {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS));
        this.backpack = backpack;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return this.backpack.rarity;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                //filter
                playerIn.openContainer(new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) ->
                        new FilterContainer(windowId, playerInventory, null), new StringTextComponent("Backpack Filter")));

            } else {
                //open
                playerIn.openContainer(new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) ->
                        new SBContainer(windowId, playerInventory, null), playerIn.getHeldItem(handIn).getDisplayName()));
            }
        }
        return ActionResult.success(playerIn.getHeldItem(handIn));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new BackpackCaps(stack, this.backpack.slots, nbt);
    }

    class BackpackCaps implements ICapabilitySerializable {
        public BackpackCaps(ItemStack stack, int size, CompoundNBT nbtIn) {
            itemStack = stack;
            this.size = size;
            inventory = new BackpackItemHandler(itemStack, size);
            optional = LazyOptional.of(() -> inventory);
        }

        private int size;
        private ItemStack itemStack;
        private BackpackItemHandler inventory;
        private LazyOptional<IItemHandler> optional;

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return optional.cast();
            }
            else
                return LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            inventory.save();
            return new CompoundNBT();
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            inventory.load();
        }
    }

    public void togglePickup(PlayerEntity playerEntity, ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();

        boolean Pickup = !nbt.getBoolean("Pickup");

        nbt.putBoolean("Pickup", Pickup);
        if (playerEntity instanceof ServerPlayerEntity)
            SimplyBackpacks.network.send(PacketDistributor.PLAYER.with(()-> (ServerPlayerEntity) playerEntity), new ToggleMessageMessage(Pickup));
        else
            playerEntity.sendStatusMessage(new StringTextComponent(I18n.format(Pickup?"simplybackpacks.autopickupenabled":"simplybackpacks.autopickupdisabled")), true);

    }


    public boolean filterItem(ItemStack item, ItemStack packItem) {
        IItemHandler tmp = packItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (tmp == null || !(tmp instanceof BackpackItemHandler))
            return false;

        int filterOpts = packItem.getOrCreateTag().getInt("Filter-OPT");
        boolean whitelist = (filterOpts & 1) > 0;
        boolean nbtMatch = (filterOpts & 2) > 0;

        BackpackItemHandler handler = (BackpackItemHandler) tmp;

        for (int i = 0; i < 16; i++) {
            ItemStack fStack = handler.filter.getStackInSlot(i);
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

    public boolean pickupEvent(EntityItemPickupEvent event, ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        if (nbt == null)
            return false;

        if (!nbt.getBoolean("Pickup"))
                return false;

        return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .map(handler -> {
                    if (!(handler instanceof BackpackItemHandler))
                        return false;

                    ((BackpackItemHandler) handler).load();

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
                }).orElse(Boolean.FALSE);
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
    }


    @Nonnull
    public static ItemStack findBackpack(PlayerEntity player) {
        return findBackpack(player, false);
    }

    @Nonnull
    public static ItemStack findBackpack(PlayerEntity player, boolean justHotbar) {
        // Search order defined here, first search the players hands
        if (isBackpack(player.getHeldItemMainhand())) {
            return player.getHeldItemMainhand();
        }

        if (isBackpack(player.getHeldItemOffhand())) {
            return player.getHeldItemOffhand();
        }

        for (int i = 0; i < (justHotbar ? 9 : player.inventory.mainInventory.size()); i++) {
            if (isBackpack(player.inventory.getStackInSlot(i))) {
                return player.inventory.getStackInSlot(i);
            }
        }

        return ItemStack.EMPTY;
    }

    public static boolean isBackpack(ItemStack stack) {
        return stack.getItem() instanceof BackpackItem;
    }
}
