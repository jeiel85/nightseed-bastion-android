extends Node
## EventBus
## Project-wide signal hub. Decouples systems so gameplay code does not need
## direct references between unrelated nodes.
##
## Listed signals match docs/10_SCENE_SCRIPT_CONTRACTS.md and must remain stable.

signal core_damaged(amount: int, source_id: String)
signal core_destroyed()
signal enemy_killed(enemy_id: StringName, reward: Dictionary)
signal hero_skill_used(skill_id: StringName)

signal night_started(night_index: int)
signal night_completed(night_index: int)
signal run_won()
signal run_lost(reason: String)

signal state_changed(previous_state: StringName, next_state: StringName)
signal data_validation_completed(error_count: int, errors: Array)
signal save_loaded(profile: Dictionary)
signal save_written(slot: String)
