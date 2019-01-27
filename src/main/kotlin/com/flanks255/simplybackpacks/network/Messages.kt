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

class FilterMessage: IMessage {
    constructor()
    constructor(filterIn: Int) { filterOpts = filterIn }
    var filterOpts: Int = 0
    override fun toBytes(buf: ByteBuf?) {
        buf?.writeInt(filterOpts)
    }

    override fun fromBytes(buf: ByteBuf?) {
        filterOpts = buf?.readInt()?:0
    }
}

class ToggleMessageMessage: IMessage {
    constructor()
    constructor(enabledIn: Boolean) { enabled = enabledIn }
    var enabled = false

    override fun fromBytes(buf: ByteBuf?) {
        enabled = buf?.readBoolean()?:false
    }

    override fun toBytes(buf: ByteBuf?) {
        buf?.writeBoolean(enabled)
    }
}