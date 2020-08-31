package com.flanks255.simplybackpacks.capability;

import javax.annotation.Nonnull;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackItemHandler extends ItemStackHandler {
    private BackpackFilterHandler filterHandler;
    public boolean dirty;

    public BackpackItemHandler(ItemStack stack, int slots) {
        super(slots);
        this.filterHandler = new BackpackFilterHandler();
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        dirty = true;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        //check for shulkers.
        if (stack.getItem() instanceof BlockItem) {
            if (((BlockItem) stack.getItem()).getBlock().isIn(BlockTags.SHULKER_BOXES)) {
                return stack;
            }
        }

        //check for some other modded inventories
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag.contains("Items") || tag.contains("Inventory"))
                return stack;
        }

        //check for itemhandler capability
        if (stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
            return stack;

        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        compound.put("filter", this.filterHandler.serializeNBT());

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.filterHandler.deserializeNBT(nbt.getCompound("filter"));
    }

    public BackpackFilterHandler getFilterHandler() {
        return filterHandler;
    }
}
