package com.box3lab.register.voxel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.box3lab.block.BouncePadBlock;
import com.box3lab.block.ConveyorBlock;
import com.box3lab.block.VoxelBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class VoxelBlockFactories {
    private static final Function<BlockBehaviour.Properties, Block> DEFAULT_FACTORY = VoxelBlock::new;
    private static final Map<String, Function<BlockBehaviour.Properties, Block>> FACTORIES = new HashMap<>();

    static {
        register("conveyor", ConveyorBlock::new);
        register("bounce_pad", BouncePadBlock::new);
    }

    private VoxelBlockFactories() {
    }

    public static void register(String texturePart, Function<BlockBehaviour.Properties, Block> factory) {
        if (texturePart == null || texturePart.isBlank() || factory == null) {
            return;
        }
        FACTORIES.put(texturePart.toLowerCase(Locale.ROOT), factory);
    }

    public static Function<BlockBehaviour.Properties, Block> factoryFor(String texturePart) {
        if (texturePart == null) {
            return DEFAULT_FACTORY;
        }

        Function<BlockBehaviour.Properties, Block> factory = FACTORIES.get(texturePart.toLowerCase(Locale.ROOT));
        return factory == null ? DEFAULT_FACTORY : factory;
    }
}
