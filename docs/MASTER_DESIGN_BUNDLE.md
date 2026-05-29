# Nightseed Bastion - Master Design Bundle

This file is a compact index. The detailed implementation source of truth is the separate documents in `docs/`.

## Identity

- Game: Nightseed Bastion
- Repo: nightseed-bastion
- Code: NSB
- Package: com.jeiel85.nightseedbastion
- Engine: Godot 4.x
- Platform: Android first
- Orientation: Portrait 9:16

## Core Loop

```text
Dawn Reward -> Day Build -> Dusk Omen/Bargain -> Night Defense -> Dawn Reward
```

## Vertical Slice

- 1 map: Moonwell Bastion
- 1 hero: Vagrant Warden
- 7 nights
- 6 buildings
- 5 standard enemies
- 1 boss
- Save/load
- Android APK/AAB pipeline

## Implementation Order

Follow `docs/13_BACKLOG.md` exactly from P0-001 onward.

## Guardrail

This project is Nightseed Bastion only. Do not include Nightseed Survivor design, code, balance, or release assumptions except as separate external brand context when explicitly requested.
