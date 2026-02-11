package com.box3lab;

import com.box3lab.client.VoxelFluidRenderHandler;
import com.box3lab.register.ModBlocks;
import com.box3lab.register.ModFluids;
import com.box3lab.util.BlockIndexUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class Box3ModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		
		// 
		for (var entry : ModBlocks.VOXEL_BLOCKS.entrySet()) {
			String registryName = entry.getKey();
			Block block = entry.getValue();

			String texturePart = registryName.startsWith("voxel_") ? registryName.substring("voxel_".length())
					: registryName;
			Integer id = BlockIndexUtil.getIdByName(texturePart);
			if (id != null && !BlockIndexUtil.isSolid(id)) {
				BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.TRANSLUCENT);
			}
		}
		Identifier stillTex = Identifier.fromNamespaceAndPath("minecraft", "block/water_still");
		Identifier flowTex = Identifier.fromNamespaceAndPath("minecraft", "block/water_flow");
		for (var info : ModFluids.SPEC_FLUIDS.values()) {
			FluidRenderHandlerRegistry.INSTANCE.register(
					info.still,
					info.flowing,
					new VoxelFluidRenderHandler(stillTex, flowTex, info.tint, info.fluidExtinction));
		}
	}
}