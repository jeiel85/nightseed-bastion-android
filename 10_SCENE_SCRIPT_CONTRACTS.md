# 10_SCENE_SCRIPT_CONTRACTS

This document is the implementation contract for vibe coding. Keep script names stable unless a decision log entry explains the change.

## Scene Targets

```text
scenes/boot/Boot.tscn
scenes/menu/MainMenu.tscn
scenes/game/GameRoot.tscn
scenes/game/Hero.tscn
scenes/game/Enemy.tscn
scenes/game/Building.tscn
scenes/game/Projectile.tscn
scenes/ui/Hud.tscn
scenes/ui/ChoiceCard.tscn
scenes/ui/BuildCard.tscn
```

## Script Targets

```text
scripts/autoload/AppConfig.gd
scripts/autoload/SceneRouter.gd
scripts/autoload/SaveManager.gd
scripts/autoload/DataRegistry.gd
scripts/autoload/RunManager.gd
scripts/core/RunStateMachine.gd
scripts/core/EventBus.gd
scripts/game/HeroController.gd
scripts/game/EnemyController.gd
scripts/game/BuildingController.gd
scripts/game/WaveDirector.gd
scripts/game/CombatResolver.gd
scripts/game/BuildSystem.gd
scripts/game/OmenSystem.gd
scripts/game/BargainSystem.gd
scripts/game/RewardSystem.gd
scripts/ui/HudController.gd
scripts/ui/BuildPanelController.gd
scripts/ui/ChoicePanelController.gd
```

## `RunStateMachine.gd`

```gdscript
class_name RunStateMachine
extends Node

signal state_changed(previous_state: StringName, next_state: StringName)

var current_state: StringName
var state_payload: Dictionary = {}

func initialize(initial_state: StringName, payload: Dictionary = {}) -> void:
    pass

func transition_to(next_state: StringName, payload: Dictionary = {}) -> bool:
    pass

func can_transition_to(next_state: StringName) -> bool:
    pass
```

## `DataRegistry.gd`

```gdscript
extends Node

var buildings: Dictionary = {}
var enemies: Dictionary = {}
var heroes: Dictionary = {}
var waves: Dictionary = {}
var maps: Dictionary = {}
var bargains: Dictionary = {}

func load_all() -> void:
    pass

func get_building(id: StringName) -> Dictionary:
    return buildings.get(id, {})

func validate_all() -> Array[String]:
    return []
```

## `BuildSystem.gd`

```gdscript
class_name BuildSystem
extends Node

signal building_placed(instance_id: String, building_id: StringName, slot_id: StringName)
signal building_upgraded(instance_id: String, new_level: int)
signal build_failed(reason: String)

func can_place(building_id: StringName, slot_id: StringName) -> bool:
    pass

func place_building(building_id: StringName, slot_id: StringName) -> String:
    pass

func upgrade_building(instance_id: String) -> bool:
    pass
```

## `WaveDirector.gd`

```gdscript
class_name WaveDirector
extends Node

signal wave_started(wave_id: StringName)
signal wave_completed(wave_id: StringName)
signal all_waves_completed(night_index: int)
signal enemy_spawn_requested(enemy_id: StringName, lane_id: StringName, modifiers: Dictionary)

func load_night(map_id: StringName, night_index: int) -> void:
    pass

func start() -> void:
    pass

func stop() -> void:
    pass

func is_complete() -> bool:
    return false
```

## `CombatResolver.gd`

```gdscript
class_name CombatResolver
extends Node

static func calculate_damage(base_damage: int, multiplier: float, armor: int) -> int:
    return max(1, floori(base_damage * multiplier - armor))
```

## Event Bus Signals

```gdscript
signal core_damaged(amount: int, source_id: String)
signal core_destroyed()
signal enemy_killed(enemy_id: StringName, reward: Dictionary)
signal hero_skill_used(skill_id: StringName)
signal night_started(night_index: int)
signal night_completed(night_index: int)
signal run_won()
signal run_lost(reason: String)
```
