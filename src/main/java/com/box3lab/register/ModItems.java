package com.box3lab.register;

public final class ModItems {

    private ModItems() {
    }

    public static void initialize() {
        // Register all model-based display items discovered from resource packs
        ModelItemRegistrar.registerAll();

        // In the future, you can add more item registrars here, e.g.:
        // ExtraItemsRegistrar.registerAll();
    }
}
