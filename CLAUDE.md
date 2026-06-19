# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

A top-down, auto-firing roguelite shooter ("космический рогалик") built on the **Greenfoot** framework. The player auto-targets the nearest enemy, survives waves on a single tiled room, and gains randomly-chosen upgrades on level-up. All UI text is in Russian.

This is a `учебный` (coursework) project — see `TODO.md` for the MVP checklist and stretch goals.

## Build & Run

This is a Greenfoot project, not a Maven/Gradle build. Normal workflow is to **open the folder in the Greenfoot IDE** and press Run; Greenfoot compiles `.java` to `.class` on Compile. `project.greenfoot` defines class targets and dependencies and is managed by the IDE — do not hand-edit it.

To compile from the command line without the IDE you must point `javac` at the Greenfoot jars (the `import greenfoot.*` packages are not on the system classpath):

```bash
javac -cp "/path/to/Greenfoot/lib/*" *.java
```

There is no test suite, linter, or CI in this repo.

`world.lastInstantiated=MainMenuWorld` in `project.greenfoot` is the world Greenfoot opens first.

## Important constraints

- **`MainMenuWorld.java` source is missing** — only `MainMenuWorld.class` exists. It is the startup world and is referenced by `HubWorld`/`MenuButton`, so it cannot be recompiled from source until reconstructed. Be aware that a full `javac *.java` will not produce it.
- `*.class` and `*.ctxt` files are committed to git (no `.gitignore`). After editing a `.java`, the stale `.class` remains until recompiled.
- `images/` and `sounds/` are currently empty — all art is generated procedurally at runtime (see below).

## Architecture

### Worlds (screen flow)
`MainMenuWorld` → `HubWorld` → `MyWorld`. Navigation happens by `Greenfoot.setWorld(new ...World())`, triggered from button actors that call back into their world's `handleAction(String)` method. `MyWorld` is the actual gameplay arena (32×20 grid of 40px `TILE`s).

### Procedural graphics: `SpriteFactory`
There are **no image assets**. `SpriteFactory` is a stateless static utility that draws every sprite, background, wall, and UI panel into `GreenfootImage`s at runtime (ships, drones, alien enemies, deck-floor tiles, button panels). When adding visuals, add a factory method here rather than loading files. `loadOrPlaceholder` / `loadAny` exist to fall back to generated placeholders if real assets are ever dropped in.

### Gameplay loop (`MyWorld`)
- `buildLevel()` constructs a `boolean[][] solid` grid (outer border + hardcoded interior blocks), then spawns a `Wall` actor per solid cell. `Wall.chooseVariant(n,e,s,w)` picks the wall sprite from neighbor adjacency.
- `act()` runs continuous spawning: target enemy count scales with `player.getLevel()`, throttled by `spawnCooldown`. `spawnEnemy()` rejects positions too close to the player or overlapping walls; ~30% of enemies are `ranged`.
- A global pause flag (`isPaused()/setPaused()`) is checked by every actor's `act()` — this is how the level-up menu freezes gameplay.

### Player & combat (`Player`)
The `Player` is the center of the design and holds nearly all combat state:
- **Movement**: WASD/arrows, axis-separated collision against `Wall` using a `px/py` double-precision shadow position (the integer `setLocation` is derived each tick).
- **Auto-fire** (`handleAutoFire`): finds nearest `Enemy`, fires `1 + bonusProjectiles` spread `Bullet`s toward it. Active upgrades layer on extra fire patterns (drone side-shot, `laser_array` piercing beam on cooldown, `nova_pulse` 8-way radial burst).
- **Stats** are plain fields (`damageMult`, `critChance`, `pierceCount`, etc.) mutated directly by `applyUpgradeItem(id)`. `rollDamage()` composes them per shot (berserk, crit, chaos variance).
- **Leveling** is fully automatic: `addXp` → on threshold, `autoApplyLevelUp()` calls `rollUpgradeChoices(3)` and applies one at random. (The interactive `LevelUpMenu`/`LUButton` UI exists but the level-up path in `Player` currently auto-picks rather than pausing for player choice.)
- On `hp <= 0`, `MyWorld.restartLevel()` swaps in a fresh `MyWorld`.

### Upgrade system (`UpgradeSystem` + `Player`)
- `UpgradeSystem` is a **data-only catalog**: an immutable `List<Def> ALL` of upgrade definitions (id, name, Russian description, `Type` ACTIVE/PASSIVE/SPECIAL, `Rarity`, `maxStacks`). It contains no behavior.
- All *effects* live in `Player.applyUpgradeItem(id)`'s `switch` on the id string. **Adding an upgrade requires editing two places**: a `Def` in `UpgradeSystem.ALL` and a matching `case` in `applyUpgradeItem` (and possibly `isSpecialUnlocked`).
- `SPECIAL` upgrades have `baseWeight 0` and are only offered when their unlock condition in `Player.isSpecialUnlocked(id)` is met (e.g. `chain_bullets` needs `scatter_shot` + `ricochet`).
- Offer logic: `rollUpgradeChoices` filters by `canOffer` (max stacks, ACTIVE/PASSIVE slot caps of 6) then `weightedPick` uses rarity `baseWeight` scaled by the player's `luck` stat.

### Other actors
- `Bullet` / `EnemyBullet`: double-precision movement, wall ricochet, pierce, optional smooth homing (`steerToNearestEnemy`).
- `Enemy`: melee chasers vs. `ranged` kiters that maintain distance and fire `EnemyBullet`s. Death awards XP via `player.onEnemyKilled()`.
- `Hud`: HP/XP bars; redrawn only when `markDirty()` is called. Gameplay code signals UI changes through `MyWorld.notifyHit()` → `hud.markDirty()`.
- `StaticImage`: trivial actor that just displays a passed-in `GreenfootImage` (used for menu titles/panels).

## Conventions

- Actors keep a `double px, py` shadow of their position and `setLocation((int)Math.round(...))` each tick for sub-pixel movement; collision is resolved one axis at a time.
- Cooldowns/timers are integer tick counters decremented in `act()` (Greenfoot runs ~60 acts/sec; `simulation.speed=50`).
- User-facing strings are Russian; keep new UI text consistent with the existing tone.
