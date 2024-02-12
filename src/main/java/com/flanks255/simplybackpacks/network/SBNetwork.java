package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class SBNetwork {
    public static void register(final RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar reg = event.registrar(SimplyBackpacks.MODID);
        reg.play(HotkeyPacket.ID, HotkeyPacket::new, handler -> handler.server(HotkeyPacket::handle));
        reg.play(FilterPacket.ID, FilterPacket::new, handler -> handler.server(FilterPacket::handle));
    }
}
