package com.box3lab.register;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class VoxelBlockPropertiesFactory {
    private VoxelBlockPropertiesFactory() {
    }

    public static BlockBehaviour.Properties create(boolean solid, SoundType soundType, int lightLevel) {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
                .sound(soundType)
                .lightLevel(state -> lightLevel);

        if (!solid) {
            props = props.noOcclusion();
        }

        return props;
    }
}
