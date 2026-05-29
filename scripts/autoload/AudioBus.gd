extends Node
## AudioBus
## Routes music and SFX with project-wide volume control.
## Concrete bus wiring will be added when first SFX asset lands.

var music_volume: float = 0.8
var sfx_volume: float = 0.9

func play_sfx(_stream: AudioStream, _pitch: float = 1.0) -> void:
	# TODO: pool AudioStreamPlayer nodes for shared SFX.
	pass

func play_music(_stream: AudioStream, _fade_in_seconds: float = 0.5) -> void:
	# TODO: crossfade between two stream players.
	pass

func stop_music(_fade_out_seconds: float = 0.5) -> void:
	pass
