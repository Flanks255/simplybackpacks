package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.FilterContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record FilterPacket(int opts) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(SimplyBackpacks.MODID, "filter");
    public FilterPacket(final FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }
    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(opts);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final FilterPacket packet, PlayPayloadContext ctx) {
        ctx.player().ifPresent(p -> {
            if (p.containerMenu instanceof FilterContainer)
                ((FilterContainer) p.containerMenu).saveFilter(packet.opts);
        });
    }
}
