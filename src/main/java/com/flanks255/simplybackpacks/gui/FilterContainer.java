package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.capability.BackpackItemHandler;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.network.FilterMessage;
import com.flanks255.simplybackpacks.network.ToggleMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class FilterContainer  extends Container {
    public FilterContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        super(SimplyBackpacks.FILTER_CONTAINER.get(), id);
        PlayerEntity playerEntity = playerInventory.player;

        item = findBackpack(playerEntity);
        if (item.isEmpty()) {
            playerEntity.closeScreen();
            return;
        }

        addPlayerSlots(playerInventory);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return !playerIn.getHeldItemMainhand().isEmpty();
    }

//    public BackpackItemHandler itemHandler;
    private int slotID;
    private PlayerEntity player;
    public ItemStack item;

    @Nonnull
    private ItemStack findBackpack(PlayerEntity playerEntity) {
        PlayerInventory inv = playerEntity.inventory;

        if (playerEntity.getHeldItemMainhand().getItem() instanceof BackpackItem) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == playerEntity.getHeldItemMainhand()) {
                    slotID = i;
                    return stack;
                }
            }
        } else if (playerEntity.getHeldItemOffhand().getItem() instanceof BackpackItem) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == playerEntity.getHeldItemOffhand()) {
                    slotID = i;
                    return stack;
                }
            }
        }
        else {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.getItem() instanceof BackpackItem) {
                    slotID = i;
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }


    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity playerIn) {
        if (slotId >= 0 && getSlot(slotId).getStack() == playerIn.getHeldItemMainhand())
            return ItemStack.EMPTY;

        if (clickTypeIn == ClickType.SWAP)
            return ItemStack.EMPTY;

        if (slotId >= 0) getSlot(slotId).inventory.markDirty();
        return super.slotClick(slotId, dragType, clickTypeIn, playerIn);
    }

    public int getFilterOpts() {
        return item.getOrCreateTag().getInt("Filter-OPT");
    }

    public boolean getPickup() {
        return item.getOrCreateTag().getBoolean("Pickup");
    }

    public boolean togglePickup() {
        CompoundNBT nbt = item.getOrCreateTag();

        boolean Pickup = !nbt.getBoolean("Pickup");
        nbt.putBoolean("Pickup",Pickup);

        if (player.getEntityWorld().isRemote)
            SimplyBackpacks.network.sendToServer(new ToggleMessage());
        return Pickup;
    }

    public int setFilterOpts(int newOpts) {
        CompoundNBT nbt = item.getOrCreateTag();
        nbt.putInt("Filter-OPT", newOpts);
        item.setTag(nbt);
        if (player.getEntityWorld().isRemote)
            SimplyBackpacks.network.sendToServer(new FilterMessage(newOpts));
        return newOpts;
    }

    public void saveFilter(int newOpts) {
        CompoundNBT nbt = item.getOrCreateTag();
        nbt.putInt("Filter-OPT", newOpts);
        item.setTag(nbt);
    }

    public void addPlayerSlots(PlayerInventory playerInventory) {

        int originX = 7;
        int originY = 83;

        //Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = originX + col * 18;
                int y = originY + row * 18;
                this.addSlot(new Slot(playerInventory, (col + row * 9) + 9, x + 1, y + 1));
            }
        }
        //Hot-bar
        for (int col = 0; col < 9; col++) {
            int x = originX + col * 18;
            int y = originY + 58;
            this.addSlot(new Slot(playerInventory, col, x + 1, y + 1));
        }
    }

    @Override
    public boolean enchantItem(PlayerEntity playerIn, int id) {
        item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            if (playerIn.inventory.getItemStack().isEmpty())
                ((BackpackItemHandler) handler).getFilterHandler().extractItem(id, 1, false);
            else {
                ItemStack fake = playerIn.inventory.getItemStack().copy();
                fake.setCount(1);

                ((BackpackItemHandler) handler).getFilterHandler().insertItem(id, fake, false);
            }
        });
        //SimplyBackpacks.LOGGER.info("EnchantPacket: " + id);

        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }
}