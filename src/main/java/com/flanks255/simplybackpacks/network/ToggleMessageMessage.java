package com.flanks255.simplybackpacks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleMessageMessage {
    public ToggleMessageMessage(boolean enabled) {
        this.enabled = enabled;
    }
    private final boolean enabled;
    public static ToggleMessageMessage decode(final PacketBuffer buffer) {
        boolean en = buffer.readBoolean();
        return new ToggleMessageMessage(en);
    }
    public static void encode(final ToggleMessageMessage message, final PacketBuffer buffer) {
        buffer.writeBoolean(message.enabled);
    }
    public static void handle(final ToggleMessageMessage message, final Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient())
            ctx.get().enqueueWork(() -> {
                Minecraft.getInstance().player.displayClientMessage(new StringTextComponent(I18n.get(message.enabled ?"simplybackpacks.autopickupenabled":"simplybackpacks.autopickupdisabled")),true);
            } );
        ctx.get().setPacketHandled(true);
    }
}
