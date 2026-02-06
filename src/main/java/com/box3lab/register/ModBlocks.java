package com.box3lab.register;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.box3lab.Box3Mod;
import com.box3lab.block.BouncePadBlock;
import com.box3lab.block.ConveyorBlock;
import com.box3lab.block.VoxelBlock;
import com.box3lab.util.BlockIndexData;
import com.box3lab.util.BlockIndexUtil;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final Map<String, Block> VOXEL_BLOCKS = new HashMap<>();

    private static SoundType soundTypeForCategory(String category) {
        if (category == null) {
            return SoundType.STONE;
        }
        String c = category.toLowerCase(Locale.ROOT);

        return switch (c) {
            case "structure" -> SoundType.STONE;
            case "nature" -> SoundType.GRASS;

            case "symbol", "number", "letter", "color" -> SoundType.STONE;

            case "wood", "plant", "tree", "leaf", "leaves" -> SoundType.WOOD;
            case "metal", "machine" -> SoundType.METAL;
            case "glass" -> SoundType.GLASS;
            case "wool", "cloth" -> SoundType.WOOL;
            case "sand" -> SoundType.SAND;
            case "snow" -> SoundType.SNOW;
            case "slime" -> SoundType.SLIME_BLOCK;
            default -> SoundType.STONE;
        };
    }

    private static String sanitizeCategoryPath(String category) {
        if (category == null || category.isBlank()) {
            return "";
        }
        String lower = category.toLowerCase(Locale.ROOT);
        return lower.replaceAll("[^a-z0-9_\\-]+", "_");
    }

    private static ItemStack defaultIcon() {
        Block iconBlock = VOXEL_BLOCKS.get("voxel_stone");
        if (iconBlock == null && !VOXEL_BLOCKS.isEmpty()) {
            iconBlock = VOXEL_BLOCKS.values().iterator().next();
        }
        return iconBlock == null ? new ItemStack(Items.STONE) : new ItemStack(iconBlock);
    }

    private static void registerCreativeTabs(BlockIndexData data) {
        Map<String, List<String>> categoryToRegistryNames = new HashMap<>();
        for (String rn : VOXEL_BLOCKS.keySet()) {
            String voxelName = rn.startsWith("voxel_") ? rn.substring("voxel_".length()) : rn;
            String category = data.categoryByName.getOrDefault(voxelName, "");
            String categoryPath = sanitizeCategoryPath(category);
            if (categoryPath != null && !categoryPath.isBlank()) {
                categoryToRegistryNames.computeIfAbsent(categoryPath, k -> new ArrayList<>()).add(rn);
            }
        }

        List<String> categories = new ArrayList<>(categoryToRegistryNames.keySet());
        categories.sort(Comparator.naturalOrder());

        for (String categoryPath : categories) {
            List<String> registryNames = categoryToRegistryNames.get(categoryPath);
            if (registryNames == null || registryNames.isEmpty()) {
                continue;
            }
            registryNames.sort(Comparator.naturalOrder());

            Block categoryIconBlock = VOXEL_BLOCKS.get(registryNames.get(0));

            ResourceKey<CreativeModeTab> key = ResourceKey.create(
                    BuiltInRegistries.CREATIVE_MODE_TAB.key(),
                    Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, "creative_tab_" + categoryPath)
            );
            CreativeModeTab tab = FabricItemGroup.builder()
                    .icon(() -> categoryIconBlock == null ? defaultIcon() : new ItemStack(categoryIconBlock))
                    .title(Component.translatable("itemGroup." + Box3Mod.MOD_ID + "." + categoryPath))
                    .displayItems((params, output) -> {
                        for (String rn : registryNames) {
                            Block b = VOXEL_BLOCKS.get(rn);
                            if (b != null) {
                                output.accept(b.asItem());
                            }
                        }
                    })
                    .build();

            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
        }
    }


    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        // Create a registry key for the block
        ResourceKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings.setId(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            ResourceKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, name));
    }
    public static void initialize() {

        BlockIndexData data = BlockIndexData.get();

        for (int i = 0; i < data.ids.length; i++) {
            int id = data.ids[i];
            if (id == 0) {
                continue;
            }
            if (BlockIndexUtil.isFluid(id)) {
                continue;
            }

            String voxelName = data.names[i];
            String texturePart = voxelName.toLowerCase(java.util.Locale.ROOT);
            String registryName = "voxel_" + texturePart;

            String category = data.categoryByName.getOrDefault(texturePart, "");
            SoundType soundType = soundTypeForCategory(category);

            int emissive = BlockIndexUtil.blockEmissiveLight(id);
            int rawLight = emissive == 0 ? 0 : (int) Math.round(15.0 * (0.8 + 0.2 * emissive / 4095.0));
            final int lightLevel = Math.max(0, Math.min(15, rawLight));

            boolean solid = BlockIndexUtil.isSolid(id);
            BlockBehaviour.Properties props = BlockBehaviour.Properties.of().sound(soundType)
                    .lightLevel(state -> lightLevel).noTerrainParticles();
            if (!solid) {
                props = props.noOcclusion();
            }

            Block block;
            if ("conveyor".equalsIgnoreCase(voxelName)) {
                block = register(registryName, ConveyorBlock::new, props, true);
            } else if ("bounce_pad".equalsIgnoreCase(voxelName)) {
                block = register(registryName, BouncePadBlock::new, props, true);
            } else {
                block = register(registryName, VoxelBlock::new, props, true);
            }
            VOXEL_BLOCKS.put(registryName, block);
        }

        registerCreativeTabs(data);
    }

}