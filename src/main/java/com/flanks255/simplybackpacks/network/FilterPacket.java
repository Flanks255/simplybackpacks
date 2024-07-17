package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FilterPacket(int opts) implements CustomPacketPayload {
    public static final Type<FilterPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SimplyBackpacks.MODID, "filter"));
    public static final StreamCodec<FriendlyByteBuf, FilterPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FilterPacket::opts, FilterPacket::new
    );

    public static void handle(final FilterPacket packet, IPayloadContext ctx) {
            if (ctx.player().containerMenu instanceof FilterContainer)
                ((FilterContainer) ctx.player().containerMenu).saveFilterServer(packet.opts);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
