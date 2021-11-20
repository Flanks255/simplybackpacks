package com.flanks255.simplybackpacks.inventory;

import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class SBItemHandler extends ItemStackHandler {
    public SBItemHandler(int size) {
        super(size);
    }

    @Override
    protected void onContentsChanged(int slot) {
        BackpackManager.get().setDirty();
    }

    public void upgrade(int slots) {
        if (slots <= this.stacks.size())
            return;
        NonNullList<ItemStack> oldStacks = this.stacks;
        this.stacks = NonNullList.withSize(slots, ItemStack.EMPTY);
        for (int i = 0; i < oldStacks.size(); i++) {
            this.stacks.set(i, oldStacks.get(i));
        }
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return BackpackUtils.filterItem(stack);
    }
}
