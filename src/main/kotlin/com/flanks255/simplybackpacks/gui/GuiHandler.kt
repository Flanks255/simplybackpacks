package com.flanks255.simplybackpacks.gui

import com.flanks255.simplybackpacks.BackpackContainer
import com.flanks255.simplybackpacks.items.ItemBackpackBase
import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

object GuiHandler: IGuiHandler {
    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World?, x: Int, y: Int, z: Int): Any? {
        if (ID == 0) { //Mainhand Held Backpack
            return if (player.heldItemMainhand.item is ItemBackpackBase) BackpackGui(BackpackContainer(player.heldItemMainhand, player.inventory, -2)) else null
        } else if (ID == 1) { //Hotbar Held Backpack
            val (stack: ItemStack?, sid: Int) = simplybackpacks.findBackpackHotbar(player)
            return if (stack != null) BackpackGui(BackpackContainer(stack, player.inventory, sid)) else null
        } else if (ID == 2) { //Bauble Held Backpack
            val stack: ItemStack? = simplybackpacks.findBackpackBauble(player)
            return if (stack != null) BackpackGui(BackpackContainer(stack, player.inventory, -1)) else null
        }
        return null
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World?, x: Int, y: Int, z: Int): Any? {
        if (ID == 0) { //Mainhand Held Backpack
            return if (player.heldItemMainhand.item is ItemBackpackBase) BackpackContainer(player.heldItemMainhand, player.inventory, -2) else null
        } else if (ID == 1) { //Hotbar Held Backpack
            val (stack: ItemStack?, sid: Int) = simplybackpacks.findBackpackHotbar(player)
            return if (stack != null) BackpackContainer(stack, player.inventory, sid) else null
        } else if (ID == 2) { //Bauble Held Backpack
            val stack: ItemStack? = simplybackpacks.findBackpackBauble(player)
            return if (stack != null) BackpackContainer(stack, player.inventory, -1) else null
        }
        return null
    }
}