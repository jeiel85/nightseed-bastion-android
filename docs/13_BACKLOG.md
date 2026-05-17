# 13_BACKLOG

Use this document as the initial GitHub issue source. Convert each item into an issue when implementation begins.

## P0 - Repository and Core Loop

### P0-001 - Create Godot project shell

**Type:** chore  
**Goal:** Initialize Godot 4.x project in repository root.

Acceptance criteria:

- `project.godot` exists.
- Portrait resolution configured.
- Empty boot scene loads.
- `.gitignore` confirmed.
- `HISTORY.md` updated.

### P0-002 - Add autoload managers

**Type:** feat  
**Goal:** Add autoloads defined in `docs/10_SCENE_SCRIPT_CONTRACTS.md`.

Acceptance criteria:

- `AppConfig`, `SceneRouter`, `SaveManager`, `DataRegistry`, `RunManager` autoloads registered.
- Boot scene loads `DataRegistry.load_all()`.
- Missing data produces readable debug error.

### P0-003 - Implement run state machine

**Type:** feat  
**Goal:** Create full phase transition skeleton.

Acceptance criteria:

- All core states exist.
- Invalid transitions are rejected.
- Debug overlay shows current state.
- Manual buttons can step through Day -> Dusk -> Night -> Dawn.

### P0-004 - Load and validate JSON gameplay data

**Type:** feat  
**Goal:** Load `data/*.json` into runtime dictionaries.

Acceptance criteria:

- Buildings, enemies, heroes, maps, waves, bargains load.
- Duplicate IDs fail validation.
- Missing references fail validation.
- Validation result is visible during boot.

### P0-005 - Implement first map blockout

**Type:** feat  
**Goal:** Create `moonwell_bastion` blockout.

Acceptance criteria:

- Core visible.
- 3 lanes visible.
- Build slots visible.
- Enemy spawn points visible in debug mode.

### P0-006 - Implement hero movement

**Type:** feat  
**Goal:** Add Vagrant Warden movement.

Acceptance criteria:

- Touch joystick placeholder works.
- Keyboard fallback works for desktop testing.
- Hero collision does not pass through major blockers.

### P0-007 - Implement enemy lane movement

**Type:** feat  
**Goal:** Spawn enemies and move them along waypoints.

Acceptance criteria:

- Enemies move from lane spawn to core.
- Enemies damage core on contact/attack.
- Core destruction triggers run loss.

### P0-008 - Implement building placement

**Type:** feat  
**Goal:** Place Watchtower and Thorn Wall during Day.

Acceptance criteria:

- Build card selects building.
- Valid slot highlights.
- Cost is deducted.
- Invalid placement shows reason.

### P0-009 - Implement basic tower combat

**Type:** feat  
**Goal:** Watchtower attacks enemies during Night.

Acceptance criteria:

- Tower finds target.
- Damage is applied through `CombatResolver`.
- Enemy death emits reward signal.

### P0-010 - Implement wave director

**Type:** feat  
**Goal:** Spawn Night 1 waves from JSON.

Acceptance criteria:

- Wave timing follows `data/waves.json`.
- All enemies cleared triggers night completion.
- Spawn queue handles pause/resume safely.

## P1 - Vertical Slice Completion

- P1-001: Dusk Omen overlay
- P1-002: Dusk Bargain card selection
- P1-003: Dawn Reward selection
- P1-004: Save/load profile and active run
- P1-005: Nights 1-7 content pass
- P1-006: Nightseed Herald boss prototype
- P1-007: Main menu and settings
- P1-008: Korean/English localization pipeline
- P1-009: Android debug APK export
- P1-010: Internal AAB export checklist

## P2 - Release Candidate

- P2-001: 3 heroes
- P2-002: 3 maps
- P2-003: Meta progression tree
- P2-004: Codex
- P2-005: Audio integration
- P2-006: Performance pass
- P2-007: QA regression checklist
- P2-008: Play Store listing assets
- P2-009: Privacy policy and store questionnaire
- P2-010: v1.0.0 release candidate tag
