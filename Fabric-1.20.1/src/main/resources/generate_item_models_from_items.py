import json
from pathlib import Path

MOD_ID = "box3"
BASE_DIR = Path(__file__).parent

ITEMS_DIR = BASE_DIR / "assets" / MOD_ID / "items"
MODELS_ITEM_DIR = BASE_DIR / "assets" / MOD_ID / "models" / "item"


def generate_item_model(item_name: str) -> dict:
    """生成一个 3D 物品模型 JSON（直接复用整个方块模型）。"""
    return {
        "parent": f"{MOD_ID}:block/{item_name}"
    }


def main():
    """扫描 assets/box3/items/*.json，为每个生成对应的 models/item/*.json。"""
    if not ITEMS_DIR.is_dir():
        print(f"❌ 目录不存在: {ITEMS_DIR}")
        return

    # 确保目标目录存在
    MODELS_ITEM_DIR.mkdir(parents=True, exist_ok=True)

    item_files = sorted(ITEMS_DIR.glob("*.json"))
    if not item_files:
        print("⚠️  没有找到任何 *.json 文件在 assets/box3/items/ 下")
        return

    for item_file in item_files:
        item_name = item_file.stem  # 去掉 .json 后缀
        model_json = generate_item_model(item_name)
        model_path = MODELS_ITEM_DIR / f"{item_name}.json"

        with open(model_path, "w", encoding="utf-8") as f:
            json.dump(model_json, f, indent=2)

        print(f"✅ 生成: {model_path.relative_to(BASE_DIR)}")

    print(f"\n🎉 完成！共生成 {len(item_files)} 个物品模型文件到 {MODELS_ITEM_DIR.relative_to(BASE_DIR)}")


if __name__ == "__main__":
    main()
