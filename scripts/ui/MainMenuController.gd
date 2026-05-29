extends Node
## MainMenuController
## Placeholder main menu. Real menu work happens under P1-007.

func _on_new_run_pressed() -> void:
	RunManager.start_new_run(&"moonwell_bastion", &"vagrant_warden")
	SceneRouter.go_to_game_root()

func _on_quit_pressed() -> void:
	get_tree().quit()
