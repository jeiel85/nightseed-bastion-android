# 04_GAME_SYSTEMS_IMPLEMENTATION

## 1. Grid and Build Slots

The first implementation should use explicit build slots rather than freeform placement.

Reasons:

- Faster to ship.
- Better for mobile touch.
- Easier to balance.
- Avoids pathfinding complexity in the first vertical slice.

### Build Slot Fields

```text
slot_id
position
allowed_tags
occupied_building_id
lane_id
is_locked
```

## 2. Building Lifecycle

```text
Available -> Placed -> Upgraded -> Damaged -> Destroyed / Repaired
```

### Building Runtime Fields

```text
instance_id
definition_id
level
hp
max_hp
slot_id
cooldown_remaining
status_effects
```

## 3. Targeting Rules

Initial targeting modes:

- `nearest_to_core`
- `highest_hp`
- `first_in_lane`
- `marked_target`

Default tower priority:

1. Marked target
2. Enemy attacking core
3. Enemy closest to core
4. Lowest HP enemy in range

## 4. Hero Movement

Use a virtual joystick for movement and one/two thumb buttons for skills.

Initial control mode:

- Left lower screen: movement joystick
- Right lower screen: Skill 1
- Right upper of Skill 1: Skill 2, locked until vertical slice stabilizes

## 5. Enemy Movement

First version:

- Lane-based paths with waypoint arrays.
- Enemies follow assigned path to target.
- If a wall blocks a path node, enemy attacks wall based on behavior.

Later version:

- NavigationAgent2D or grid pathfinding if freeform building becomes necessary.

## 6. Combat Calculation

Recommended first formula:

```text
final_damage = max(1, floor(base_damage * damage_multiplier - armor))
```

Status effects:

| Effect | Implementation |
|---|---|
| slow | multiply movement speed |
| burn | periodic damage tick |
| mark | increase tower damage received |
| stun | movement and attack disabled |

## 7. Wave Director

The wave director reads `data/waves.json` and spawns groups based on:

```text
map_id
night_index
wave_index
lane_id
spawn_time
enemy_id
count
interval
modifiers
```

The wave director emits signals:

```gdscript
signal wave_started(wave_id)
signal wave_completed(wave_id)
signal all_waves_completed(night_index)
signal elite_spawned(enemy_instance)
signal boss_spawned(enemy_instance)
```

## 8. Dusk Omen System

Omen visibility levels:

| Level | Revealed Info |
|---|---|
| 0 | lane danger only |
| 1 | enemy family icons |
| 2 | elite/boss warning |
| 3 | exact wave counts and spawn timing |

Bell Shrine increases omen level.

## 9. Dusk Bargain System

Bargains are run modifiers. They must be data-driven.

Runtime modifier examples:

```text
tower_damage_multiplier
core_max_hp_delta
enemy_spawn_rate_multiplier
reward_choice_delta
elite_injection
hero_skill_cooldown_multiplier
```

## 10. Dawn Reward System

Reward types:

- Moonshards
- Repair
- Relic
- Building unlock for this run
- Hero stat buff
- Omen level buff

Dawn reward selection should be built using the same card UI component as relic/bargain choice screens.
