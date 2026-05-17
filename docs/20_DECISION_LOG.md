# 20_DECISION_LOG

## 2026-05-17 - Android-first Godot project

Decision:

- Build Nightseed Bastion as Android-first using Godot 4.x and GDScript.

Reason:

- User has Windows/Galaxy development environment and Android device access.
- Godot is suitable for 2D pixel games and solo development.
- Android-first release is more realistic than immediate iOS release without Mac/iPhone workflow.

Status: Accepted

## 2026-05-17 - Build-slot placement for first version

Decision:

- Use fixed build slots for the first implementation instead of freeform RTS building placement.

Reason:

- Faster to implement.
- Easier touch UX.
- Easier balance and lane readability.
- Avoids early pathfinding complexity.

Status: Accepted

## 2026-05-17 - No forced ads in initial design

Decision:

- Do not design forced ads into the core loop for the first release plan.

Reason:

- Tactical premium feel is a core product goal.
- Forced ads can damage retention and trust in strategy games.
- Store and SDK complexity should be avoided until the core game is validated.

Status: Accepted

## 2026-05-17 - GitHub Pages from /docs branch=main

Decision:

- Serve the public project site from `/docs` on the `main` branch using GitHub Pages with the `pages-themes/cayman` remote theme.

Reason:

- All design markdown already lives in `/docs`; Pages can render them with zero duplication.
- Remote theme keeps the repository free of vendored Jekyll assets.
- Single source of truth for both contributors (README) and external readers (Pages).

Status: Accepted

## 2026-05-17 - Initial Godot 4.x project shell created

Decision:

- Land the Godot project skeleton (`project.godot` + autoload stubs + Boot/MainMenu scenes) together with the design package so P0-001 starts from a verifiable baseline.

Reason:

- AGENTS.md and `docs/13_BACKLOG.md` make P0-001 the first task; landing the shell now means subsequent backlog items only add behavior, not structural setup.
- Autoload contracts in `docs/10_SCENE_SCRIPT_CONTRACTS.md` are now executable code, not just specification text.

Status: Accepted
