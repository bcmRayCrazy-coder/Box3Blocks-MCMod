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

		for (var entry : ModBlocks.BLOCKS.entrySet()) {
			String registryName = entry.getKey();
			Block block = entry.getValue();

			Integer id = BlockIndexUtil.getIdByName(registryName);
			if (id != null && !BlockIndexUtil.isSolid(id)) {
				BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.TRANSLUCENT);
			}
		}

	}
}