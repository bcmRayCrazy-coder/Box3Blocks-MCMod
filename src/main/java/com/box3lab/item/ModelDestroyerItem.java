package com.box3lab.item;

import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ModelDestroyerItem extends Item {
    private static final double RANGE = 10.0;

    public ModelDestroyerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            // 客户端或非服务端世界，直接视为成功但不执行删除逻辑
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        boolean deleted = deleteTarget(serverLevel, player);
        return deleted ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private static boolean deleteTarget(ServerLevel level, Player player) {
        Display.ItemDisplay target = findDisplay(level, player, RANGE);
        if (target == null) {
            return false;
        }
        target.discard();
        return true;
    }

    private static Display.ItemDisplay findDisplay(ServerLevel level, Player player, double range) {
        Vec3 start = player.getEyePosition(1.0f);
        Vec3 look = player.getViewVector(1.0f);
        Vec3 end = start.add(look.scale(range));
        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.5);

        List<Display.ItemDisplay> displays = level.getEntitiesOfClass(Display.ItemDisplay.class, searchBox);
        Display.ItemDisplay closest = null;
        double bestDist = range * range;

        for (Display.ItemDisplay display : displays) {
            AABB box = display.getBoundingBox().inflate(0.8);
            var hit = box.clip(start, end);
            if (hit.isEmpty()) {
                continue;
            }
            double dist = start.distanceToSqr(hit.get());
            if (dist < bestDist) {
                bestDist = dist;
                closest = display;
            }
        }

        return closest;
    }
}
