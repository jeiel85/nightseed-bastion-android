package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.game.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayBuildScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentNight by viewModel.currentNight.collectAsState()
    val coreHp by viewModel.coreHp.collectAsState()
    val coreMaxHp by viewModel.coreMaxHp.collectAsState()
    val moonshards by viewModel.moonshards.collectAsState()
    val hope by viewModel.hope.collectAsState()
    val placedBuildings by viewModel.placedBuildings.collectAsState()

    var activeDialogSlot by remember { mutableStateOf<Pair<PlayLane, SlotPosition>?>(null) }
    var showCoreRepairAlert by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicBlack, Color(0xFF0F121E))
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Day Phase Top Resource HUD
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NIGHTS SURVIVED: ${currentNight - 1} / 6",
                            color = MoonGold,
                            fontWeight = FontWeight.Bold,
                            size = 12.sp
                        )
                        HorizontalDivider(modifier = Modifier.width(60.dp).height(1.dp).background(MoonGold))
                        Text(
                            text = "DAY PHASE BUILD",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            size = 18.sp
                        )
                    }

                    // Exit Run Button
                    IconButton(
                        onClick = { viewModel.exitToMainMenu() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Exit to Menu", tint = ShadowCrimson)
                    }
                }

                // Core & Wealth Status Block
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Core Life Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B2E)),
                        modifier = Modifier.weight(1f).border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                        onClick = { showCoreRepairAlert = true }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Favorite, contentDescription = "", tint = ShadowCrimson, size = 16.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bastion Core", color = Color.White.copy(alpha = 0.7f), size = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$coreHp / $coreMaxHp", color = Color.White, fontWeight = FontWeight.Bold, size = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { coreHp.toFloat() / coreMaxHp.toFloat() },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                color = ShadowCrimson,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }
                    }

                    // Wealth/Shards Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B2E)),
                        modifier = Modifier.weight(1f).border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "", tint = MoonGold, size = 16.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Moonshards", color = Color.White.copy(alpha = 0.7f), size = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$moonshards Shards", color = MoonGold, fontWeight = FontWeight.Bold, size = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AddCircle, contentDescription = "", tint = CosmicTeal, size = 12.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Morale Hope: $hope", color = CosmicTeal, size = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Grid Map Lanes Selection (Scrollable layout matching columns)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                // Layout visual lanes side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlayLane.entries.forEach { lane ->
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Lane title header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF222944), RoundedCornerShape(6.dp))
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(lane.name, color = CosmicTeal, fontWeight = FontWeight.Bold, size = 11.sp)
                            }

                            // Pos slots in lane
                            SlotPosition.entries.forEach { slotPos ->
                                val key = "${lane.name}_${slotPos.name}"
                                val building = placedBuildings[key]

                                TactSlotWidget(
                                    positionLabel = slotPos.displayName,
                                    building = building,
                                    onClick = { activeDialogSlot = Pair(lane, slotPos) },
                                    modifier = Modifier.testTag("slot_${lane.name}_${slotPos.name}")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button Area (Repair Core, Begin Dusk)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Giant primary Warning Trigger
                Button(
                    onClick = { viewModel.triggerBeginDusk() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("begin_dusk_button")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CosmicBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "INITIATE DUSK WARNING",
                            color = CosmicBlack,
                            fontWeight = FontWeight.ExtraBold,
                            size = 14.sp
                        )
                    }
                }
            }
        }

        // Slot Detail or Deploy Builder Dialog
        activeDialogSlot?.let { (lane, pos) ->
            val key = "${lane.name}_${pos.name}"
            val existing = placedBuildings[key]

            if (existing != null) {
                // Manage existing Building Sheet
                SlotManageDialog(
                    instance = existing,
                    moonshards = moonshards,
                    onUpgrade = {
                        viewModel.upgradeBuilding(lane, pos)
                        activeDialogSlot = null
                    },
                    onRepair = {
                        viewModel.repairBuilding(lane, pos)
                        activeDialogSlot = null
                    },
                    onDemolish = {
                        viewModel.demolishBuilding(lane, pos)
                        activeDialogSlot = null
                    },
                    onDismiss = { activeDialogSlot = null }
                )
            } else {
                // Build options list
                SlotBuildDialog(
                    lane = lane,
                    pos = pos,
                    moonshards = moonshards,
                    onDeploy = { type ->
                        viewModel.buildBuilding(lane, pos, type)
                        activeDialogSlot = null
                    },
                    onDismiss = { activeDialogSlot = null }
                )
            }
        }

        // Dialog for Core Repair Action
        if (showCoreRepairAlert) {
            AlertDialog(
                onDismissRequest = { showCoreRepairAlert = false },
                containerColor = Color(0xFF161B2E),
                title = { Text("Bastion Core Retrofit", color = MoonGold, fontWeight = FontWeight.Bold, size = 18.sp) },
                text = {
                    Text(
                        "Spend 25 Moonshards to perform emergency reinforcement repairing 40 Core HP immediately?",
                        color = Color.White.copy(alpha = 0.8f),
                        size = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.repairCoreProgressive()
                            showCoreRepairAlert = false
                        },
                        enabled = moonshards >= 25 && coreHp < coreMaxHp
                    ) {
                        Text("REPAIR (25 Shards)", color = NeonAmber, fontWeight = FontWeight.Bold, size = 12.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCoreRepairAlert = false }) {
                        Text("CANCEL", color = Color.White.copy(alpha = 0.5f), size = 12.sp)
                    }
                }
            )
        }
    }
}

@Composable
fun TactSlotWidget(
    positionLabel: String,
    building: BuildingInstance?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (building != null) Color(0xFF1B223D) else Color(0xFF111422)
    val strokeColor = if (building != null) CosmicTeal else Color.White.copy(alpha = 0.1f)

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .border(1.dp, strokeColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = positionLabel,
                color = Color.White.copy(alpha = 0.4f),
                size = 9.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (building != null) {
                Icon(
                    imageVector = getBuildingIcon(building.type),
                    contentDescription = "",
                    tint = getBuildingColor(building.type),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "${building.type.displayName} (Lvl ${building.level})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    size = 11.sp,
                    textAlign = TextAlign.Center
                )
                // HP bar
                LinearProgressIndicator(
                    progress = { building.currentHp.toFloat() / building.maxHp.toFloat() },
                    modifier = Modifier.fillMaxWidth(0.85f).height(4.dp).clip(CircleShape),
                    color = CosmicTeal,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            } else {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape).size(30.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Deploy", tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "DEPLOY DEFENSE",
                    color = CosmicTeal.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    size = 10.sp
                )
            }
        }
    }
}

@Composable
fun SlotBuildDialog(
    lane: PlayLane,
    pos: SlotPosition,
    moonshards: Int,
    onDeploy: (BuildingType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161B2E),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        title = {
            Column {
                Text(
                    text = "DEPLOY FORREST LAYER",
                    color = CosmicTeal,
                    fontWeight = FontWeight.Bold,
                    size = 16.sp
                )
                Text(
                    text = "$lane / $pos",
                    color = Color.White.copy(alpha = 0.5f),
                    size = 11.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // List of deployable structural types (Excluding center Bastion core which is un-placed)
                BuildingType.entries.filter { !it.isCore }.forEach { buildType ->
                    val canAfford = moonshards >= buildType.cost

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF222944).copy(alpha = if (canAfford) 1.0f else 0.4f), RoundedCornerShape(8.dp))
                            .clickable(enabled = canAfford) { onDeploy(buildType) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = getBuildingIcon(buildType),
                                contentDescription = "",
                                tint = getBuildingColor(buildType),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(buildType.displayName, color = Color.White, fontWeight = FontWeight.Bold, size = 13.sp)
                                Text(buildType.description, color = Color.White.copy(alpha = 0.5f), size = 10.sp)
                            }
                        }

                        // Cost Card
                        Box(
                            modifier = Modifier
                                .background(if (canAfford) MoonGold else Color.Gray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("${buildType.cost} G", color = CosmicBlack, fontWeight = FontWeight.Bold, size = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = Color.White.copy(alpha = 0.5f), size = 12.sp)
            }
        }
    )
}

@Composable
fun SlotManageDialog(
    instance: BuildingInstance,
    moonshards: Int,
    onUpgrade: () -> Unit,
    onRepair: () -> Unit,
    onDemolish: () -> Unit,
    onDismiss: () -> Unit
) {
    val upgradeCost = instance.getUpgradeCost()
    val maxed = instance.level >= instance.type.maxLevel
    val canUpgrade = moonshards >= upgradeCost && !maxed
    val canRepair = moonshards >= 10 && instance.currentHp < instance.maxHp

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161B2E),
        title = {
            Column {
                Text(
                    text = "${instance.type.displayName} (Level ${instance.level})",
                    color = CosmicTeal,
                    fontWeight = FontWeight.Bold,
                    size = 16.sp
                )
                Text(
                    text = "Vitality durability: ${instance.currentHp} / ${instance.maxHp} HP",
                    color = Color.White.copy(alpha = 0.6f),
                    size = 12.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Command layer operations. Spend Moonshards to reinforce our line defenses or claim scrap refund.",
                    color = Color.White.copy(alpha = 0.5f),
                    size = 11.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Upgrade button
                    Button(
                        onClick = onUpgrade,
                        enabled = canUpgrade,
                        colors = ButtonDefaults.buttonColors(containerColor = MoonGold),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "", tint = CosmicBlack, size = 18.dp)
                            Text(
                                if (maxed) "MAX LEVEL" else "UPGRADE ($upgradeCost G)",
                                color = CosmicBlack,
                                fontWeight = FontWeight.Bold,
                                size = 10.sp
                            )
                        }
                    }

                    // Repair button
                    Button(
                        onClick = onRepair,
                        enabled = canRepair,
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTeal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Build, contentDescription = "", tint = CosmicBlack, size = 18.dp)
                            Text("REPAIR (10 G)", color = CosmicBlack, fontWeight = FontWeight.Bold, size = 10.sp)
                        }
                    }
                }

                // Scrap Demolition button
                Button(
                    onClick = onDemolish,
                    colors = ButtonDefaults.buttonColors(containerColor = ShadowCrimson.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, ShadowCrimson, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "", tint = ShadowCrimson, size = 16.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DEMOLISH (DEMOLISH REFUND: +${instance.type.cost / 2} SHARDS)", color = ShadowCrimson, fontWeight = FontWeight.Bold, size = 11.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = Color.White.copy(alpha = 0.5f), size = 12.sp)
            }
        }
    )
}

fun getBuildingIcon(type: BuildingType): ImageVector {
    return when (type) {
        BuildingType.BASTION_CORE -> Icons.Default.Favorite
        BuildingType.MOONWELL -> Icons.Default.Star
        BuildingType.WATCHTOWER -> Icons.Default.KeyboardArrowUp
        BuildingType.EMBER_BRAZIER -> Icons.Default.ThumbUp
        BuildingType.THORN_WALL -> Icons.Default.Lock
        BuildingType.GRAVE_SNARE -> Icons.Default.Notifications
        BuildingType.BELL_SHRINE -> Icons.Default.Info
    }
}

fun getBuildingColor(type: BuildingType): Color {
    return when (type) {
        BuildingType.BASTION_CORE -> ShadowCrimson
        BuildingType.MOONWELL -> MoonGold
        BuildingType.WATCHTOWER -> CosmicTeal
        BuildingType.EMBER_BRAZIER -> NeonAmber
        BuildingType.THORN_WALL -> Color.LightGray
        BuildingType.GRAVE_SNARE -> Color(0xFFE91E63)
        BuildingType.BELL_SHRINE -> Color(0xFF9C27B0)
    }
}
