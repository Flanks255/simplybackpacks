package com.flanks255.simplybackpacks.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class LockedSlot extends Slot {
    public LockedSlot(IInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
    }

    @Override
    public boolean mayPickup(PlayerEntity p_82869_1_) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack p_75214_1_) {
        return false;
    }


}
