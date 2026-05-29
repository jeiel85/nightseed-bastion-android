# Smoke Test · Boot Flow

Run this manual checklist after any changes to boot/autoload/data scripts.

## Steps

1. Open the project in Godot 4.x.
2. Run the main scene (F5).
3. Watch the bottom status label.

## Expected

- [ ] Window opens in portrait orientation.
- [ ] Title "Nightseed Bastion" is centered.
- [ ] Status shows "Loading game data…" then "Ready. Tap to continue." within ~1s.
- [ ] Output panel prints `[AppConfig]` line.
- [ ] Output panel prints `[DataRegistry] all data loaded · …`.
- [ ] No `push_error` lines in the output panel.
- [ ] Tapping / pressing any key routes to the main menu placeholder.

## Failure Notes

If `DataRegistry` reports validation issues, look at the warnings in the output panel — they tell you which file/id is wrong before any gameplay runs.
