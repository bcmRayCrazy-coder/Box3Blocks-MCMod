# 神奇代码岛方块MC模组

导入神奇代码岛的384个方块到我的世界，让你在MC中也能使用熟悉的方块进行创作，还支持将神奇代码岛中的建筑结构完整迁移到Minecraft世界中，保持原汁原味的建造风格。

## 📦 安装要求

| 组件              | 最低版本 | 推荐版本 |
| ----------------- | -------- | -------- |
| **Minecraft**     | >=1.21.8 | 1.21.11  |
| **Fabric Loader** | >=0.18.4 | 0.18.4   |
| **Fabric API**    | 任意版本 | 0.119.0+ |
| **Java**          | >=21     | Java 21  |

## 🌟 主要功能

### 🎨 丰富的方块

- **384种方块**：包括字母、数字、符号、颜色、元素等
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

- **JSON 地图导入**：支持从 `minecraft/config/box3mod/` 目录中的 JSON 文件导入方块地图。
- **建筑迁移**：可将神奇代码岛中的建筑结构迁移到 Minecraft 世界中，保持方块外观一致。
- **获取建筑文件**：访问 https://box3lab.com/build2mc 获取神奇代码岛建筑的JSON文件。
- **指令参数**：
  - `/box3import <fileName>`  
    从 `config/box3mod/<fileName>.json` 导入建筑。
  - `/box3import <fileName> <ignoreBarrier>`  
    当 `ignoreBarrier = true` 时，跳过透明屏障方块（不会在世界中放置这些方块）。
  - `/box3import <fileName> <ignoreBarrier> <useVanillaWater>`  
    当 `useVanillaWater = true` 时，所有流体统一替换为MC原版水方块。

📋 **完整方块列表**：查看 [block_id_reference.md](block_id_reference.md) 获取所有方块的ID、注册Key和中英文名称对照表。

## 📄 许可证

本项目采用 Apache License 2.0 许可证。

## 🙏 致谢

- 神奇代码岛提供的方块，神岛实验室开发模组
- FabricMC 团队提供的 Fabric 模组加载器
- Minecraft 社区的支持和反馈
