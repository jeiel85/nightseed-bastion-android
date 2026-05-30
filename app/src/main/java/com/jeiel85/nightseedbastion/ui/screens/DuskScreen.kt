package com.jeiel85.nightseedbastion.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeiel85.nightseedbastion.R
import com.jeiel85.nightseedbastion.data.*
import com.jeiel85.nightseedbastion.game.GameViewModel
import com.jeiel85.nightseedbastion.ui.localizedName

@Composable
fun DuskScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentNight by viewModel.currentNight.collectAsState()
    val placedBuildings by viewModel.placedBuildings.collectAsState()
    val bargainOptions by viewModel.duskBargainOptions.collectAsState()
    val spentBargain by viewModel.spentBargainThisDusk.collectAsState()
    val moonshards by viewModel.moonshards.collectAsState()
    val upcomingEnemies by viewModel.upcomingEnemies.collectAsState()
    val context = LocalContext.current

    // Back from the Dusk omen steps back to the Day build phase.
    BackHandler { viewModel.returnToDayBuild() }

    val hasBellShrine = remember(placedBuildings) {
        placedBuildings.values.any { it.type == BuildingType.BELL_SHRINE }
    }

    val laneTelemetry = remember(upcomingEnemies) {
        PlayLane.entries.associateWith { lane ->
            upcomingEnemies.filter { it.lane == lane }
                .groupBy { it.type }
                .map { (type, list) -> "${type.localizedName(context)} x${list.size}" }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F121E), Color(0xFF1E101A))
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    stringResource(R.string.dusk_title, currentNight),
                    color = ShadowCrimson,
                    fontWeight = FontWeight.Bold,
                    size = 12.sp,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.omens_bargains),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    size = 20.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(modifier = Modifier.width(100.dp).height(1.dp).background(ShadowCrimson))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Omnious Lane Previews (Tactical Intel)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F121D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ShadowCrimson.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "", tint = ShadowCrimson, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.lane_threat_preview),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            size = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (hasBellShrine) {
                        // Advanced Telemetry using real upcoming wave data
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF880E4F).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE91E63), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    stringResource(R.string.bell_decryption_active),
                                    color = Color(0xFFFF4081),
                                    size = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                laneTelemetry.forEach { (lane, list) ->
                                    val laneDesc = if (list.isNotEmpty()) list.joinToString(", ") else stringResource(R.string.quiet_no_signatures)
                                    Text(
                                        stringResource(R.string.lane_telemetry_line, lane.localizedName(), laneDesc),
                                        color = Color(0xFFFF80AB),
                                        size = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    } else {
                        // Vague / Mysterious Forecast
                        Column {
                            Text(
                                stringResource(R.string.dusk_vague_forecast, upcomingEnemies.size),
                                color = Color.White.copy(alpha = 0.6f),
                                size = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                stringResource(R.string.bell_shrine_hint),
                                color = MoonGold.copy(alpha = 0.7f),
                                size = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Dusk Bargains Card Panels
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.contract_bargain),
                    color = MoonGold,
                    fontWeight = FontWeight.Bold,
                    size = 11.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                if (spentBargain) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.bargain_spent),
                            color = MoonGold,
                            fontWeight = FontWeight.SemiBold,
                            size = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    bargainOptions.forEach { bargain ->
                        BargainOptionCard(
                            bargain = bargain,
                            onAccept = { viewModel.acceptBargain(bargain) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 3. Begin Night Action Button
            Button(
                onClick = { viewModel.launchNightBattle() },
                colors = ButtonDefaults.buttonColors(containerColor = ShadowCrimson),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("begin_night_button")
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.engage_night_fight),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        size = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BargainOptionCard(
    bargain: DuskBargain,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF231422)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF512DA8).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(bargain.title, color = MoonGold, fontWeight = FontWeight.Bold, size = 15.sp)
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(stringResource(R.string.accept_covenant), color = Color.White, fontWeight = FontWeight.Bold, size = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(bargain.description, color = Color.White.copy(alpha = 0.6f), size = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "", tint = Color(0xFF00E676), size = 14.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(bargain.benefitText, color = Color(0xFF00E676), fontWeight = FontWeight.Bold, size = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "", tint = ShadowCrimson, size = 14.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(bargain.costText, color = ShadowCrimson, fontWeight = FontWeight.SemiBold, size = 11.sp)
                }
            }
        }
    }
}
