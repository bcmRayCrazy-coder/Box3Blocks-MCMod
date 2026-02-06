package com.box3lab;

import com.box3lab.register.ModBlocks;
import com.box3lab.util.BlockIndexUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;

public class Box3ModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		for (var entry : ModBlocks.VOXEL_BLOCKS.entrySet()) {
			String registryName = entry.getKey();
			Block block = entry.getValue();

			String texturePart = registryName.startsWith("voxel_") ? registryName.substring("voxel_".length()) : registryName;
			Integer id = BlockIndexUtil.getIdByName(texturePart);
			if (id != null && !BlockIndexUtil.isSolid(id)) {
				BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.TRANSLUCENT);
			}
		}
	}
}