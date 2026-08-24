# Nice TP Mod

Fabric mod for Minecraft 1.20.1. Adds a **Teleportation Tablet**: an item
that lets a player save up to 12 named locations and teleport back to them.

## Features

- **Save & teleport** — right-click the tablet to open its GUI, name and
  save your current position, then click a saved entry to teleport there.
- **Same-dimension only** — entries from another dimension are shown but
  greyed out; teleporting there is blocked server-side too.
- **XP cost** — each teleport costs 1 experience level (waived in creative).
- **Per-player storage** — waypoints are tied to the player (not the
  item stack), stored server-side and persisted with the world save.
- **Custom held-item model** — a 3D Blockbench model is shown in hand/on
  the ground, while the inventory still uses a flat 2D icon.
- **Crafting recipe** — nether star (center), purple stained glass panes
  (corners), white stained glass panes (edges).
- **Loot** — 20% chance to find a tablet in an End City ship chest.

## Project layout

- `item/` — the `TeleportationTabletItem` and item registration.
- `teleport/` — `Waypoint` data and `WaypointState` (server-side persistence).
- `network/` — client/server packets that keep the GUI in sync with the
  server-authoritative waypoint list.
- `GUI/` — the client-side `Screen` for managing waypoints.
- `loot/` — injects the tablet into vanilla loot tables.
- `mixin/` — renders the custom 3D model for non-GUI render modes.
- `src/main/resources/data/nicetpmod/` — recipe and recipe-unlock advancement.

## Building & running

Requires JDK 17.

```bash
./gradlew build       # compile and produce the mod jar (build/libs)
./gradlew runClient    # launch a dev Minecraft client with the mod loaded
```
