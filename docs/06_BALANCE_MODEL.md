# 06_BALANCE_MODEL

## Balance Philosophy

The player should usually lose because of a readable strategic mistake, not hidden math.

Examples of readable mistakes:

- Ignored a lane omen.
- Over-invested in economy.
- Accepted too many risky bargains.
- Failed to handle ranged enemies.
- Left core unrepaired.

## Initial Economy Curve

```text
starting_moonshards = 60
base_dawn_income = 25 + night_index * 8
moonwell_income = 12 + level * 8
perfect_night_bonus = 15
core_damage_penalty = floor(core_damage_taken * 0.2)
```

## Enemy Scaling

```text
enemy_hp_multiplier = 1.0 + (night_index - 1) * 0.18
enemy_damage_multiplier = 1.0 + (night_index - 1) * 0.12
enemy_count_multiplier = 1.0 + (night_index - 1) * 0.10
```

## Tower Upgrade Costs

```text
level_1_cost = base_cost
level_2_cost = round(base_cost * 1.65)
level_3_cost = round(base_cost * 2.45)
```

## Tuning Targets

| Metric | Target |
|---|---:|
| Night 1 clear rate after tutorial | 95%+ |
| First run boss clear rate | 25-40% |
| Average first run length | 10-14 min |
| Average player action per day | 3-6 decisions |
| Time to first meaningful choice | < 45 sec |

## Balance Sheet Columns

When the project moves to spreadsheet balancing, use these columns:

```text
id, display_name, category, base_hp, base_damage, range, cooldown, cost, upgrade_cost_2, upgrade_cost_3, tags, notes
```
