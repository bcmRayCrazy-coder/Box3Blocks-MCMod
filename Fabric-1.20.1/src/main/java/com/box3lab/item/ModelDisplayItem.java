package com.box3lab.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ModelDisplayItem extends Item {
    public ModelDisplayItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        Display.ItemDisplay display = (Display.ItemDisplay) EntityType.ITEM_DISPLAY.create(serverLevel);
        if (display == null) {
            return InteractionResult.FAIL;
        }

        BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
        Vec3 pos = Vec3.atCenterOf(placePos);
        display.setPos(pos.x, pos.y, pos.z);

        Player player = context.getPlayer();
        if (player != null) {
            display.setYRot(player.getYRot());
        }

        ItemStack displayStack = context.getItemInHand().copyWithCount(1);
        display.getSlot(0).set(displayStack);

        display.setNoGravity(true);
        serverLevel.addFreshEntity(display);

        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
