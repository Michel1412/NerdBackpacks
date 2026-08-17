#!/usr/bin/env python3
"""
Import Blockbench .bbmodel backpacks into NerdBackpacks assets.

Reads the 5 tier folders (Base, Tier2..Tier5), extracts embedded PNGs,
converts cubes + UVs into Minecraft Java block/item models, and wires
blockstates so the same 3D model is used for:
  - inventory / hotbar icon
  - GUI slot
  - placed block in the world

Usage (from repo root):
  python scripts/import_bbmodels.py
  python scripts/import_bbmodels.py --source "E:/Arquivos_Mods/itens/Backpacks/Backpacks"
"""

from __future__ import annotations

import argparse
import base64
import json
import re
import sys
from pathlib import Path
from typing import Any

FACE_KEYS = ("north", "east", "south", "west", "up", "down")

# Inventory / hand / ground defaults (Blockbench java_block style).
DEFAULT_DISPLAY: dict[str, Any] = {
    "gui": {
        "rotation": [24.38, -37.12, -0.66],
        "translation": [0, 0, 0],
        "scale": [1, 1, 1],
    },
    "ground": {
        "rotation": [0, 0, 0],
        "translation": [0, 3, 0],
        "scale": [0.25, 0.25, 0.25],
    },
    "fixed": {
        "rotation": [0, 180, 0],
        "translation": [0, 0, 0],
        "scale": [0.5, 0.5, 0.5],
    },
    "thirdperson_righthand": {
        "rotation": [75, 45, 0],
        "translation": [0, 2.5, 0],
        "scale": [0.375, 0.375, 0.375],
    },
    "thirdperson_lefthand": {
        "rotation": [75, 45, 0],
        "translation": [0, 2.5, 0],
        "scale": [0.375, 0.375, 0.375],
    },
    "firstperson_righthand": {
        "rotation": [0, 45, 0],
        "translation": [0, 0, 0],
        "scale": [0.4, 0.4, 0.4],
    },
    "firstperson_lefthand": {
        "rotation": [0, 225, 0],
        "translation": [0, 0, 0],
        "scale": [0.4, 0.4, 0.4],
    },
}

# Folder name under the Backpacks root → registry item/block id
TIER_MAP = {
    "Base": "mochila_1",
    "Tier2": "mochila_2",
    "Tier3": "mochila_3",
    "Tier4": "mochila_4",
    "Tier5": "mochila_5",
}

MOD_ID = "nerdbackpacks"
DATA_URL_RE = re.compile(r"^data:image/(\w+);base64,(.+)$", re.DOTALL)


def as_vec3(value: Any) -> list[float] | None:
    if not isinstance(value, (list, tuple)) or len(value) != 3:
        return None
    try:
        return [float(value[0]), float(value[1]), float(value[2])]
    except (TypeError, ValueError):
        return None


def convert_rotation(element: dict[str, Any]) -> dict[str, Any] | None:
    origin = as_vec3(element.get("origin")) or [8.0, 8.0, 8.0]
    rot = element.get("rotation")

    if isinstance(rot, dict) and "axis" in rot and "angle" in rot:
        return {
            "origin": origin,
            "axis": rot["axis"],
            "angle": float(rot["angle"]),
        }

    if not isinstance(rot, (list, tuple)) or len(rot) != 3:
        return None

    try:
        rx, ry, rz = float(rot[0]), float(rot[1]), float(rot[2])
    except (TypeError, ValueError):
        return None

    axes = [("x", rx), ("y", ry), ("z", rz)]
    nonzero = [(axis, angle) for axis, angle in axes if abs(angle) > 1e-6]
    if not nonzero:
        return None
    if len(nonzero) > 1:
        # Minecraft allows one axis; keep the largest magnitude.
        nonzero.sort(key=lambda pair: abs(pair[1]), reverse=True)
    axis, angle = nonzero[0]
    # Snap to common Blockbench export angles.
    for candidate in (-45, -22.5, 0, 22.5, 45):
        if abs(angle - candidate) < 0.01:
            angle = candidate
            break
    if abs(angle) < 1e-6:
        return None
    return {"origin": origin, "axis": axis, "angle": angle}


def texture_index(face: dict[str, Any]) -> int | None:
    raw = face.get("texture")
    if raw is None or raw is False:
        return None
    try:
        return int(raw)
    except (TypeError, ValueError):
        return None


def convert_face(
    face: dict[str, Any],
    tex_meta: dict[int, dict[str, Any]],
    used_textures: set[int],
) -> dict[str, Any] | None:
    tex_id = texture_index(face)
    if tex_id is None:
        return None

    meta = tex_meta.get(tex_id)
    if meta is None:
        return None

    uv = face.get("uv")
    if not isinstance(uv, list) or len(uv) != 4:
        return None
    try:
        u0, v0, u1, v1 = (float(uv[0]), float(uv[1]), float(uv[2]), float(uv[3]))
    except (TypeError, ValueError):
        return None

    # Blockbench UV space → Minecraft pixel UV for this texture.
    sx = meta["width"] / meta["uv_width"]
    sy = meta["height"] / meta["uv_height"]
    uv_out = [u0 * sx, v0 * sy, u1 * sx, v1 * sy]

    used_textures.add(tex_id)
    out: dict[str, Any] = {"uv": uv_out, "texture": f"#{tex_id}"}

    rotation = face.get("rotation")
    if isinstance(rotation, int) and rotation in (90, 180, 270):
        out["rotation"] = rotation

    tint = face.get("tintindex")
    if isinstance(tint, int):
        out["tintindex"] = tint

    return out


def convert_element(
    element: dict[str, Any],
    tex_meta: dict[int, dict[str, Any]],
    used_textures: set[int],
) -> dict[str, Any] | None:
    if element.get("export") is False:
        return None
    if str(element.get("type") or "cube").lower() not in {"cube", ""}:
        return None

    frm = as_vec3(element.get("from"))
    to = as_vec3(element.get("to"))
    if frm is None or to is None:
        return None

    out: dict[str, Any] = {"from": frm, "to": to, "faces": {}}
    name = element.get("name")
    if isinstance(name, str) and name.strip():
        out["name"] = name.strip()
    if element.get("shade") is False:
        out["shade"] = False

    rotation = convert_rotation(element)
    if rotation is not None:
        out["rotation"] = rotation

    faces_in = element.get("faces") or {}
    faces_out: dict[str, Any] = {}
    if isinstance(faces_in, dict):
        for face_name in FACE_KEYS:
            face = faces_in.get(face_name)
            if not isinstance(face, dict):
                continue
            converted = convert_face(face, tex_meta, used_textures)
            if converted is not None:
                faces_out[face_name] = converted
    out["faces"] = faces_out
    return out


def build_texture_meta(textures: list[dict[str, Any]]) -> dict[int, dict[str, Any]]:
    meta: dict[int, dict[str, Any]] = {}
    for tex in textures:
        try:
            tex_id = int(tex.get("id"))
        except (TypeError, ValueError):
            continue
        width = int(tex.get("width") or 16)
        height = int(tex.get("height") or 16)
        uv_width = float(tex.get("uv_width") or width)
        uv_height = float(tex.get("uv_height") or height)
        if uv_width <= 0:
            uv_width = float(width)
        if uv_height <= 0:
            uv_height = float(height)
        meta[tex_id] = {
            "width": width,
            "height": height,
            "uv_width": uv_width,
            "uv_height": uv_height,
            "particle": bool(tex.get("particle")),
            "source": tex.get("source") or "",
            "name": str(tex.get("name") or f"tex_{tex_id}"),
        }
    return meta


def write_png_from_source(source: str, dest: Path) -> None:
    match = DATA_URL_RE.match(source.strip())
    if not match:
        raise ValueError(f"Texture is not an embedded data URL: {dest.name}")
    raw = base64.b64decode(match.group(2))
    dest.parent.mkdir(parents=True, exist_ok=True)
    dest.write_bytes(raw)


def convert_bbmodel(bbmodel_path: Path, item_id: str, assets_root: Path) -> dict[str, Any]:
    data = json.loads(bbmodel_path.read_text(encoding="utf-8"))
    if not isinstance(data, dict):
        raise ValueError(f"Invalid bbmodel: {bbmodel_path}")

    tex_meta = build_texture_meta(list(data.get("textures") or []))
    used_textures: set[int] = set()

    elements_out: list[dict[str, Any]] = []
    for element in data.get("elements") or []:
        if not isinstance(element, dict):
            continue
        converted = convert_element(element, tex_meta, used_textures)
        if converted is not None:
            elements_out.append(converted)

    if not elements_out:
        raise ValueError(f"No exportable cubes in {bbmodel_path}")
    if not used_textures:
        raise ValueError(f"No textures referenced by cubes in {bbmodel_path}")

    texture_dir = assets_root / "textures" / "block" / item_id
    # Clean previous import for this tier
    if texture_dir.exists():
        for old in texture_dir.glob("*.png"):
            old.unlink()
    texture_dir.mkdir(parents=True, exist_ok=True)

    textures_json: dict[str, str] = {}
    particle_id = None
    for tex_id in sorted(used_textures):
        meta = tex_meta[tex_id]
        png_name = f"{tex_id}.png"
        write_png_from_source(meta["source"], texture_dir / png_name)
        textures_json[str(tex_id)] = f"{MOD_ID}:block/{item_id}/{tex_id}"
        if meta["particle"] and particle_id is None:
            particle_id = tex_id
    if particle_id is None:
        particle_id = min(used_textures)
    textures_json["particle"] = f"{MOD_ID}:block/{item_id}/{particle_id}"

    # texture_size: use project resolution when present (Blockbench), else first texture.
    resolution = data.get("resolution") or {}
    tex_w = int(resolution.get("width") or tex_meta[particle_id]["width"])
    tex_h = int(resolution.get("height") or tex_meta[particle_id]["height"])

    model: dict[str, Any] = {
        "credit": f"Converted from {bbmodel_path.name} by scripts/import_bbmodels.py",
        "texture_size": [tex_w, tex_h],
        "textures": textures_json,
        "elements": elements_out,
    }

    display = data.get("display")
    if isinstance(display, dict) and display:
        model["display"] = display
    else:
        model["display"] = dict(DEFAULT_DISPLAY)

    ambient = data.get("ambientocclusion")
    if isinstance(ambient, bool):
        model["ambientocclusion"] = ambient

    return model


def write_json(path: Path, payload: dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(payload, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def import_tier(source_dir: Path, item_id: str, assets_root: Path) -> None:
    bbmodels = sorted(source_dir.glob("*.bbmodel"))
    if not bbmodels:
        raise FileNotFoundError(f"No .bbmodel in {source_dir}")
    bbmodel = bbmodels[0]

    model = convert_bbmodel(bbmodel, item_id, assets_root)

    block_model_path = assets_root / "models" / "block" / f"{item_id}.json"
    write_json(block_model_path, model)

    # Item uses the block model so icon / slot / hand match the world block.
    item_model = {"parent": f"{MOD_ID}:block/{item_id}"}
    write_json(assets_root / "models" / "item" / f"{item_id}.json", item_model)

    blockstate = {"variants": {"": {"model": f"{MOD_ID}:block/{item_id}"}}}
    write_json(assets_root / "blockstates" / f"{item_id}.json", blockstate)

    print(
        f"[ok] {source_dir.name} -> {item_id} "
        f"({len(model['elements'])} cubes, {len(model['textures']) - 1} textures)"
    )


def main() -> int:
    repo_root = Path(__file__).resolve().parents[1]
    default_source = Path(r"E:\Arquivos_Mods\itens\Backpacks\Backpacks")
    default_assets = repo_root / "src" / "main" / "resources" / "assets" / MOD_ID

    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--source",
        type=Path,
        default=default_source,
        help="Folder containing Base/Tier2/…/Tier5 with NBackPack.bbmodel",
    )
    parser.add_argument(
        "--assets",
        type=Path,
        default=default_assets,
        help="Target assets/nerdbackpacks directory",
    )
    args = parser.parse_args()

    if not args.source.is_dir():
        print(f"Source not found: {args.source}", file=sys.stderr)
        return 1

    # Remove legacy shared placeholder model if present.
    legacy = args.assets / "models" / "block" / "mochila.json"
    if legacy.exists():
        legacy.unlink()
        print(f"[clean] removed {legacy.relative_to(repo_root)}")

    for folder_name, item_id in TIER_MAP.items():
        tier_dir = args.source / folder_name
        if not tier_dir.is_dir():
            print(f"[skip] missing folder {tier_dir}", file=sys.stderr)
            continue
        import_tier(tier_dir, item_id, args.assets)

    print("Done. Re-run the game / resource reload to see the new models.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
