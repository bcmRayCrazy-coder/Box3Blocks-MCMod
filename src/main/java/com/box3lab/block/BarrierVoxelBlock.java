package com.box3lab.block;

import com.box3lab.util.ConfigUtil;
import com.google.gson.JsonObject;

import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import static com.box3lab.util.ConfigUtil.CONFIG_DIR_NAME;

public class BarrierVoxelBlock extends VoxelBlock {

    private static volatile boolean visible = false;

    static {
        loadConfig();
    }

    public BarrierVoxelBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return isVisible() ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    public static boolean isVisible() {
        return visible;
    }

    public static void setVisible(boolean value) {
        visible = value;
        saveConfig();
    }

    private static void loadConfig() {
        JsonObject obj = ConfigUtil.readConfig(CONFIG_DIR_NAME);
        if (obj == null) {
            return;
        }
        if (obj.has("barrierVisible")) {
            visible = obj.get("barrierVisible").getAsBoolean();
        }
    }

    private static void saveConfig() {
        JsonObject obj = new JsonObject();
        obj.addProperty("barrierVisible", visible);
        ConfigUtil.writeConfig(CONFIG_DIR_NAME, obj);
    }
}
