package com.flanks255.simplybackpacks.commands;

import com.flanks255.simplybackpacks.gui.SBContainer;
import com.flanks255.simplybackpacks.inventory.BackpackData;
import com.flanks255.simplybackpacks.inventory.BackpackManager;
import com.flanks255.simplybackpacks.util.BackpackUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class Open {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("open")
            .requires(cs -> cs.hasPermission(1))
            .then(Commands.argument("UUID", StringArgumentType.string()).suggests(((context, builder) -> ISuggestionProvider.suggest(BackpackUtils.getUUIDSuggestions(context), builder))).executes(cs -> open(cs, StringArgumentType.getString(cs, "UUID"))));
    }

    public static int open(CommandContext<CommandSource> ctx, String stringUUID) throws CommandSyntaxException {
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
                NetworkHooks.openGui(player, new SimpleNamedContainerProvider( (windowId, playerInventory, playerEntity) -> new SBContainer(windowId, playerInventory, uuid, backpack.getTier(), backpack.getHandler()), new StringTextComponent(backpack.getTier().name)), (buffer -> buffer.writeUUID(uuid).writeInt(backpack.getTier().ordinal())));
            });
        } else
            ctx.getSource().sendFailure(new TranslationTextComponent("simplybackpacks.invaliduuid"));
        return 0;
    }
}
