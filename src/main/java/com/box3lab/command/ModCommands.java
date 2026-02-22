package com.box3lab.command;

import java.io.IOException;
import java.util.List;

import com.box3lab.block.BarrierVoxelBlock;
import com.box3lab.register.VoxelImport;
import com.box3lab.util.Box3ImportFiles;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class ModCommands {
        private ModCommands() {
        }

        private static final SuggestionProvider<CommandSourceStack> BOX3_FILE_SUGGESTIONS = (context, builder) -> {
                try {
                        List<String> files = Box3ImportFiles.listJsonFiles();
                        for (String file : files) {
                                String name = file;
                                if (name.endsWith(".gz")) {
                                        name = name.substring(0, name.length() - 3);
                                }
                                builder.suggest(name);
                        }
                } catch (IOException ignored) {

                }
                return builder.buildFuture();
        };

        public static void register() {
                CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                        dispatcher.register(
                                        literal("box3import")
                                                        .executes(context -> listBox3ImportFiles(context.getSource()))
                                                        .then(argument("fileName", StringArgumentType.word())
                                                                        .suggests(BOX3_FILE_SUGGESTIONS)
                                                                        .executes(context -> executeBox3Import(
                                                                                        context.getSource(),
                                                                                        StringArgumentType.getString(
                                                                                                        context,
                                                                                                        "fileName"),
                                                                                        false,
                                                                                        false))
                                                                        .then(argument("ignoreBarrier",
                                                                                        BoolArgumentType.bool())
                                                                                        .executes(context -> executeBox3Import(
                                                                                                        context.getSource(),
                                                                                                        StringArgumentType
                                                                                                                        .getString(context,
                                                                                                                                        "fileName"),
                                                                                                        BoolArgumentType.getBool(
                                                                                                                        context,
                                                                                                                        "ignoreBarrier"),
                                                                                                        false))
                                                                                        .then(argument("ignoreWater",
                                                                                                        BoolArgumentType.bool())
                                                                                                        .executes(context -> executeBox3Import(
                                                                                                                        context.getSource(),
                                                                                                                        StringArgumentType
                                                                                                                                        .getString(context,
                                                                                                                                                        "fileName"),
                                                                                                                        BoolArgumentType.getBool(
                                                                                                                                        context,
                                                                                                                                        "ignoreBarrier"),
                                                                                                                        BoolArgumentType.getBool(
                                                                                                                                        context,
                                                                                                                                        "ignoreWater")))))));

                        dispatcher.register(
                                        literal("box3barrier")
                                                        .executes(context -> showBarrierStatus(context.getSource()))
                                                        .then(argument("value", BoolArgumentType.bool())
                                                                        .executes(context -> setBarrierVisible(
                                                                                        context.getSource(),
                                                                                        BoolArgumentType.getBool(
                                                                                                        context,
                                                                                                        "value")))
                                                                        .then(literal("toggle")
                                                                                        .executes(context -> toggleBarrierVisible(
                                                                                                        context.getSource())))));
                });
        }

        private static int listBox3ImportFiles(CommandSourceStack source) {
                var dir = Box3ImportFiles.getImportDir();

                try {
                        List<String> files = Box3ImportFiles.listJsonFiles();

                        if (files.isEmpty()) {
                                source.sendSuccess(
                                                () -> Component.translatable(
                                                                "command.box3mod.box3import.list.empty",
                                                                dir.toString()),
                                                false);
                        } else {
                                String joined = String.join(", ", files);
                                source.sendSuccess(
                                                () -> Component.translatable(
                                                                "command.box3mod.box3import.list.success",
                                                                dir.toString(), joined),
                                                false);
                        }
                } catch (IOException e) {
                        source.sendFailure(
                                        Component.translatable(
                                                        "command.box3mod.box3import.list.error",
                                                        dir.toString(), e.getMessage()));
                }

                return 1;
        }

        private static String resolveMapName(String fileName) {
                if (fileName != null && fileName.startsWith("Box3Build-")) {

                        String suffix = fileName.substring("Box3Build-".length());
                        if (!suffix.isEmpty()) {
                                boolean allDigits = true;
                                for (int i = 0; i < suffix.length(); i++) {
                                        if (!Character.isDigit(suffix.charAt(i))) {
                                                allDigits = false;
                                                break;
                                        }
                                }
                                if (allDigits) {
                                        return "https://static.pgaot.com/MC/Build/" + suffix + ".gz";
                                }
                        }
                }
                return fileName;
        }

        private static int executeBox3Import(CommandSourceStack source, String fileName,
                        boolean ignoreBarrier, boolean useVanillaWater) {
                ServerLevel level = source.getServer().overworld();
                try {
                        ServerPlayer player = source.getPlayer();
                        String mapName = resolveMapName(fileName);
                        VoxelImport.apply(null, level, mapName,
                                        player != null ? player.position() : new BlockPos(0, 0, 0).getCenter(),
                                        player,
                                        ignoreBarrier,
                                        useVanillaWater);

                        source.sendSuccess(
                                        () -> Component.translatable("command.box3mod.box3import.success",
                                                        mapName),
                                        false);
                } catch (Exception e) {
                        source.sendFailure(
                                        Component.translatable("command.box3mod.box3import.failure", e.getMessage()));
                }
                return 1;
        }

        private static int showBarrierStatus(CommandSourceStack source) {
                boolean visible = BarrierVoxelBlock.isVisible();
                source.sendSuccess(
                                () -> Component.translatable("command.box3mod.box3barrier.status",
                                                String.valueOf(visible)),
                                false);
                return 1;
        }

        private static int setBarrierVisible(CommandSourceStack source, boolean value) {
                BarrierVoxelBlock.setVisible(value);
                source.sendSuccess(
                                () -> Component.translatable("command.box3mod.box3barrier.set", String.valueOf(value)),
                                false);
                return 1;
        }

        private static int toggleBarrierVisible(CommandSourceStack source) {
                boolean current = BarrierVoxelBlock.isVisible();
                boolean next = !current;
                BarrierVoxelBlock.setVisible(next);
                source.sendSuccess(
                                () -> Component.translatable("command.box3mod.box3barrier.toggled",
                                                String.valueOf(next)),
                                false);
                return 1;
        }
}
