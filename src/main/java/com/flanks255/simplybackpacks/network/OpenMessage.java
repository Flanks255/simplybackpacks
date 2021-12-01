package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.function.Supplier;

public class OpenMessage {
    public static OpenMessage decode(final FriendlyByteBuf buffer) {
        buffer.readByte();
        return new OpenMessage();
    }
    public static void encode(final OpenMessage message, final FriendlyByteBuf buffer) {
        buffer.writeByte(0);
    }
    public static void handle(final OpenMessage message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()-> {
            ServerPlayer player = ctx.get().getSender();
            ItemStack backpack = BackpackUtils.findBackpackForHotkeys(player);
            if (backpack.getOrCreateTag().contains("UUID")) {
                Optional<BackpackData> data = BackpackManager.get().getBackpack(backpack.getTag().getUUID("UUID"));
                if (!backpack.isEmpty() && data.isPresent()) {
                    data.get().updateAccessRecords(player.getName().getString(), System.currentTimeMillis());
                    NetworkHooks.openGui(player, new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, data.get().getUuid(), data.get().getTier(), data.get().getHandler()), backpack.getHoverName()), (buffer) -> buffer.writeUUID(data.get().getUuid()).writeInt(BackpackItem.getTier(backpack).ordinal()));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
