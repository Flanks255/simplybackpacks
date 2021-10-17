package com.flanks255.simplybackpacks;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SBContainerSlot extends SlotItemHandler {
    public SBContainerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        return super.getMaxStackSize();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return SimplyBackpacks.filterItem(stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (getItemHandler() instanceof BackpackItemHandler)
            ((BackpackItemHandler) getItemHandler()).setDirty();
    }
}
