package com.flanks255.simplybackpacks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class BackpackItemHandler extends ItemStackHandler {
    public BackpackItemHandler(ItemStack itemStack, int size){
        super(size);
        this.size = size;
        this.itemStack = itemStack;
    }
        private ItemStack itemStack;
        private int size;

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
            return stack;

        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        save();
    }

    public void load() {
        /*
        if(itemStack.hasTag())
            load(itemStack.getTag());
            */
        load(itemStack.getOrCreateTag());
    }
    public void load(@Nonnull CompoundNBT nbt) {
        if (nbt.contains("Inventory"))
            deserializeNBT(nbt.getCompound("Inventory"));
    }

    public void save() {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        nbt.put("Inventory", serializeNBT());
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        setSize(size);
        ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                stacks.set(slot, ItemStack.read(itemTags));
            }
        }
        onLoad();
    }
}
