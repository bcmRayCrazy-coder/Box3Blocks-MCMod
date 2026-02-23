# Box3Blocks（神岛材质包）-- Minecraft Mod

![Modrinth Downloads](https://img.shields.io/modrinth/dt/iG3hRUix?logo=modrinth)
![CurseForge Downloads](https://img.shields.io/curseforge/dt/1456138?logo=curseforge)

[简体中文](README.md) | [English](README_en.md)

导入神奇代码岛的373个方块到我的世界，让你在MC中也能使用熟悉的方块进行创作，还支持将神奇代码岛中的建筑/模型结构完整迁移到Minecraft世界中，保持原汁原味的建造风格。

## 🌟 主要功能

### 🎨 丰富的方块

- **373种方块**：包括字母、数字、符号、颜色、元素等
- **9个创造标签**：分类整理，方便查找
  - Box3:字母 - A-Z字母方块
  - Box3:数字 - 0-9数字方块
  - Box3:符号 - 各种符号方块
  - Box3:颜色 - 彩色方块
  - Box3:元素 - 化学元素方块
  - Box3:食物 - 食物相关方块
  - Box3:灯光 - 发光方块
  - Box3:自然 - 自然材质方块
  - Box3:建筑 - 建筑材料方块

### 🏗 导入神奇代码岛建筑

- **地形文件导入**：支持从 `config/box3/` 目录中的压缩地形文件（`.gz`）导入方块地图。
- **建筑迁移**：可将神奇代码岛中的建筑结构迁移到 Minecraft 世界中，保持方块外观一致。
- **获取建筑文件**：访问 https://box3lab.com/build2mc 获取神奇代码岛建筑的地形文件（`.gz`）。
- **导入指令**：
  - `/box3import`  
    列出 `config/box3/` 目录下所有可导入的地形文件（`.gz`）。
  - `/box3import <fileName>`  
    从 `config/box3/<fileName>.gz` 导入建筑（命令中不需要带后缀，会自动补 `.gz`）。
  - `/box3import <fileName> <ignoreBarrier>`  
    当 `ignoreBarrier = true` 时，跳过屏障方块（不会在世界中放置这些方块）。
  - `/box3import <fileName> <ignoreBarrier> <ignoreWater>`  
    当 `ignoreWater = true` 时，所有流体统一替换为空气。

### 🧩 导入神奇代码岛的模型物品

- **资源文件导入**：支持从 `resourcepacks/` 目录文件导入资源包。
- **资源包加载模型**：将模型放入资源包即可自动注册到创造模式。
- **模型物品标签页**：`Box3:模型` 标签页用于管理模型物品。
- **模型销毁器**：右键模型可删除（道具名：模型销毁器）。
- **生成模型资源包**：访问 https://box3lab.com/mc-resource-pack 获取适用于本模组的资源包文件。

### 🔍 屏障可见性切换

- **屏障可见性切换 `/box3barrier`**：
  - `/box3barrier`：查看当前屏障是否可见。
  - `/box3barrier <bool>`：开启/关闭屏障显示（屏障始终有碰撞，只是是否渲染）。
  - `/box3barrier toggle`：在开启/关闭之间快速切换。状态会保存到本地配置文件，下次进入世界自动沿用。

📋 **完整方块列表**：查看 [block_id.md](block_id.md) 获取所有方块的 ID、注册 Key 和中英文名称对照表。

## 📄 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证。

## 🙏 致谢

- 神奇代码岛提供的方块，神岛实验室开发模组
- FabricMC 团队提供的 Fabric 模组加载器

## 星历史

[![Star History Chart](https://api.star-history.com/svg?repos=box3lab/Box3Blocks-MCMod&type=date&legend=top-left)](https://www.star-history.com/#box3lab/Box3Blocks-MCMod&type=date&legend=top-left)
