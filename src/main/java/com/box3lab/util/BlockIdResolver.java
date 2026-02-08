package com.box3lab.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.box3lab.Box3Mod;
import com.box3lab.register.ModBlocks;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class BlockIdResolver {

    private static JsonObject blockIdMapping = null;

    private BlockIdResolver() {
    }

    private static void loadBlockIdMapping() {
        if (blockIdMapping != null) {
            return;
        }

        try (InputStream is = BlockIdResolver.class.getClassLoader().getResourceAsStream("block-id.json")) {
            if (is == null) {
                throw new RuntimeException(Component
                        .translatable("command.box3mod.block_id.missing_file")
                        .getString());
            }
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                blockIdMapping = JsonParser.parseReader(reader).getAsJsonObject();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    Component.translatable("command.box3mod.block_id.read_failed").getString(),
                    e);
        }
    }

    public static Block getBlockById(int id) {
        return getBlockById(id, false);
    }

    public static Block getBlockById(int id, boolean useVanillaWater) {
        loadBlockIdMapping();

        String idStr = String.valueOf(id);
        if (!blockIdMapping.has(idStr)) {
            System.err.println(Component
                    .translatable("command.box3mod.block_id.no_mapping_for_id", idStr)
                    .getString());
            return Blocks.STONE;
        }

        String registryKey = blockIdMapping.get(idStr).getAsString();
        String normalizedKey = registryKey.toLowerCase(Locale.ROOT);

        if (normalizedKey.startsWith("spec_")) {
            if (useVanillaWater) {
                return Blocks.WATER;
            }
            Identifier idKey = Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, normalizedKey);
            Block fluidBlock = BuiltInRegistries.BLOCK.get(idKey)
                    .map(Holder::value)
                    .orElse(Blocks.WATER);
            if (fluidBlock != null && fluidBlock != Blocks.AIR) {
                return fluidBlock;
            }
            System.err.println(Component
                    .translatable("command.box3mod.block_id.missing_fluid_block", normalizedKey)
                    .getString());
            return Blocks.WATER;
        }

        Block block = ModBlocks.VOXEL_BLOCKS.get(normalizedKey);
        if (block == null) {
            System.err.println(Component
                    .translatable("command.box3mod.block_id.missing_registered_block", registryKey)
                    .getString());
            return Blocks.STONE;
        }

        return block;
    }

    public static boolean isBarrierId(int id) {
        loadBlockIdMapping();

        String idStr = String.valueOf(id);
        if (!blockIdMapping.has(idStr)) {
            return false;
        }
        String registryKey = blockIdMapping.get(idStr).getAsString();
        return "voxel_barrier".equalsIgnoreCase(registryKey);
    }
}
