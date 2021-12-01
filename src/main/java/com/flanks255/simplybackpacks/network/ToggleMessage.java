package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleMessage {
    public static ToggleMessage decode(final FriendlyByteBuf buffer) {
        buffer.readByte();
        return new ToggleMessage();
    }
    public static void encode(final ToggleMessage message, final FriendlyByteBuf buffer) {
        buffer.writeByte(0);
    }
    public static void handle(final ToggleMessage message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(
            ()-> {
                Player player = ctx.get().getSender();
                if (player == null)
                    return;
                ItemStack backpack = BackpackUtils.findBackpackForHotkeys(player);
                BackpackItem.togglePickup(player, backpack);
            }
        );
        ctx.get().setPacketHandled(true);
    }
}
