package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.items.ItemBackpackBase;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

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
            BackpackData data = ItemBackpackBase.getData(backpack);
            if (!backpack.isEmpty() && data != null) {
                NetworkHooks.openGui(player, new SimpleNamedContainerProvider( (windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, data.getHandler()), backpack.getDisplayName() ), (buffer) -> buffer.writeInt(ItemBackpackBase.getTier(backpack).slots));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
