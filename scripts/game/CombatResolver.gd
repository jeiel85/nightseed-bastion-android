class_name CombatResolver
extends Node
## CombatResolver
## Pure-function damage math used by towers, hero attacks, and traps.
## Keep this script free of node references so it can be unit tested headlessly.

static func calculate_damage(base_damage: int, multiplier: float = 1.0, armor: int = 0) -> int:
	return max(1, floori(base_damage * multiplier - armor))
