package com.box3lab.register;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.box3lab.Box3Mod;
import com.box3lab.fluid.VoxelSpecFluid;
import com.box3lab.util.BlockIndexData;
import com.box3lab.util.BlockIndexUtil;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

import com.box3lab.register.creative.CreativeTabExtras;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;

public final class ModFluids {
    private ModFluids() {
    }

    public static final class FluidRenderInfo {
        public final FlowingFluid still;
        public final FlowingFluid flowing;
        public final int tint;
        public final double fluidExtinction;

        public FluidRenderInfo(FlowingFluid still, FlowingFluid flowing, int tint, double fluidExtinction) {
            this.still = still;
            this.flowing = flowing;
            this.tint = tint;
            this.fluidExtinction = fluidExtinction;
        }
    }

    public static final Map<String, FluidRenderInfo> SPEC_FLUIDS = new HashMap<>();

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, name));
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, name));
    }

    private static String sanitizeCategoryPath(String category) {
        if (category == null || category.isBlank()) {
            return "food";
        }
        String lower = category.toLowerCase(Locale.ROOT);
        String cleaned = lower.replaceAll("[^a-z0-9_\\-]+", "_");
        return cleaned.isBlank() ? "food" : cleaned;
    }

    private static int tintFromPackedRgba(long packedRgba) {
        int r = (int) (packedRgba & 255L);
        int g = (int) ((packedRgba & 65280L) >> 8);
        int b = (int) ((packedRgba & 16711680L) >>> 16);
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public static void initialize() {
        BlockIndexData data = BlockIndexData.get();

        for (var entry : data.fluidsById.entrySet()) {
            int id = entry.getKey();
            String name = BlockIndexUtil.getVoxelNameLowerCaseById(id);
            if (name == null || name.isBlank()) {
                continue;
            }

            if ("air".equals(name)) {
                continue;
            }

            String category = data.categoryByName.getOrDefault(name, "");
            String categoryPath = sanitizeCategoryPath(category);

            String base = "spec_" + name;
            Identifier stillId = Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, base);
            Identifier flowingId = Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, "flowing_" + base);

            ResourceKey<Block> fluidBlockKey = keyOfBlock(base + "_block");
            ResourceKey<Item> bucketKey = keyOfItem(base + "_bucket");

            final FlowingFluid[] stillRef = new FlowingFluid[1];
            final FlowingFluid[] flowingRef = new FlowingFluid[1];
            final Block[] blockRef = new Block[1];
            final Item[] bucketRef = new Item[1];

            stillRef[0] = (FlowingFluid) Registry.register(
                    BuiltInRegistries.FLUID,
                    stillId,
                    new VoxelSpecFluid.Still(() -> stillRef[0], () -> flowingRef[0], () -> bucketRef[0],
                            () -> blockRef[0]));

            flowingRef[0] = (FlowingFluid) Registry.register(
                    BuiltInRegistries.FLUID,
                    flowingId,
                    new VoxelSpecFluid.Flowing(() -> stillRef[0], () -> flowingRef[0], () -> bucketRef[0],
                            () -> blockRef[0]));

            blockRef[0] = Registry.register(
                    BuiltInRegistries.BLOCK,
                    fluidBlockKey,
                    new LiquidBlock(stillRef[0],
                            BlockBehaviour.Properties.of().noCollision().strength(100.0F).setId(fluidBlockKey) .mapColor(MapColor.COLOR_BLUE)));

            bucketRef[0] = Registry.register(
                    BuiltInRegistries.ITEM,
                    bucketKey,
                    new BucketItem(stillRef[0],
                            new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).setId(bucketKey)));

            CreativeTabExtras.add(categoryPath, bucketRef[0]);

            int tint = tintFromPackedRgba(entry.getValue().info);
            SPEC_FLUIDS.put(name,
                    new FluidRenderInfo(stillRef[0], flowingRef[0], tint, entry.getValue().fluidExtinction));
        }
    }
}
