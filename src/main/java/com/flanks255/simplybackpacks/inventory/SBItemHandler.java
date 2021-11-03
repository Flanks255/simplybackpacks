package com.flanks255.simplybackpacks.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

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
}
