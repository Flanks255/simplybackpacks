package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.items.BackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
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
            if (player == null) {
                return;
            }

            ItemStack backpack = BackpackItem.findBackpack(player);
            if (!backpack.isEmpty()) {
                player.openContainer(new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) ->
                        new SBContainer(windowId, playerInventory, null), backpack.getDisplayName()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
