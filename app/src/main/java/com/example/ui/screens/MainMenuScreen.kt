package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameViewModel
import kotlin.random.Random

// Vibrant Cosmic Color Tokens to bypass dry "AI slop" defaults
val CosmicBlack = Color(0xFF090A0F)
val DarkGreyBlue = Color(0xFF111422)
val MoonGold = Color(0xFFFFD54F)
val NeonAmber = Color(0xFFFF9100)
val CosmicTeal = Color(0xFF00E5FF)
val ShadowCrimson = Color(0xFFFF1744)
val SlateBlue = Color(0xFF3F51B5)

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val account by viewModel.accountState.collectAsState()
    var showForge by remember { mutableStateOf(false) }

    // Floating sparks background effect utilizing drawBehind
    val infiniteTransition = rememberInfiniteTransition(label = "sparks")
    val sparkOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicBlack, DarkGreyBlue, CosmicBlack)
                )
            )
            .drawBehind {
                // Procedural background stars
                val rand = Random(42)
                for (i in 0..30) {
                    val starX = rand.nextFloat() * size.width
                    val starY = (rand.nextFloat() * size.height - sparkOffset) % size.height
                    drawCircle(
                        color = MoonGold.copy(alpha = 0.35f),
                        radius = rand.nextFloat() * 3f + 1f,
                        center = Offset(starX, if (starY < 0) starY + size.height else starY)
                    )
                }
            }
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Logo Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = "NIGHTSEED",
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = MoonGold,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 8.sp,
                        fontFamily = FontFamily.Serif
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "BASTION",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Decorative divider
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, MoonGold, Color.Transparent)
                            )
                        )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "The tactical rogue-lite mobile defense slice",
                    color = Color.White.copy(alpha = 0.6f),
                    size = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Main menu options
            AnimatedContent(
                targetState = showForge,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                },
                label = "menuContent"
            ) { isForge ->
                if (isForge) {
                    SeedAshForge(
                        viewModel = viewModel,
                        seedAsh = account.seedAsh,
                        onBack = { showForge = false }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Play Button
                        PlayButton(
                            text = "START DEFENSE RUN",
                            icon = Icons.Default.PlayArrow,
                            color = NeonAmber,
                            onClick = { viewModel.startNewRun() },
                            modifier = Modifier.testTag("start_run_button")
                        )

                        // Forge Trigger
                        PlayButton(
                            text = "SEED ASH FORGE",
                            icon = Icons.Default.Star,
                            color = CosmicTeal,
                            onClick = { showForge = true },
                            modifier = Modifier.testTag("forge_btn")
                        )

                        // Info status Card
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = DarkGreyBlue.copy(alpha = 0.8f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = "", tint = MoonGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ARCHIVE RECORDS", color = MoonGold, fontWeight = FontWeight.Bold, size = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Seed Ash Collected: ${account.seedAsh}", color = Color.White, size = 13.sp)
                                Text("Bastions Cleared: ${account.totalRunsCleared}", color = Color.White, size = 13.sp)
                                Text("Defensive Faults: ${account.totalLosses}", color = Color.White, size = 13.sp)
                            }
                        }
                    }
                }
            }

            // Bottom brand credits
            Text(
                text = "v1.2.5 - Powered by AI Studio",
                color = Color.White.copy(alpha = 0.3f),
                size = 11.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun PlayButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(color.copy(alpha = 0.15f), color.copy(alpha = 0.05f))
                )
            )
            .border(2.dp, color, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    color = color,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    size = 14.sp
                )
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = color)
        }
    }
}

@Composable
fun SeedAshForge(
    viewModel: GameViewModel,
    seedAsh: Int,
    onBack: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkGreyBlue.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CosmicTeal.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "SEED ASH FORGE",
                        color = MoonGold,
                        fontWeight = FontWeight.Bold,
                        size = 18.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "", tint = MoonGold, size = 16.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$seedAsh Ash", color = Color.White, fontWeight = FontWeight.SemiBold, size = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Impart permanent celestial blessings into your next defenses.",
                    color = Color.White.copy(alpha = 0.6f),
                    size = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                ForgeItemCard(
                    title = "Reinforced Stem (Core HP)",
                    description = "Gives the Bastion Core +20 MAX HP perpetually in all future runs.",
                    cost = 15,
                    purchased = viewModel.hasUpgrade("core_mhp"),
                    seedAsh = seedAsh,
                    onBuy = { viewModel.buyMetaUpgrade("core_mhp", 15) }
                )
            }

            item {
                ForgeItemCard(
                    title = "Deep Roots (Starting Shards)",
                    description = "Commence runs with +20 additional Moonshards for rapid outer expansions.",
                    cost = 20,
                    purchased = viewModel.hasUpgrade("starting_shards"),
                    seedAsh = seedAsh,
                    onBuy = { viewModel.buyMetaUpgrade("starting_shards", 20) }
                )
            }

            item {
                ForgeItemCard(
                    title = "Warden's Polished Edge (Hero Damage)",
                    description = "Infuse celestial light into Vagrant Warden, boosting attack power +25%.",
                    cost = 30,
                    purchased = viewModel.hasUpgrade("hero_atk"),
                    seedAsh = seedAsh,
                    onBuy = { viewModel.buyMetaUpgrade("hero_atk", 30) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DONE UPGRADING", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ForgeItemCard(
    title: String,
    description: String,
    cost: Int,
    purchased: Boolean,
    seedAsh: Int,
    onBuy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (purchased) CosmicTeal.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.03f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (purchased) CosmicTeal.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    color = if (purchased) CosmicTeal else Color.White,
                    fontWeight = FontWeight.Bold,
                    size = 14.sp
                )
                if (purchased) {
                    Box(
                        modifier = Modifier
                            .background(CosmicTeal.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    ) {
                        Text("ACTIVE", color = CosmicTeal, fontWeight = FontWeight.Bold, size = 10.sp)
                    }
                } else {
                    Button(
                        onClick = onBuy,
                        enabled = seedAsh >= cost,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MoonGold,
                            disabledContainerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "", tint = CosmicBlack, size = 12.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$cost Ash", color = CosmicBlack, fontWeight = FontWeight.Bold, size = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, color = Color.White.copy(alpha = 0.5f), size = 11.sp)
        }
    }
}


