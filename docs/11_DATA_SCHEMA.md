# 11_DATA_SCHEMA

All gameplay data should be JSON-first where practical.

## ID Rules

- Use lowercase snake_case.
- IDs are immutable after release.
- Display names are localization keys, not hardcoded text.

## Building Schema

```json
{
  "id": "watchtower",
  "name_key": "building.watchtower.name",
  "description_key": "building.watchtower.desc",
  "tags": ["tower", "physical"],
  "base_cost": 35,
  "max_level": 3,
  "levels": [
    {"level": 1, "hp": 45, "damage": 8, "range": 150, "cooldown": 0.9},
    {"level": 2, "hp": 60, "damage": 13, "range": 165, "cooldown": 0.82},
    {"level": 3, "hp": 80, "damage": 19, "range": 180, "cooldown": 0.75}
  ]
}
```

## Enemy Schema

```json
{
  "id": "huskling",
  "name_key": "enemy.huskling.name",
  "role": "swarm",
  "hp": 18,
  "speed": 62,
  "damage": 4,
  "armor": 0,
  "attack_range": 18,
  "attack_cooldown": 1.1,
  "targeting": "core",
  "reward": {"ember": 1}
}
```

## Wave Schema

```json
{
  "map_id": "moonwell_bastion",
  "night_index": 1,
  "waves": [
    {
      "wave_id": "n1_w1",
      "spawn_time": 0.0,
      "lane_id": "east_hollow",
      "enemy_id": "huskling",
      "count": 8,
      "interval": 0.8,
      "modifiers": {}
    }
  ]
}
```

## Save Schema v1

```json
{
  "schema_version": 1,
  "app_version": "0.1.0",
  "profile": {
    "seed_ash": 0,
    "unlocked_heroes": ["vagrant_warden"],
    "unlocked_maps": ["moonwell_bastion"],
    "codex_seen": []
  },
  "current_run": null,
  "settings": {
    "language": "ko",
    "camera_shake": true,
    "music_volume": 0.8,
    "sfx_volume": 0.9
  }
}
```

## Migration Policy

When save schema changes:

1. Add a migration function.
2. Keep previous schema loading.
3. Add a manual test case.
4. Update this document.
5. Record the change in `HISTORY.md` and `CHANGELOG.md` if user-impacting.
