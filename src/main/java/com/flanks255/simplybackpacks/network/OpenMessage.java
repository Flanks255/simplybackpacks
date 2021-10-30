package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.SBContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenMessage {
    public static OpenMessage decode(final PacketBuffer buffer) {
        buffer.readByte();
        return new OpenMessage();
    }
    public static void encode(final OpenMessage message, final PacketBuffer buffer) {
        buffer.writeByte(0);
    }
    public static void handle(final OpenMessage message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()-> {
            ServerPlayerEntity player = ctx.get().getSender();
            ItemStack backpack = SimplyBackpacks.findBackpack(player);
            if (!backpack.isEmpty()) {
                player.openContainer(new SimpleNamedContainerProvider( (windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, null), backpack.getDisplayName()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
