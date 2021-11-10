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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Recover {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("recover")
            .requires(cs -> cs.hasPermission(1))
            .then(Commands.argument("UUID", StringArgumentType.string()).suggests(((context, builder) -> ISuggestionProvider.suggest(getUUIDSuggestions(context), builder))).executes(cs -> recover(cs, StringArgumentType.getString(cs, "UUID"))));
    }
    public static Set<String> getUUIDSuggestions(CommandContext<CommandSource> commandSource) {
        BackpackManager backpacks = BackpackManager.get();
        Set<String> list = new HashSet<>();

        backpacks.getMap().forEach((uuid, backpackData) -> list.add(uuid.toString()));

        return list;
    }

    public static int recover(CommandContext<CommandSource> ctx, String stringUUID) throws CommandSyntaxException {
        UUID uuid;
        try {
            uuid = UUID.fromString(stringUUID);
        }
        catch(IllegalArgumentException e){
            return 0;
        }
        BackpackManager backpacks = BackpackManager.get();

        if (backpacks.getMap().containsKey(uuid)) {
            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();

            Optional<BackpackData> data = backpacks.getBackpack(uuid);

            data.ifPresent(backpack -> {
                ItemStack stack = new ItemStack(backpack.getTier().item.get());
                stack.getOrCreateTag().putUUID("UUID", backpack.getUuid());

                ItemHandlerHelper.giveItemToPlayer(player, stack);
            });
        }
        return 0;
    }
}
