package com.flanks255.simplybackpacks;

import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SBContainerSlot extends Slot {
    public SBContainerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, boolean locked) {
        super(inventoryIn, index, xPosition, yPosition);
        isLocked = locked;
    }
    public boolean isLocked;
    /*
    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return !isLocked && super.canTakeStack(playerIn);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() instanceof ItemBackpackBase)
            return false;
        return super.isItemValid(stack);
    }*/
}
