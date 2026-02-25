package com.box3lab.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class PackModelEntityBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PackModelEntityBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PackModelBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        // This block is rendered by ItemDisplay instead of block model,
        // so vanilla block-break particles would resolve to missing textures.
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PackModelBlockEntity modelBe)) {
            return InteractionResult.PASS;
        }

        if (stack.is(Items.PAPER)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            modelBe.copyConfig(player);
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.BOOK)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                modelBe.pasteConfig(serverLevel, pos, state, player);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (stack.is(Items.STICK)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                modelBe.adjustCurrentMode(serverLevel, pos, state, 1, player);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (stack.is(Items.BLAZE_ROD)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                modelBe.adjustCurrentMode(serverLevel, pos, state, -1, player);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (!stack.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        modelBe.cycleMode(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }

        BlockEntityType<?> expectedType = com.box3lab.register.modelbe.PackModelBlockEntityRegistrar
                .typeFor(state.getBlock());
        if (blockEntityType != expectedType) {
            return null;
        }

        return (lvl, pos, blockState, be) -> PackModelBlockEntity.serverTick(
                lvl,
                pos,
                blockState,
                (PackModelBlockEntity) be);
    }
}
