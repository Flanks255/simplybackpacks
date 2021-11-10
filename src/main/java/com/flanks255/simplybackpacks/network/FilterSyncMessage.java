package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.inventory.FilterItemHandler;
import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class FilterSyncMessage {
    final FilterItemHandler handler;
    final UUID uuid;

    public FilterSyncMessage(UUID uuid, FilterItemHandler filterItemHandler) { this.uuid = uuid; this.handler = filterItemHandler; }

    public static FilterSyncMessage decode(final PacketBuffer buffer) {
        FilterItemHandler handler = new FilterItemHandler();
        UUID uuid = buffer.readUUID();
        int slotCount = buffer.readInt();
        for (int i = 0; i < slotCount; i++) {
            handler.setStackInSlot(i, buffer.readItem());
        }

        return new FilterSyncMessage(uuid, handler);
    }
    public static void encode(final FilterSyncMessage message, final PacketBuffer buffer) {
        buffer.writeUUID(message.uuid);
        buffer.writeInt(message.handler.getSlots());
        for (int i = 0; i < message.handler.getSlots(); i++) {
            buffer.writeItem(message.handler.getStackInSlot(i));
        }

    }
    public static void consume(final FilterSyncMessage message, final Supplier<NetworkEvent .Context> ctx) {
        ctx.get().enqueueWork(()-> BackpackManager.blankClient.getOrCreateBackpack(message.uuid, Backpack.COMMON).getFilter().deserializeNBT(message.handler.serializeNBT()));
        ctx.get().setPacketHandled(true);
    }
}
