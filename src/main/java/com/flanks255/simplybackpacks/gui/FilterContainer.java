package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.BackpackItemHandler;
import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import com.flanks255.simplybackpacks.network.FilterMessage;
import com.flanks255.simplybackpacks.network.ToggleMessage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class FilterContainer extends AbstractContainerMenu {
    @Override
    public boolean stillValid(Player playerIn) {
        if (slotID == -106)
            return playerIn.getOffhandItem().getItem() instanceof ItemBackpackBase; //whoops guess you can...
        return playerIn.getInventory().getItem(slotID).getItem() instanceof ItemBackpackBase;
    }

    public BackpackItemHandler itemHandler;
    private int slotID;
    private Player player;
    private ItemStack item;

    public FilterContainer(final int windowId, final Inventory playerInventory, FriendlyByteBuf extra) {
        this(windowId, playerInventory.player.level, playerInventory.player.blockPosition(), playerInventory, playerInventory.player);
    }

    public FilterContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        super(SimplyBackpacks.FILTERCONTAINER.get(), windowId);

        item = findBackpack(playerEntity);

        if (item == null || item.isEmpty()) {
            playerEntity.closeContainer();
            return;
        }

        IItemHandler tmp = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (tmp == null)
            return;

        if (tmp instanceof BackpackItemHandler) {
            itemHandler = (BackpackItemHandler)tmp;
            itemHandler.load();
            player = playerEntity;
        } else {
            playerEntity.closeContainer();
        }


        addPlayerSlots(playerInventory);
    }

    private ItemStack findBackpack(Player playerEntity) {
        Inventory inv = playerEntity.getInventory();

        if (playerEntity.getMainHandItem().getItem() instanceof ItemBackpackBase) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack == playerEntity.getMainHandItem()) {
                    slotID = i;
                    return stack;
                }
            }
        } else if (playerEntity.getOffhandItem().getItem() instanceof ItemBackpackBase) {
            slotID = -106;
            return playerEntity.getOffhandItem();
        }
        else {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() instanceof ItemBackpackBase) {
                    slotID = i;
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }


    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player playerIn) {
        if (slotId >= 0 && getSlot(slotId).getItem() == playerIn.getMainHandItem())
            return;

        if (clickTypeIn == ClickType.SWAP)
            return;

        if (slotId >= 0) getSlot(slotId).container.setChanged();
        super.clicked(slotId, dragType, clickTypeIn, playerIn);
    }

    public int getFilterOpts() {
        return item.getOrCreateTag().getInt("Filter-OPT");
    }

    public boolean getPickup() {
        return item.getOrCreateTag().getBoolean("Pickup");
    }

    public boolean togglePickup() {
        CompoundTag nbt = item.getOrCreateTag();

        boolean Pickup = !nbt.getBoolean("Pickup");
        nbt.putBoolean("Pickup",Pickup);

        if (player.getCommandSenderWorld().isClientSide)
            SimplyBackpacks.NETWORK.sendToServer(new ToggleMessage());
        return Pickup;
    }

    public int setFilterOpts(int newOpts) {
        CompoundTag nbt = item.getOrCreateTag();
        nbt.putInt("Filter-OPT", newOpts);
        item.setTag(nbt);
        if (player.getCommandSenderWorld().isClientSide)
            SimplyBackpacks.NETWORK.sendToServer(new FilterMessage(newOpts));
        return newOpts;
    }

    public void saveFilter(int newOpts) {
        CompoundTag nbt = item.getOrCreateTag();
        nbt.putInt("Filter-OPT", newOpts);
        item.setTag(nbt);
    }


    public void addPlayerSlots(Inventory playerInventory) {

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
    public boolean clickMenuButton(Player playerIn, int id) {
        //SimplyBackpacks.LOGGER.info("EnchantPacket: " + id);
        if (getCarried().isEmpty())
            itemHandler.filter.removeItem(id);
        else {
            ItemStack fake = getCarried().copy();
            fake.setCount(1);
            itemHandler.filter.setItem(id, fake);
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }
}