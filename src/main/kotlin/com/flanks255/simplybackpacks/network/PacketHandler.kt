package com.flanks255.simplybackpacks.network

import com.flanks255.simplybackpacks.gui.FilterContainer
import com.flanks255.simplybackpacks.items.ItemBackpackBase
import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ToggleHandler: IMessageHandler<ToggleMessage, IMessage> {
    override fun onMessage(message: ToggleMessage?, ctx: MessageContext?): IMessage? {
        val player: EntityPlayerMP? = ctx?.serverHandler?.player

        player?.serverWorld?.addScheduledTask {
            var wasHotbar = false
            if (player.heldItemMainhand.item is ItemBackpackBase)
                (player.heldItemMainhand.item as ItemBackpackBase).togglePickup(player, player.heldItemMainhand)
            else {
                for (i in 0..8){
                    val stack: ItemStack = player.inventory.getStackInSlot(i)
                    if (stack.item is ItemBackpackBase) {
                        (stack.item as ItemBackpackBase).togglePickup(player, stack)
                        wasHotbar = true
                        break
                    }
                }
                if (!wasHotbar && simplybackpacks.isBaubles) {
                    simplybackpacks.toggleBauble(player)
                }
            }
        }
        return null
    }
}

class OpenHandler: IMessageHandler<OpenMessage, IMessage> {
    override fun onMessage(message: OpenMessage?, ctx: MessageContext?): IMessage? {
        val player: EntityPlayerMP? = ctx?.serverHandler?.player

        player?.serverWorld?.addScheduledTask {
            if (player.heldItemMainhand.item is ItemBackpackBase)
                player.openGui(simplybackpacks, 0, player.serverWorld, player.position.x, player.position.y, player.position.z )
            else {
                var (stack: ItemStack?,_) = simplybackpacks.findBackpackHotbar(player)
                if (stack != null) {
                    player.openGui(simplybackpacks, 1, player.serverWorld, player.position.x, player.position.y, player.position.z )
                }
                else {
                    if (simplybackpacks.isBaubles) {
                        stack = simplybackpacks.findBackpackBauble(player)
                        if (stack != null)
                            player.openGui(simplybackpacks, 2, player.serverWorld, player.position.x, player.position.y, player.position.z)
                    }
                }

            }
        }

        return null
    }
}

class FilterHandler: IMessageHandler<FilterMessage, IMessage> {
    override fun onMessage(message: FilterMessage?, ctx: MessageContext?): IMessage? {
        val player: EntityPlayerMP? = ctx?.serverHandler?.player

        player?.serverWorld?.addScheduledTask {
            if (player.openContainer is FilterContainer)
                (player.openContainer as FilterContainer).saveFilter(message?.filterOpts?:0)
        }
        return null
    }
}

class ToggleMessageHandler: IMessageHandler<ToggleMessageMessage, IMessage> {
    override fun onMessage(message: ToggleMessageMessage?, ctx: MessageContext?): IMessage? {
        if (ctx?.side?.isClient?:true)
            Minecraft.getMinecraft().addScheduledTask {
                Minecraft.getMinecraft().player.sendStatusMessage(TextComponentString(I18n.format("simplybackpacks.autopickup"+ if (message?.enabled?:false) "enabled" else "disabled")),true)
            }
        return null
    }
}