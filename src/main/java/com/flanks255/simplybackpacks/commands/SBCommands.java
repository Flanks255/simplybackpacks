package com.flanks255.simplybackpacks.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class SBCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            Commands.literal("simplybackpacks")
                .then(List.register())
                .then(Recover.register())
                .then(Open.register())
        );

        dispatcher.register(
            Commands.literal("sb")
                .then(List.register())
                .then(Recover.register())
                .then(Open.register())
        );
    }
}
