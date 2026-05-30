package com.jeiel85.nightseedbastion.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeiel85.nightseedbastion.R
import com.jeiel85.nightseedbastion.game.GameViewModel

@Composable
fun VictoryScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val statsSummary by viewModel.runStatsSummary.collectAsState()

    // The run is already resolved here; back returns to the main menu.
    BackHandler { viewModel.exitToMainMenu() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF07120F), Color(0xFF0B141E))
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cosmic glow crown
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "",
                    tint = MoonGold,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.victory_decreed),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = MoonGold,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.corruption_purged),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(modifier = Modifier.width(180.dp).height(2.dp).background(MoonGold))
            }

            // Body info summary Card
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF131B2B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.nights_sleep_safely),
                        color = CosmicTeal,
                        fontWeight = FontWeight.Bold,
                        size = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = statsSummary,
                        color = Color.White.copy(alpha = 0.8f),
                        size = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Return Button
            Button(
                onClick = { viewModel.exitToMainMenu() },
                colors = ButtonDefaults.buttonColors(containerColor = MoonGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("victory_exit_button")
            ) {
                Text(
                    stringResource(R.string.convert_to_forge),
                    color = CosmicBlack,
                    fontWeight = FontWeight.ExtraBold,
                    size = 13.sp
                )
            }
        }
    }
}

@Composable
fun LossScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val statsSummary by viewModel.runStatsSummary.collectAsState()

    // The run is already resolved here; back returns to the main menu.
    BackHandler { viewModel.exitToMainMenu() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1D090E), Color(0xFF0E0B0B))
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cracked Core Symbol
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "",
                    tint = ShadowCrimson,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.bastion_fallen),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = ShadowCrimson,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.night_seed_faded),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(modifier = Modifier.width(180.dp).height(2.dp).background(ShadowCrimson))
            }

            // Loss info card
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF221316)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ShadowCrimson.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.force_mutate_detected),
                        color = ShadowCrimson,
                        fontWeight = FontWeight.Bold,
                        size = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = statsSummary,
                        color = Color.White.copy(alpha = 0.8f),
                        size = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Return Button
            Button(
                onClick = { viewModel.exitToMainMenu() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("loss_exit_button")
            ) {
                Text(
                    stringResource(R.string.convert_to_forge),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    size = 13.sp
                )
            }
        }
    }
}
