package com.flanks255.simplybackpacks.commands;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SBCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var cmds = dispatcher.register(
            Commands.literal("simplybackpacks")
                .then(List.register())
                .then(Recover.register())
                .then(Open.register())
                .then(Delete.register())
        );

        dispatcher.register(Commands.literal("sb").redirect(cmds));
    }
}
