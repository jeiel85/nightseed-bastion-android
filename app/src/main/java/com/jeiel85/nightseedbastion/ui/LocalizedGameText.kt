package com.jeiel85.nightseedbastion.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.jeiel85.nightseedbastion.R
import com.jeiel85.nightseedbastion.data.BuildingType
import com.jeiel85.nightseedbastion.data.EnemyType
import com.jeiel85.nightseedbastion.data.PlayLane
import com.jeiel85.nightseedbastion.data.SlotPosition

@StringRes
private fun BuildingType.nameRes(): Int = when (this) {
    BuildingType.BASTION_CORE -> R.string.building_bastion_core
    BuildingType.MOONWELL -> R.string.building_moonwell
    BuildingType.WATCHTOWER -> R.string.building_watchtower
    BuildingType.EMBER_BRAZIER -> R.string.building_ember_brazier
    BuildingType.THORN_WALL -> R.string.building_thorn_wall
    BuildingType.GRAVE_SNARE -> R.string.building_grave_snare
    BuildingType.BELL_SHRINE -> R.string.building_bell_shrine
}

@StringRes
private fun BuildingType.descRes(): Int = when (this) {
    BuildingType.BASTION_CORE -> R.string.building_bastion_core_desc
    BuildingType.MOONWELL -> R.string.building_moonwell_desc
    BuildingType.WATCHTOWER -> R.string.building_watchtower_desc
    BuildingType.EMBER_BRAZIER -> R.string.building_ember_brazier_desc
    BuildingType.THORN_WALL -> R.string.building_thorn_wall_desc
    BuildingType.GRAVE_SNARE -> R.string.building_grave_snare_desc
    BuildingType.BELL_SHRINE -> R.string.building_bell_shrine_desc
}

@StringRes
private fun EnemyType.nameRes(): Int = when (this) {
    EnemyType.HUSKLING -> R.string.enemy_huskling
    EnemyType.BONE_RUNNER -> R.string.enemy_bone_runner
    EnemyType.LANTERN_EATER -> R.string.enemy_lantern_eater
    EnemyType.GRAVE_BRUTE -> R.string.enemy_grave_brute
    EnemyType.HEX_ARCHER -> R.string.enemy_hex_archer
    EnemyType.NIGHTSEED_HERALD -> R.string.enemy_nightseed_herald
}

@StringRes
private fun EnemyType.descRes(): Int = when (this) {
    EnemyType.HUSKLING -> R.string.enemy_huskling_desc
    EnemyType.BONE_RUNNER -> R.string.enemy_bone_runner_desc
    EnemyType.LANTERN_EATER -> R.string.enemy_lantern_eater_desc
    EnemyType.GRAVE_BRUTE -> R.string.enemy_grave_brute_desc
    EnemyType.HEX_ARCHER -> R.string.enemy_hex_archer_desc
    EnemyType.NIGHTSEED_HERALD -> R.string.enemy_nightseed_herald_desc
}

@StringRes
private fun PlayLane.nameRes(): Int = when (this) {
    PlayLane.LEFT -> R.string.lane_left
    PlayLane.CENTER -> R.string.lane_center
    PlayLane.RIGHT -> R.string.lane_right
}

@StringRes
private fun SlotPosition.nameRes(): Int = when (this) {
    SlotPosition.OUTER -> R.string.slot_outer
    SlotPosition.MID -> R.string.slot_mid
    SlotPosition.INNER -> R.string.slot_inner
}

fun BuildingType.localizedName(context: Context): String = context.getString(nameRes())
fun BuildingType.localizedDescription(context: Context): String = context.getString(descRes())
fun EnemyType.localizedName(context: Context): String = context.getString(nameRes())
fun EnemyType.localizedDescription(context: Context): String = context.getString(descRes())
fun PlayLane.localizedName(context: Context): String = context.getString(nameRes())
fun SlotPosition.localizedName(context: Context): String = context.getString(nameRes())

@Composable
fun BuildingType.localizedName(): String = localizedName(LocalContext.current)

@Composable
fun BuildingType.localizedDescription(): String = localizedDescription(LocalContext.current)

@Composable
fun EnemyType.localizedName(): String = localizedName(LocalContext.current)

@Composable
fun PlayLane.localizedName(): String = localizedName(LocalContext.current)

@Composable
fun SlotPosition.localizedName(): String = localizedName(LocalContext.current)
