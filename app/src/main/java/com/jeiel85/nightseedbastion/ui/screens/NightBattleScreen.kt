package com.jeiel85.nightseedbastion.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.jeiel85.nightseedbastion.R
import com.jeiel85.nightseedbastion.data.*
import com.jeiel85.nightseedbastion.game.GameViewModel
import kotlin.random.Random
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.CornerRadius
import kotlin.math.*

@Composable
fun NightBattleScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    // --- Low-frequency HUD state: safe to read at composable scope ---
    val currentNight by viewModel.currentNight.collectAsState()
    val coreHp by viewModel.coreHp.collectAsState()
    val coreMaxHp by viewModel.coreMaxHp.collectAsState()
    val ember by viewModel.ember.collectAsState()
    val moonshards by viewModel.moonshards.collectAsState()
    val placedBuildings by viewModel.placedBuildings.collectAsState()
    val heroDowned by viewModel.heroDowned.collectAsState()
    val heroDownedSeconds by viewModel.heroDownedSecondsLeft.collectAsState()
    val combatFlash by viewModel.combatFlash.collectAsState()
    val wardensMarkActive by viewModel.wardensMarkTimeLeft.collectAsState()
    val waveProgress by viewModel.waveSpawnProgressPercent.collectAsState()

    // --- High-frequency state (changes every frame/tick) ---
    // Kept as State and read via .value inside draw / small scopes so a change
    // triggers only the draw phase or a scoped recompose, never a full
    // recomposition of this whole screen. This is the key perf optimization.
    val enemiesState = viewModel.battleEnemies.collectAsState()
    val projectilesState = viewModel.battleProjectiles.collectAsState()
    val heroXState = viewModel.battleHeroX.collectAsState()
    val heroYState = viewModel.battleHeroY.collectAsState()
    val heroTargetXState = viewModel.battleHeroTargetX.collectAsState()
    val heroDashingState = viewModel.heroDashing.collectAsState()
    val lastLanternActiveState = viewModel.lastLanternActive.collectAsState()
    val floatingTextsState = viewModel.floatingTexts.collectAsState()
    val heroHpState = viewModel.battleHeroHp.collectAsState()
    val heroMaxHpState = viewModel.battleHeroMaxHp.collectAsState()

    // Derived HUD readouts that only change occasionally (avoid per-tick churn)
    val enemyCount by remember { derivedStateOf { enemiesState.value.size } }
    val heroHpInt by remember { derivedStateOf { heroHpState.value.toInt() } }
    val heroMaxHpInt by remember { derivedStateOf { heroMaxHpState.value.toInt() } }
    val heroHpFraction by remember {
        derivedStateOf { (heroHpState.value / heroMaxHpState.value).coerceIn(0f, 1f) }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Back during a live battle asks before abandoning the night (returning to
    // the menu replays this night from the last saved Day build state).
    var showAbandonConfirm by remember { mutableStateOf(false) }
    BackHandler { showAbandonConfirm = true }
    if (showAbandonConfirm) {
        AlertDialog(
            onDismissRequest = { showAbandonConfirm = false },
            containerColor = Color(0xFF161B2E),
            title = { Text(stringResource(R.string.abandon_night_title), color = MoonGold, fontWeight = FontWeight.Bold, size = 18.sp) },
            text = { Text(stringResource(R.string.abandon_night_desc), color = Color.White.copy(alpha = 0.8f), size = 13.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showAbandonConfirm = false
                    viewModel.exitToMainMenu()
                }) {
                    Text(stringResource(R.string.abandon_night_confirm), color = ShadowCrimson, fontWeight = FontWeight.Bold, size = 13.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAbandonConfirm = false }) {
                    Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.6f), size = 13.sp)
                }
            }
        )
    }

    // --- Local Neon-Fantasy Particle System & Screen Shake State ---
    val particles = remember { mutableStateListOf<CombatParticle>() }
    var nextParticleId by remember { mutableLongStateOf(0L) }
    var prevEnemies by remember { mutableStateOf(listOf<EnemyInstance>()) }

    var prevCoreHp by remember { mutableIntStateOf(coreHp) }
    var cameraShakeTimeLeft by remember { mutableIntStateOf(0) }
    // Held as State objects (not `by`) so the frame loop can write and the
    // graphicsLayer / draw lambda can read them without recomposing anything.
    val shakeX = remember { mutableStateOf(0f) }
    val shakeY = remember { mutableStateOf(0f) }
    val flashAlpha = remember { mutableStateOf(0f) }
    var prevCombatFlash by remember { mutableIntStateOf(combatFlash) }

    // Skill / big-hit feedback: brief white flash + screen shake
    LaunchedEffect(combatFlash) {
        if (combatFlash != prevCombatFlash) {
            flashAlpha.value = 0.30f
            cameraShakeTimeLeft = 10
            prevCombatFlash = combatFlash
        }
    }

    // Core pulsing and Hero rotation transitions.
    // Held as State (not `by`) and read with .value inside the draw lambda so
    // the per-frame animation only re-runs the draw phase, not recomposition.
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rotate")
    val corePulseScaleState = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulseScale"
    )
    val heroAngleState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heroAngle"
    )
    // Shared animation clock for procedural sprites (degrees-aligned so ring
    // rotations don't jump on loop; sin() phase reset is imperceptible).
    val spriteTimeState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3600f,
        animationSpec = infiniteRepeatable(
            animation = tween(120000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spriteTime"
    )

    // Screen Shake Trigger on Core damage
    LaunchedEffect(coreHp) {
        if (coreHp < prevCoreHp) {
            cameraShakeTimeLeft = 14 // shake for 14 frames (~230ms)
        }
        prevCoreHp = coreHp
    }

    // High-performance 60fps local frame loop
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { _ ->
                // A. Update particles
                val iterator = particles.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.x += p.vx
                    p.y += p.vy
                    p.ticksRemaining--
                    
                    // Embers have a slow fade, hit particles fade quickly
                    if (p.isEmber) {
                        p.size = p.initialSize * (p.ticksRemaining.toFloat() / p.maxTicks.toFloat())
                    } else {
                        p.size = p.initialSize * (p.ticksRemaining.toFloat() / p.maxTicks.toFloat())
                        // Add deceleration to combat sparks
                        p.vx *= 0.94f
                        p.vy *= 0.94f
                    }

                    if (p.ticksRemaining <= 0 || p.size <= 0.1f) {
                        iterator.remove()
                    }
                }

                // B. Spawn ambient floating glowing embers
                if (Random.nextFloat() < 0.12f && particles.size < 60) {
                    particles.add(
                        CombatParticle(
                            id = nextParticleId++,
                            x = Random.nextFloat() * 700f, // logical canvas width scale
                            y = 800f + Random.nextFloat() * 50f, // start from bottom
                            vx = (Random.nextFloat() - 0.5f) * 0.6f,
                            vy = -Random.nextFloat() * 1.8f - 0.4f,
                            color = MoonGold.copy(alpha = Random.nextFloat() * 0.35f + 0.15f),
                            initialSize = Random.nextFloat() * 5f + 3f,
                            size = 0f,
                            maxTicks = Random.nextInt(160, 240),
                            ticksRemaining = Random.nextInt(160, 240),
                            isEmber = true
                        )
                    )
                }

                // C. Update screen shake offset
                if (cameraShakeTimeLeft > 0) {
                    val intensity = (cameraShakeTimeLeft.toFloat() / 14f) * 16f
                    shakeX.value = (Random.nextFloat() - 0.5f) * intensity
                    shakeY.value = (Random.nextFloat() - 0.5f) * intensity
                    cameraShakeTimeLeft--
                } else {
                    if (shakeX.value != 0f) shakeX.value = 0f
                    if (shakeY.value != 0f) shakeY.value = 0f
                }

                // D. Decay skill flash overlay
                if (flashAlpha.value > 0f) {
                    flashAlpha.value = (flashAlpha.value - 0.04f).coerceAtLeast(0f)
                }
            }
        }
    }

    // Particle Burst Trigger for Hit & Death events
    LaunchedEffect(enemiesState.value) {
        val enemies = enemiesState.value
        // O(n) lookups via maps instead of nested find()/any()
        val prevById = prevEnemies.associateBy { it.id }
        val currentIds = HashSet<String>(enemies.size)
        enemies.forEach { currentIds.add(it.id) }

        // 1. Detect hits on existing enemies
        enemies.forEach { enemy ->
            val prev = prevById[enemy.id]
            if (prev != null && enemy.currentHp < prev.currentHp) {
                val lx = when (enemy.lane) {
                    PlayLane.LEFT -> 0.23f
                    PlayLane.CENTER -> 0.5f
                    PlayLane.RIGHT -> 0.77f
                }
                val baseLx = lx * 700f
                val baseLy = 50f + 700f * (enemy.progress / 100f)

                // Spawn 6-10 bright sparks
                val count = Random.nextInt(6, 11)
                repeat(count) {
                    val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                    val speed = Random.nextFloat() * 6f + 2f
                    particles.add(
                        CombatParticle(
                            id = nextParticleId++,
                            x = baseLx,
                            y = baseLy,
                            vx = cos(angle.toDouble()).toFloat() * speed,
                            vy = sin(angle.toDouble()).toFloat() * speed,
                            color = if (enemy.type.isBoss) Color(0xFFD500F9) else CosmicTeal,
                            initialSize = Random.nextFloat() * 4f + 3f,
                            size = 0f,
                            maxTicks = Random.nextInt(14, 22),
                            ticksRemaining = Random.nextInt(14, 22)
                        )
                    )
                }
            }
        }

        // 2. Detect enemy defeats (Death bursts)
        prevEnemies.forEach { prevEnemy ->
            val stillExists = currentIds.contains(prevEnemy.id)
            if (!stillExists && prevEnemy.currentHp <= 0) {
                val lx = when (prevEnemy.lane) {
                    PlayLane.LEFT -> 0.23f
                    PlayLane.CENTER -> 0.5f
                    PlayLane.RIGHT -> 0.77f
                }
                val baseLx = lx * 700f
                val baseLy = 50f + 700f * (prevEnemy.progress / 100f)

                // Spawn 14-20 explosion death particles
                val count = Random.nextInt(14, 21)
                repeat(count) {
                    val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                    val speed = Random.nextFloat() * 4f + 1.5f
                    particles.add(
                        CombatParticle(
                            id = nextParticleId++,
                            x = baseLx,
                            y = baseLy,
                            vx = cos(angle.toDouble()).toFloat() * speed,
                            vy = sin(angle.toDouble()).toFloat() * speed,
                            color = if (prevEnemy.type.isBoss) Color(0xFFD500F9) else ShadowCrimson.copy(alpha = 0.85f),
                            initialSize = Random.nextFloat() * 7f + 4f,
                            size = 0f,
                            maxTicks = Random.nextInt(25, 40),
                            ticksRemaining = Random.nextInt(25, 40)
                        )
                    )
                }
            }
        }

        // Keep a copy of previous list
        prevEnemies = enemies.map { it.copy() }
    }

    // Spawn sparks during Hero Dash
    LaunchedEffect(heroDashingState.value, heroXState.value, heroYState.value) {
        if (heroDashingState.value) {
            val hx = heroXState.value
            val hy = heroYState.value
            repeat(6) {
                particles.add(
                    CombatParticle(
                        id = nextParticleId++,
                        x = hx + (Random.nextFloat() - 0.5f) * 15f,
                        y = hy + (Random.nextFloat() - 0.5f) * 15f,
                        vx = (Random.nextFloat() - 0.5f) * 1.5f,
                        vy = (Random.nextFloat() - 0.5f) * 1.5f,
                        color = CosmicTeal.copy(alpha = 0.65f),
                        initialSize = Random.nextFloat() * 5f + 3f,
                        size = 0f,
                        maxTicks = Random.nextInt(12, 18),
                        ticksRemaining = Random.nextInt(12, 18)
                    )
                )
            }
        }
    }

    // Local Composable pieces to allow neat layout composition
    val topHud = @Composable {
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
                        Text(stringResource(R.string.night_battle_title, currentNight), color = ShadowCrimson, fontWeight = FontWeight.Bold, size = 11.sp)
                        Text(stringResource(R.string.hold_bastion_line), color = Color.White, fontWeight = FontWeight.ExtraBold, size = 14.sp)
                    }

                    // Swarm Count Indicators
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(stringResource(R.string.hostiles_left, enemyCount), color = Color.White, fontWeight = FontWeight.SemiBold, size = 11.sp)
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
                            Text(stringResource(R.string.core_health, coreHp, coreMaxHp), color = Color.White, size = 11.sp, fontWeight = FontWeight.Bold)
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
                        Text(stringResource(R.string.wave_spawner_progress, (waveProgress * 100).toInt()), color = Color.White.copy(alpha = 0.6f), size = 10.sp)
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
    }

    val skillDeck = @Composable {
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
                        Text(stringResource(R.string.ember_essence, ember), color = Color.White, fontWeight = FontWeight.SemiBold, size = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "", tint = MoonGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.scrapped_gold, moonshards), color = MoonGold, fontWeight = FontWeight.SemiBold, size = 11.sp)
                    }
                }

                // Ember ProgressBar
                LinearProgressIndicator(
                    progress = { ember.toFloat() / 100f },
                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                    color = NeonAmber,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )

                // Warden vitality
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.hero_health, heroHpInt, heroMaxHpInt),
                        color = if (heroDowned) ShadowCrimson else Color.White.copy(alpha = 0.75f),
                        size = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                LinearProgressIndicator(
                    progress = { heroHpFraction },
                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                    color = if (heroHpFraction < 0.3f) ShadowCrimson else CosmicTeal,
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
                            Text(stringResource(R.string.skill_mooncut), fontWeight = FontWeight.Bold, size = 11.sp)
                            Text(stringResource(R.string.costs_ember, 15), size = 8.sp, fontWeight = FontWeight.Normal)
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
                            Text(stringResource(R.string.skill_wardens_mark), fontWeight = FontWeight.Bold, size = 11.sp)
                            Text(
                                if (wardensMarkActive > 0) stringResource(R.string.active_seconds, wardensMarkActive / 30) else stringResource(R.string.costs_ember, 20),
                                size = 8.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    val battleArena = @Composable { arenaModifier: Modifier ->
        Box(
            modifier = arenaModifier
                // Camera shake via graphicsLayer: the block runs in the draw phase
                // and reads shakeX/Y deferred, so it never triggers recomposition.
                .graphicsLayer {
                    translationX = shakeX.value
                    translationY = shakeY.value
                }
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .background(Color(0xFF0C0E17))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Convert pixel tap coords -> the renderer's fixed 700x850 logical grid
                        val logicalX = if (size.width > 0) offset.x / size.width * 700f else offset.x
                        val logicalY = if (size.height > 0) offset.y / size.height * 850f else offset.y
                        viewModel.handleHeroTap(logicalX, logicalY)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Deferred reads of high-frequency state — recorded under the draw
                // phase so each change only re-runs drawing, not recomposition.
                val enemies = enemiesState.value
                val projectiles = projectilesState.value
                val heroX = heroXState.value
                val heroY = heroYState.value
                val heroTargetX = heroTargetXState.value
                val heroDashing = heroDashingState.value
                val lastLanternActive = lastLanternActiveState.value
                val corePulseScale = corePulseScaleState.value
                val heroAngle = heroAngleState.value
                val spriteTime = spriteTimeState.value

                // A. Draw lanes as deep vertical pathways with stone tile grids and glowing side borders
                val laneL_X = w * 0.23f
                val laneC_X = w * 0.5f
                val laneR_X = w * 0.77f

                val drawLanesX = listOf(laneL_X, laneC_X, laneR_X)
                drawLanesX.forEach { lx ->
                    // Paved road backing
                    drawLine(
                        color = Color(0xFF090B15),
                        start = Offset(lx, 0f),
                        end = Offset(lx, h),
                        strokeWidth = 56f
                    )
                    // Left neon guide line
                    drawLine(
                        color = SlateBlue.copy(alpha = 0.35f),
                        start = Offset(lx - 28f, 0f),
                        end = Offset(lx - 28f, h),
                        strokeWidth = 2f
                    )
                    // Right neon guide line
                    drawLine(
                        color = SlateBlue.copy(alpha = 0.35f),
                        start = Offset(lx + 28f, 0f),
                        end = Offset(lx + 28f, h),
                        strokeWidth = 2f
                    )
                    // Center fine guide line
                    drawLine(
                        color = CosmicTeal.copy(alpha = 0.12f),
                        start = Offset(lx, 0f),
                        end = Offset(lx, h),
                        strokeWidth = 1f
                    )
                    // Draw subtle paved segments every 60px
                    var segmentY = 0f
                    while (segmentY < h) {
                        drawLine(
                            color = Color(0xFF1B223D).copy(alpha = 0.25f),
                            start = Offset(lx - 26f, segmentY),
                            end = Offset(lx + 26f, segmentY),
                            strokeWidth = 2f
                        )
                        segmentY += 60f
                    }
                }

                // B. Draw Bastion Core Area as an animated pulsing, glowing celestial heart
                val coreRadius = w * 0.15f
                val coreCenter = Offset(laneC_X, h + 20f)
                val coreColor = if (coreHp < coreMaxHp * 0.3f) ShadowCrimson else CosmicTeal

                // Background pulsing gradient glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(coreColor.copy(alpha = 0.3f), Color.Transparent),
                        center = coreCenter,
                        radius = coreRadius * corePulseScale * 1.5f
                    ),
                    radius = coreRadius * corePulseScale * 1.5f,
                    center = coreCenter
                )
                // Inner solid energy barrier
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(coreColor.copy(alpha = 0.45f), Color(0xFF111422)),
                        center = coreCenter,
                        radius = coreRadius
                    ),
                    radius = coreRadius,
                    center = coreCenter
                )
                // Pulsing neon border
                drawCircle(
                    color = coreColor,
                    radius = coreRadius * corePulseScale,
                    center = coreCenter,
                    style = Stroke(width = 3.5f)
                )
                // Runic outer ring
                drawCircle(
                    color = coreColor.copy(alpha = 0.25f),
                    radius = coreRadius * 1.25f,
                    center = coreCenter,
                    style = Stroke(width = 1f)
                )

                // C. Draw Placed Buildings with neon gradients and custom runic geometries
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
                    val bCenter = Offset(bLaneX, bY)

                    val color = getBuildingColor(b.type)
                    val radiusVal = if (b.type == BuildingType.THORN_WALL) 18f else 15f

                    // Procedural building sprite
                    drawBuildingSprite(b, bCenter, color, spriteTime)

                    // Miniature durability bar
                    val length = 32f
                    val hpPercent = b.currentHp.toFloat() / b.maxHp.toFloat()
                    drawLine(
                        color = Color.Black.copy(alpha = 0.6f),
                        start = Offset(bLaneX - length / 2, bY + radiusVal + 7f),
                        end = Offset(bLaneX + length / 2, bY + radiusVal + 7f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = CosmicTeal,
                        start = Offset(bLaneX - length / 2, bY + radiusVal + 7f),
                        end = Offset(bLaneX - length / 2 + length * hpPercent, bY + radiusVal + 7f),
                        strokeWidth = 3f
                    )
                }

                // D. Draw Enemies as corrupted dark neon orbs with glowing centers
                enemies.forEach { enemy ->
                    val elX = when (enemy.lane) {
                        PlayLane.LEFT -> laneL_X
                        PlayLane.CENTER -> laneC_X
                        PlayLane.RIGHT -> laneR_X
                    }
                    val elY = 50f + (h - 100f) * (enemy.progress / 100f)
                    val enemyCenter = Offset(elX, elY)

                    val radiusVal = if (enemy.type.isBoss) 26f else if (enemy.type == EnemyType.GRAVE_BRUTE) 19f else 11f
                    val baseColor = if (enemy.type.isBoss) Color(0xFFD500F9) else if (enemy.type == EnemyType.BONE_RUNNER) MoonGold else ShadowCrimson

                    // Frozen / slowed state tint
                    val effectColor = if (enemy.isStunned) CosmicTeal else if (enemy.isSlowed) SlateBlue else baseColor

                    // Procedural enemy sprite (distinct silhouette per type)
                    drawEnemySprite(enemy, enemyCenter, spriteTime, effectColor)

                    // HP mini bar
                    val length = 26f
                    val hpPercent = maxOf(0f, enemy.currentHp.toFloat() / enemy.maxHp.toFloat())
                    drawLine(
                        color = Color.Black.copy(alpha = 0.6f),
                        start = Offset(elX - length / 2, elY - radiusVal - 8f),
                        end = Offset(elX + length / 2, elY - radiusVal - 8f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = ShadowCrimson,
                        start = Offset(elX - length / 2, elY - radiusVal - 8f),
                        end = Offset(elX - length / 2 + length * hpPercent, elY - radiusVal - 8f),
                        strokeWidth = 3f
                    )
                }

                // E. Draw Projectiles as glowing comets with linear gradient trails
                projectiles.forEach { p ->
                    val pCanvasX = (p.currentX / 700f) * w
                    val pCanvasY = (p.currentY / 800f) * h

                    val trailLength = 22f
                    val gradientBrush = Brush.linearGradient(
                        colors = listOf(MoonGold, Color.Transparent),
                        start = Offset(pCanvasX, pCanvasY),
                        end = Offset(pCanvasX, pCanvasY + trailLength)
                    )
                    drawLine(
                        brush = gradientBrush,
                        start = Offset(pCanvasX, pCanvasY),
                        end = Offset(pCanvasX, pCanvasY + trailLength),
                        strokeWidth = 5f
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.5f,
                        center = Offset(pCanvasX, pCanvasY)
                    )
                    drawCircle(
                        color = MoonGold.copy(alpha = 0.5f),
                        radius = 7f,
                        center = Offset(pCanvasX, pCanvasY)
                    )
                }

                // F. Draw Hero: Vagrant Warden — procedural lantern-bearer sprite
                val hCanvasX = (heroX / 700f) * w
                val hCanvasY = (heroY / 850f) * h
                val heroCenter = Offset(hCanvasX, hCanvasY)
                drawHeroSprite(
                    center = heroCenter,
                    facingRight = heroTargetX >= heroX,
                    dashing = heroDashing,
                    lanternActive = lastLanternActive,
                    downed = heroDowned,
                    time = spriteTime,
                    heroAngle = heroAngle
                )

                // G. Draw particles in Canvas loop
                particles.forEach { p ->
                    val pCanvasX = (p.x / 700f) * w
                    val pCanvasY = (p.y / 850f) * h
                    drawCircle(
                        color = p.color,
                        radius = p.size,
                        center = Offset(pCanvasX, pCanvasY)
                    )
                }

                // H. Skill / hit feedback flash overlay
                val flash = flashAlpha.value
                if (flash > 0f) {
                    drawRect(color = Color.White.copy(alpha = flash), size = size)
                }
            }

            // Dynamic Floating text popups (Render custom UI texts layered on Canvas)
            floatingTextsState.value.forEach { textVal ->
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

            // Warden downed banner
            if (heroDowned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(ShadowCrimson.copy(alpha = 0.85f), RoundedCornerShape(10.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.hero_downed_label, heroDownedSeconds),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        size = 13.sp
                    )
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBlack)
            .systemBarsPadding()
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Column: Scrollable Top HUD and Skills Deck
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    topHud()
                    skillDeck()
                }

                // Right Panel: full height Battlefield Canvas
                Box(
                    modifier = Modifier.weight(1.5f)
                ) {
                    battleArena(Modifier.fillMaxHeight())
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                topHud()
                battleArena(Modifier.weight(1f).fillMaxWidth().padding(vertical = 10.dp))
                skillDeck()
            }
        }
    }
}

data class CombatParticle(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val initialSize: Float,
    var size: Float,
    val maxTicks: Int,
    var ticksRemaining: Int,
    val isEmber: Boolean = false
)

