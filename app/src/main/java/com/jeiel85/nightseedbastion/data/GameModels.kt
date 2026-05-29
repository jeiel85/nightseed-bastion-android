package com.jeiel85.nightseedbastion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlayLane(val displayName: String) {
    LEFT("Left Lane"),
    CENTER("Center Lane"),
    RIGHT("Right Lane")
}

enum class SlotPosition(val displayName: String) {
    OUTER("Outer Defense"),
    MID("Mid Line"),
    INNER("Inner Safeguard")
}

enum class BuildingType(
    val id: String,
    val displayName: String,
    val description: String,
    val cost: Int,
    val maxLevel: Int,
    val baseHp: Int,
    val isCore: Boolean = false
) {
    BASTION_CORE("bastion_core", "Bastion Core", "The life force of the Nightseed. If destroyed, the defense fails.", 0, 1, 100, true),
    MOONWELL("moonwell", "Moonwell", "Economy center. Generates +25 Moonshards at dawn.", 30, 3, 80),
    WATCHTOWER("watchtower", "Watchtower", "Defensive tower. Shoots projectiles dealing 15 single-target damage.", 25, 3, 100),
    EMBER_BRAZIER("ember_brazier", "Ember Brazier", "Skill support. Hero near this charges skills 50% faster.", 20, 3, 110),
    THORN_WALL("thorn_wall", "Thorn Wall", "Robust barricade. Blocks enemy movement with high durability.", 15, 3, 220),
    GRAVE_SNARE("grave_snare", "Grave Snare", "Trap. Slows and explodes dealing 40 damage when triggered.", 15, 1, 40),
    BELL_SHRINE("bell_shrine", "Bell Shrine", "Utility detector. Reveals precise dusk omen enemy numbers.", 20, 2, 70);

    companion object {
        fun fromId(id: String): BuildingType = entries.find { it.id == id } ?: WATCHTOWER
    }
}

data class BuildingInstance(
    val id: String,
    val type: BuildingType,
    val lane: PlayLane,
    val position: SlotPosition,
    var level: Int = 1,
    var maxHp: Int = type.baseHp,
    var currentHp: Int = type.baseHp
) {
    fun getUpgradeCost(): Int = type.cost + (level * 15)
    fun getUpgradeHp(): Int = type.baseHp + (level * 25)
}

enum class EnemyType(
    val id: String,
    val displayName: String,
    val description: String,
    val maxHp: Int,
    val baseDamage: Int,
    val baseSpeed: Float, // speed per game tick
    val emberReward: Int,
    val isBoss: Boolean = false
) {
    HUSKLING("huskling", "Huskling", "The common skeletal swarm. Reliable marching fodder.", 30, 3, 0.45f, 2),
    BONE_RUNNER("bone_runner", "Bone Runner", "Extremely agile shade. Bypasses traps' slowing effects.", 20, 2, 0.85f, 3),
    LANTERN_EATER("lantern_eater", "Lantern Eater", "Glow-draining fiend. Emits an aura that cuts skill recovery in half.", 50, 4, 0.4f, 4),
    GRAVE_BRUTE("grave_brute", "Grave Brute", "Monstrous tank. Crushes defensive walls before proceeding.", 120, 12, 0.25f, 6),
    HEX_ARCHER("hex_archer", "Hex Archer", "Speaks death from range. Fires dark arrows onto nearby defenses.", 40, 5, 0.35f, 4),
    NIGHTSEED_HERALD("nightseed_herald", "Nightseed Herald", "Looming Harbinger of Corruption. Summons roots to disable towers.", 600, 25, 0.20f, 25, true);

    companion object {
        fun fromId(id: String): EnemyType = entries.find { it.id == id } ?: HUSKLING
    }
}

data class EnemyInstance(
    val id: String,
    val type: EnemyType,
    var lane: PlayLane,
    var progress: Float = 0f, // 0f to 100f (100f means reaching core)
    var maxHp: Int = type.maxHp,
    var currentHp: Int = type.maxHp,
    var speed: Float = type.baseSpeed,
    var chargeTicks: Int = 0, // Used for attack speed
    var isSlowed: Boolean = false,
    var slowedTimer: Int = 0,
    var isStunned: Boolean = false,
    var stunnedTimer: Int = 0,
    var specialAbilityCooldown: Int = 50 // Hex Archer or Herald special cooldown
)

data class DuskBargain(
    val id: String,
    val title: String,
    val description: String,
    val benefitText: String,
    val costText: String
)

data class DawnRewardOption(
    val id: String,
    val title: String,
    val description: String,
    val costText: String = "Free"
)

// Active Projectile during Battle
data class CombatProjectile(
    val id: String,
    val sourceLane: PlayLane,
    var currentX: Float,
    var currentY: Float,
    val targetEnemyId: String,
    val damage: Int,
    val speed: Float = 15f
)

// Damage popups to animate over Canvas
data class FloatingText(
    val text: String,
    var x: Float,
    var y: Float,
    val colorHex: Long,
    var ticksRemaining: Int = 30 // lasts for 30 ticks (~1 second)
)

@Entity(tableName = "account_state")
data class AccountStateEntity(
    @PrimaryKey val id: Int = 1,
    val seedAsh: Int = 0,
    val accountExp: Int = 0,
    val unlockedUpgradesJson: String = "[]", // Stores comma-separated upgrades bought
    val totalRunsCleared: Int = 0,
    val totalLosses: Int = 0
)

@Entity(tableName = "active_run")
data class ActiveRunEntity(
    @PrimaryKey val id: Int = 1,
    val isRunActive: Boolean = false,
    val currentNight: Int = 1,
    val coreMhp: Int = 100,
    val coreHp: Int = 100,
    val moonshards: Int = 60,
    val ember: Int = 20,
    val hope: Int = 50,
    val heroAtkBonus: Float = 1.0f,
    val towerRangeBonus: Float = 0f,
    val placedBuildingsJson: String = "", // Map of Lane+SlotPosition -> BuildingInstance data
    val activeBargainsJson: String = "[]", // List of IDs of bargains accepted
    val runLogJson: String = "[]"
)
