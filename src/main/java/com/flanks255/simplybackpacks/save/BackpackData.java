package com.flanks255.simplybackpacks.save;

import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.UUID;

public class BackpackData {
    private UUID uuid;
    private int size;
    private Backpack tierData;
    private NeoBackpackItemHandler inventory;
    private LazyOptional<IItemHandler> optional;
    private UUID owner;
    private UUID lastOpenedBy;


    public BackpackData(UUID uuid, Backpack tier) {
        this.uuid = uuid;
        this.size = tier.slots;
        tierData = tier;
        this.inventory = new NeoBackpackItemHandler(size);
        this.optional = LazyOptional.of(() -> inventory);
    }

    public LazyOptional<IItemHandler> getOptional() {
        return optional;
    }

    public NeoBackpackItemHandler getHandler() {
        return inventory;
    }

    public int getSize() {
        return size;
    }

    public UUID getID() {
        return uuid;
    }

    public static LazyOptional<BackpackData> fromNBT(CompoundNBT nbtIn) {
        if (nbtIn.contains("UUID")) {
            BackpackData backpack = new BackpackData(nbtIn.getUniqueId("UUID"), Backpack.values()[nbtIn.getInt("Tier")]);
            backpack.inventory.deserializeNBT(nbtIn.getCompound("Inventory"));
            backpack.inventory.filter.deserializeNBT(nbtIn.getCompound("Filter"));
        }
        return LazyOptional.empty();
    }

    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putUniqueId("UUID", uuid);
        nbt.putInt("Tier", tierData.ordinal());

        nbt.put("Inventory",inventory.serializeNBT());
        nbt.put("Filter",inventory.filter.serializeNBT());

        return nbt;
    }

    public static class NeoBackpackItemHandler extends ItemStackHandler {
        NeoFilterItemHandler filter = new NeoFilterItemHandler();
        public NeoBackpackItemHandler(int size) {
            super(size);
        }









        public static class NeoFilterItemHandler extends ItemStackHandler {
            public NeoFilterItemHandler() {
                super(16);
            }

            public void removeItem(int slot) {
                this.setStackInSlot(slot, ItemStack.EMPTY);
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
}
