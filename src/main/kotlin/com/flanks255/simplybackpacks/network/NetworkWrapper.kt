package com.flanks255.simplybackpacks.network

import com.flanks255.simplybackpacks.simplybackpacks
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side


object NetworkWrapper {
    val wrapper: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(simplybackpacks.MODID)

    fun registerPackets() {
        var id = 0
        wrapper.registerMessage(ToggleHandler::class.java, ToggleMessage::class.java, id++, Side.SERVER)
        wrapper.registerMessage(OpenHandler::class.java, OpenMessage::class.java, id++, Side.SERVER)
        wrapper.registerMessage(FilterHandler::class.java, FilterMessage::class.java, id++, Side.SERVER)
        wrapper.registerMessage(ToggleMessageHandler::class.java, ToggleMessageMessage::class.java, id++, Side.CLIENT)
    }
}