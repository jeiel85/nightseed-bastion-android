class_name RunStateMachine
extends Node
## RunStateMachine
## Phase state machine for the Day / Dusk / Night / Dawn run flow.
## States and transitions follow docs/03_CORE_LOOP_AND_STATE_MACHINE.md.
## Invalid transitions are rejected and logged.

signal state_changed(previous_state: StringName, next_state: StringName)

const STATE_BOOT: StringName = &"BOOT"
const STATE_MAIN_MENU: StringName = &"MAIN_MENU"
const STATE_RUN_INIT: StringName = &"RUN_INIT"
const STATE_DAWN_REWARD: StringName = &"DAWN_REWARD"
const STATE_DAY_BUILD: StringName = &"DAY_BUILD"
const STATE_DUSK_OMEN: StringName = &"DUSK_OMEN"
const STATE_DUSK_BARGAIN: StringName = &"DUSK_BARGAIN"
const STATE_NIGHT_INTRO: StringName = &"NIGHT_INTRO"
const STATE_NIGHT_COMBAT: StringName = &"NIGHT_COMBAT"
const STATE_NIGHT_RESOLUTION: StringName = &"NIGHT_RESOLUTION"
const STATE_RUN_WIN: StringName = &"RUN_WIN"
const STATE_RUN_LOSE: StringName = &"RUN_LOSE"
const STATE_META_REWARD: StringName = &"META_REWARD"
const STATE_SETTINGS: StringName = &"SETTINGS"

const _TRANSITIONS: Dictionary = {
	&"BOOT": [&"MAIN_MENU"],
	&"MAIN_MENU": [&"RUN_INIT", &"SETTINGS"],
	&"SETTINGS": [&"MAIN_MENU"],
	&"RUN_INIT": [&"DAWN_REWARD"],
	&"DAWN_REWARD": [&"DAY_BUILD"],
	&"DAY_BUILD": [&"DUSK_OMEN"],
	&"DUSK_OMEN": [&"DUSK_BARGAIN"],
	&"DUSK_BARGAIN": [&"NIGHT_INTRO"],
	&"NIGHT_INTRO": [&"NIGHT_COMBAT"],
	&"NIGHT_COMBAT": [&"NIGHT_RESOLUTION"],
	&"NIGHT_RESOLUTION": [&"DAWN_REWARD", &"RUN_WIN", &"RUN_LOSE"],
	&"RUN_WIN": [&"META_REWARD"],
	&"RUN_LOSE": [&"META_REWARD"],
	&"META_REWARD": [&"MAIN_MENU"],
}

var current_state: StringName = STATE_BOOT
var state_payload: Dictionary = {}

func initialize(initial_state: StringName, payload: Dictionary = {}) -> void:
	current_state = initial_state
	state_payload = payload
	state_changed.emit(STATE_BOOT, initial_state)
	EventBus.state_changed.emit(STATE_BOOT, initial_state)

func transition_to(next_state: StringName, payload: Dictionary = {}) -> bool:
	if not can_transition_to(next_state):
		push_warning("[RunStateMachine] illegal transition %s -> %s" % [current_state, next_state])
		return false
	var prev := current_state
	current_state = next_state
	state_payload = payload
	state_changed.emit(prev, next_state)
	EventBus.state_changed.emit(prev, next_state)
	return true

func can_transition_to(next_state: StringName) -> bool:
	var allowed: Array = _TRANSITIONS.get(current_state, [])
	return allowed.has(next_state)
