package com.flanks255.simplybackpacks;


import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SBContainerSlot extends SlotItemHandler {
    public SBContainerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return super.getSlotStackLimit();
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return SimplyBackpacks.filterItem(stack);
    }

}
