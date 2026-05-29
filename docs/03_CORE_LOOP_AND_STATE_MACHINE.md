# 03_CORE_LOOP_AND_STATE_MACHINE

## State List

```text
BOOT
MAIN_MENU
RUN_INIT
DAWN_REWARD
DAY_BUILD
DUSK_OMEN
DUSK_BARGAIN
NIGHT_INTRO
NIGHT_COMBAT
NIGHT_RESOLUTION
RUN_WIN
RUN_LOSE
META_REWARD
SETTINGS
```

## Required State Contract

Every state implements:

```gdscript
func enter(payload: Dictionary = {}) -> void
func exit() -> void
func update(delta: float) -> void
func can_transition_to(next_state: StringName) -> bool
```

## Transition Table

| Current | Next | Trigger |
|---|---|---|
| BOOT | MAIN_MENU | assets/data/localization loaded |
| MAIN_MENU | RUN_INIT | New Run pressed |
| RUN_INIT | DAWN_REWARD | run data created |
| DAWN_REWARD | DAY_BUILD | reward accepted |
| DAY_BUILD | DUSK_OMEN | Begin Dusk pressed |
| DUSK_OMEN | DUSK_BARGAIN | omen read complete |
| DUSK_BARGAIN | NIGHT_INTRO | bargain accepted/skipped |
| NIGHT_INTRO | NIGHT_COMBAT | countdown complete |
| NIGHT_COMBAT | NIGHT_RESOLUTION | all waves clear or core destroyed |
| NIGHT_RESOLUTION | DAWN_REWARD | night cleared and nights remain |
| NIGHT_RESOLUTION | RUN_WIN | final night cleared |
| NIGHT_RESOLUTION | RUN_LOSE | core destroyed |
| RUN_WIN | META_REWARD | result confirmed |
| RUN_LOSE | META_REWARD | result confirmed |
| META_REWARD | MAIN_MENU | rewards saved |

## Save Points

Save after:

- Run initialized
- Dawn reward accepted
- Day build committed
- Night resolution
- Meta reward applied

Do not save during every combat frame. Combat autosave may be added later only at safe checkpoints.

## Anti-Bug Rules

- Night cannot begin unless wave data exists.
- Day cannot end while build placement is invalid.
- Core HP cannot exceed current max HP.
- Rewards cannot be claimed twice.
- The same enemy wave cannot spawn twice after pause/resume.
