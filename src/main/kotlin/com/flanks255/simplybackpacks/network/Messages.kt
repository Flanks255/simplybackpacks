package com.flanks255.simplybackpacks.network

import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

class ToggleMessage: IMessage {
    override fun fromBytes(buf: ByteBuf?) {
        buf?.readInt()
    }

    override fun toBytes(buf: ByteBuf?) {
        buf?.writeInt(0)
    }
}

class OpenMessage: IMessage {
    override fun fromBytes(buf: ByteBuf?) {
        buf?.readInt()
    }

    override fun toBytes(buf: ByteBuf?) {
        buf?.writeInt(0)
    }
}