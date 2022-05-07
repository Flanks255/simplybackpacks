package com.flanks255.simplybackpacks.commands;

import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.UUID;

public class Delete {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("delete")
            .requires(cs -> cs.hasPermission(1))
            .then(Commands.argument("UUID", StringArgumentType.string()).executes(cs -> delete(cs, StringArgumentType.getString(cs, "UUID"))))
            .then(Commands.argument("CONFIRMATION", StringArgumentType.string()).executes(cs -> delete(cs, StringArgumentType.getString(cs, "CONFIRMATION"))));
    }

    public static int delete(CommandContext<CommandSource> ctx, String stringUUID) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        BackpackManager backpacks = BackpackManager.get();

        if (stringUUID.length() == 8){
            BackpackUtils.getConfirmation(stringUUID).ifPresent(confirmation -> {
                if (player.getUUID().equals(confirmation.getPlayer())) {
                    backpacks.removeBackpack(confirmation.getBackpack());
                    BackpackUtils.removeConfirmation(stringUUID);
                    ctx.getSource().sendSuccess(new TranslationTextComponent("simplybackpacks.delete.finished", confirmation.getBackpack()), false);
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

        data.ifPresent(backpack -> {
            String code = BackpackUtils.generateCode(player.level.random);
            BackpackUtils.addConfirmation(code, player.getUUID(), backpack.getUuid());
            ctx.getSource().sendSuccess(new TranslationTextComponent("simplybackpacks.delete.confirmation", new StringTextComponent(code).withStyle(TextFormatting.GOLD)), false);
        });

        if (!data.isPresent()) {
            ctx.getSource().sendFailure(new TranslationTextComponent("simplybackpacks.invaliduuid"));
        }
        return 0;
    }
}
