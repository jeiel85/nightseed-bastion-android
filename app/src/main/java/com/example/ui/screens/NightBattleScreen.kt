package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.game.GameViewModel
import kotlin.random.Random

@Composable
fun NightBattleScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentNight by viewModel.currentNight.collectAsState()
    val coreHp by viewModel.coreHp.collectAsState()
    val coreMaxHp by viewModel.coreMaxHp.collectAsState()
    val ember by viewModel.ember.collectAsState()
    val moonshards by viewModel.moonshards.collectAsState()
    val placedBuildings by viewModel.placedBuildings.collectAsState()

    // Live Battlefield stats
    val enemies by viewModel.battleEnemies.collectAsState()
    val projectiles by viewModel.battleProjectiles.collectAsState()
    val heroX by viewModel.battleHeroX.collectAsState()
    val heroY by viewModel.battleHeroY.collectAsState()
    val heroDashing by viewModel.heroDashing.collectAsState()
    val lastLanternActive by viewModel.lastLanternActive.collectAsState()
    val wardensMarkActive by viewModel.wardensMarkTimeLeft.collectAsState()
    val floatingTexts by viewModel.floatingTexts.collectAsState()
    val waveProgress by viewModel.waveSpawnProgressPercent.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBlack)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Battle Top status HUD
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkGreyBlue.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ShadowCrimson.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("NIGHT $currentNight COALITION COMBAT", color = ShadowCrimson, fontWeight = FontWeight.Bold, size = 11.sp)
                            Text("HOLD THE BASTION LINE", color = Color.White, fontWeight = FontWeight.ExtraBold, size = 14.sp)
                        }

                        // Swarm Count Indicators
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Hostiles left: ${enemies.size}", color = Color.White, fontWeight = FontWeight.SemiBold, size = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Core HP in Battle Screen
                        Column(modifier = Modifier.weight(1.5f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = "", tint = ShadowCrimson, size = 12.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Core Health: $coreHp / $coreMaxHp", color = Color.White, size = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            LinearProgressIndicator(
                                progress = { coreHp.toFloat() / coreMaxHp.toFloat() },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                color = if (coreHp < coreMaxHp * 0.3f) ShadowCrimson else CosmicTeal,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }

                        // Wave spawning progress
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Wave Spawner progress: ${(waveProgress * 100).toInt()}%", color = Color.White.copy(alpha = 0.6f), size = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            LinearProgressIndicator(
                                progress = { waveProgress },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                color = MoonGold,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }

            // 2. Interactive Graphical Battle Arena Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .background(Color(0xFF0C0E17))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Update Hero target destination
                            viewModel.handleHeroTap(offset.x, offset.y)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // A. Draw lanes as deep vertical pathways
                    val laneL_X = w * 0.23f
                    val laneC_X = w * 0.5f
                    val laneR_X = w * 0.77f

                    val drawLanesX = listOf(laneL_X, laneC_X, laneR_X)
                    drawLanesX.forEach { lx ->
                        drawLine(
                            color = Color(0xFF1E2644),
                            start = Offset(lx, 0f),
                            end = Offset(lx, h),
                            strokeWidth = 44f
                        )
                        drawLine(
                            color = Color(0xFF3F51B5).copy(alpha = 0.25f),
                            start = Offset(lx, 0f),
                            end = Offset(lx, h),
                            strokeWidth = 2f
                        )
                    }

                    // B. Draw Bastion Core Area at the bottom as defensive barrier
                    drawCircle(
                        color = Color(0xFF161F38),
                        radius = w * 0.15f,
                        center = Offset(laneC_X, h + 20f)
                    )
                    drawCircle(
                        color = if (coreHp < coreMaxHp * 0.3f) ShadowCrimson.copy(alpha = 0.3f) else CosmicTeal.copy(alpha = 0.25f),
                        radius = w * 0.15f,
                        center = Offset(laneC_X, h + 20f),
                        style = Stroke(width = 4f)
                    )

                    // C. Draw Placed Buildings
                    placedBuildings.forEach { (_, b) ->
                        val bLaneX = when (b.lane) {
                            PlayLane.LEFT -> laneL_X
                            PlayLane.CENTER -> laneC_X
                            PlayLane.RIGHT -> laneR_X
                        }
                        val progressPercent = when (b.position) {
                            SlotPosition.OUTER -> 0.3f
                            SlotPosition.MID -> 0.5f
                            SlotPosition.INNER -> 0.7f
                        }
                        val bY = h * progressPercent

                        // Color schemes depending on building type
                        val color = getBuildingColor(b.type)
                        val radiusVal = if (b.type == BuildingType.THORN_WALL) 18f else 15f

                        // Draw base
                        drawCircle(
                            color = color.copy(alpha = 0.25f),
                            radius = radiusVal + 10f,
                            center = Offset(bLaneX, bY)
                        )
                        drawCircle(
                            color = color,
                            radius = radiusVal,
                            center = Offset(bLaneX, bY)
                        )

                        // If Thorn Wall, draw barricade shape
                        if (b.type == BuildingType.THORN_WALL) {
                            drawRect(
                                color = Color.White.copy(alpha = 0.15f),
                                topLeft = Offset(bLaneX - 18f, bY - 6f),
                                size = Size(36f, 12f)
                            )
                        }

                        // Miniature durability bar
                        val length = 30f
                        val hpPercent = b.currentHp.toFloat() / b.maxHp.toFloat()
                        drawLine(
                            color = Color.Black.copy(alpha = 0.5f),
                            start = Offset(bLaneX - length / 2, bY + radiusVal + 6f),
                            end = Offset(bLaneX + length / 2, bY + radiusVal + 6f),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = CosmicTeal,
                            start = Offset(bLaneX - length / 2, bY + radiusVal + 6f),
                            end = Offset(bLaneX - length / 2 + length * hpPercent, bY + radiusVal + 6f),
                            strokeWidth = 3f
                        )
                    }

                    // D. Draw Enemies
                    enemies.forEach { enemy ->
                        val elX = when (enemy.lane) {
                            PlayLane.LEFT -> laneL_X
                            PlayLane.CENTER -> laneC_X
                            PlayLane.RIGHT -> laneR_X
                        }
                        // Progress 0f to 100f translates to Y coordinate from 50f to h-50f
                        val elY = 50f + (h - 100f) * (enemy.progress / 100f)

                        // Scale size for boss or heavies
                        val radiusVal = if (enemy.type.isBoss) 25f else if (enemy.type == EnemyType.GRAVE_BRUTE) 18f else 10f
                        val baseColor = if (enemy.type.isBoss) Color(0xFFD500F9) else if (enemy.type == EnemyType.BONE_RUNNER) MoonGold else ShadowCrimson

                        // Frozen state indicator
                        val effectColor = if (enemy.isStunned) CosmicTeal else if (enemy.isSlowed) SlateBlue else baseColor

                        // Draw body
                        drawCircle(
                            color = effectColor.copy(alpha = 0.3f),
                            radius = radiusVal + 6f,
                            center = Offset(elX, elY)
                        )
                        drawCircle(
                            color = effectColor,
                            radius = radiusVal,
                            center = Offset(elX, elY)
                        )

                        // HP mini bar
                        val length = 24f
                        val hpPercent = maxOf(0f, enemy.currentHp.toFloat() / enemy.maxHp.toFloat())
                        drawLine(
                            color = Color.Black.copy(alpha = 0.5f),
                            start = Offset(elX - length / 2, elY - radiusVal - 6f),
                            end = Offset(elX + length / 2, elY - radiusVal - 6f),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = ShadowCrimson,
                            start = Offset(elX - length / 2, elY - radiusVal - 6f),
                            end = Offset(elX - length / 2 + length * hpPercent, elY - radiusVal - 6f),
                            strokeWidth = 3f
                        )
                    }

                    // E. Draw Projectiles
                    projectiles.forEach { p ->
                        // Translate logical coordinates to Canvas bounds
                        val pCanvasX = (p.currentX / 700f) * w
                        val pCanvasY = (p.currentY / 800f) * h

                        drawCircle(
                            color = MoonGold,
                            radius = 5f,
                            center = Offset(pCanvasX, pCanvasY)
                        )
                    }

                    // F. Draw Hero: Vagrant Warden!
                    // Coordinates conversion for responsive rendering
                    val hCanvasX = (heroX / 700f) * w
                    val hCanvasY = (heroY / 850f) * h

                    // Draw engagement halo radius range
                    drawCircle(
                        color = CosmicTeal.copy(alpha = 0.08f),
                        radius = 80f,
                        center = Offset(hCanvasX, hCanvasY)
                    )

                    // Draw dash tail if heroDashing
                    if (heroDashing) {
                        drawCircle(
                            color = CosmicTeal.copy(alpha = 0.4f),
                            radius = 20f,
                            center = Offset(hCanvasX, hCanvasY)
                        )
                    }

                    // Last Lantern passive flame glow
                    if (lastLanternActive) {
                        drawCircle(
                            color = NeonAmber.copy(alpha = 0.35f),
                            radius = 24f,
                            center = Offset(hCanvasX, hCanvasY)
                        )
                    }

                    // Body
                    drawCircle(
                        color = CosmicTeal,
                        radius = 12f,
                        center = Offset(hCanvasX, hCanvasY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 5f,
                        center = Offset(hCanvasX, hCanvasY)
                    )
                }

                // Dynamic Floating text popups (Render custom UI texts layered on Canvas)
                floatingTexts.forEach { textVal ->
                    Text(
                        text = textVal.text,
                        color = Color(textVal.colorHex),
                        fontWeight = FontWeight.Bold,
                        size = 12.sp,
                        modifier = Modifier.offset(
                            x = (textVal.x / 700f * 360f).dp, // approximate scale layout
                            y = (textVal.y / 850f * 480f).dp
                        )
                    )
                }
            }

            // 3. Command Skill Deck (Ember resources, active cooldown selectors)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131522)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Resource display Left/Right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddCircle, contentDescription = "", tint = NeonAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ember Essence: $ember", color = Color.White, fontWeight = FontWeight.SemiBold, size = 12.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "", tint = MoonGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scrapped: $moonshards G", color = MoonGold, fontWeight = FontWeight.SemiBold, size = 11.sp)
                        }
                    }

                    // Ember ProgressBar
                    LinearProgressIndicator(
                        progress = { ember.toFloat() / 100f },
                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                        color = NeonAmber,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Skill hotkeys
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Skill 1: Mooncut
                        Button(
                            onClick = { viewModel.useSkillMooncut() },
                            enabled = ember >= 15,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CosmicTeal,
                                contentColor = CosmicBlack,
                                disabledContainerColor = Color.White.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("skill_mooncut")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌙 MOONCUT", fontWeight = FontWeight.Bold, size = 11.sp)
                                Text("Costs 15 Ember", size = 8.sp, fontWeight = FontWeight.Normal)
                            }
                        }

                        // Skill 2: Warden's Mark
                        Button(
                            onClick = { viewModel.useSkillWardensMark() },
                            enabled = ember >= 20,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD500F9),
                                contentColor = Color.White,
                                disabledContainerColor = Color.White.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("skill_mark")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎯 WARDEN'S MARK", fontWeight = FontWeight.Bold, size = 11.sp)
                                Text(
                                    if (wardensMarkActive > 0) "ACTIVE (${wardensMarkActive / 30}s)" else "Costs 20 Ember",
                                    size = 8.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
