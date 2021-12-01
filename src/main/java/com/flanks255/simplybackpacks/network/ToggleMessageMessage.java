package com.flanks255.simplybackpacks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleMessageMessage {
    public ToggleMessageMessage(boolean enabled) {
        this.enabled = enabled;
    }
    private final boolean enabled;
    public static ToggleMessageMessage decode(final FriendlyByteBuf buffer) {
        boolean en = buffer.readBoolean();
        return new ToggleMessageMessage(en);
    }
    public static void encode(final ToggleMessageMessage message, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.enabled);
    }
    public static void handle(final ToggleMessageMessage message, final Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient())
            ctx.get().enqueueWork(() -> {
                Minecraft.getInstance().player.displayClientMessage(new TextComponent(I18n.get(message.enabled ?"simplybackpacks.autopickupenabled":"simplybackpacks.autopickupdisabled")),true);
            } );
        ctx.get().setPacketHandled(true);
    }
}
