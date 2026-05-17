extends Node2D
## Mock run controller for P0-003.
## Owns the phase state machine until concrete gameplay scenes replace it.

@onready var _state_label: Label = $UILayer/DebugPanel/MarginContainer/VBoxContainer/StateLabel
@onready var _details_label: Label = $UILayer/DebugPanel/MarginContainer/VBoxContainer/DetailsLabel
@onready var _message_label: Label = $UILayer/CenterContainer/VBoxContainer/MessageLabel
@onready var _next_button: Button = $UILayer/CenterContainer/VBoxContainer/ButtonRow/NextButton
@onready var _clear_button: Button = $UILayer/CenterContainer/VBoxContainer/ButtonRow/ClearNightButton
@onready var _lose_button: Button = $UILayer/CenterContainer/VBoxContainer/ButtonRow/LoseNightButton

var _state_machine: RunStateMachine
var _night_cleared: bool = false

func _ready() -> void:
	_state_machine = RunStateMachine.new()
	_state_machine.name = "RunStateMachine"
	add_child(_state_machine)
	_state_machine.state_changed.connect(_on_state_changed)
	_state_machine.initialize(RunStateMachine.STATE_RUN_INIT, {
		"map_id": RunManager.map_id,
		"hero_id": RunManager.hero_id,
		"seed": RunManager.seed,
	})
	_transition_to(RunStateMachine.STATE_DAWN_REWARD, {"reason": "run_initialized"})

func _transition_to(next_state: StringName, payload: Dictionary = {}) -> bool:
	var accepted := _state_machine.transition_to(next_state, payload)
	if accepted:
		_update_overlay()
	return accepted

func _on_state_changed(_previous_state: StringName, next_state: StringName) -> void:
	match next_state:
		RunStateMachine.STATE_DAWN_REWARD:
			if RunManager.night_index == 0:
				_message_label.text = "Dawn Reward: new run provisions are ready."
			else:
				_message_label.text = "Dawn Reward: night %d cleared." % RunManager.night_index
		RunStateMachine.STATE_DAY_BUILD:
			_message_label.text = "Day Build: mock build planning phase."
		RunStateMachine.STATE_DUSK_OMEN:
			_message_label.text = "Dusk Omen: lane threats previewed."
		RunStateMachine.STATE_DUSK_BARGAIN:
			_message_label.text = "Dusk Bargain: bargain skipped for mock flow."
		RunStateMachine.STATE_NIGHT_INTRO:
			_message_label.text = "Night Intro: countdown placeholder."
		RunStateMachine.STATE_NIGHT_COMBAT:
			RunManager.night_index = max(1, RunManager.night_index + 1)
			_night_cleared = false
			EventBus.night_started.emit(RunManager.night_index)
			_message_label.text = "Night Combat: resolve the mock night."
		RunStateMachine.STATE_NIGHT_RESOLUTION:
			if _night_cleared:
				EventBus.night_completed.emit(RunManager.night_index)
				_message_label.text = "Night Resolution: victory."
			else:
				_message_label.text = "Night Resolution: defeat."
		RunStateMachine.STATE_RUN_WIN:
			RunManager.end_run(true)
			_message_label.text = "Run Win: final night cleared."
		RunStateMachine.STATE_RUN_LOSE:
			if RunManager.active:
				RunManager.end_run(false, "mock_core_destroyed")
			_message_label.text = "Run Lose: Bastion Core destroyed."
		RunStateMachine.STATE_META_REWARD:
			_message_label.text = "Meta Reward: result saved placeholder."
		RunStateMachine.STATE_MAIN_MENU:
			SceneRouter.go_to_main_menu()
	_update_overlay()

func _on_next_button_pressed() -> void:
	match _state_machine.current_state:
		RunStateMachine.STATE_DAWN_REWARD:
			_transition_to(RunStateMachine.STATE_DAY_BUILD, {"reward_id": "mock_supplies"})
		RunStateMachine.STATE_DAY_BUILD:
			_transition_to(RunStateMachine.STATE_DUSK_OMEN)
		RunStateMachine.STATE_DUSK_OMEN:
			_transition_to(RunStateMachine.STATE_DUSK_BARGAIN)
		RunStateMachine.STATE_DUSK_BARGAIN:
			_transition_to(RunStateMachine.STATE_NIGHT_INTRO)
		RunStateMachine.STATE_NIGHT_INTRO:
			_transition_to(RunStateMachine.STATE_NIGHT_COMBAT)
		RunStateMachine.STATE_NIGHT_COMBAT:
			_transition_to(RunStateMachine.STATE_DAWN_REWARD)
		RunStateMachine.STATE_NIGHT_RESOLUTION:
			if _night_cleared and RunManager.night_index >= RunManager.max_nights:
				_transition_to(RunStateMachine.STATE_RUN_WIN)
			elif _night_cleared:
				_transition_to(RunStateMachine.STATE_DAWN_REWARD)
			else:
				_transition_to(RunStateMachine.STATE_RUN_LOSE)
		RunStateMachine.STATE_RUN_WIN, RunStateMachine.STATE_RUN_LOSE:
			_transition_to(RunStateMachine.STATE_META_REWARD)
		RunStateMachine.STATE_META_REWARD:
			_transition_to(RunStateMachine.STATE_MAIN_MENU)
		_:
			push_warning("[GameRoot] no manual transition for %s" % _state_machine.current_state)
	_update_overlay()

func _on_clear_night_button_pressed() -> void:
	if _state_machine.current_state != RunStateMachine.STATE_NIGHT_COMBAT:
		_transition_to(RunStateMachine.STATE_NIGHT_RESOLUTION)
		return
	_night_cleared = true
	_transition_to(RunStateMachine.STATE_NIGHT_RESOLUTION, {"cleared": true})

func _on_lose_night_button_pressed() -> void:
	if _state_machine.current_state != RunStateMachine.STATE_NIGHT_COMBAT:
		_transition_to(RunStateMachine.STATE_RUN_LOSE)
		return
	RunManager.core_hp = 0
	_night_cleared = false
	_transition_to(RunStateMachine.STATE_NIGHT_RESOLUTION, {"cleared": false})

func _update_overlay() -> void:
	if _state_label == null:
		return
	_state_label.text = "State: %s" % _state_machine.current_state
	_details_label.text = "Night: %d/%d | Core: %d/%d | Seed: %d" % [
		RunManager.night_index,
		RunManager.max_nights,
		RunManager.core_hp,
		RunManager.core_hp_max,
		RunManager.seed,
	]
	var in_combat := _state_machine.current_state == RunStateMachine.STATE_NIGHT_COMBAT
	_clear_button.disabled = not in_combat
	_lose_button.disabled = not in_combat
	if in_combat:
		_next_button.disabled = true
	else:
		_next_button.disabled = false
