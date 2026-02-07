package com.box3lab.client;

import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;

public class VoxelFluidRenderHandler extends SimpleFluidRenderHandler {
    private final double fluidExtinction;

    public VoxelFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, int tint, double fluidExtinction) {
        super(stillTexture, flowingTexture, tint);
        this.fluidExtinction = fluidExtinction;
    }

    @Override
    public int getFluidColor(BlockAndTintGetter view, BlockPos pos, FluidState state) {
        int baseColor = super.getFluidColor(view, pos, state);
        // Apply extinction by reducing alpha (higher extinction = more transparent)
        int alpha = (int) (255 * (1.0 - Math.min(1.0, Math.max(0.0, fluidExtinction))));
        return (baseColor & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }
}
