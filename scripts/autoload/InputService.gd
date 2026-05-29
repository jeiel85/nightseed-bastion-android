extends Node
## InputService
## Abstracts touch / virtual joystick / keyboard so gameplay code only reads
## a single normalized move vector and skill button states.

var move_vector: Vector2 = Vector2.ZERO
var skill_1_pressed: bool = false
var skill_2_pressed: bool = false

func _process(_delta: float) -> void:
	# Desktop keyboard fallback for development.
	var keyboard := Vector2.ZERO
	if Input.is_key_pressed(KEY_A) or Input.is_key_pressed(KEY_LEFT):
		keyboard.x -= 1.0
	if Input.is_key_pressed(KEY_D) or Input.is_key_pressed(KEY_RIGHT):
		keyboard.x += 1.0
	if Input.is_key_pressed(KEY_W) or Input.is_key_pressed(KEY_UP):
		keyboard.y -= 1.0
	if Input.is_key_pressed(KEY_S) or Input.is_key_pressed(KEY_DOWN):
		keyboard.y += 1.0
	if keyboard != Vector2.ZERO:
		move_vector = keyboard.normalized()
	skill_1_pressed = Input.is_key_pressed(KEY_J)
	skill_2_pressed = Input.is_key_pressed(KEY_K)
