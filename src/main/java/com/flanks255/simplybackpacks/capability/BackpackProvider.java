package com.flanks255.simplybackpacks.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BackpackProvider implements ICapabilitySerializable<CompoundNBT> {
    private LazyOptional<IItemHandler> lazyBackpackItemHandler;
    private BackpackItemHandler backpackItemHandler;

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
        return backpackItemHandler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        backpackItemHandler.deserializeNBT(nbt);
    }
}
