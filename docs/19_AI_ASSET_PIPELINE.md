# 19_AI_ASSET_PIPELINE

## Asset Workflow

1. Generate rough concepts.
2. Select direction.
3. Redraw or clean manually into production pixel art.
4. Slice into game-ready layers.
5. Name files consistently.
6. Import into Godot with correct filtering settings.
7. Test readability on device.

## Pixel Import Settings

Recommended Godot import settings for pixel art:

- Filter: Off / Nearest
- Mipmaps: Off for UI and small sprites
- Repeat: Disabled unless texture is tileable
- Compression: Lossless or VRAM-safe after visual check

## Naming Convention

```text
ui_button_primary_9slice.png
ui_icon_moonshard_32.png
hero_vagrant_idle_01.png
enemy_huskling_walk_01.png
building_watchtower_lvl1.png
fx_mooncut_slash_01.png
```

## Main Menu Concept Usage

Included file:

```text
assets/concept/main_menu_concept_generated.png
```

Use it as:

- Mood reference
- Composition reference
- Color reference
- Store screenshot inspiration

Do not use it directly as final shipping UI without text, logo, and rights review.
