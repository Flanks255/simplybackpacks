package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.inventory.FilterItemHandler;
import com.flanks255.simplybackpacks.items.Backpack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class FilterSyncMessage {
    FilterItemHandler handler;
    UUID uuid;

    public FilterSyncMessage(UUID uuid, FilterItemHandler filterItemHandler) { this.uuid = uuid; this.handler = filterItemHandler; }

    public static FilterSyncMessage decode(final PacketBuffer buffer) {
        FilterItemHandler handler = new FilterItemHandler();
        UUID uuid = buffer.readUniqueId();
        int slotCount = buffer.readInt();
        for (int i = 0; i < slotCount; i++) {
            handler.setStackInSlot(i, buffer.readItemStack());
        }

        return new FilterSyncMessage(uuid, handler);
    }
    public static void encode(final FilterSyncMessage message, final PacketBuffer buffer) {
        buffer.writeUniqueId(message.uuid);
        buffer.writeInt(message.handler.getSlots());
        for (int i = 0; i < message.handler.getSlots(); i++) {
            buffer.writeItemStack(message.handler.getStackInSlot(i));
        }

    }
    public static void consume(final FilterSyncMessage message, final Supplier<NetworkEvent .Context> ctx) {
        ctx.get().enqueueWork(()-> {
            BackpackManager.blankClient.getOrCreateBackpack(message.uuid, Backpack.COMMON).getFilter().deserializeNBT(message.handler.serializeNBT());
        });
        ctx.get().setPacketHandled(true);
    }
}
