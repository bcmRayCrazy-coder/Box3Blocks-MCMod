package com.box3lab.register;

import com.box3lab.Box3Mod;
import com.box3lab.block.VoxelBlock;
import com.box3lab.util.BlockIndexData;
import com.box3lab.util.BlockIndexUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModBlocks {
    public static final Map<String, Block> VOXEL_BLOCKS = new HashMap<>();

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

            int emissive = BlockIndexUtil.blockEmissiveLight(id);
            int rawLight = emissive == 0 ? 0 : (int) Math.round(15.0 * (0.8 + 0.2 * emissive / 4095.0));
            final int lightLevel = Math.max(0, Math.min(15, rawLight));

            boolean solid = BlockIndexUtil.isSolid(id);
            BlockBehaviour.Properties props = BlockBehaviour.Properties.of().sound(SoundType.STONE)
                    .lightLevel(state -> lightLevel);
            if (!solid) {
                props = props.noOcclusion();
            }

            Block block = register(registryName, VoxelBlock::new, props, true);
            VOXEL_BLOCKS.put(registryName, block);
        }

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((itemGroup) -> {
            for (Block block : VOXEL_BLOCKS.values()) {
                itemGroup.accept(block.asItem());
            }
        });
    }

}