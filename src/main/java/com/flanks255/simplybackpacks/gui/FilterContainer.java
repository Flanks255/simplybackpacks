package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.inventory.FilterItemHandler;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
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

import java.util.UUID;

public class FilterContainer  extends Container {
    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if (slotID == -106)
            return playerIn.getHeldItemOffhand().getItem() instanceof ItemBackpackBase; //whoops guess you can...
        return playerIn.inventory.getStackInSlot(slotID).getItem() instanceof ItemBackpackBase;
    }

    public FilterItemHandler filterHandler;
    private int slotID;
    private PlayerEntity playerEntity;
    private ItemStack stack;
    private UUID uuid;

    public static FilterContainer fromNetwork(final int windowId, final PlayerInventory playerInventory, PacketBuffer extra) {
        UUID uuid = extra.readUniqueId();
        return new FilterContainer(windowId, playerInventory, uuid, BackpackManager.blankClient.getOrCreateBackpack(uuid, Backpack.COMMON).getFilter());
    }

    public FilterContainer(int windowId, PlayerInventory playerInventory, UUID uuid, FilterItemHandler handlerIn) {
        super(SimplyBackpacks.FILTERCONTAINER.get(), windowId);

        playerEntity = playerInventory.player;
        this.uuid = uuid;
        stack = findBackpack(playerEntity);
        this.filterHandler = handlerIn;

        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemBackpackBase)) {
            playerEntity.closeScreen();
            return;
        }


        addPlayerSlots(playerInventory);
    }

    private ItemStack findBackpack(PlayerEntity playerEntity) {
        PlayerInventory inv = playerEntity.inventory;

        if (playerEntity.getHeldItemMainhand().getItem() instanceof ItemBackpackBase) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == playerEntity.getHeldItemMainhand()) {
                    slotID = i;
                    return stack;
                }
            }
        } else if (playerEntity.getHeldItemOffhand().getItem() instanceof ItemBackpackBase) {
            slotID = -106;
            return playerEntity.getHeldItemOffhand();
        }
        else {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.getItem() instanceof ItemBackpackBase) {
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
        return stack.getOrCreateTag().getInt("Filter-OPT");
    }

    public boolean getPickup() {
        return stack.getOrCreateTag().getBoolean("Pickup");
    }

    public boolean togglePickup() {
        CompoundNBT nbt = stack.getOrCreateTag();

        boolean Pickup = !nbt.getBoolean("Pickup");
        nbt.putBoolean("Pickup",Pickup);

        if (playerEntity.getEntityWorld().isRemote)
            SimplyBackpacks.NETWORK.sendToServer(new ToggleMessage());
        return Pickup;
    }

    public int setFilterOpts(int newOpts) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("Filter-OPT", newOpts);
        stack.setTag(nbt);
        if (playerEntity.getEntityWorld().isRemote)
            SimplyBackpacks.NETWORK.sendToServer(new FilterMessage(newOpts));
        return newOpts;
    }

    public void saveFilter(int newOpts) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("Filter-OPT", newOpts);
        stack.setTag(nbt);
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
        if (playerIn.inventory.getItemStack().isEmpty())
            filterHandler.removeItem(id);
        else {
            ItemStack fake = playerIn.inventory.getItemStack().copy();
            fake.setCount(1);
            filterHandler.setItem(id, fake);
        }
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }
}