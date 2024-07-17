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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record HotkeyPacket(HotKey hotKey) implements CustomPacketPayload {
    public static final Type<HotkeyPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SimplyBackpacks.MODID, "hotkey"));

    public HotkeyPacket(byte type) {
        this(HotKey.values()[type]);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, HotkeyPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, packet -> ((byte) packet.hotKey.ordinal()),
            HotkeyPacket::new
    );

    public enum HotKey {
        OPEN,
        TOGGLE
    }

    public HotkeyPacket(final FriendlyByteBuf buffer) {
        this(HotKey.values()[buffer.readByte()]);
    }

    public static void handle(final HotkeyPacket packet, IPayloadContext ctx) {
        switch (packet.hotKey) {
            case OPEN -> ctx.enqueueWork(() -> open(ctx));
            case TOGGLE -> ctx.enqueueWork(() -> toggle(ctx));
        }
    }
    private static void open(IPayloadContext ctx) {
        Player player = ctx.player();
        ItemStack backpack = BackpackUtils.findBackpackForHotkeys(player, false);
        if (backpack.has(SimplyBackpacks.BACKPACK_UUID)) {
            Optional<BackpackData> data = BackpackManager.get().getBackpack(backpack.get(SimplyBackpacks.BACKPACK_UUID));
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
    private static void toggle(IPayloadContext ctx) {
        ItemStack backpack = BackpackUtils.findBackpackForHotkeys(ctx.player(), true);
        BackpackItem.togglePickup(ctx.player(), backpack);
    }
}
