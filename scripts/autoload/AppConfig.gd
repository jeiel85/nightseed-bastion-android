extends Node
## AppConfig
## App-wide constants, version, build flags, and platform detection.
## Autoload singleton. Stateless on the gameplay side — only read configuration.

const APP_NAME: String = "Nightseed Bastion"
const PROJECT_CODE: String = "NSB"
const APP_VERSION: String = "0.1.0"
const SAVE_SCHEMA_VERSION: int = 1

const PRIMARY_LOCALE: String = "ko"
const FALLBACK_LOCALE: String = "en"

const TARGET_FPS: int = 60
const MIN_FPS: int = 30
const MAX_ACTIVE_ENEMIES: int = 80
const MAX_ACTIVE_PROJECTILES: int = 120

var is_debug_build: bool = OS.is_debug_build()
var is_mobile: bool = false

func _ready() -> void:
	is_mobile = OS.has_feature("mobile") or OS.has_feature("android") or OS.has_feature("ios")
	Engine.max_fps = TARGET_FPS
	if is_debug_build:
		print("[AppConfig] %s v%s · debug=%s mobile=%s" % [APP_NAME, APP_VERSION, is_debug_build, is_mobile])
