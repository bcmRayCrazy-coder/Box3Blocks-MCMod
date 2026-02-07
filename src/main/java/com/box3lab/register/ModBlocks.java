package com.box3lab.register;

import java.util.HashMap;
import java.util.Map;

import com.box3lab.Box3Mod;
import com.box3lab.register.core.BlockRegistrar;
import com.box3lab.register.creative.CreativeTabRegistrar;
import com.box3lab.register.sound.CategorySoundTypes;
import com.box3lab.register.voxel.VoxelBlockFactories;
import com.box3lab.register.voxel.VoxelBlockPropertiesFactory;
import com.box3lab.register.voxel.VoxelLightLevelMapper;
import com.box3lab.util.BlockIndexData;
import com.box3lab.util.BlockIndexUtil;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class ModBlocks {
    public static final Map<String, Block> VOXEL_BLOCKS = new HashMap<>();

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
            SoundType soundType = CategorySoundTypes.soundTypeForCategory(category);

            int emissive = BlockIndexUtil.blockEmissiveLight(id);
            final int lightLevel = VoxelLightLevelMapper.lightLevelFromEmissivePacked(emissive);

            boolean solid = BlockIndexUtil.isSolid(id);
            var props = VoxelBlockPropertiesFactory.create(solid, soundType, lightLevel);

            Block block =BlockRegistrar.register(
                    Box3Mod.MOD_ID,
                    registryName,
                    VoxelBlockFactories.factoryFor(texturePart),
                    props,
                    true
            );
            VOXEL_BLOCKS.put(registryName, block);
        }

        CreativeTabRegistrar.registerCreativeTabs(Box3Mod.MOD_ID, VOXEL_BLOCKS, data);
    }

}