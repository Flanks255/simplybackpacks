package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.items.Backpack;
import com.flanks255.simplybackpacks.items.BackpackItem;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public record HotkeyPacket(HotKey hotKey) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(SimplyBackpacks.MODID, "hotkey");

    public enum HotKey {
        OPEN,
        TOGGLE
    }

    public HotkeyPacket(final FriendlyByteBuf buffer) {
        this(HotKey.values()[buffer.readByte()]);
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeByte(hotKey.ordinal());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(final HotkeyPacket packet, PlayPayloadContext ctx) {
        switch (packet.hotKey) {
            case OPEN -> ctx.workHandler().submitAsync(() -> open(ctx));
            case TOGGLE -> ctx.workHandler().submitAsync(() -> toggle(ctx));
        }
    }
    private static void open(PlayPayloadContext ctx) {
        if (ctx.player().isEmpty())
            return;

        Player player = ctx.player().get();
        ItemStack backpack = BackpackUtils.findBackpackForHotkeys(player, false);
        if (backpack.getOrCreateTag().contains("UUID")) {
            Optional<BackpackData> data = BackpackManager.get().getBackpack(backpack.getTag().getUUID("UUID"));
            if (!backpack.isEmpty() && data.isPresent()) {
                Backpack itemTier = BackpackItem.getTier(backpack);
                if (data.get().getTier().ordinal() < itemTier.ordinal()) {
                    data.get().upgrade(itemTier);
                    player.sendSystemMessage(Component.literal("Backpack upgraded to " + itemTier.name));
                }
                data.get().updateAccessRecords(player.getName().getString(), System.currentTimeMillis());
                player.openMenu(new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, data.get().getUuid(), data.get().getTier(), data.get().getHandler()), backpack.getHoverName()), (buffer) -> buffer.writeUUID(data.get().getUuid()).writeInt(itemTier.ordinal()));
            }
        }
    }
    private static void toggle(PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> {
            ItemStack backpack = BackpackUtils.findBackpackForHotkeys(player, true);
            BackpackItem.togglePickup(player, backpack);
        });
    }
}
