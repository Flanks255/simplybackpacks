package com.flanks255.simplybackpacks

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler



class BackpackContainer(item: ItemStack, playerInventory: InventoryPlayer, mySlotIn: Int): Container() {

    var slotcount: Int = 0
    var itemHandler: IItemHandler? = null
    var mySlot: Int = -1
    init {
        mySlot = mySlotIn
        itemHandler = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        slotcount = itemHandler?.slots?:0

        addMySlots(item)
        addPlayerSlots(playerInventory)

    }
    val player: EntityPlayer = playerInventory.player

    //dupe bug #2
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return when(mySlot) {
            -1 -> true
            -2 -> !player.heldItemMainhand.isEmpty
            else -> !playerIn.inventory.getStackInSlot(mySlot).isEmpty
        }
    }

    //don't move it...
    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: EntityPlayer): ItemStack {
       /* if (slotId >= 0 && getSlot(slotId).stack == player.heldItemMainhand)
            return ItemStack.EMPTY
        */
        if (slotId >= 0){
            if (mySlot == -2) {
                if (getSlot(slotId).stack == player.heldItemMainhand) return ItemStack.EMPTY
            } else if (mySlot > -1) {
                if (getSlot(slotId).stack == player.inventory.getStackInSlot(mySlot)) return ItemStack.EMPTY
            }
        }
        //go away dupe bug #1
        if(slotId >= 0) getSlot(slotId).inventory.markDirty()
        return super.slotClick(slotId, dragType, clickTypeIn, player)
    }

    fun addPlayerSlots(playerInventory: InventoryPlayer) {

        val originX: Int = when(slotcount) {
            18 -> 7
            else -> 25
        }
        val originY: Int = when(slotcount) {
            18 -> 47
            33 -> 65
            66 -> 119
            else -> 173
        }
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

fun addMySlots(item: ItemStack) {
    if (itemHandler == null) return

    val cols: Int = if (slotcount == 18) 9 else 11
    val rows: Int = slotcount / cols
    var slotIndex = 0

    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val x = 7 + col * 18
            val y = 7 + row * 18
            this.addSlotToContainer(SlotItemHandler(itemHandler, slotIndex, x + 1, y + 1))
            slotIndex++
            if (slotIndex >= slotcount)
                break
        }
    }
}

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val bagslotcount: Int = inventorySlots.size - playerIn.inventory.mainInventory.size
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()
            if (index < bagslotcount) {
                if (!this.mergeItemStack(itemstack1, bagslotcount, this.inventorySlots.size, true))
                    return ItemStack.EMPTY
            } else if (!this.mergeItemStack(itemstack1, 0, bagslotcount, false)) {
                return ItemStack.EMPTY
            }
            if (itemstack1.isEmpty) slot.putStack(ItemStack.EMPTY) else slot.onSlotChanged()
        }
        return itemstack
    }
}