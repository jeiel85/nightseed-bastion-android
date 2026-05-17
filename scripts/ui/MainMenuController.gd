extends Node
## MainMenuController
## Placeholder main menu. Real menu work happens under P1-007.

func _on_new_run_pressed() -> void:
	# Real wiring will live in P0-003 / P0-005.
	push_warning("[MainMenu] New Run pressed — RunStateMachine wiring pending.")

func _on_quit_pressed() -> void:
	get_tree().quit()
