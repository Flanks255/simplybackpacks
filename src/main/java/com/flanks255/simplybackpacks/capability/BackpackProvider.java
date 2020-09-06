package com.flanks255.simplybackpacks.capability;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BackpackProvider implements ICapabilitySerializable<CompoundNBT> {
    private final LazyOptional<IItemHandler> lazyBackpackItemHandler;
    private final BackpackItemHandler backpackItemHandler;

    // Holds the last compound until the itemHandler is marked as dirty
    // I have literally no clue if this is the best way of handling this...
    private CompoundNBT lastWrite = new CompoundNBT();

    public BackpackProvider(ItemStack stack, int slots) {
        backpackItemHandler = new BackpackItemHandler(stack, slots);
        lazyBackpackItemHandler = LazyOptional.of(() -> backpackItemHandler);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyBackpackItemHandler.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        // This is called every tick, not sure why, so we'll cache the result once dirty
        if (backpackItemHandler.dirty) {
            lastWrite = backpackItemHandler.serializeNBT();
            backpackItemHandler.dirty = false;
        }

        return lastWrite;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        backpackItemHandler.deserializeNBT(nbt);
    }
}
