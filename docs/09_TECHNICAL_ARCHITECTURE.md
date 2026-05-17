# 09_TECHNICAL_ARCHITECTURE

## Engine

Godot 4.x with GDScript.

## Architecture Goals

- Data-driven content.
- Small scenes with clear ownership.
- Autoload managers for cross-scene state.
- Runtime objects created from definitions.
- Save migration from version 1 onward.

## Proposed Autoloads

| Autoload | Responsibility |
|---|---|
| `AppConfig` | version, platform flags, constants |
| `SceneRouter` | scene transitions |
| `SaveManager` | load/save/migration |
| `DataRegistry` | load JSON definitions |
| `AudioBus` | BGM/SFX routing |
| `LocalizationService` | text lookup |
| `RunManager` | current run state |
| `InputService` | touch/gamepad abstraction |

## Runtime Scene Tree

```text
GameRoot
├── WorldRoot
│   ├── MapLayer
│   ├── BuildSlotLayer
│   ├── BuildingLayer
│   ├── EnemyLayer
│   ├── HeroLayer
│   └── VfxLayer
├── UILayer
│   ├── HudController
│   ├── PhaseBanner
│   ├── BuildPanel
│   ├── OmenPanel
│   └── RewardPanel
└── DebugLayer
```

## Data Loading

All JSON files in `data/` are loaded by `DataRegistry` at boot.

Required validation:

- IDs are unique.
- Referenced IDs exist.
- Numeric values are within valid range.
- Localization keys exist.
- Wave files refer to known maps, lanes, and enemies.

## Save Data Location

Use Godot `user://` storage. Do not store saves in repository or external shared folders.

## Performance Targets

| Target | Value |
|---|---:|
| FPS | 60 preferred, 30 minimum |
| Max active enemies vertical slice | 80 |
| Max active projectiles | 120 |
| Main menu load | < 3 sec on mid Android |
| Night start transition | < 1 sec |

## Debug Tools

Add a debug overlay only in debug builds:

- Current phase
- Night index
- Active enemies
- Spawn queue count
- Core HP
- FPS
- Seed

## Dependency Policy

Do not add external plugins until base loop ships. Exceptions require a decision log entry.
