extends Node
## DataRegistry
## Loads all JSON gameplay definitions from `data/` at boot and validates them.
## Other systems read content through getters here — never re-parse JSON elsewhere.

const DATA_FILES: Dictionary = {
	"buildings": "res://data/buildings.json",
	"enemies": "res://data/enemies.json",
	"heroes": "res://data/heroes.json",
	"maps": "res://data/maps.json",
	"waves": "res://data/waves.json",
	"bargains": "res://data/bargains.json",
	"rewards": "res://data/rewards.json",
}

var buildings: Dictionary = {}
var enemies: Dictionary = {}
var heroes: Dictionary = {}
var maps: Dictionary = {}
var waves: Dictionary = {}
var bargains: Dictionary = {}
var rewards: Dictionary = {}

var validation_errors: Array[String] = []

func load_all() -> void:
	buildings = _load_collection(DATA_FILES["buildings"], "buildings", "id")
	enemies   = _load_collection(DATA_FILES["enemies"],   "enemies",   "id")
	heroes    = _load_collection(DATA_FILES["heroes"],    "heroes",    "id")
	maps      = _load_collection(DATA_FILES["maps"],      "maps",      "id")
	bargains  = _load_collection(DATA_FILES["bargains"],  "bargains",  "id")
	rewards   = _load_collection(DATA_FILES["rewards"],   "rewards",   "id")
	waves     = _load_raw(DATA_FILES["waves"])
	validation_errors = validate_all()
	EventBus.data_validation_completed.emit(validation_errors.size(), validation_errors)
	if validation_errors.size() > 0:
		push_warning("[DataRegistry] %d validation issue(s):" % validation_errors.size())
		for err in validation_errors:
			push_warning("  - " + err)
	elif AppConfig.is_debug_build:
		print("[DataRegistry] all data loaded · buildings=%d enemies=%d heroes=%d maps=%d" % [
			buildings.size(), enemies.size(), heroes.size(), maps.size()
		])

func validate_all() -> Array[String]:
	var errors: Array[String] = []
	# Wave files must reference known maps, lanes, and enemies.
	if typeof(waves) == TYPE_DICTIONARY and waves.has("nights"):
		for night in waves["nights"]:
			if not maps.has(StringName(night.get("map_id", ""))):
				errors.append("waves: unknown map_id '%s'" % night.get("map_id", ""))
			for w in night.get("waves", []):
				if not enemies.has(StringName(w.get("enemy_id", ""))):
					errors.append("waves: unknown enemy_id '%s'" % w.get("enemy_id", ""))
	return errors

func get_building(id: StringName) -> Dictionary:
	return buildings.get(id, {})

func get_enemy(id: StringName) -> Dictionary:
	return enemies.get(id, {})

func get_hero(id: StringName) -> Dictionary:
	return heroes.get(id, {})

func get_map(id: StringName) -> Dictionary:
	return maps.get(id, {})

func _load_raw(path: String) -> Dictionary:
	if not FileAccess.file_exists(path):
		validation_errors.append("missing data file: %s" % path)
		return {}
	var file := FileAccess.open(path, FileAccess.READ)
	if file == null:
		validation_errors.append("cannot open: %s" % path)
		return {}
	var text := file.get_as_text()
	file.close()
	var parsed: Variant = JSON.parse_string(text)
	if typeof(parsed) != TYPE_DICTIONARY:
		validation_errors.append("invalid JSON object: %s" % path)
		return {}
	return parsed

func _load_collection(path: String, root_key: String, id_field: String) -> Dictionary:
	var raw := _load_raw(path)
	var result: Dictionary = {}
	if not raw.has(root_key):
		validation_errors.append("'%s' missing root key '%s'" % [path, root_key])
		return result
	for entry in raw[root_key]:
		var entry_id_raw: String = entry.get(id_field, "")
		if entry_id_raw == "":
			validation_errors.append("'%s' entry missing '%s'" % [path, id_field])
			continue
		var key := StringName(entry_id_raw)
		if result.has(key):
			validation_errors.append("'%s' duplicate id '%s'" % [path, entry_id_raw])
			continue
		result[key] = entry
	return result
