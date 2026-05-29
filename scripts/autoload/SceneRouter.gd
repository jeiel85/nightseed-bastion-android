extends Node
## SceneRouter
## Centralizes scene transitions so individual scenes do not call
## `get_tree().change_scene_to_*` directly.

const PATH_BOOT: String = "res://scenes/boot/Boot.tscn"
const PATH_MAIN_MENU: String = "res://scenes/menu/MainMenu.tscn"
const PATH_GAME_ROOT: String = "res://scenes/game/GameRoot.tscn"

var current_path: String = ""

func go_to(scene_path: String) -> void:
	if scene_path == "":
		push_error("[SceneRouter] empty scene path")
		return
	if not ResourceLoader.exists(scene_path):
		push_warning("[SceneRouter] scene not found yet: %s" % scene_path)
		return
	current_path = scene_path
	get_tree().change_scene_to_file(scene_path)

func go_to_main_menu() -> void:
	go_to(PATH_MAIN_MENU)

func go_to_game_root() -> void:
	go_to(PATH_GAME_ROOT)
