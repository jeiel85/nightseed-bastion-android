package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.game.GameViewModel

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

    val hasBellShrine = remember(placedBuildings) {
        placedBuildings.values.any { it.type == BuildingType.BELL_SHRINE }
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
                    "NIGHT $currentNight DUSK APPARITION",
                    color = ShadowCrimson,
                    fontWeight = FontWeight.Bold,
                    size = 12.sp,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "OMENS & BARGAINS",
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
                            "LANES THREAT PREVIEW (OMEN)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            size = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (hasBellShrine) {
                        // Advanced Telemetry
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF880E4F).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE91E63), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                "🔔 Bell Shrine Intel Clear! Echo signals identify incoming enemy squads:\n" +
                                        "• Left Lane: Elite Huskling swarms packing Bone Runners.\n" +
                                        "• Center Lane: Massive heavy Tank signatures expected.\n" +
                                        "• Right Lane: Flying hex archers and supportive lantern monsters.",
                                color = Color(0xFFFF80AB),
                                size = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // Vague / Mysterious Forecast
                        Text(
                            "Darkness blankets the outer paths. Distant growls echo in the woods. The wind carries elite pressure warnings. (Build a Bell Shrine in your slot to decrypt perfect wave signals!)",
                            color = Color.White.copy(alpha = 0.6f),
                            size = 12.sp
                        )
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
                    "CONTRACT BARGAIN (CHOOSE CHANCE)",
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
                            "You have bound your soul to a contract this dusk. Prepare for combat.",
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
                        "ENGAGE COALITION NIGHT FIGHT",
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
                    Text("ACCEPT CONVENANT", color = Color.White, fontWeight = FontWeight.Bold, size = 10.sp)
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
