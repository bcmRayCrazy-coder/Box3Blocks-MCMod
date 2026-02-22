# Box3Blocks（神岛材质包）-- Minecraft Mod

![Modrinth Downloads](https://img.shields.io/modrinth/dt/iG3hRUix?logo=modrinth)
![CurseForge Downloads](https://img.shields.io/curseforge/dt/1456138?logo=curseforge)

[简体中文](README.md) | [English](README_en.md)

Import 373 blocks from the Box3 (Magic Code Island) platform into Minecraft, so you can build with the same familiar blocks inside MC.  
You can also migrate structures from Box3 directly into your Minecraft world, preserving the original building style.

## 📦 Requirements

| Component         | Minimum  | Recommended |
| ----------------- | -------- | ----------- |
| **Minecraft**     | >=1.21.8 | 1.21.11     |
| **Fabric Loader** | >=0.18.4 | 0.18.4      |
| **Fabric API**    | Any      | 0.119.0+    |
| **Java**          | >=21     | Java 21     |

## 🌟 Features

### 🎨 Rich Block Library

- **373 blocks**: including letters, numbers, symbols, colors, elements, etc.
- **9 creative tabs**: organized for easy search and use
  - Box3: Letters – A-Z letter blocks
  - Box3: Numbers – 0-9 number blocks
  - Box3: Symbols – various symbol blocks
  - Box3: Colors – colorful blocks
  - Box3: Elements – chemical element blocks
  - Box3: Food – food-related blocks
  - Box3: Lights – emissive / light blocks
  - Box3: Nature – natural-texture blocks
  - Box3: Building – building material blocks

### 🏗 Importing Box3 Structures

- **Terrain file import**: supports importing compressed terrain files (`.gz`) from the `config/box3mod/` directory into your world.
- **Structure migration**: migrate structures from Box3 into Minecraft while keeping the same block appearance.
- **Get terrain files**: visit https://box3lab.com/build2mc to download Box3 building terrain files (`.gz`).
- **Import commands**:
  - `/box3import`  
    List all available terrain files (`.gz`) under `config/box3mod/`.
  - `/box3import <fileName>`  
    Import a structure from `config/box3mod/<fileName>.gz`.  
    (You don’t need to type the `.gz` extension in the command.)
  - `/box3import <fileName> <ignoreBarrier>`  
    When `ignoreBarrier = true`, barrier blocks will be skipped (they will not be placed in the world).
  - `/box3import <fileName> <ignoreBarrier> <ignoreWater>`  
    When `ignoreWater = true`, all fluids are uniformly replaced with air.

### 🧩 Importing Box3 Model Items

- **Resource file import**: Supports importing resource packs from the `resourcepacks/` directory.
- **Resource pack model loading**: Put models into a resource pack to auto-register them in Creative.
- **Model creative tab**: `Box3:Models` tab for model items.
- **Model Destroyer**: Right-click a model to delete it (item name: Model Destroyer).
- **Generate model resource pack**: Visit https://box3lab.com/mc-resource-pack to get a pack compatible with this mod.

### 🔍 Barrier Visibility Toggle

- **Barrier visibility command `/box3barrier`**:
  - `/box3barrier`  
    Show whether barriers are currently visible.
  - `/box3barrier <bool>`  
    Turn barrier rendering on or off (barriers always have collision; this only controls rendering).
  - `/box3barrier toggle`  
    Quickly toggle barrier visibility.  
    The state is saved to a local config file and will be applied automatically next time you enter the world.

📋 **Full block list**: see [block_id.md](block_id.md) for all block IDs, registry keys, and Chinese–English name mapping.

## 📄 License

This project is licensed under the [Apache License 2.0](LICENSE).

## 🙏 Acknowledgements

- Blocks provided by Box3, and the mod developed by Box3Lab
- FabricMC team for the Fabric mod loader

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=box3lab/Box3Blocks-MCMod&type=date&legend=top-left)](https://www.star-history.com/#box3lab/Box3Blocks-MCMod&type=date&legend=top-left)
