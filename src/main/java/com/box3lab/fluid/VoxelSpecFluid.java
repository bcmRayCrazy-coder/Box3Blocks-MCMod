package com.box3lab.fluid;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public abstract class VoxelSpecFluid extends FlowingFluid {
    private final Supplier<? extends Fluid> still;
    private final Supplier<? extends Fluid> flowing;
    private final Supplier<? extends Item> bucket;
    private final Supplier<? extends Block> block;

    protected VoxelSpecFluid(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, Supplier<? extends Item> bucket, Supplier<? extends Block> block) {
        this.still = still;
        this.flowing = flowing;
        this.bucket = bucket;
        this.block = block;
    }

    @Override
    public Fluid getFlowing() {
        return flowing.get();
    }

    @Override
    public Fluid getSource() {
        return still.get();
    }

    @Override
    public Item getBucket() {
        return bucket.get();
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return block.get().defaultBlockState().setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == still.get() || fluid == flowing.get();
    }

    @Override
    public int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 4;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public boolean canConvertToSource(ServerLevel level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level instanceof Level l) {
            Block.dropResources(state, l, pos);
        }
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        builder.add(LEVEL);
    }

    public static final class Flowing extends VoxelSpecFluid {
        public Flowing(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, Supplier<? extends Item> bucket, Supplier<? extends Block> block) {
            super(still, flowing, bucket, block);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
    }

    public static final class Still extends VoxelSpecFluid {
        public Still(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, Supplier<? extends Item> bucket, Supplier<? extends Block> block) {
            super(still, flowing, bucket, block);
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
    }
}
