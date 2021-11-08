package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.items.BackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleMessage {
    public static ToggleMessage decode(final PacketBuffer buffer) {
        buffer.readByte();
        return new ToggleMessage();
    }
    public static void encode(final ToggleMessage message, final PacketBuffer buffer) {
        buffer.writeByte(0);
    }
    public static void handle(final ToggleMessage message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(
            ()-> {
                PlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;
                ItemStack backpack = SimplyBackpacks.findBackpackForHotkeys(player);
                BackpackItem.togglePickup(player, backpack);
            }
        );
        ctx.get().setPacketHandled(true);
    }
}
