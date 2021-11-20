package com.flanks255.simplybackpacks.gui;


import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class LockableSlot extends Slot {
    private final boolean locked;

    public LockableSlot(Inventory inventory, int slotIndex, int x, int y, boolean lock) {
        super(inventory, slotIndex, x, y);
        this.locked = lock;
    }

    @Override
    public boolean mayPickup(@Nonnull Player p_82869_1_) {
        return !this.locked;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack p_75214_1_) {
        return !this.locked;
    }


}
