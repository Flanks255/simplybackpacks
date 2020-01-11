package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.BackpackItemHandler;
import com.flanks255.simplybackpacks.SBContainerSlot;
import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import com.flanks255.simplylight.SimplyLight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SBContainer extends Container {
    public SBContainer(final int windowId, final PlayerInventory playerInventory) {
        this(windowId, playerInventory.player.world, playerInventory.player.getPosition(), playerInventory, playerInventory.player);
    }

    public SBContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        super(type, windowId);

        playerInv = playerInventory;
        ItemStack stack = findBackpack(playerEntity);

        if (stack == null || stack.isEmpty()) {
            playerEntity.closeScreen();
            return;
        }

        IItemHandler tmp = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);


        if (tmp instanceof BackpackItemHandler) {
            handler = (BackpackItemHandler)tmp;
            handler.load();
            slotcount = tmp.getSlots();
            itemKey = stack.getTranslationKey();



            addMySlots(stack);
            addPlayerSlots(playerInv);
        }
        else
            playerEntity.closeScreen();
    }

    public SBContainer(int openType, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        this(windowId, world, pos, playerInventory, playerEntity);
    }

    public int slotcount = 0;

    private int slotID;
    public String itemKey = "";
    public static final ContainerType type = new ContainerType<>(SBContainer::new).setRegistryName("sb_container");
    private PlayerInventory playerInv;
    public BackpackItemHandler handler;

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return !playerIn.inventory.getStackInSlot(slotID).isEmpty();
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
        int originX = 0;
        int originY = 0;
        switch(slotcount) {
            case 18:
                originX = 7;
                originY = 57;
                break;
            case 33:
                originX = 25;
                originY = 75;
                break;
            case 66:
                originX = 25;
                originY = 129;
                break;
            default:
                originX = 25;
                originY = 183;
        }

        //Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = originX + col * 18;
                int y = originY + row * 18;
                int index = (col + row * 9) + 9;
                this.addSlot(new SBContainerSlot(playerInventory, index, x+1, y+1, index == slotID));
            }
        }

        //Hotbar
        for (int col = 0; col < 9; col++) {
            int x = originX + col * 18;
            int y = originY + 58;
            this.addSlot(new SBContainerSlot(playerInventory, col, x+1, y+1, col == slotID));
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

                this.addSlot(new SlotItemHandler(handler, slotindex, x + 1, y + 1));
                slotindex++;
                if (slotindex >= slotcount)
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
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == playerEntity.getHeldItemOffhand()) {
                    slotID = i;
                    return stack;
                }
            }
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
