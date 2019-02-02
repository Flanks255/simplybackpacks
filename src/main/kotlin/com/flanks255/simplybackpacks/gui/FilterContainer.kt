package com.flanks255.simplybackpacks.gui

import com.flanks255.simplybackpacks.BackpackItemStackHandler
import com.flanks255.simplybackpacks.FilterItemStackHandler
import com.flanks255.simplybackpacks.network.FilterMessage
import com.flanks255.simplybackpacks.network.NetworkWrapper
import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler



class FilterContainer(val item: ItemStack, playerInventory: InventoryPlayer): Container() {
    var itemHandler: IItemHandler? = null
    var filterHandler: IItemHandler? = null
    init {
        itemHandler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)

        if (itemHandler is BackpackItemStackHandler)
            filterHandler = (itemHandler as BackpackItemStackHandler).filter

        //addFilterSlots(item)
        addPlayerSlots(playerInventory)

    }
    val player: EntityPlayer = playerInventory.player

    //close if it vanishes
    override fun canInteractWith(playerIn: EntityPlayer): Boolean = !player.heldItemMainhand.isEmpty

    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: EntityPlayer): ItemStack {
        //Dont move it...
        if (slotId >= 0 && getSlot(slotId).stack == player.heldItemMainhand)
            return ItemStack.EMPTY

        if(slotId >= 0) getSlot(slotId).inventory.markDirty()
        return super.slotClick(slotId, dragType, clickTypeIn, player)
    }

    fun getFilterOpts(): Int {
        if (item.hasTagCompound())
            return item.tagCompound?.getInteger("Filter")?:0
        else
            return 0
    }

    fun setFilterOpts(new: Int) {
        val nbt: NBTTagCompound = if (item.hasTagCompound()) item.tagCompound?: NBTTagCompound() else NBTTagCompound()
        nbt.setInteger("Filter", new)
        item.tagCompound = nbt
        if (simplybackpacks.proxy?.isClient()?:false)
            NetworkWrapper.wrapper.sendToServer(FilterMessage(new))
    }

    fun saveFilter(filterIn: Int) {
        val nbt: NBTTagCompound = if (item.hasTagCompound()) item.tagCompound?: NBTTagCompound() else NBTTagCompound()
        nbt.setInteger("Filter", filterIn)
        item.tagCompound = nbt
    }


    fun addPlayerSlots(playerInventory: InventoryPlayer) {

        val originX = 7
        val originY = 83

        //Player Inventory
        for (row in 0..2) {
            for (col in 0..8) {
                val x = originX + col * 18
                val y = originY + row * 18
                this.addSlotToContainer(Slot(playerInventory, (col + row * 9) + 9, x + 1, y + 1))
        }
        }
        //Hot-bar
        for (row in 0..8) {
            val x = originX + row * 18
            val y = originY + 58
            this.addSlotToContainer(Slot(playerInventory, row, x + 1, y + 1))
        }
    }

    override fun enchantItem(playerIn: EntityPlayer, id: Int): Boolean {
        if (player.inventory.itemStack.isEmpty)
            (filterHandler as FilterItemStackHandler).removeItem(id)
        else {
            val fakeitem: ItemStack = player.inventory.itemStack.copy()
            fakeitem.count = 1
            (filterHandler as FilterItemStackHandler).setItem(id, fakeitem)
        }
        return true
    }

    fun addFilterSlots(item: ItemStack) {
        if (itemHandler == null) return
        if (filterHandler == null) return

        val cols = 4
        val rows = 4
        var slotIndex = 0

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val x = 7 + col * 18
                val y = 7 + row * 18
                this.addSlotToContainer(SlotItemHandler(filterHandler, slotIndex, x + 1, y + 1))
                slotIndex++
                if (slotIndex >= 16)
                    break
            }
        }
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        return ItemStack.EMPTY
    }
}