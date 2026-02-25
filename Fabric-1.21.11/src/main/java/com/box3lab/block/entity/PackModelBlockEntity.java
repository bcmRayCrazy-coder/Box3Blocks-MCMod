package com.box3lab.block.entity;

import com.box3lab.register.modelbe.PackModelBlockEntityRegistrar;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PackModelBlockEntity extends BlockEntity {
    private static final long RESPAWN_INTERVAL_TICKS = 20L;
    private static final String DISPLAY_TAG_PREFIX = "box3_pack_model:";

    public PackModelBlockEntity(BlockPos pos, BlockState state) {
        super(PackModelBlockEntityRegistrar.typeFor(state.getBlock()), pos, state);
    }

    @Override
    public void setRemoved() {
        removeDisplaysAt(this.level, this.getBlockPos());
        super.setRemoved();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PackModelBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (serverLevel.getGameTime() % RESPAWN_INTERVAL_TICKS != 0) {
            return;
        }

        String tag = displayTag(pos);
        var displays = serverLevel.getEntitiesOfClass(
                Display.ItemDisplay.class,
                new AABB(pos),
                display -> display.getTags().contains(tag));
        if (!displays.isEmpty()) {
            return;
        }

        spawnDisplay(serverLevel, pos, state, tag);
    }

    public static void removeDisplaysAt(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        String tag = displayTag(pos);
        var displays = serverLevel.getEntitiesOfClass(
                Display.ItemDisplay.class,
                new AABB(pos).inflate(0.25D),
                display -> display.getTags().contains(tag));
        for (Display.ItemDisplay display : displays) {
            display.discard();
        }
    }

    private static void spawnDisplay(ServerLevel level, BlockPos pos, BlockState state, String tag) {
        Display.ItemDisplay display = EntityType.ITEM_DISPLAY.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
        if (display == null) {
            return;
        }

        Vec3 center = Vec3.atCenterOf(pos);
        display.setPos(center.x, center.y, center.z);
        display.setNoGravity(true);
        display.setInvulnerable(true);
        if (state.hasProperty(PackModelEntityBlock.HORIZONTAL_FACING)) {
            display.setYRot(state.getValue(PackModelEntityBlock.HORIZONTAL_FACING).toYRot());
        }
        display.getSlot(0).set(new ItemStack(state.getBlock()));
        display.addTag(tag);

        level.addFreshEntity(display);
    }

    private static String displayTag(BlockPos pos) {
        return DISPLAY_TAG_PREFIX + pos.asLong();
    }
}
