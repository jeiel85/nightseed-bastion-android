extends Node
## LocalizationService
## Thin wrapper over Godot's TranslationServer that prefers project-defined CSV.
## CSV sources: localization/strings_ko.csv (primary) and localization/strings_en.csv.
##
## Future: load both into TranslationServer; for now, return the key with a
## fallback marker so missing strings are visible during development.

var current_locale: String = "ko"

func _ready() -> void:
	current_locale = AppConfig.PRIMARY_LOCALE
	TranslationServer.set_locale(current_locale)

func set_locale(locale: String) -> void:
	current_locale = locale
	TranslationServer.set_locale(locale)

func t(key: String) -> String:
	var translated := TranslationServer.translate(key)
	if translated == key:
		return "⟦%s⟧" % key
	return translated
