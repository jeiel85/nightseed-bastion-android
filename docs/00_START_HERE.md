# 00_START_HERE - Implementation Entry Point

## Immediate Objective

Create a playable **Nightseed Bastion vertical slice** that proves the full core loop rather than a single isolated mechanic.

The first target is:

```text
Main Menu -> New Run -> Day Build -> Dusk Omen -> Night Defense -> Dawn Reward -> Repeat -> Win/Lose
```

## Recommended First 10 Work Items

1. Create Godot 4.x project in repository root.
2. Add portrait mobile resolution settings.
3. Add autoload singletons from `docs/10_SCENE_SCRIPT_CONTRACTS.md`.
4. Implement `RunStateMachine` with mock screens only.
5. Implement first playable map as blocked-out tile grid.
6. Implement hero movement with touch joystick placeholder.
7. Implement enemy spawner and enemy pathing toward Bastion Core.
8. Implement basic building placement during Day phase.
9. Implement one tower attacking enemies during Night phase.
10. Implement Dawn reward and local save.

## Definition of First Playable

A build is considered first playable only when the player can:

- Start a new run from menu.
- Place at least one building.
- Begin the night.
- Fight enemies with hero movement and one active skill.
- See the Bastion Core take damage.
- Win or lose the night.
- Receive a Dawn reward.
- Continue to the next day.
- Quit and reload progress.

## Development Rule

Do not start content expansion before the core loop exists. No additional heroes, extra maps, or advanced menus until the first playable loop works end-to-end.
