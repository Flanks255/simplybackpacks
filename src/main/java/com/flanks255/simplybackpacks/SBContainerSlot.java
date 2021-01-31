package com.flanks255.simplybackpacks;


import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.items.CapabilityItemHandler;
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
        //check for shulkers.
        if (stack.getItem() instanceof BlockItem) {
            if (((BlockItem) stack.getItem()).getBlock().isIn(BlockTags.field_226150_J_)) {
                return false;
            }
        }
        if (stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
            return false;
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            return !(tag.contains("Items") || tag.contains("Inventory"));
        }
        return true;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        if (getItemHandler() instanceof BackpackItemHandler)
            ((BackpackItemHandler) getItemHandler()).setDirty();
    }
}
