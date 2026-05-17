# 02_GAME_DESIGN_DOCUMENT

## 1. High Concept

The player defends the last bastion grown around a corrupted Nightseed. Each day, the player expands the fortress. At dusk, omens reveal incoming threats. At night, the player controls a hero directly while towers, walls, traps, and villagers hold the line.

## 2. Core Loop

```text
Dawn Reward
-> Day Build
-> Dusk Omen + Bargain
-> Night Defense
-> Damage / Victory / Defeat Resolution
-> Dawn Reward
```

## 3. Win and Lose Conditions

### Run Win

Clear the final boss night of the map.

### Run Loss

The Bastion Core reaches 0 HP during Night phase.

### Partial Retention

Even on loss, the player keeps:

- Account-level experience
- Unlocked codex entries
- Some earned shards
- Achievement progress

The player does not keep:

- Temporary buildings
- Temporary run relics
- Day-specific resource state

## 4. Main Resources

| Resource | Used For | Earned From | Persists? |
|---|---|---|---|
| Moonshards | Buildings and upgrades during a run | Dawn, buildings, objectives | Run only |
| Ember | Emergency repairs and hero skill charge | Combat, brazier buildings | Run only |
| Seed Ash | Meta unlocks | Run result, achievements | Account |
| Hope | Morale score affecting villagers | Dawn choices, no-core-damage nights | Run only |

## 5. Phase Design

### Day

The player can:

- Place buildings.
- Upgrade existing buildings.
- Repair walls and core.
- Reposition certain light structures.
- Inspect map lanes.

Day phase has a soft time limit in later difficulties, but the first release can use a manual `Begin Dusk` button.

### Dusk

The player sees:

- Enemy lane previews.
- Special threat icons.
- Boss warning if applicable.
- Optional Dusk Bargain choices.

The player can make final changes using a restricted action budget.

### Night

The player controls the hero while buildings act automatically.

Night goals:

- Defeat incoming waves.
- Protect Bastion Core.
- Prioritize dangerous enemies.
- Use hero skills to patch weak lanes.

### Dawn

The player receives:

- Resource payout.
- One reward choice.
- Repairs if conditions are met.
- Narrative/codex unlocks at milestones.

## 6. First Map: Moonwell Bastion

| Parameter | Value |
|---|---|
| Nights | 7 |
| Boss Night | 7 |
| Lanes | 3 |
| Core HP | 100 |
| Starting Moonshards | 60 |
| Starting Buildings | Core + 2 build slots |
| First Hero | Vagrant Warden |

## 7. First Hero: Vagrant Warden

### Role

Mobile lane fixer. Strong at finishing weakened enemies and interrupting elite units.

### Basic Attack

Short melee arc. Auto-aims to nearest enemy in front cone.

### Skill 1: Mooncut

Dash a short distance and slash enemies in a line.

### Skill 2: Warden's Mark

Mark one elite or boss. Towers deal increased damage to marked target for a short duration.

### Passive: Last Lantern

When the core falls below 30% HP for the first time each night, gain a temporary damage and movement boost.

## 8. First Building Set

| Building | Role | Notes |
|---|---|---|
| Bastion Core | Lose-condition structure | Fixed center objective |
| Moonwell | Economy | Generates Moonshards at Dawn |
| Watchtower | Single-target damage | Reliable anti-elite |
| Ember Brazier | Area support | Charges hero skill faster nearby |
| Thorn Wall | Path blocking / delay | High HP, no attack |
| Grave Snare | Trap | Slows and damages first enemy group |
| Bell Shrine | Utility | Reveals stronger dusk omen details |

## 9. Enemy Set

| Enemy | Role | Behavior |
|---|---|---|
| Huskling | Basic swarm | Moves to core |
| Bone Runner | Fast pressure | Ignores some slow effects |
| Lantern Eater | Support disruptor | Drains brazier/skill charge aura |
| Grave Brute | Tank | Attacks walls first |
| Hex Archer | Ranged threat | Stops outside tower range if possible |
| Nightseed Herald | Boss | Changes lanes and summons roots |

## 10. Dusk Bargain

Each Dusk can offer 0-2 bargains. The player may accept one.

Examples:

| Bargain | Benefit | Cost |
|---|---|---|
| Blood Mortar | Towers gain +25% damage tonight | Core loses 10 max HP this run |
| Hungry Walls | Walls regenerate during night | Enemies spawn +15% faster |
| Ashen Tithe | Gain 40 Moonshards now | Dawn reward choice reduced by 1 |
| Lantern Oath | Hero skill cooldown -20% | Elite enemy appears tonight |

## 11. Release Content Target

Minimum release content target after vertical slice:

- 3 maps
- 3 heroes
- 18 buildings/upgrades
- 18 enemy variants
- 9 bosses or boss modifiers
- 40 relics/bargains
- Meta progression tree
- Korean and English text
- Android AAB internal test and production release pipeline
