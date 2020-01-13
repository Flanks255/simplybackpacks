package com.flanks255.simplybackpacks;

import com.flanks255.simplybackpacks.items.ItemBackpackBase;
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

        public FilterItemHandler filter = new FilterItemHandler();

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
        if (nbt.contains("Filter"))
            filter.deserializeNBT(nbt.getCompound("Filter"));
    }

    public void save() {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        nbt.put("Inventory", serializeNBT());
        nbt.put("Filter", filter.serializeNBT());
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

    public class FilterItemHandler extends ItemStackHandler {
        public FilterItemHandler() {
            super(16);
        }

        public void removeItem(int slot) {
            this.setStackInSlot(slot, ItemStack.EMPTY);
            save();
        }

        public void setItem(int slot, ItemStack item) {
            if (item.getItem() instanceof ItemBackpackBase || item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
                return;
            else
                this.setStackInSlot(slot, item);
            save();
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent() || stack.getItem() instanceof ItemBackpackBase)
                return stack;

            return super.insertItem(slot, stack, simulate);
        }
    }
}
