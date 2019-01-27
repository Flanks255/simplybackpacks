package com.flanks255.simplybackpacks


import com.flanks255.simplybackpacks.config.ConfigHelper
import net.minecraft.item.ItemShulkerBox
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler

class BackpackItemStackHandler(val size: Int): ItemStackHandler(size) {
    val filter: ItemStackHandler = FilterItemStackHandler()

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN) || stack.item is ItemShulkerBox || ConfigHelper.checkBlacklist(stack.item.registryName.toString()))
            return stack
        return super.insertItem(slot, stack, simulate)
    }
}

class FilterItemStackHandler: ItemStackHandler(16) {
    fun removeItem(slot: Int) {
        setStackInSlot(slot, ItemStack.EMPTY)
    }
    fun setItem(slot: Int, item: ItemStack) {
        setStackInSlot(slot, item)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN) || stack.item is ItemShulkerBox || ConfigHelper.checkBlacklist(stack.item.registryName.toString()))
            return stack
        return super.insertItem(slot, stack, simulate)
    }
}