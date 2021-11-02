package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SBContainerSlot;
import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SBContainer extends Container {

    public static SBContainer fromNetwork(final int windowId, final PlayerInventory playerInventory, PacketBuffer data) {
        return new SBContainer(windowId, playerInventory, new ItemStackHandler(data.readInt()));
    }

    public SBContainer(final int windowId, final PlayerInventory playerInventory, IItemHandler handler) {
        super(SimplyBackpacks.SBCONTAINER.get(), windowId);

        playerEntity = playerInventory.player;
        playerInv = playerInventory;
        this.handler = handler;
        ItemStack stack = findBackpack(playerEntity);

        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemBackpackBase)) {
            playerEntity.closeScreen();
            return;
        }

        this.tier = ItemBackpackBase.getTier(stack);

            itemKey = stack.getTranslationKey();

            addMySlots(stack);
            addPlayerSlots(playerInv);
    }

    private int slotID;
    public String itemKey = "";
    private PlayerInventory playerInv;
    public IItemHandler handler;
    private Backpack tier;
    private PlayerEntity playerEntity;

    public Backpack getTier() {
        return tier;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if (slotID == -106)
            return playerIn.getHeldItemOffhand().getItem() instanceof ItemBackpackBase; //whoops guess you can...
        return playerIn.inventory.getStackInSlot(slotID).getItem() instanceof ItemBackpackBase;
    }



    @Override
    public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if (slot >= 0) {
            if (getSlot(slot).getStack().getItem() instanceof ItemBackpackBase)
                return ItemStack.EMPTY;
        }
        if (clickTypeIn == ClickType.SWAP)
            return ItemStack.EMPTY;

        if (slot >= 0) getSlot(slot).inventory.markDirty();
        return super.slotClick(slot, dragType, clickTypeIn, player);
    }

    private void addPlayerSlots(PlayerInventory playerInventory) {
        int originX = tier.slotXOffset;
        int originY = tier.slotYOffset;

        //Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = originX + col * 18;
                int y = originY + row * 18;
                int index = (col + row * 9) + 9;
                this.addSlot(new Slot(playerInventory, index, x+1, y+1));
            }
        }

        //Hotbar
        for (int col = 0; col < 9; col++) {
            int x = originX + col * 18;
            int y = originY + 58;
            this.addSlot(new Slot(playerInventory, col, x+1, y+1));
        }
    }

    private void addMySlots(ItemStack stack) {
        if (handler == null ) return;

        int cols = tier.slots == 18? 9:11;
        int rows = tier.slots / cols;
        int slotindex = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 7 + col * 18;
                int y = 17 + row * 18;

                this.addSlot(new SBContainerSlot(handler, slotindex, x + 1, y + 1));
                slotindex++;
                if (slotindex >= tier.slots)
                    break;
            }
        }

    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(index);

            if (slot != null && slot.getHasStack()) {
                int bagslotcount = inventorySlots.size() - playerIn.inventory.mainInventory.size();
                ItemStack itemstack1 = slot.getStack();
                itemstack = itemstack1.copy();
                if (index < bagslotcount) {
                    if (!this.mergeItemStack(itemstack1, bagslotcount, this.inventorySlots.size(), true))
                        return ItemStack.EMPTY;
                } else if (!this.mergeItemStack(itemstack1, 0, bagslotcount, false)) {
                    return ItemStack.EMPTY;
                }
                if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
            }
            return itemstack;
    }

    private ItemStack findBackpack(PlayerEntity playerEntity) {
        PlayerInventory inv = playerEntity.inventory;

        if (playerEntity.getHeldItemMainhand().getItem() instanceof ItemBackpackBase) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == playerEntity.getHeldItemMainhand()) {
                    slotID = i;
                    return stack;
                }
            }
        } else if (playerEntity.getHeldItemOffhand().getItem() instanceof ItemBackpackBase) {
            slotID = -106;
            return playerEntity.getHeldItemOffhand();
        }
        else {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.getItem() instanceof ItemBackpackBase) {
                    slotID = i;
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
