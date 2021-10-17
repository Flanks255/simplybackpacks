package com.flanks255.simplybackpacks.network;

import com.flanks255.simplybackpacks.SimplyBackpacks;
import com.flanks255.simplybackpacks.gui.SBContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import javax.annotation.Nullable;
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
            if (!SimplyBackpacks.findBackpack(player).isEmpty()) {
                player.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return SimplyBackpacks.findBackpack(player).getHoverName();
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                        return new SBContainer(p_createMenu_1_, p_createMenu_3_.level, p_createMenu_3_.blockPosition(), p_createMenu_2_, p_createMenu_3_);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
