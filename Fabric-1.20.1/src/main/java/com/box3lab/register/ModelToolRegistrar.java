package com.box3lab.register;

import com.box3lab.Box3;
import com.box3lab.item.ModelDestroyerItem;
import com.box3lab.register.creative.CreativeTabExtras;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModelToolRegistrar {
    private ModelToolRegistrar() {
    }

    public static void registerAll() {
        registerDestroyer();
        registerDestroyerHandler();
    }

    private static void registerDestroyer() {
        ResourceLocation id = new ResourceLocation(Box3.MOD_ID, "model_destroyer");
        if (BuiltInRegistries.ITEM.containsKey(id)) {
            return;
        }

        Item item = new ModelDestroyerItem(new Item.Properties().stacksTo(1));
        Registry.register(BuiltInRegistries.ITEM, id, item);
        CreativeTabExtras.add(ModelItemRegistrar.DEFAULT_TAB, item);
    }

    private static void registerDestroyerHandler() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof ModelDestroyerItem)) {
                return InteractionResult.PASS;
            }

            if (!(entity instanceof Display.ItemDisplay display)) {
                return InteractionResult.PASS;
            }

            if (!(world instanceof ServerLevel)) {
                return InteractionResult.SUCCESS;
            }

            display.discard();
            return InteractionResult.SUCCESS;
        });
    }
}
