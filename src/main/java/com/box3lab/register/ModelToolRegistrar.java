package com.box3lab.register;

import com.box3lab.Box3Mod;
import com.box3lab.item.ModelDestroyerItem;
import com.box3lab.register.creative.CreativeTabExtras;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
        Identifier id = Identifier.fromNamespaceAndPath(Box3Mod.MOD_ID, "model_destroyer");
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        if (BuiltInRegistries.ITEM.containsKey(key)) {
            return;
        }

        Item item = new ModelDestroyerItem(new Item.Properties().setId(key).stacksTo(1));
        Registry.register(BuiltInRegistries.ITEM, key, item);
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
