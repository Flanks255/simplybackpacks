package com.flanks255.simplybackpacks.inventory;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class SBItemHandler extends ItemStackHandler {
    public SBItemHandler(int size) {
        super(size);
    }

    @Override
    protected void onContentsChanged(int slot) {
        BackpackManager.get().markDirty();
    }

    public void upgrade(int slots) {
        if (slots <= stacks.size())
            return;
        NonNullList<ItemStack> oldStacks = stacks;
        stacks = NonNullList.withSize(slots, ItemStack.EMPTY);
        for (int i = 0; i < oldStacks.size(); i++) {
            stacks.set(i, oldStacks.get(i));
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!SimplyBackpacks.filterItem(stack))
            return stack;

        return super.insertItem(slot, stack, simulate);
    }
}
