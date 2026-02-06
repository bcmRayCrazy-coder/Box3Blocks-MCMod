package com.box3lab.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BlockIndexData {
    public static final class FluidInfo {
        public final int id;
        public final int mass;
        public final long info;

        public FluidInfo(int id, int mass, long info) {
            this.id = id;
            this.mass = mass;
            this.info = info;
        }
    }

    public final int[] ids;
    public final String[] names;
    public final int[] emissive;
    public final Set<Integer> notSolidIds;
    public final Map<Integer, FluidInfo> fluidsById;

    public final Map<Integer, Integer> indexById;
    public final Map<String, Integer> idByName;

    private BlockIndexData(
            int[] ids,
            String[] names,
            int[] emissive,
            Set<Integer> notSolidIds,
            Map<Integer, FluidInfo> fluidsById
    ) {
        this.ids = ids;
        this.names = names;
        this.emissive = emissive;
        this.notSolidIds = notSolidIds;
        this.fluidsById = fluidsById;

        Map<Integer, Integer> indexByIdTmp = new HashMap<>(ids.length * 2);
        for (int i = 0; i < ids.length; i++) {
            indexByIdTmp.put(ids[i], i);
        }
        this.indexById = indexByIdTmp;

        Map<String, Integer> idByNameTmp = new HashMap<>(names.length * 2);
        int len = Math.min(ids.length, names.length);
        for (int i = 0; i < len; i++) {
            idByNameTmp.put(names[i], ids[i]);
        }
        this.idByName = idByNameTmp;
    }

    private static volatile BlockIndexData INSTANCE;

    public static BlockIndexData get() {
        BlockIndexData inst = INSTANCE;
        if (inst != null) {
            return inst;
        }
        synchronized (BlockIndexData.class) {
            if (INSTANCE == null) {
                INSTANCE = load();
            }
            return INSTANCE;
        }
    }

    private static BlockIndexData load() {
        JsonObject root;
        try (InputStream is = BlockIndexData.class.getClassLoader().getResourceAsStream("block-spec.json")) {
            if (is == null) {
                throw new IllegalStateException("Missing resource: block-spec.json");
            }
            root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load block-spec.json", e);
        }

        final class Entry {
            final String name;
            final int id;
            final int emissive;
            final boolean transparent;
            final boolean fluid;
            final int mass;
            final int fluidR;
            final int fluidG;
            final int fluidB;

            Entry(String name, int id, int emissive, boolean transparent, boolean fluid, int mass, int fluidR, int fluidG, int fluidB) {
                this.name = name;
                this.id = id;
                this.emissive = emissive;
                this.transparent = transparent;
                this.fluid = fluid;
                this.mass = mass;
                this.fluidR = fluidR;
                this.fluidG = fluidG;
                this.fluidB = fluidB;
            }
        }

        List<Entry> entries = new ArrayList<>(root.size());
        for (Map.Entry<String, JsonElement> e : root.entrySet()) {
            String name = e.getKey();
            JsonObject obj = e.getValue().getAsJsonObject();

            int id = obj.has("id") ? obj.get("id").getAsInt() : -1;
            boolean transparent = obj.has("transparent") && obj.get("transparent").getAsBoolean();
            boolean fluid = obj.has("fluid") && obj.get("fluid").getAsBoolean();
            int mass = obj.has("mass") ? obj.get("mass").getAsInt() : 0;

            int emissivePacked = 0;
            if (obj.has("emissive") && obj.get("emissive").isJsonArray() && obj.getAsJsonArray("emissive").size() >= 3) {
                double er = obj.getAsJsonArray("emissive").get(0).getAsDouble();
                double eg = obj.getAsJsonArray("emissive").get(1).getAsDouble();
                double eb = obj.getAsJsonArray("emissive").get(2).getAsDouble();
                double max = Math.max(er, Math.max(eg, eb));
                emissivePacked = (int) Math.round(Math.max(0.0, Math.min(1.0, max / 15.0)) * 4095.0);
            }

            int fr = 0, fg = 0, fb = 0;
            if (obj.has("fluidColor") && obj.get("fluidColor").isJsonArray() && obj.getAsJsonArray("fluidColor").size() >= 3) {
                fr = obj.getAsJsonArray("fluidColor").get(0).getAsInt();
                fg = obj.getAsJsonArray("fluidColor").get(1).getAsInt();
                fb = obj.getAsJsonArray("fluidColor").get(2).getAsInt();
            }

            if (id >= 0) {
                entries.add(new Entry(name, id, emissivePacked, transparent, fluid, mass, fr, fg, fb));
            }
        }

        entries.sort((a, b) -> Integer.compare(a.id, b.id));

        int[] ids = new int[entries.size()];
        String[] names = new String[entries.size()];
        int[] emissive = new int[entries.size()];

        Set<Integer> notSolidSet = new HashSet<>();
        Map<Integer, FluidInfo> fluidsById = new HashMap<>();

        for (int i = 0; i < entries.size(); i++) {
            Entry en = entries.get(i);
            ids[i] = en.id;
            names[i] = en.name;
            emissive[i] = en.emissive;

            if (en.transparent || en.fluid) {
                notSolidSet.add(en.id);
            }
            if (en.fluid) {
                int a = 255;
                long info = (en.fluidR & 255L) | ((en.fluidG & 255L) << 8) | ((en.fluidB & 255L) << 16) | ((a & 255L) << 24);
                fluidsById.put(en.id, new FluidInfo(en.id, en.mass, info));
            }
        }

        return new BlockIndexData(ids, names, emissive, notSolidSet, fluidsById);
    }
}
