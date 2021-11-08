package com.flanks255.simplybackpacks.network;

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
                    if (player.getHeldItemMainhand().getItem() instanceof BackpackItem)
                        ((BackpackItem) player.getHeldItemMainhand().getItem()).togglePickup(player, player.getHeldItemMainhand());
                    else if (player.getHeldItemOffhand().getItem() instanceof BackpackItem)
                        ((BackpackItem) player.getHeldItemOffhand().getItem()).togglePickup(player, player.getHeldItemOffhand());
                    else {
                        //check hotbar
                        for (int i = 0; i <= 8; i++ ) {
                            ItemStack stack = player.inventory.getStackInSlot(i);
                            if (stack.getItem() instanceof BackpackItem) {
                                ((BackpackItem) stack.getItem()).togglePickup(player, stack);
                                break;
                            }
                        }
                    }
                }
        );
        ctx.get().setPacketHandled(true);
    }
}
