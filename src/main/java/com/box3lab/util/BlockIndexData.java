package com.box3lab.util;

import java.util.HashMap;
import java.util.HashSet;
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
        Set<Integer> notSolidSet = new HashSet<>();
        for (int v : BlockIndexStatic.NOT_SOLID_IDS) {
            notSolidSet.add(v);
        }

        Map<Integer, FluidInfo> fluidsById = new HashMap<>();
        for (Map.Entry<Integer, BlockIndexStatic.FluidInfo> e : BlockIndexStatic.FLUIDS_BY_ID.entrySet()) {
            BlockIndexStatic.FluidInfo f = e.getValue();
            fluidsById.put(e.getKey(), new FluidInfo(f.id, f.mass, f.info));
        }

        return new BlockIndexData(BlockIndexStatic.IDS, BlockIndexStatic.NAMES, BlockIndexStatic.EMISSIVE, notSolidSet, fluidsById);
    }
}
