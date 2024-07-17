package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class SBNetwork {
    public static void register(final RegisterPayloadHandlersEvent event) {
        event.registrar(SimplyBackpacks.MODID)
        .playToServer(HotkeyPacket.TYPE, HotkeyPacket.CODEC, HotkeyPacket::handle)
        .playToServer(FilterPacket.TYPE, FilterPacket.CODEC, FilterPacket::handle);
    }
}
