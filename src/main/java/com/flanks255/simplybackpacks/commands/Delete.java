package com.flanks255.simplybackpacks.commands;

import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public class Delete {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("delete")
            .requires(cs -> cs.hasPermission(1))
            .then(Commands.argument("UUID", StringArgumentType.string()).executes(cs -> delete(cs, StringArgumentType.getString(cs, "UUID"))))
            .then(Commands.argument("CONFIRMATION", StringArgumentType.string()).executes(cs -> delete(cs, StringArgumentType.getString(cs, "CONFIRMATION"))));
    }

    public static int delete(CommandContext<CommandSourceStack> ctx, String stringUUID) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BackpackManager backpacks = BackpackManager.get();

        if (stringUUID.length() == 8){
            BackpackUtils.getConfirmation(stringUUID).ifPresent(confirmation -> {
                if (player.getUUID().equals(confirmation.player())) {
                    backpacks.removeBackpack(confirmation.backpack());
                    BackpackUtils.removeConfirmation(stringUUID);
                    ctx.getSource().sendSuccess(new TranslatableComponent("simplybackpacks.delete.finished", confirmation.backpack()), false);
                }
            });
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(stringUUID);
        }
        catch(IllegalArgumentException e){
            return 0;
        }
            Optional<BackpackData> data = backpacks.getBackpack(uuid);

            data.ifPresentOrElse(backpack -> {
                String code = BackpackUtils.generateCode(player.level.random);
                BackpackUtils.addConfirmation(code, player.getUUID(), backpack.getUuid());
                ctx.getSource().sendSuccess(new TranslatableComponent("simplybackpacks.delete.confirmation", new TextComponent(code).withStyle(ChatFormatting.GOLD)), false);
            }, () -> ctx.getSource().sendFailure(new TranslatableComponent("simplybackpacks.invaliduuid")));

            ;
        return 0;
    }
}
