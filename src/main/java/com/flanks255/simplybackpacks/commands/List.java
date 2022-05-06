package com.flanks255.simplybackpacks.commands;

import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class List {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("list").requires(cs -> cs.hasPermission(1)).executes(List::list)
            .then(Commands.literal("firstOpenedBy").then(Commands.argument("PlayerName", StringArgumentType.string())
                .suggests((cs, builder) -> ISuggestionProvider.suggest(getPlayerSuggestions(cs), builder))
                .executes(cs -> first(cs, StringArgumentType.getString(cs, "PlayerName")))))
            .then(Commands.literal("lastOpenedBy").then(Commands.argument("PlayerName", StringArgumentType.string())
                .suggests((cs, builder) -> ISuggestionProvider.suggest(getPlayerSuggestions(cs), builder))
                .executes(cs -> last(cs, StringArgumentType.getString(cs, "PlayerName")))));
    }

    public static Set<String> getPlayerSuggestions(CommandContext<CommandSource> commandSource) {
        Set<String> list = new HashSet<>();
        commandSource.getSource().getServer().getPlayerList().getPlayers().forEach( serverPlayerEntity -> list.add(serverPlayerEntity.getName().getString()));

        return list;
    }

    public static void sendBackpack(PlayerEntity player, BackpackData backpack) {
        player.sendMessage(new StringTextComponent("===========================").withStyle(TextFormatting.DARK_GRAY), Util.NIL_UUID);
        player.sendMessage(new StringTextComponent(
            backpack.getUuid().toString().substring(0,8) + "...\nFirst: " + backpack.meta.getFirstAccessedPlayer() + "\n" + SDF.format(new Date(backpack.meta.getFirstAccessedTime())) +
                "\nLast: " + backpack.meta.getLastAccessedPlayer() + "\n" + SDF.format(new Date(backpack.meta.getLastAccessedTime()))
        ), Util.NIL_UUID);


        TextComponent open_link = new StringTextComponent("Open");
        open_link.withStyle(style -> style
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sb open " + backpack.getUuid().toString()))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Open Backpack")))
            .withColor(TextFormatting.BLUE)
            .withUnderlined(true));
        TextComponent recover_link = new StringTextComponent("Recover");
        recover_link.withStyle(style -> style
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sb recover " + backpack.getUuid().toString()))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Recover Backpack")))
            .withColor(TextFormatting.GREEN)
            .withUnderlined(true));
        TextComponent delete_link = new StringTextComponent("Delete");
        delete_link.withStyle(style -> style
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sb delete " + backpack.getUuid().toString()))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Delete Backpack")))
            .withColor(TextFormatting.RED)
            .withUnderlined(true));

        player.sendMessage(new StringTextComponent("[").append(open_link).append("] - [").append(recover_link).append("] - [").append(delete_link).append("]"), Util.NIL_UUID);
    }

    public static int list(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        BackpackManager backpacks = BackpackManager.get();
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();

        if (backpacks.getMap().size() == 0) {
            ctx.getSource().sendSuccess(new StringTextComponent("[ ]"), false);
            return 0;
        }

        backpacks.getMap().forEach( (uuid, backpack) -> sendBackpack(player, backpack));

        return 0;
    }

    public static int first(CommandContext<CommandSource> ctx, String playerName) throws CommandSyntaxException {
        BackpackManager backpacks = BackpackManager.get();
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();

        if (backpacks.getMap().size() == 0) {
            ctx.getSource().sendSuccess(new StringTextComponent("[ ]"), false);
            return 0;
        }

        backpacks.getMap().forEach( (uuid, backpack) -> sendBackpack(player, backpack));

        return 0;
    }

    public static int last(CommandContext<CommandSource> ctx, String playerName) throws CommandSyntaxException {
        BackpackManager backpacks = BackpackManager.get();
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();

        if (backpacks.getMap().size() == 0) {
            ctx.getSource().sendSuccess(new StringTextComponent("[ ]"), false);
            return 0;
        }

        backpacks.getMap().forEach( (uuid, backpack) -> sendBackpack(player, backpack));

        return 0;
    }
}
