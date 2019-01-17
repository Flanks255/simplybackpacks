package com.flanks255.simplybackpacks


import com.flanks255.simplybackpacks.config.ConfigHelper
import net.minecraft.item.ItemShulkerBox
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class BackpackItemStackHandler(val size: Int): ItemStackHandler(size) {
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN) || stack.item is ItemShulkerBox || ConfigHelper.checkBlacklist(stack.item.registryName.toString()))
            return stack
        return super.insertItem(slot, stack, simulate)
    }
}