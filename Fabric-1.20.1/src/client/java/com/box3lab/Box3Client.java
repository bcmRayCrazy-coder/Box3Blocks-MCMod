package com.box3lab;

import com.box3lab.register.ModBlocks;
import com.box3lab.util.BlockIndexUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class Box3Client implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		for (var entry : ModBlocks.BLOCKS.entrySet()) {
			String registryName = entry.getKey();
			Block block = entry.getValue();

			Integer id = BlockIndexUtil.getIdByName(registryName);
			if (id != null && !BlockIndexUtil.isSolid(id)) {
				BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());
			}
		}

	}
}