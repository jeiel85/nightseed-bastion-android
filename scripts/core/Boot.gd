extends Node
## Boot
## First scene of the application. Loads data, validates content, then
## transitions to the main menu (or stays on a placeholder until P0-007).

@onready var _status_label: Label = $CenterContainer/VBoxContainer/StatusLabel
@onready var _version_label: Label = $CenterContainer/VBoxContainer/VersionLabel

func _ready() -> void:
	if _version_label:
		_version_label.text = "%s v%s" % [AppConfig.APP_NAME, AppConfig.APP_VERSION]
	_status("Loading game data…")
	DataRegistry.load_all()
	SaveManager.load_profile()
	await get_tree().create_timer(0.4).timeout
	if DataRegistry.validation_errors.size() > 0:
		_status("Data validation: %d issue(s) — see output log." % DataRegistry.validation_errors.size())
	else:
		_status("Ready. Tap to continue.")

func _status(text: String) -> void:
	if _status_label:
		_status_label.text = text
	if AppConfig.is_debug_build:
		print("[Boot] " + text)

func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventScreenTouch and event.pressed:
		_continue()
	elif event is InputEventMouseButton and event.pressed:
		_continue()
	elif event is InputEventKey and event.pressed and not event.echo:
		_continue()

func _continue() -> void:
	SceneRouter.go_to_main_menu()
