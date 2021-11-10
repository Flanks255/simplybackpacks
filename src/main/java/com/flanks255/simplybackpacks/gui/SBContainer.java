package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import java.util.UUID;

public class SBContainer extends Container {

    public static SBContainer fromNetwork(final int windowId, final PlayerInventory playerInventory, PacketBuffer data) {
        UUID uuidIn = data.readUUID();
        return new SBContainer(windowId, playerInventory, uuidIn, new ItemStackHandler(data.readInt()));
    }

    public SBContainer(final int windowId, final PlayerInventory playerInventory, UUID uuid, IItemHandler handler) {
        super(SimplyBackpacks.SBCONTAINER.get(), windowId);

        PlayerEntity playerEntity = playerInventory.player;
        this.handler = handler;
        ItemStack stack = findBackpack(playerEntity);

        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BackpackItem)) {
            playerEntity.closeContainer();
            return;
        }

        this.tier = BackpackItem.getTier(stack);

        itemKey = stack.getDescriptionId();

        addPlayerSlots(playerInventory);
        addMySlots(stack);
    }

    private int slotID;
    public String itemKey = "";
    public final IItemHandler handler;
    private Backpack tier;

    public Backpack getTier() {
        return tier;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity playerIn) {
        if (slotID == -106)
            return playerIn.getOffhandItem().getItem() instanceof BackpackItem; //whoops guess you can...
        if (slotID == -768)
            return true;
        return playerIn.inventory.getItem(slotID).getItem() instanceof BackpackItem;
    }



    @Override
    @Nonnull
    public ItemStack clicked(int slot, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull PlayerEntity player) {
        if (slot >= 0) {
            if (getSlot(slot).getItem().getItem() instanceof BackpackItem && slot == slotID)
                return ItemStack.EMPTY;
        }
        if (clickTypeIn == ClickType.SWAP)
            return ItemStack.EMPTY;

        if (slot >= 0) getSlot(slot).container.setChanged();
        return super.clicked(slot, dragType, clickTypeIn, player);
    }

    private void addPlayerSlots(PlayerInventory playerInventory) {
        int originX = tier.slotXOffset;
        int originY = tier.slotYOffset;

        //Hotbar
        for (int col = 0; col < 9; col++) {
            int x = originX + col * 18;
            int y = originY + 58;
            this.addSlot(new Slot(playerInventory, col, x+1, y+1));
        }

        //Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = originX + col * 18;
                int y = originY + row * 18;
                int index = (col + row * 9) + 9;
                this.addSlot(new Slot(playerInventory, index, x+1, y+1));
            }
        }
    }

    private void addMySlots(ItemStack stack) {
        if (handler == null ) return;

        int cols = tier.slotCols;
        int rows = tier.slotRows;

        int slot_index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 7 + col * 18;
                int y = 17 + row * 18;

                if (row > 7 && col > 2 && col < 13)
                    continue;

                this.addSlot(new SBContainerSlot(handler, slot_index, x + 1, y + 1));
                slot_index++;
                if (slot_index >= tier.slots)
                    break;
            }
        }

    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            int bagslotcount = slots.size();
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < playerIn.inventory.items.size()) {
                if (!this.moveItemStackTo(itemstack1, playerIn.inventory.items.size(), bagslotcount, false))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(itemstack1, 0, playerIn.inventory.items.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        }
        return itemstack;
}

    private ItemStack findBackpack(@Nonnull PlayerEntity playerEntity) {
        PlayerInventory inv = playerEntity.inventory;

        if (playerEntity.getMainHandItem().getItem() instanceof BackpackItem) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack == playerEntity.getMainHandItem()) {
                    slotID = i;
                    return stack;
                }
            }
        } else if (playerEntity.getOffhandItem().getItem() instanceof BackpackItem) {
            slotID = -106;
            return playerEntity.getOffhandItem();
        }
        else {
            if (BackpackUtils.curiosLoaded) {
                ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(BackpackItem::isBackpack, playerEntity).map(data -> {
                    if (data.getRight().getItem() instanceof BackpackItem) {
                        return data.getRight();
                    }
                    return ItemStack.EMPTY;
                }).orElse(ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    slotID = -768;
                    return stack;
                }
            }

            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() instanceof BackpackItem) {
                    slotID = i;
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
