package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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
                    if (player.getMainHandItem().getItem() instanceof ItemBackpackBase)
                        ((ItemBackpackBase) player.getMainHandItem().getItem()).togglePickup(player, player.getMainHandItem());
                    else if (player.getOffhandItem().getItem() instanceof  ItemBackpackBase)
                        ((ItemBackpackBase) player.getOffhandItem().getItem()).togglePickup(player, player.getOffhandItem());
                    else {
                        //check hotbar
                        for (int i = 0; i <= 8; i++ ) {
                            ItemStack stack = player.getInventory().getItem(i);
                            if (stack.getItem() instanceof  ItemBackpackBase) {
                                ((ItemBackpackBase) stack.getItem()).togglePickup(player, stack);
                                break;
                            }
                        }
                    }
                }
        );
        ctx.get().setPacketHandled(true);
    }
}
