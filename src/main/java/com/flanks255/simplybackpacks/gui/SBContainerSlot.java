package com.flanks255.simplybackpacks.gui;


import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class SBContainerSlot extends SlotItemHandler {
    private Predicate<ItemStack> filterPredicate = BackpackUtils::filterItem;
    private int index;
    public SBContainerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.index = index;
    }

    public SBContainerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> filterPredicate) {
        super(itemHandler, index, xPosition, yPosition);
        this.filterPredicate = filterPredicate;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        return stack.getMaxStackSize();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        if(!super.mayPlace(stack))
            return false;
        return filterPredicate.test(stack);
    }

    //bandage till forge PR fixes this
    @Override
    public void initialize(@NotNull ItemStack itemStack) {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(index, itemStack);
    }
}
