# 16_TESTING_QA_RELEASE

## Local Verification Layers

1. JSON validation.
2. Scene load smoke test.
3. State transition test.
4. Combat formula test.
5. Save/load test.
6. Manual vertical slice playtest.
7. Android debug APK device test.
8. AAB internal test.

## Manual Smoke Test: First Playable

- Launch app.
- Main menu appears.
- Start new run.
- Day phase appears.
- Place Watchtower.
- Start Dusk.
- Read omen.
- Start Night.
- Enemies spawn.
- Tower attacks.
- Hero moves.
- Core can be damaged.
- Night can be won.
- Dawn reward appears.
- Exit and reload.

## Android Device Test Matrix

| Device Class | Required? | Notes |
|---|---|---|
| Mid Samsung phone | Yes | User target device family |
| Low-end Android | Yes before release | Performance baseline |
| Android tablet | Optional | UI scale check |
| Foldable | Optional | Safe area check |

## Release Asset Checklist

Before tagging a release:

- App version matches tag.
- Android versionCode increased.
- CHANGELOG updated.
- HISTORY updated.
- Store release notes updated.
- APK generated for local smoke test.
- AAB generated for Play Console.
- Signing keys are not committed.
- Screenshots are current.
- Privacy policy status is clear.
- If any external SDK exists, store policy disclosures are updated.

## Known Unverified Items in This Pack

- No Godot project has been generated yet.
- No build command has been executed yet.
- No APK/AAB exists yet.
- The concept image is not final production art.
