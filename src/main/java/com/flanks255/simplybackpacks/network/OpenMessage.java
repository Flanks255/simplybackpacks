package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Optional;
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
            ItemStack backpack = BackpackUtils.findBackpackForHotkeys(player);
            if (backpack.getOrCreateTag().contains("UUID")) {
                Optional<BackpackData> data = BackpackManager.get().getBackpack(backpack.getTag().getUUID("UUID"));
                if (!backpack.isEmpty() && data.isPresent()) {
                    data.get().updateAccessRecords(player.getName().getString(), System.currentTimeMillis());
                    NetworkHooks.openGui(player, new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, data.get().getUuid(), data.get().getTier(), data.get().getHandler()), backpack.getHoverName()), (buffer) -> buffer.writeUUID(data.get().getUuid()).writeInt(BackpackItem.getTier(backpack).ordinal()));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
