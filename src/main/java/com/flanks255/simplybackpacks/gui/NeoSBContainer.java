package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;

public class NeoSBContainer extends Container {
    private final Location location;

    public NeoSBContainer(int id, Location loc) {
        super(SimplyBackpacks.NEOSBCONTAINER.get(), id);
        this.location = loc;



    }

    @Nonnull
    public static NeoSBContainer fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        return new NeoSBContainer(windowId, findBackpack(inv.player));
    }

    public static Location findBackpack(PlayerEntity player) {
        PlayerInventory inv = player.inventory;

        if (player.getHeldItemMainhand().getItem() instanceof ItemBackpackBase) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == player.getHeldItemMainhand()) {
                    return new Location(i, stack);
                }
            }
        } else if (player.getHeldItemOffhand().getItem() instanceof ItemBackpackBase) {
            return new Location(-106, player.getHeldItemOffhand());
        }
        else {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.getItem() instanceof ItemBackpackBase) {
                    return new Location(i, stack);
                }
            }
        }
        return new Location(0, ItemStack.EMPTY);
    }

    public static class Location {
        private final int slot;
        private final ItemStack stack;

        public Location(int slot, ItemStack stack) {
            this.slot = slot;
            this.stack = stack;
        }

        public int getSlot() {
            return slot;
        }

        public ItemStack getStack() {
            return stack;
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if (location.getSlot() == -106)
            return playerIn.getHeldItemOffhand().getItem() instanceof ItemBackpackBase;
        return playerIn.inventory.getStackInSlot(location.getSlot()).getItem() instanceof ItemBackpackBase;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }
}
