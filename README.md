# NerdBackpacks

Tiered backpacks for **Minecraft 1.20.1 (Forge)** with a vanilla-style inventory, wearable models, and optional Curios support.

---

## Overview

**NerdBackpacks** adds five upgradeable backpacks that grow by **18 slots** each tier (two extra chest rows). You can hold them, place them in the world, wear them on the chestplate slot, or equip them through Curios when that mod is installed.

Contents are stored on the item (and on the block when placed), so upgrades keep your inventory.

| Tier | Item | Slots |
|------|------|-------|
| I | Backpack Level I | 27 |
| II | Backpack Level II | 45 |
| III | Backpack Level III | 63 |
| IV | Backpack Level IV | 81 |
| V | Backpack Level V | 99 |

---

## Requirements

| | |
|---|---|
| **Minecraft** | 1.20.1 |
| **Loader** | Forge 47+ |
| **Hard dependencies** | None beyond Forge |

### Optional integrations

| Mod | What it adds |
|-----|----------------|
| **Curios** | Equip on `back` / `chest` slots; on-body render |
| **JEI** | Shows backpack crafting / upgrade recipes |
| **Jade** | Shows placed backpack contents (hold Shift) |
| **Mouse Tweaks** | Drag-and-drop in the backpack GUI (scroll wheel sorting is disabled so backpack scrolling works) |

---

## How to use

### Crafting

1. Craft a **Sack** (`saco`) from string and leather.
2. Craft **Backpack Level I**, then upgrade through the crafting table using the previous backpack + materials.
3. Upgrading **preserves** the backpack’s stored items (NBT).

### Inventory GUI

- Right-click a backpack in your hand to open it.
- Larger backpacks (IV–V) use a **scrollbar** and mouse wheel (up to 7 visible rows).
- **Middle-click** over the backpack area to **sort** (A→Z, full stacks first).
- Backpacks cannot be placed inside other backpacks.

### Place & pick up

- **Shift + right-click** with a backpack to **place** it in the world.
- **Right-click** a placed backpack to open it.
- **Shift + right-click** a placed backpack to **pick it up** (inventory is kept on the item).

### Wear & open equipped

- Equip in the vanilla **chestplate** slot, **or** in Curios **Back** / **Chest**.
- Only **one** backpack can be worn at a time (chest **or** Curios, not both / not two Curios slots).
- Press **B** (default) to open the equipped backpack — remappable under *Controls → Nerd Backpacks*.
- Empty-hand right-click in the air also opens the worn backpack.

Placed backpacks use a **model-fitting hitbox** (not a full block). Worn backpacks render on the player’s back.

---

## Languages

- English (`en_us`)
- Portuguese — Brazil (`pt_br`)

---

## Version

Current release: **1.0.0** — see [`changelog/1.0.0.md`](changelog/1.0.0.md).

---

## License

See `mods.toml` / project license field (`All Rights Reserved` unless otherwise stated by the author).
