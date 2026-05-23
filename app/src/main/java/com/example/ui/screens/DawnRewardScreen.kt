package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.game.GameViewModel

@Composable
fun DawnRewardScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentNight by viewModel.currentNight.collectAsState()
    val rewardOptions by viewModel.dawnRewardOptions.collectAsState()
    val moonshards by viewModel.moonshards.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C1021), Color(0xFF131113))
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "DAWN ROSE AT THE MOONWELL BASTION",
                    color = CosmicTeal,
                    fontWeight = FontWeight.Bold,
                    size = 11.sp,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "CHOOSE DAWN REWARD",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    size = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.width(120.dp).height(2.dp).background(CosmicTeal))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Night ${currentNight} survived! Choose one blessing below. You will also receive automatic Moonwell shard distributions for your next day expansions.",
                    color = Color.White.copy(alpha = 0.5f),
                    size = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // 3 Card Choice Deck
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                rewardOptions.forEachIndexed { idx, option ->
                    DawnRewardCard(
                        option = option,
                        onSelect = { viewModel.selectDawnReward(option) },
                        modifier = Modifier.testTag("dawn_reward_item_$idx")
                    )
                }
            }

            // Active bank report
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = "", tint = MoonGold, size = 16.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Your Fortress Shards: $moonshards Shards", color = Color.White, fontWeight = FontWeight.Bold, size = 12.sp)
            }
        }
    }
}

@Composable
fun DawnRewardCard(
    option: DawnRewardOption,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF191D32)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CosmicTeal.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    color = MoonGold,
                    fontWeight = FontWeight.Bold,
                    size = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.description,
                    color = Color.White.copy(alpha = 0.6f),
                    size = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Action Selection
            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(containerColor = CosmicTeal),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("CLAIM", color = CosmicBlack, fontWeight = FontWeight.Bold, size = 10.sp)
            }
        }
    }
}
