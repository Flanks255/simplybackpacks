package com.flanks255.simplybackpacks.gui;

import com.flanks255.simplybackpacks.BackpackItemHandler;
import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import com.flanks255.simplybackpacks.network.FilterMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class FilterContainer  extends Container {
    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return !playerIn.getHeldItemMainhand().isEmpty();
    }

    public BackpackItemHandler itemHandler;
    private int slotID;
    private PlayerEntity player;
    private ItemStack item;

    public static final ContainerType type = new ContainerType<>(FilterContainer::new).setRegistryName("sb_filter_container");

    public FilterContainer(final int windowId, final PlayerInventory playerInventory) {
        this(windowId, playerInventory.player.world, playerInventory.player.getPosition(), playerInventory, playerInventory.player);
    }

    public FilterContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        super(type, windowId);

        item = findBackpack(playerEntity);

        if (item == null || item.isEmpty()) {
            playerEntity.closeScreen();
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
            playerEntity.closeScreen();
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
        return item.getOrCreateTag().getInt("Filter-OPT");
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
        //SimplyBackpacks.LOGGER.info("EnchantPacket: " + id);
        if (playerIn.inventory.getItemStack().isEmpty())
            itemHandler.filter.removeItem(id);
        else {
            ItemStack fake = playerIn.inventory.getItemStack().copy();
            fake.setCount(1);
            itemHandler.filter.setItem(id, fake);
        }
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }
}