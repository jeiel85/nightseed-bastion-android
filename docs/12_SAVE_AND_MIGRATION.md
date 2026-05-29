# 12_SAVE_AND_MIGRATION

## Save Philosophy

Nightseed Bastion must never casually destroy player progress. Save files are versioned from the first implementation.

## Save Slots

Initial release:

- One profile save.
- One active run save.
- No cloud save until platform integration is explicitly approved.

## Save Timing

Save at safe points only:

- New profile created.
- Run initialized.
- Dawn reward accepted.
- Day build committed.
- Night resolved.
- Meta progression applied.
- Settings changed.

## Failure Behavior

If save fails:

- Show a user-readable error.
- Keep in-memory state.
- Allow retry.
- Log technical detail in debug output.

## Migration Function Contract

```gdscript
func migrate_save(save_data: Dictionary) -> Dictionary:
    var version = int(save_data.get("schema_version", 0))
    if version < 1:
        save_data = migrate_0_to_1(save_data)
    return save_data
```

## Data Loss Stop Condition

Any change that cannot migrate older saves safely must stop and require explicit approval.
