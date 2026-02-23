package com.box3lab.register;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.box3lab.Box3;
import com.box3lab.item.ModelDisplayItem;
import com.box3lab.register.creative.CreativeTabExtras;
import com.box3lab.register.creative.CreativeTabRegistrar;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ModelItemRegistrar {
    private static final String ITEMS_DIR_PREFIX = "assets/" + Box3.MOD_ID + "/items/";
    public static final String DEFAULT_TAB = "models";

    private ModelItemRegistrar() {
    }

    public static void registerAll() {
        Set<String> itemPaths = discoverModelItemPaths();
        if (itemPaths.isEmpty()) {
            return;
        }

        for (String path : itemPaths) {
            ResourceLocation id;
            try {
                id = ResourceLocation.fromNamespaceAndPath(Box3.MOD_ID, path);
            } catch (IllegalArgumentException e) {
                continue;
            }

            if (BuiltInRegistries.ITEM.containsKey(id)) {
                continue;
            }

            Item item = new ModelDisplayItem(new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, id, item);
            CreativeTabExtras.add(DEFAULT_TAB, item);
        }

        CreativeTabRegistrar.registerModelTab(Box3.MOD_ID);
    }

    private static Set<String> discoverModelItemPaths() {
        Set<String> results = new LinkedHashSet<>();
        Path resourcepacksDir = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
        if (!Files.isDirectory(resourcepacksDir)) {
            return results;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(resourcepacksDir)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    scanDirectoryPack(entry, results);
                } else if (isArchive(entry)) {
                    scanZipPack(entry, results);
                }
            }
        } catch (IOException ignored) {
        }

        return results;
    }

    private static boolean isArchive(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".zip") || name.endsWith(".jar");
    }

    private static void scanDirectoryPack(Path packDir, Set<String> out) {
        Path itemsDir = packDir.resolve("assets").resolve(Box3.MOD_ID).resolve("items");
        if (!Files.isDirectory(itemsDir)) {
            return;
        }

        try (var paths = Files.walk(itemsDir)) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        String name = file.getFileName().toString();
                        if (!name.endsWith(".json")) {
                            return;
                        }

                        String rel = itemsDir.relativize(file).toString().replace(File.separatorChar, '/');
                        if (rel.endsWith(".json")) {
                            rel = rel.substring(0, rel.length() - 5);
                        }
                        if (!rel.isBlank()) {
                            out.add(rel);
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    private static void scanZipPack(Path zipPath, Set<String> out) {
        try (ZipFile zip = new ZipFile(zipPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (!name.startsWith(ITEMS_DIR_PREFIX) || !name.endsWith(".json")) {
                    continue;
                }
                String rel = name.substring(ITEMS_DIR_PREFIX.length(), name.length() - 5);
                if (!rel.isBlank()) {
                    out.add(rel);
                }
            }
        } catch (IOException ignored) {
        }
    }
}
