# Art Assets

This folder contains early Nightseed Bastion image assets intended for prototype use and visual direction.

## Prototype Asset Batch - 2026-05-17

Source:

- User-provided generated PNG files from `docs/assets/.temp`.
- Files were renamed and moved into durable runtime asset folders on 2026-05-17.

Usage status:

- Prototype / concept-integrated assets only.
- Not final release art.
- Review IP originality, pixel consistency, transparency, and mobile readability before shipping.

| File | Size | Purpose |
|---|---:|---|
| `ui/ui_header_moon_bastion_frame.png` | 2508x627 | Wide ornate header frame for menu or HUD experiments |
| `ui/ui_panel_portrait_stone_frame.png` | 1024x1536 | Portrait panel frame for reward, bargain, or detail screens |
| `ui/ui_banner_arcane_glow.png` | 2172x724 | Glowing banner frame for phase or omen messaging |
| `ui/ui_banner_stone_plain.png` | 2172x724 | Plain stone banner frame for lower-noise UI labels |
| `ui/ui_icon_nightseed_eye.png` | 1254x1254 | Nightseed / omen icon concept |
| `resources/resource_moonshard_stack.png` | 1254x1254 | Moonshard resource icon concept |
| `resources/resource_nightseed_crystal.png` | 1254x1254 | Nightseed crystal / premium shard concept |
| `buildings/building_watchtower_concept.png` | 1254x1254 | Watchtower building concept |
| `buildings/building_thorn_wall_concept.png` | 1254x1254 | Thorn Wall building concept |

Implementation notes:

- Keep gameplay references data-driven. Do not hardcode these filenames into logic.
- Prefer loading final art through scene/resource definitions once the art pipeline is stable.
- Downscale or redraw into consistent pixel modules before final Android export.
