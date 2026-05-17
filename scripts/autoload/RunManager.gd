extends Node
## RunManager
## Holds the *current* run's transient state. Persistent meta-progression
## lives in SaveManager.profile.
## All run-only resources reset when a run ends (win or lose).

var active: bool = false
var map_id: StringName = &""
var hero_id: StringName = &""
var seed: int = 0

var night_index: int = 0
var max_nights: int = 7

var moonshards: int = 0
var ember: int = 0
var hope: int = 0
var core_hp: int = 0
var core_hp_max: int = 0

func start_new_run(p_map_id: StringName, p_hero_id: StringName, p_seed: int = -1) -> void:
	active = true
	map_id = p_map_id
	hero_id = p_hero_id
	seed = p_seed if p_seed >= 0 else int(Time.get_unix_time_from_system())
	night_index = 0
	var map_def := DataRegistry.get_map(p_map_id)
	max_nights = int(map_def.get("nights", 7))
	moonshards = int(map_def.get("starting_moonshards", 60))
	ember = 0
	hope = 50
	core_hp_max = int(map_def.get("core_hp", 100))
	core_hp = core_hp_max

func end_run(victory: bool, reason: String = "") -> void:
	active = false
	if victory:
		EventBus.run_won.emit()
	else:
		EventBus.run_lost.emit(reason)

func damage_core(amount: int, source_id: String = "") -> void:
	core_hp = max(0, core_hp - amount)
	EventBus.core_damaged.emit(amount, source_id)
	if core_hp == 0:
		EventBus.core_destroyed.emit()
		end_run(false, "core_destroyed")
