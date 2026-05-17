extends Node
## SaveManager
## Reads and writes player save data to `user://` storage.
## Save schema is documented in docs/11_DATA_SCHEMA.md and migrations in docs/12_SAVE_AND_MIGRATION.md.
##
## Never store save data inside the repository directory.

const SAVE_PATH: String = "user://save_profile.json"

const DEFAULT_PROFILE: Dictionary = {
	"schema_version": 1,
	"app_version": "0.1.0",
	"profile": {
		"seed_ash": 0,
		"unlocked_heroes": ["vagrant_warden"],
		"unlocked_maps": ["moonwell_bastion"],
		"codex_seen": []
	},
	"current_run": null,
	"settings": {
		"language": "ko",
		"camera_shake": true,
		"music_volume": 0.8,
		"sfx_volume": 0.9
	}
}

var loaded_profile: Dictionary = {}

func load_profile() -> Dictionary:
	if not FileAccess.file_exists(SAVE_PATH):
		loaded_profile = DEFAULT_PROFILE.duplicate(true)
		return loaded_profile
	var file := FileAccess.open(SAVE_PATH, FileAccess.READ)
	if file == null:
		push_warning("[SaveManager] cannot open save file, using default")
		loaded_profile = DEFAULT_PROFILE.duplicate(true)
		return loaded_profile
	var text := file.get_as_text()
	file.close()
	var parsed: Variant = JSON.parse_string(text)
	if typeof(parsed) != TYPE_DICTIONARY:
		push_warning("[SaveManager] save file corrupted, using default")
		loaded_profile = DEFAULT_PROFILE.duplicate(true)
		return loaded_profile
	loaded_profile = _migrate_if_needed(parsed)
	EventBus.save_loaded.emit(loaded_profile)
	return loaded_profile

func save_profile() -> bool:
	var file := FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	if file == null:
		push_error("[SaveManager] cannot write save file")
		return false
	file.store_string(JSON.stringify(loaded_profile, "\t"))
	file.close()
	EventBus.save_written.emit("profile")
	return true

func _migrate_if_needed(profile: Dictionary) -> Dictionary:
	# Future migrations should be appended here. Keep older readers working.
	var version: int = int(profile.get("schema_version", 0))
	if version < AppConfig.SAVE_SCHEMA_VERSION:
		push_warning("[SaveManager] save schema v%d -> v%d (no-op stub)" % [version, AppConfig.SAVE_SCHEMA_VERSION])
		profile["schema_version"] = AppConfig.SAVE_SCHEMA_VERSION
		profile["app_version"] = AppConfig.APP_VERSION
	return profile
