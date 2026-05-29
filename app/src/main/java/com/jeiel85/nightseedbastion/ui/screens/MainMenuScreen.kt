package com.jeiel85.nightseedbastion.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import android.content.res.Configuration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeiel85.nightseedbastion.R
import com.jeiel85.nightseedbastion.game.GameViewModel
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
    var showCodex by remember { mutableStateOf(false) }
    
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val versionName = remember(context) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

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
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Logo & Version Info
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.brand_nightseed),
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = MoonGold,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 6.sp,
                            fontFamily = FontFamily.Serif,
                            fontSize = 32.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.brand_bastion),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 10.sp,
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, MoonGold, Color.Transparent)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.tagline),
                        color = Color.White.copy(alpha = 0.6f),
                        size = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.version_credit, versionName),
                        color = Color.White.copy(alpha = 0.3f),
                        size = 10.sp
                    )
                }

                // Right Column: Interactive Deck (Scrollable buttons/records)
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                PlayButton(
                                    text = stringResource(R.string.start_defense_run),
                                    icon = Icons.Default.PlayArrow,
                                    color = NeonAmber,
                                    onClick = { viewModel.startNewRun() },
                                    modifier = Modifier.testTag("start_run_button")
                                )
                                PlayButton(
                                    text = stringResource(R.string.seed_ash_forge),
                                    icon = Icons.Default.Star,
                                    color = CosmicTeal,
                                    onClick = { showForge = true },
                                    modifier = Modifier.testTag("forge_btn")
                                )
                                PlayButton(
                                    text = stringResource(R.string.codex_archive),
                                    icon = Icons.Default.Info,
                                    color = MoonGold,
                                    onClick = { showCodex = true },
                                    modifier = Modifier.testTag("codex_btn")
                                )
                                ElevatedCard(
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = DarkGreyBlue.copy(alpha = 0.8f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Info, contentDescription = "", tint = MoonGold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(stringResource(R.string.archive_records), color = MoonGold, fontWeight = FontWeight.Bold, size = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        val level = 1 + account.accountExp / 100
                                        val expProgress = (account.accountExp % 100) / 100f
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(stringResource(R.string.account_rank_level, level), color = Color.White, size = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(stringResource(R.string.exp_compact, account.accountExp % 100), color = Color.White.copy(alpha = 0.6f), size = 10.sp)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { expProgress },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                            color = CosmicTeal,
                                            trackColor = Color.White.copy(alpha = 0.1f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(stringResource(R.string.seed_ash_value, account.seedAsh), color = Color.White, size = 11.sp)
                                            Text(stringResource(R.string.cleared_value, account.totalRunsCleared), color = Color.White, size = 11.sp)
                                            Text(stringResource(R.string.faults_value, account.totalLosses), color = Color.White, size = 11.sp)
                                        }
                                    }
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
                        text = stringResource(R.string.brand_nightseed),
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = MoonGold,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 8.sp,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.brand_bastion),
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
                        text = stringResource(R.string.tagline),
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
                                text = stringResource(R.string.start_defense_run),
                                icon = Icons.Default.PlayArrow,
                                color = NeonAmber,
                                onClick = { viewModel.startNewRun() },
                                modifier = Modifier.testTag("start_run_button")
                            )

                            // Forge Trigger
                            PlayButton(
                                text = stringResource(R.string.seed_ash_forge),
                                icon = Icons.Default.Star,
                                color = CosmicTeal,
                                onClick = { showForge = true },
                                modifier = Modifier.testTag("forge_btn")
                            )

                            // Codex Trigger
                            PlayButton(
                                text = stringResource(R.string.codex_archive),
                                icon = Icons.Default.Info,
                                color = MoonGold,
                                onClick = { showCodex = true },
                                modifier = Modifier.testTag("codex_btn")
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
                                        Text(stringResource(R.string.archive_records), color = MoonGold, fontWeight = FontWeight.Bold, size = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    val level = 1 + account.accountExp / 100
                                    val expProgress = (account.accountExp % 100) / 100f
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(stringResource(R.string.account_rank_level, level), color = Color.White, size = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(stringResource(R.string.exp_verbose, account.accountExp % 100), color = Color.White.copy(alpha = 0.6f), size = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { expProgress },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                        color = CosmicTeal,
                                        trackColor = Color.White.copy(alpha = 0.1f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Text(stringResource(R.string.seed_ash_collected, account.seedAsh), color = Color.White, size = 13.sp)
                                    Text(stringResource(R.string.bastions_cleared, account.totalRunsCleared), color = Color.White, size = 13.sp)
                                    Text(stringResource(R.string.defensive_faults, account.totalLosses), color = Color.White, size = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Bottom brand credits
                Text(
                    text = stringResource(R.string.version_credit, versionName),
                    color = Color.White.copy(alpha = 0.3f),
                    size = 11.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        if (showCodex) {
            CodexArchiveDialog(
                accountLevel = 1 + account.accountExp / 100,
                onDismiss = { showCodex = false }
            )
        }
    }
}

data class CodexEntry(
    val name: String,
    val typeName: String, // "Defense Structure" or "Hostile Fiend"
    val unlockLevel: Int,
    val details: String,
    val color: Color
)

@Composable
fun CodexArchiveDialog(
    accountLevel: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkGreyBlue.copy(alpha = 0.98f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .border(1.dp, CosmicTeal.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.codex_archive), color = MoonGold, fontWeight = FontWeight.Bold, size = 18.sp)
                Box(
                    modifier = Modifier
                        .background(CosmicTeal.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(stringResource(R.string.account_level, accountLevel), color = CosmicTeal, fontWeight = FontWeight.Bold, size = 11.sp)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.codex_intro),
                    color = Color.White.copy(alpha = 0.6f),
                    size = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                val entries = listOf(
                    CodexEntry(stringResource(R.string.building_bastion_core), stringResource(R.string.defense_structure), 1, stringResource(R.string.codex_bastion_core_desc), ShadowCrimson),
                    CodexEntry(stringResource(R.string.enemy_huskling), stringResource(R.string.hostile_fiend), 1, stringResource(R.string.codex_huskling_desc), Color.LightGray),
                    CodexEntry(stringResource(R.string.building_moonwell), stringResource(R.string.defense_structure), 1, stringResource(R.string.codex_moonwell_desc), MoonGold),
                    CodexEntry(stringResource(R.string.building_watchtower), stringResource(R.string.defense_structure), 1, stringResource(R.string.codex_watchtower_desc), CosmicTeal),
                    CodexEntry(stringResource(R.string.building_thorn_wall), stringResource(R.string.defense_structure), 2, stringResource(R.string.codex_thorn_wall_desc), Color.LightGray),
                    CodexEntry(stringResource(R.string.enemy_bone_runner), stringResource(R.string.hostile_fiend), 2, stringResource(R.string.codex_bone_runner_desc), MoonGold),
                    CodexEntry(stringResource(R.string.building_ember_brazier), stringResource(R.string.defense_structure), 3, stringResource(R.string.codex_ember_brazier_desc), NeonAmber),
                    CodexEntry(stringResource(R.string.enemy_lantern_eater), stringResource(R.string.hostile_fiend), 3, stringResource(R.string.codex_lantern_eater_desc), Color(0xFFD500F9)),
                    CodexEntry(stringResource(R.string.building_grave_snare), stringResource(R.string.defense_structure), 4, stringResource(R.string.codex_grave_snare_desc), Color(0xFFE91E63)),
                    CodexEntry(stringResource(R.string.enemy_grave_brute), stringResource(R.string.hostile_fiend), 4, stringResource(R.string.codex_grave_brute_desc), Color(0xFFFF5555)),
                    CodexEntry(stringResource(R.string.building_bell_shrine), stringResource(R.string.defense_structure), 5, stringResource(R.string.codex_bell_shrine_desc), Color(0xFF9C27B0)),
                    CodexEntry(stringResource(R.string.enemy_hex_archer), stringResource(R.string.hostile_fiend), 5, stringResource(R.string.codex_hex_archer_desc), Color(0xFF00E5FF)),
                    CodexEntry(stringResource(R.string.enemy_nightseed_herald), stringResource(R.string.hostile_fiend), 6, stringResource(R.string.codex_nightseed_herald_desc), Color(0xFFE040FB))
                )

                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entries.size) { index ->
                        val item = entries[index]
                        val unlocked = accountLevel >= item.unlockLevel
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (unlocked) Color.White.copy(alpha = 0.03f) else Color.Black.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (unlocked) item.color.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.04f),
                                    RoundedCornerShape(10.dp)
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
                                        text = if (unlocked) item.name else stringResource(R.string.codex_locked_entry),
                                        color = if (unlocked) item.color else Color.White.copy(alpha = 0.4f),
                                        fontWeight = FontWeight.Bold,
                                        size = 13.sp
                                    )
                                    Text(
                                        text = item.typeName,
                                        color = if (unlocked) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                                        size = 9.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (unlocked) item.details else stringResource(R.string.codex_unlocks_at, item.unlockLevel),
                                    color = if (unlocked) Color.White.copy(alpha = 0.7f) else MoonGold.copy(alpha = 0.5f),
                                    size = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CosmicTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.close_archive), color = CosmicBlack, fontWeight = FontWeight.Bold)
            }
        }
    )
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
                        stringResource(R.string.seed_ash_forge),
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
                        Text(stringResource(R.string.seed_ash_amount, seedAsh), color = Color.White, fontWeight = FontWeight.SemiBold, size = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.forge_intro),
                    color = Color.White.copy(alpha = 0.6f),
                    size = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                ForgeItemCard(
                    title = stringResource(R.string.upgrade_reinforced_stem),
                    description = stringResource(R.string.upgrade_reinforced_stem_desc),
                    cost = 15,
                    purchased = viewModel.hasUpgrade("core_mhp"),
                    seedAsh = seedAsh,
                    onBuy = { viewModel.buyMetaUpgrade("core_mhp", 15) }
                )
            }

            item {
                ForgeItemCard(
                    title = stringResource(R.string.upgrade_deep_roots),
                    description = stringResource(R.string.upgrade_deep_roots_desc),
                    cost = 20,
                    purchased = viewModel.hasUpgrade("starting_shards"),
                    seedAsh = seedAsh,
                    onBuy = { viewModel.buyMetaUpgrade("starting_shards", 20) }
                )
            }

            item {
                ForgeItemCard(
                    title = stringResource(R.string.upgrade_warden_edge),
                    description = stringResource(R.string.upgrade_warden_edge_desc),
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
                    Text(stringResource(R.string.done_upgrading), color = Color.White)
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
                        Text(stringResource(R.string.active), color = CosmicTeal, fontWeight = FontWeight.Bold, size = 10.sp)
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
                        Text(stringResource(R.string.seed_ash_amount, cost), color = CosmicBlack, fontWeight = FontWeight.Bold, size = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, color = Color.White.copy(alpha = 0.5f), size = 11.sp)
        }
    }
}


