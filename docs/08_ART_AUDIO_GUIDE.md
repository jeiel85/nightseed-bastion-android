# 08_ART_AUDIO_GUIDE

## Visual Direction

Dark fantasy pixel art with a moonlit palette and strong readability.

### Palette Anchors

| Name | Hex | Usage |
|---|---|---|
| Deep Navy | `#0B0E17` | Background, shadows |
| Moonlight | `#DDEBFF` | Highlights, UI text |
| Ember Gold | `#F2C66A` | Reward, fire, positive accents |
| Curse Purple | `#8B4CFF` | Nightseed corruption |
| Blood Ember | `#D95B43` | danger, damage |
| Spirit Cyan | `#7CB8FF` | skill effects, omen detail |

## Pixel Rules

- Prefer native 16x16, 24x24, 32x32, 48x48 modules.
- Avoid anti-aliased mixed-resolution assets in final game art.
- Use bold silhouettes for mobile readability.
- Do not rely on fine decorative detail for gameplay objects.

## Concept Art Disclaimer

`assets/concept/main_menu_concept_generated.png` is a concept reference, not final shippable UI.

Before using in release:

- Redraw title/logo manually or generate controlled title assets separately.
- Verify all text is correct in Korean and English.
- Separate background art, logo, buttons, and icons into layered assets.
- Ensure no UI element imitates another game.

## Prototype Image Asset Inventory

The first prototype image batch lives under `assets/art/` and is documented in `assets/art/README.md`.

Current categories:

- `assets/art/ui/`: ornate frame, panel, banner, and omen icon concepts.
- `assets/art/resources/`: Moonshard and Nightseed crystal resource icon concepts.
- `assets/art/buildings/`: Watchtower and Thorn Wall building concepts.

These assets may be used for early scene mockups, but they are not final release art. Before production use, confirm transparent edges, pixel scale consistency, Korean/English UI fit, and originality against the IP guardrails.

## Audio Direction

- Main menu: low drone, distant bell, soft ember crackle.
- Day: quiet construction, wind, soft strings.
- Dusk: heartbeat-like pulse and omen chime.
- Night: percussion, distorted choir pads, hit impacts.
- Dawn: relief chord, bird/ash wind ambience.

## SFX Priority

1. Button confirm/back
2. Building placed
3. Tower attack
4. Enemy hit/death
5. Core damage warning
6. Wave start
7. Dawn reward
8. Bargain accepted
