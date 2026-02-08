package com.box3lab.command;

import com.box3lab.util.VoxelImport;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class ModCommands {
    private ModCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("box3import")
                            .executes(context -> showHelp(context.getSource()))
                            .then(argument("file", StringArgumentType.word())
                                    .executes(context -> executeBox3Import(
                                            context.getSource(),
                                            StringArgumentType.getString(context, "file")))));
        });
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(
                () -> Component.translatable("command.box3mod.box3import.usage"),
                false);
        return 1;
    }

    private static int executeBox3Import(CommandSourceStack source, String fileName) {
        ServerLevel level = source.getServer().overworld();
        try {
            VoxelImport.apply(null, level, fileName, source.getPlayer().position());

            source.sendSuccess(
                    () -> Component.translatable("command.box3mod.box3import.success", fileName + ".json"),
                    false);
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.box3mod.box3import.failure", e.getMessage()));
        }
        return 1;
    }
}
