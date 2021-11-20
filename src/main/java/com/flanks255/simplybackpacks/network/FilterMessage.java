package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.gui.FilterContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class FilterMessage {
    public FilterMessage(int opts) {
        this.opts = opts;
    }
    private final int opts;
    public static FilterMessage decode(final FriendlyByteBuf buffer) {
        return new FilterMessage(buffer.readInt());
    }
    public static void encode(final FilterMessage message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.opts);
    }
    public static void handle(final FilterMessage message, final Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isServer())
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null && player.containerMenu instanceof FilterContainer)
                    ((FilterContainer) player.containerMenu).saveFilter(message.opts);

            } );
        ctx.get().setPacketHandled(true);
    }
}
