# AGENTS.md

This document defines how AI coding agents should work in this repository.

## 1. Project Settings

```text
Project Name: Nightseed Bastion
Project Code: NSB
Repository: https://github.com/jeiel85/nightseed-bastion.git
Main Branch: main
Primary Spec: docs/02_GAME_DESIGN_DOCUMENT.md
Technical Spec: docs/09_TECHNICAL_ARCHITECTURE.md
Task Document: docs/13_BACKLOG.md
Decision Log: docs/20_DECISION_LOG.md
History Document: HISTORY.md
Changelog: CHANGELOG.md
Version Files: project.godot, export_presets.cfg, docs/releases, play_store/release_notes
Build/Test Commands: godot --headless --path . --quit; custom commands to be added after project creation
Release Trigger: tag push
CI System: GitHub Actions
Expected Assets: APK, AAB, ZIP, store release notes
Primary Platform: Android
Future Platform: iOS
Primary Locale: ko
Required Locale: en
```

## 2. Operating Principle

Default behavior is **finish the requested scoped task without unnecessary confirmation**. Ask for confirmation only when a task can cause data loss, security exposure, cost, store-policy risk, license conflict, or irreversible release damage.

## 3. Required Work Order

Before changing code or documents:

```bash
git fetch origin
git checkout main
git pull origin main
git status
```

Then read, in order:

1. `AGENTS.md`
2. `docs/00_START_HERE.md`
3. `docs/02_GAME_DESIGN_DOCUMENT.md`
4. `docs/09_TECHNICAL_ARCHITECTURE.md`
5. `docs/13_BACKLOG.md`
6. `docs/20_DECISION_LOG.md`
7. `HISTORY.md`
8. `CHANGELOG.md`

If `git status` is not clean, do not overwrite user changes.

## 4. Scope Control

Work on one backlog item at a time. Do not perform broad refactors, unrelated UI redesigns, dependency migrations, or file cleanups unless the backlog item explicitly requires them.

## 5. Implementation Rules

- Keep gameplay logic separate from UI.
- Keep data definitions in `data/*.json` where possible.
- Avoid hardcoding names, stats, upgrade values, and level tables.
- Use deterministic seeds for simulation tests.
- Design save data with versioning from the beginning.
- Prefer small scripts with clear responsibilities over large scene scripts.
- Add comments only where the code would be hard to understand without them.

## 6. IP and Clone Guardrails

Do not copy any existing game's exact:

- title or naming pattern
- iconography
- color identity
- map layout
- building list
- upgrade text
- screen composition
- unit silhouettes
- economy values
- marketing claims

Reference genre patterns only. Implement original Nightseed-specific systems, names, tuning, presentation, UI, and content.

## 7. Approval Required

Stop and report before doing any of these:

- `git reset --hard`
- `git clean -fd`
- `git push --force`
- remote branch or tag deletion
- release key / keystore / certificate changes
- analytics, ads, login, payment, remote config, or crash reporting SDK addition
- network permissions
- collection or transmission of user data
- store submission using real credentials
- destructive save-data migration

## 8. Documentation Rules

Any gameplay, architecture, release, or content change must update the relevant documents:

- `HISTORY.md` for work history
- `CHANGELOG.md` for public-facing change summary
- `docs/13_BACKLOG.md` for task state
- `docs/20_DECISION_LOG.md` for meaningful technical/product decisions
- `docs/11_DATA_SCHEMA.md` if JSON/save schema changes

Never record a test, build, export, or CI status as successful unless it was actually executed.

## 9. Commit Rules

Use Korean commit summaries by default:

```bash
git add <files>
git commit -m "feat: 낮/밤 상태 머신 추가"
git push origin <branch>
```

Allowed prefixes:

- `feat:` feature
- `fix:` bug fix
- `docs:` documentation
- `refactor:` structure change without behavior change
- `test:` tests
- `chore:` project or build setup
- `balance:` gameplay tuning
- `art:` art or asset integration

## 10. Final Report Format

```text
작업 요약:
- 

변경 파일:
- 

검증:
- 로컬:
- CI:
- 생략한 검증:

커밋:
- 

푸시:
- 

후속 작업:
- 
```
