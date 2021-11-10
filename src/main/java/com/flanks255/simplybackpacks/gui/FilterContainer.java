package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.inventory.FilterItemHandler;
import com.flanks255.simplybackpacks.items.Backpack;
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

import javax.annotation.Nonnull;
import java.util.UUID;

public class FilterContainer  extends Container {
    @Override
    public boolean stillValid(@Nonnull PlayerEntity playerIn) {
        if (slotID == -106)
            return playerIn.getOffhandItem().getItem() instanceof BackpackItem; //whoops guess you can...
        return playerIn.inventory.getItem(slotID).getItem() instanceof BackpackItem;
    }

    public final FilterItemHandler filterHandler;
    private int slotID;
    private final PlayerEntity playerEntity;
    private final ItemStack stack;

    public static FilterContainer fromNetwork(final int windowId, final PlayerInventory playerInventory, PacketBuffer extra) {
        UUID uuid = extra.readUUID();
        return new FilterContainer(windowId, playerInventory, uuid, BackpackManager.blankClient.getOrCreateBackpack(uuid, Backpack.COMMON).getFilter());
    }

    public FilterContainer(int windowId, PlayerInventory playerInventory, UUID uuid, FilterItemHandler handlerIn) {
        super(SimplyBackpacks.FILTERCONTAINER.get(), windowId);

        playerEntity = playerInventory.player;
        stack = findBackpack(playerEntity);
        this.filterHandler = handlerIn;

        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BackpackItem)) {
            playerEntity.closeContainer();
            return;
        }


        addPlayerSlots(playerInventory);
    }

    private ItemStack findBackpack(PlayerEntity playerEntity) {
        PlayerInventory inv = playerEntity.inventory;

        if (playerEntity.getMainHandItem().getItem() instanceof BackpackItem) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack == playerEntity.getMainHandItem()) {
                    slotID = i;
                    return stack;
                }
            }
        } else if (playerEntity.getOffhandItem().getItem() instanceof BackpackItem) {
            slotID = -106;
            return playerEntity.getOffhandItem();
        }
        else {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() instanceof BackpackItem) {
                    slotID = i;
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }


    @Override
    @Nonnull
    public ItemStack clicked(int slotId, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull PlayerEntity playerIn) {
        if (slotId >= 0 && getSlot(slotId).getItem() == playerIn.getMainHandItem())
            return ItemStack.EMPTY;

        if (clickTypeIn == ClickType.SWAP)
            return ItemStack.EMPTY;

        if (slotId >= 0) getSlot(slotId).container.setChanged();
        return super.clicked(slotId, dragType, clickTypeIn, playerIn);
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

        if (playerEntity.getCommandSenderWorld().isClientSide)
            SimplyBackpacks.NETWORK.sendToServer(new ToggleMessage());
        return Pickup;
    }

    public int setFilterOpts(int newOpts) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("Filter-OPT", newOpts);
        stack.setTag(nbt);
        if (playerEntity.getCommandSenderWorld().isClientSide)
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
    public boolean clickMenuButton(PlayerEntity playerIn, int id) {
        if (playerIn.inventory.getCarried().isEmpty())
            filterHandler.removeItem(id);
        else {
            ItemStack fake = playerIn.inventory.getCarried().copy();
            fake.setCount(1);
            filterHandler.setItem(id, fake);
        }
        return true;
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }
}