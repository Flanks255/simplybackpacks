package com.flanks255.simplybackpacks.commands;

import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class Open {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("open")
            .requires(cs -> cs.hasPermission(1))
            .then(Commands.argument("UUID", StringArgumentType.string()).suggests(((context, builder) -> SharedSuggestionProvider.suggest(BackpackUtils.getUUIDSuggestions(context), builder))).executes(cs -> open(cs, StringArgumentType.getString(cs, "UUID"))));
    }

    public static int open(CommandContext<CommandSourceStack> ctx, String stringUUID) throws CommandSyntaxException {
        UUID uuid;
        try {
            uuid = UUID.fromString(stringUUID);
        }
        catch(IllegalArgumentException e){
            return 0;
        }
        BackpackManager backpacks = BackpackManager.get();

        if (backpacks.getMap().containsKey(uuid)) {
            ServerPlayer player = ctx.getSource().getPlayerOrException();

            Optional<BackpackData> data = backpacks.getBackpack(uuid);

            data.ifPresent(backpack -> {
                NetworkHooks.openScreen(player, new SimpleMenuProvider( (windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, uuid, backpack.getTier(), backpack.getHandler()), Component.literal(backpack.getTier().name)), (buffer -> buffer.writeUUID(uuid).writeInt(backpack.getTier().ordinal())));
            });
        } else
            ctx.getSource().sendFailure(Component.translatable("simplyebackpacks.invaliduuid"));
        return 0;
    }
}
