package com.box3lab.register;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.box3lab.util.BlockIdResolver;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public final class VoxelImport {

    public static void apply(Object voxels, Object world, String mapName, Vec3 customOrigin,
            ServerPlayer player, boolean ignoreBarrier, boolean useVanillaWater) throws Exception {
        JsonObject cfg = loadConfig(mapName);
        if (cfg == null) {
            throw new Exception(Component
                    .translatable("command.box3mod.box3import.config_invalid")
                    .getString());
        }

        int[] shape = toIntArray(cfg.getAsJsonArray("shape"));
        int[] dir = toIntArray(cfg.getAsJsonArray("dir"));
        int[] indices = toIntArray(cfg.getAsJsonArray("indices"));
        int[] data = toIntArray(cfg.getAsJsonArray("data"));

        int total = Math.min(indices.length, data.length);
        int lastProgress = -1;

        int[] origin = new int[] {
                (int) Math.floor(customOrigin.x),
                (int) Math.floor(customOrigin.y),
                (int) Math.floor(customOrigin.z)
        };

        for (int i = 0; i < indices.length && i < data.length; i++) {
            int idx = indices[i];
            int id = data[i];

            // 可选：忽略 barrier（空气墙）方块
            if (ignoreBarrier && BlockIdResolver.isBarrierId(id)) {
                continue;
            }

            int x = idx % shape[0];
            int y = (idx / shape[0]) % shape[1];
            int z = idx / (shape[0] * shape[1]);

            int wx = origin[0] + dir[0] * x;
            int wy = origin[1] + dir[1] * y;
            int wz = origin[2] + dir[2] * z;

            if (world instanceof ServerLevel level) {
                BlockPos pos = new BlockPos(wx, wy, wz);
                Block block = BlockIdResolver.getBlockById(id, useVanillaWater);
                level.setBlock(pos, block.defaultBlockState(), 3);

                if (player != null) {
                    int progress = (i + 1) * 100 / total;
                    if (progress / 10 > lastProgress / 10) {
                        lastProgress = progress;
                        player.sendSystemMessage(Component.translatable(
                                "command.box3mod.box3import.progress", mapName, progress));
                    }
                }
            }
        }
    }

    private static JsonObject loadConfig(String mapName) {
        Path configPath = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("box3mod")
                .resolve(mapName.endsWith(".json") ? mapName : mapName + ".json");

        if (!Files.exists(configPath)) {
            System.err.println(Component
                    .translatable("command.box3mod.box3import.config_missing", configPath.toString())
                    .getString());
            return null;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException | JsonParseException e) {
            System.err.println(Component
                    .translatable("command.box3mod.box3import.config_read_failed", configPath.toString())
                    .getString());
            return null;
        }
    }

    private static int[] toIntArray(JsonArray array) {
        if (array == null) {
            return new int[0];
        }
        int[] result = new int[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i).getAsInt();
        }
        return result;
    }
}