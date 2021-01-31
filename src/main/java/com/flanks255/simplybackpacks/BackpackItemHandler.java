package com.flanks255.simplybackpacks;

import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
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
        private boolean dirty = false;
        private boolean loaded = false;

        public FilterItemHandler filter = new FilterItemHandler();

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        //check for shulkers.
        if (stack.getItem() instanceof BlockItem) {
            if (((BlockItem) stack.getItem()).getBlock().isIn(BlockTags.field_226150_J_)) {
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
        dirty = true;
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        dirty = true;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        validateSlotIndex(slot);
        if (!ItemStack.areItemStackTagsEqual(stack, stacks.get(slot))) {
            onContentsChanged(slot);
        }
        this.stacks.set(slot, stack);
    }

    public void setDirty() {
        this.dirty = true;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        dirty = true;
    }

    public void load() {
        load(itemStack.getOrCreateTag());
    }

    public void loadIfNotLoaded() {
        if (!loaded)
            load();
        loaded = true;
    }

    public void load(@Nonnull CompoundNBT nbt) {
        if (nbt.contains("Inventory"))
            deserializeNBT(nbt.getCompound("Inventory"));
        if (nbt.contains("Filter"))
            filter.deserializeNBT(nbt.getCompound("Filter"));
    }

    public void save() {
        if (dirty) {
            CompoundNBT nbt = itemStack.getOrCreateTag();
            nbt.put("Inventory", serializeNBT());
            nbt.put("Filter", filter.serializeNBT());
            dirty = false;
        }
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
            dirty = true;
            save();
        }

        public void setItem(int slot, ItemStack item) {
            if (item.hasTag()) {
                CompoundNBT tag = item.getTag();
                if (tag.contains("Items") || tag.contains("BlockEntityTag") || tag.contains("Inventory"))
                    return;
            }
            if (item.getItem() instanceof ItemBackpackBase || item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
                return;
            else {
                this.setStackInSlot(slot, item);
                dirty = true;
                save();
            }
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
