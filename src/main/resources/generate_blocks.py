import json
from pathlib import Path

# 模组 ID，用于生成资源路径前缀
MOD_ID = "box3mod"

# 当前 py 文件目录
BASE_DIR = Path(__file__).parent

# 贴图目录
TEXTURES_DIR = BASE_DIR / "assets" / MOD_ID / "textures" / "block"

# 六个面对应的后缀
FACES = ["top", "bottom", "front", "back", "left", "right"]


def scan_texture_parts():
    """扫描贴图目录，提取所有方块的基础名称（texture_part）。"""
    texture_parts = set()
    for file_path in TEXTURES_DIR.glob("*.png"):
        # 文件名格式: {texture_part}_{face}_0.png
        parts = file_path.stem.split("_")
        if len(parts) >= 3 and parts[-1] == "0" and parts[-2] in FACES:
            texture_part = "_".join(parts[:-2])
            texture_parts.add(texture_part)
    return sorted(texture_parts)


def check_all_faces_exist(texture_part):
    """检查指定 texture_part 的六个面贴图是否都存在。"""
    missing = []
    for face in FACES:
        texture_file = TEXTURES_DIR / f"{texture_part}_{face}_0.png"
        if not texture_file.exists():
            missing.append(texture_file)
    return missing


def generate_blockstate(texture_part):
    """生成指定方块的 blockstate JSON 数据结构。"""
    return {
        "variants": {
            "": {"model": f"{MOD_ID}:block/{texture_part}"}
        }
    }


def generate_block_model(texture_part):
    """生成指定方块的方块模型 JSON 数据结构，统一使用 cube 并按规则生成六面贴图。"""
    return {
        "parent": "minecraft:block/cube",
        "textures": {
            "up":    f"{MOD_ID}:block/{texture_part}_top_0",
            "down":  f"{MOD_ID}:block/{texture_part}_bottom_0",
            "north": f"{MOD_ID}:block/{texture_part}_front_0",
            "south": f"{MOD_ID}:block/{texture_part}_back_0",
            "west":  f"{MOD_ID}:block/{texture_part}_left_0",
            "east":  f"{MOD_ID}:block/{texture_part}_right_0"
        }
    }


def generate_item_model(texture_part):
    """生成指定方块对应物品的模型 JSON 数据结构。"""
    return {
        "model": {
            "type": "minecraft:model",
            "model": f"{MOD_ID}:block/{texture_part}"
        }
    }


def main():
    """脚本入口：扫描贴图目录并为每个方块生成所有 JSON 文件。"""
    texture_parts = scan_texture_parts()
    print(f"发现 {len(texture_parts)} 个方块: {', '.join(texture_parts)}")
    
    for texture_part in texture_parts:
        # 检查六个面贴图是否都存在
        missing = check_all_faces_exist(texture_part)
        if missing:
            print(f"⚠️  {texture_part} 缺少贴图: {', '.join(m.name for m in missing)}")
            continue
        
        # 生成三种 JSON 文件
        configs = [
            (BASE_DIR / "assets" / MOD_ID / "blockstates" / f"{texture_part}.json",
             generate_blockstate(texture_part)),
            (BASE_DIR / "assets" / MOD_ID / "models" / "block" / f"{texture_part}.json",
             generate_block_model(texture_part)),
            (BASE_DIR / "assets" / MOD_ID / "items" / f"{texture_part}.json",
             generate_item_model(texture_part)),
        ]
        
        for path, data in configs:
            path.parent.mkdir(parents=True, exist_ok=True)
            with open(path, "w") as f:
                json.dump(data, f, indent=2)
        
        print(f"✅ Generated: {texture_part}")


if __name__ == "__main__":
    main()