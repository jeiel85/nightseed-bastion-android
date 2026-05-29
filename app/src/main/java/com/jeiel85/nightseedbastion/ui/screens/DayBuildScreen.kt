package com.jeiel85.nightseedbastion.ui.screens

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
import com.jeiel85.nightseedbastion.data.*
import com.jeiel85.nightseedbastion.game.GameViewModel
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import android.content.res.Configuration
import com.jeiel85.nightseedbastion.R
import com.jeiel85.nightseedbastion.ui.localizedDescription
import com.jeiel85.nightseedbastion.ui.localizedName

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

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var activeDialogSlot by remember { mutableStateOf<Pair<PlayLane, SlotPosition>?>(null) }
    var showCoreRepairAlert by remember { mutableStateOf(false) }

    // Floating stardust background transition
    val infiniteTransition = rememberInfiniteTransition(label = "stardust")
    val starOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicBlack, Color(0xFF0F121E))
                )
            )
            .drawBehind {
                val rand = kotlin.random.Random(88)
                for (i in 0..25) {
                    val starX = rand.nextFloat() * size.width
                    val starY = (rand.nextFloat() * size.height - starOffset) % size.height
                    drawCircle(
                        color = MoonGold.copy(alpha = 0.28f),
                        radius = rand.nextFloat() * 2.5f + 1f,
                        center = Offset(starX, if (starY < 0) starY + size.height else starY)
                    )
                }
            }
            .systemBarsPadding()
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Builder HUD & Actions
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.nights_survived, currentNight - 1),
                                    color = MoonGold,
                                    fontWeight = FontWeight.Bold,
                                    size = 11.sp
                                )
                                Text(
                                    text = stringResource(R.string.day_build),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    size = 16.sp
                                )
                            }
                            IconButton(
                                onClick = { viewModel.exitToMainMenu() },
                                modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape).size(32.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.exit), tint = ShadowCrimson, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Stack HUD Cards
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Core
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B2E)),
                                modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                                onClick = { showCoreRepairAlert = true }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Favorite, contentDescription = "", tint = ShadowCrimson, size = 14.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(stringResource(R.string.core_life), color = Color.White.copy(alpha = 0.7f), size = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("$coreHp / $coreMaxHp", color = Color.White, fontWeight = FontWeight.Bold, size = 13.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    LinearProgressIndicator(
                                        progress = { coreHp.toFloat() / coreMaxHp.toFloat() },
                                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                        color = ShadowCrimson,
                                        trackColor = Color.White.copy(alpha = 0.1f)
                                    )
                                }
                            }

                            // Moonshards
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B2E)),
                                modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "", tint = MoonGold, size = 14.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(stringResource(R.string.moonshards), color = Color.White.copy(alpha = 0.7f), size = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(stringResource(R.string.shards_amount, moonshards), color = MoonGold, fontWeight = FontWeight.Bold, size = 13.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AddCircle, contentDescription = "", tint = CosmicTeal, size = 11.dp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.hope_amount, hope), color = CosmicTeal, size = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Dusk action button at bottom of left column
                    Button(
                        onClick = { viewModel.triggerBeginDusk() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("begin_dusk_button")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = CosmicBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                stringResource(R.string.initiate_dusk),
                                color = CosmicBlack,
                                fontWeight = FontWeight.ExtraBold,
                                size = 12.sp
                            )
                        }
                    }
                }

                // Right Column: Grid Map lanes
                Column(
                    modifier = Modifier
                        .weight(2.2f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlayLane.entries.forEach { lane ->
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF222944), RoundedCornerShape(6.dp))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(lane.localizedName(), color = CosmicTeal, fontWeight = FontWeight.Bold, size = 10.sp)
                                }

                                SlotPosition.entries.forEach { slotPos ->
                                    val key = "${lane.name}_${slotPos.name}"
                                    val building = placedBuildings[key]

                                    TactSlotWidget(
                                        positionLabel = slotPos.localizedName(),
                                        building = building,
                                        onClick = { activeDialogSlot = Pair(lane, slotPos) },
                                        modifier = Modifier.testTag("slot_${lane.name}_${slotPos.name}")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
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
                                text = stringResource(R.string.nights_survived, currentNight - 1),
                                color = MoonGold,
                                fontWeight = FontWeight.Bold,
                                size = 12.sp
                            )
                            HorizontalDivider(modifier = Modifier.width(60.dp).height(1.dp).background(MoonGold))
                            Text(
                                text = stringResource(R.string.day_phase_build),
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
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.exit_to_menu), tint = ShadowCrimson)
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
                                    Text(stringResource(R.string.building_bastion_core), color = Color.White.copy(alpha = 0.7f), size = 11.sp, fontWeight = FontWeight.SemiBold)
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
                                    Text(stringResource(R.string.moonshards), color = Color.White.copy(alpha = 0.7f), size = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(stringResource(R.string.shards_amount, moonshards), color = MoonGold, fontWeight = FontWeight.Bold, size = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AddCircle, contentDescription = "", tint = CosmicTeal, size = 12.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.morale_hope_amount, hope), color = CosmicTeal, size = 10.sp, fontWeight = FontWeight.Bold)
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
                                    Text(lane.localizedName(), color = CosmicTeal, fontWeight = FontWeight.Bold, size = 11.sp)
                                }

                                // Pos slots in lane
                                SlotPosition.entries.forEach { slotPos ->
                                    val key = "${lane.name}_${slotPos.name}"
                                    val building = placedBuildings[key]

                                    TactSlotWidget(
                                        positionLabel = slotPos.localizedName(),
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
                                stringResource(R.string.initiate_dusk_warning),
                                color = CosmicBlack,
                                fontWeight = FontWeight.ExtraBold,
                                size = 14.sp
                            )
                        }
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
                title = { Text(stringResource(R.string.core_retrofit_title), color = MoonGold, fontWeight = FontWeight.Bold, size = 18.sp) },
                text = {
                    Text(
                        stringResource(R.string.core_retrofit_desc),
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
                        Text(stringResource(R.string.repair_25_shards), color = NeonAmber, fontWeight = FontWeight.Bold, size = 12.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCoreRepairAlert = false }) {
                        Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.5f), size = 12.sp)
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
    val infiniteTransition = rememberInfiniteTransition(label = "slot_pulse")
    val emptyPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emptyPulseAlpha"
    )

    // Glassmorphic properties
    val containerColor = if (building != null) Color(0xE01B223D) else Color(0x75111422)
    val strokeColor = if (building != null) getBuildingColor(building.type).copy(alpha = 0.65f) else CosmicTeal.copy(alpha = emptyPulseAlpha)
    val borderWidth = if (building != null) 1.5.dp else 1.dp

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .border(borderWidth, strokeColor, RoundedCornerShape(10.dp))
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
                color = Color.White.copy(alpha = 0.45f),
                size = 9.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (building != null) {
                // Glow badge behind building icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(getBuildingColor(building.type).copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getBuildingIcon(building.type),
                        contentDescription = "",
                        tint = getBuildingColor(building.type),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = stringResource(R.string.building_level, building.type.localizedName(), building.level),
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
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.deploy), tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = stringResource(R.string.deploy_defense),
                    color = CosmicTeal.copy(alpha = 0.65f),
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
                    text = stringResource(R.string.deploy_forest_layer),
                    color = CosmicTeal,
                    fontWeight = FontWeight.Bold,
                    size = 16.sp
                )
                Text(
                    text = stringResource(R.string.slot_location, lane.localizedName(), pos.localizedName()),
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
                                Text(buildType.localizedName(), color = Color.White, fontWeight = FontWeight.Bold, size = 13.sp)
                                Text(buildType.localizedDescription(), color = Color.White.copy(alpha = 0.5f), size = 10.sp)
                            }
                        }

                        // Cost Card
                        Box(
                            modifier = Modifier
                                .background(if (canAfford) MoonGold else Color.Gray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(stringResource(R.string.gold_cost, buildType.cost), color = CosmicBlack, fontWeight = FontWeight.Bold, size = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close), color = Color.White.copy(alpha = 0.5f), size = 12.sp)
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
                    text = stringResource(R.string.building_level_full, instance.type.localizedName(), instance.level),
                    color = CosmicTeal,
                    fontWeight = FontWeight.Bold,
                    size = 16.sp
                )
                Text(
                    text = stringResource(R.string.vitality_durability, instance.currentHp, instance.maxHp),
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
                    stringResource(R.string.manage_building_desc),
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
                                if (maxed) stringResource(R.string.max_level) else stringResource(R.string.upgrade_cost, upgradeCost),
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
                            Text(stringResource(R.string.repair_cost), color = CosmicBlack, fontWeight = FontWeight.Bold, size = 10.sp)
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
                    Text(stringResource(R.string.demolish_refund, instance.type.cost / 2), color = ShadowCrimson, fontWeight = FontWeight.Bold, size = 11.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close), color = Color.White.copy(alpha = 0.5f), size = 12.sp)
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
