package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.BackpackItemHandler;
import com.flanks255.simplybackpacks.SBContainerSlot;
import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class SBContainer extends AbstractContainerMenu {
    public SBContainer(final int windowId, final Inventory playerInventory, FriendlyByteBuf extra) {
        this(windowId, playerInventory.player.level, playerInventory.player.blockPosition(), playerInventory, playerInventory.player);
    }

    public SBContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        super(SimplyBackpacks.SBCONTAINER.get(), windowId);

        playerInv = playerInventory;
        ItemStack stack = findBackpack(playerEntity);

        if (stack == null || stack.isEmpty()) {
            playerEntity.closeContainer();
            return;
        }

        IItemHandler tmp = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);


        if (tmp instanceof BackpackItemHandler) {
            handler = (BackpackItemHandler)tmp;
            handler.load();
            slotcount = tmp.getSlots();
            itemKey = stack.getDescriptionId();



            addMySlots(stack);
            addPlayerSlots(playerInv);
        }
        else
            playerEntity.closeContainer();
    }

    public SBContainer(int openType, int windowId, Level world, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        this(windowId, world, pos, playerInventory, playerEntity);
    }

    public int slotcount = 0;

    private int slotID;
    public String itemKey = "";
    private Inventory playerInv;
    public BackpackItemHandler handler;

    @Override
    public boolean stillValid(Player playerIn) {
        if (slotID == -106)
            return playerIn.getOffhandItem().getItem() instanceof ItemBackpackBase; //whoops guess you can...
        return playerIn.getInventory().getItem(slotID).getItem() instanceof ItemBackpackBase;
    }



    @Override
    public void clicked(int slot, int dragType, ClickType clickTypeIn, Player player) {
        if (slot >= 0) {
            if (getSlot(slot).getItem().getItem() instanceof ItemBackpackBase)
                return;
        }
        if (clickTypeIn == ClickType.SWAP)
            return;

        if (slot >= 0) getSlot(slot).container.setChanged();
        super.clicked(slot, dragType, clickTypeIn, player);
    }

    private void addPlayerSlots(Inventory playerInventory) {
        int originX = 0;
        int originY = 0;
        switch(slotcount) {
            case 18:
                originX = 7;
                originY = 67;
                break;
            case 33:
                originX = 25;
                originY = 85;
                break;
            case 66:
                originX = 25;
                originY = 139;
                break;
            default:
                originX = 25;
                originY = 193;
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

        //Hotbar
        for (int col = 0; col < 9; col++) {
            int x = originX + col * 18;
            int y = originY + 58;
            this.addSlot(new Slot(playerInventory, col, x+1, y+1));
        }
    }

    private void addMySlots(ItemStack stack) {
        if (handler == null ) return;

        int cols = slotcount == 18? 9:11;
        int rows = slotcount / cols;
        int slotindex = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 7 + col * 18;
                int y = 17 + row * 18;

                this.addSlot(new SBContainerSlot(handler, slotindex, x + 1, y + 1));
                slotindex++;
                if (slotindex >= slotcount)
                    break;
            }
        }

    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.slots.get(index);

            if (slot != null && slot.hasItem()) {
                int bagslotcount = slots.size() - playerIn.getInventory().items.size();
                ItemStack itemstack1 = slot.getItem();
                itemstack = itemstack1.copy();
                if (index < bagslotcount) {
                    if (!this.moveItemStackTo(itemstack1, bagslotcount, this.slots.size(), true))
                        return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(itemstack1, 0, bagslotcount, false)) {
                    return ItemStack.EMPTY;
                }
                if (itemstack1.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
            }
            return itemstack;
    }

    private ItemStack findBackpack(Player playerEntity) {
        Inventory inv = playerEntity.getInventory();

        if (playerEntity.getMainHandItem().getItem() instanceof ItemBackpackBase) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack == playerEntity.getMainHandItem()) {
                    slotID = i;
                    return stack;
                }
            }
        } else if (playerEntity.getOffhandItem().getItem() instanceof ItemBackpackBase) {
            slotID = -106;
            return playerEntity.getOffhandItem();
        }
        else {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() instanceof ItemBackpackBase) {
                    slotID = i;
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
