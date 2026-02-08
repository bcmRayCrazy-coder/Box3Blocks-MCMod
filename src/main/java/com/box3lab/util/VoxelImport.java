package com.box3lab.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import com.box3lab.Box3Mod;
import com.box3lab.register.ModBlocks;

public final class VoxelImport {

    private static JsonObject blockIdMapping = null;

    private static void loadBlockIdMapping() {
        if (blockIdMapping != null) {
            return;
        }

        try (InputStream is = VoxelImport.class.getClassLoader().getResourceAsStream("block-id.json")) {
            if (is == null) {
                throw new RuntimeException("无法找到 block-id.json 文件");
            }
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                blockIdMapping = JsonParser.parseReader(reader).getAsJsonObject();
            }
        } catch (Exception e) {
            throw new RuntimeException("读取 block-id.json 失败", e);
        }
    }

    private static Block getBlockById(int id) {
        loadBlockIdMapping();

        String idStr = String.valueOf(id);
        if (!blockIdMapping.has(idStr)) {
            System.err.println("[Box3Mod] 未找到 ID " + id + " 对应的方块注册 key");
            return Blocks.STONE;
        }

        String registryKey = blockIdMapping.get(idStr).getAsString();
        String normalizedKey = registryKey.toLowerCase(Locale.ROOT);

        if (normalizedKey.startsWith("spec_")) {
            Identifier idKey = Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, normalizedKey);
            Block fluidBlock = BuiltInRegistries.BLOCK.get(idKey)
                    .map(Holder::value)
                    .orElse(Blocks.WATER);
            if (fluidBlock != null && fluidBlock != Blocks.AIR) {
                return fluidBlock;
            }
            System.err.println("[Box3Mod] 未找到流体方块: " + normalizedKey + "，使用 Blocks.WATER 代替");
            return Blocks.WATER;
        }

        Block block = ModBlocks.VOXEL_BLOCKS.get(normalizedKey);
        if (block == null) {
            System.err.println("[Box3Mod] 未找到注册的方块: " + registryKey);
            return Blocks.STONE;
        }

        return block;
    }

    public static void apply(Object voxels, Object world, String mapName, Vec3 customOrigin) throws Exception {
        JsonObject cfg = loadConfig(mapName);
        if (cfg == null) {
            throw new Exception("配置文件读取失败，请检查文件是否存在且格式正确");
        }

        int[] shape = toIntArray(cfg.getAsJsonArray("shape"));
        int[] dir = toIntArray(cfg.getAsJsonArray("dir"));
        int[] indices = toIntArray(cfg.getAsJsonArray("indices"));
        int[] data = toIntArray(cfg.getAsJsonArray("data"));

        int[] origin = new int[] {
                (int) Math.floor(customOrigin.x),
                (int) Math.floor(customOrigin.y),
                (int) Math.floor(customOrigin.z)
        };

        for (int i = 0; i < indices.length && i < data.length; i++) {
            int idx = indices[i];
            int id = data[i];

            int x = idx % shape[0];
            int y = (idx / shape[0]) % shape[1];
            int z = idx / (shape[0] * shape[1]);

            int wx = origin[0] + dir[0] * x;
            int wy = origin[1] + dir[1] * y;
            int wz = origin[2] + dir[2] * z;

            if (world instanceof ServerLevel level) {
                BlockPos pos = new BlockPos(wx, wy, wz);
                // 根据 ID 获取对应的方块
                Block block = getBlockById(id);
                level.setBlock(pos, block.defaultBlockState(), 3);
            }
        }
    }

    private static JsonObject loadConfig(String mapName) {
        Path configPath = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("box3mod")
                .resolve(mapName.endsWith(".json") ? mapName : mapName + ".json");

        if (!Files.exists(configPath)) {
            System.err.println("[Box3Mod] 配置文件不存在: " + configPath);
            return null;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException | JsonParseException e) {
            System.err.println("[Box3Mod] 读取配置失败: " + configPath);
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