package com.jeiel85.nightseedbastion.ui.screens

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.jeiel85.nightseedbastion.data.BuildingInstance
import com.jeiel85.nightseedbastion.data.BuildingType
import com.jeiel85.nightseedbastion.data.EnemyInstance
import com.jeiel85.nightseedbastion.data.EnemyType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// -------------------------------------------------------------------
// Procedural "sprites" — everything drawn from primitives on the Canvas.
// `time` is a continuously increasing float (radians-ish) shared by the
// whole battlefield so animation phases stay in sync but can be offset
// per entity via a stable hash.
// -------------------------------------------------------------------

private const val TAU = (2.0 * PI).toFloat()

fun DrawScope.drawEnemySprite(
    enemy: EnemyInstance,
    center: Offset,
    time: Float,
    effectColor: Color
) {
    val phase = (enemy.id.hashCode() % 628) / 100f
    val r = when {
        enemy.type.isBoss -> 26f
        enemy.type == EnemyType.GRAVE_BRUTE -> 19f
        enemy.type == EnemyType.LANTERN_EATER -> 14f
        enemy.type == EnemyType.HEX_ARCHER -> 13f
        else -> 11f
    }

    // Soft corruption aura behind every fiend
    drawCircle(
        brush = Brush.radialGradient(
            listOf(effectColor.copy(alpha = 0.40f), Color.Transparent),
            center = center, radius = r * 2.1f
        ),
        radius = r * 2.1f, center = center
    )

    when (enemy.type) {
        EnemyType.HUSKLING -> drawHuskling(center, r, effectColor, time, phase)
        EnemyType.BONE_RUNNER -> drawBoneRunner(center, r, effectColor, time, phase)
        EnemyType.LANTERN_EATER -> drawLanternEater(center, r, effectColor, time, phase)
        EnemyType.GRAVE_BRUTE -> drawGraveBrute(center, r, effectColor)
        EnemyType.HEX_ARCHER -> drawHexArcher(center, r, effectColor, time, phase)
        EnemyType.NIGHTSEED_HERALD -> drawHerald(center, r, effectColor, time, phase)
    }

    // Frozen indicator ring
    if (enemy.isStunned) {
        drawCircle(CosmicTeal.copy(alpha = 0.85f), r * 1.3f, center, style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawHuskling(center: Offset, r: Float, color: Color, time: Float, phase: Float) {
    val bob = sin(time * 4f + phase) * 1.5f
    val cx = center.x
    val cy = center.y + bob
    // Ribcage body
    drawRoundRect(
        color = color.copy(alpha = 0.9f),
        topLeft = Offset(cx - r * 0.6f, cy - r * 0.05f),
        size = Size(r * 1.2f, r * 1.0f),
        cornerRadius = CornerRadius(r * 0.3f, r * 0.3f)
    )
    for (i in 0..1) {
        drawLine(
            Color(0xFF0C0E17),
            Offset(cx - r * 0.45f, cy + r * 0.25f + i * r * 0.3f),
            Offset(cx + r * 0.45f, cy + r * 0.25f + i * r * 0.3f),
            strokeWidth = 1.5f
        )
    }
    // Skull
    drawCircle(Color(0xFFEDE7D9), r * 0.62f, Offset(cx, cy - r * 0.5f))
    drawCircle(Color(0xFF12060A), r * 0.16f, Offset(cx - r * 0.22f, cy - r * 0.5f))
    drawCircle(Color(0xFF12060A), r * 0.16f, Offset(cx + r * 0.22f, cy - r * 0.5f))
}

private fun DrawScope.drawBoneRunner(center: Offset, r: Float, color: Color, time: Float, phase: Float) {
    val dart = sin(time * 9f + phase) * 2f
    val cx = center.x
    val cy = center.y + dart
    // Motion streaks behind (upward)
    for (i in 1..3) {
        drawLine(
            color.copy(alpha = 0.35f / i),
            Offset(cx, cy - r - i * 5f),
            Offset(cx, cy - r - i * 5f - 6f),
            strokeWidth = 2f
        )
    }
    // Downward arrowhead body
    val p = Path().apply {
        moveTo(cx, cy + r)
        lineTo(cx - r * 0.9f, cy - r * 0.4f)
        lineTo(cx, cy - r * 0.05f)
        lineTo(cx + r * 0.9f, cy - r * 0.4f)
        close()
    }
    drawPath(p, color)
    drawPath(p, Color.White.copy(alpha = 0.5f), style = Stroke(width = 1.5f))
    drawCircle(Color.White, r * 0.13f, Offset(cx, cy - r * 0.12f))
}

private fun DrawScope.drawLanternEater(center: Offset, r: Float, color: Color, time: Float, phase: Float) {
    drawCircle(Color(0xFF0B0710), r, center)
    drawCircle(color, r, center, style = Stroke(width = 2f))
    // Swirling intake arcs
    rotate(time * 60f, center) {
        for (k in 0..2) {
            drawArc(
                color = color.copy(alpha = 0.7f),
                startAngle = k * 120f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(center.x - r * 1.4f, center.y - r * 1.4f),
                size = Size(r * 2.8f, r * 2.8f),
                style = Stroke(width = 2f)
            )
        }
    }
    // Glowing maw
    val pulse = sin(time * 5f + phase) * 0.5f + 0.5f
    drawCircle(MoonGold.copy(alpha = 0.5f + 0.4f * pulse), r * 0.4f, center)
}

private fun DrawScope.drawGraveBrute(center: Offset, r: Float, color: Color) {
    val hex = Path()
    for (i in 0..5) {
        val a = PI.toFloat() / 3f * i - PI.toFloat() / 2f
        val px = center.x + cos(a) * r
        val py = center.y + sin(a) * r
        if (i == 0) hex.moveTo(px, py) else hex.lineTo(px, py)
    }
    hex.close()
    drawPath(hex, Color(0xFF2A1016))
    drawPath(hex, color, style = Stroke(width = 3f))
    drawCircle(color.copy(alpha = 0.25f), r * 0.5f, center)
    drawCircle(MoonGold, r * 0.12f, Offset(center.x - r * 0.25f, center.y - r * 0.05f))
    drawCircle(MoonGold, r * 0.12f, Offset(center.x + r * 0.25f, center.y - r * 0.05f))
}

private fun DrawScope.drawHexArcher(center: Offset, r: Float, color: Color, time: Float, phase: Float) {
    val bob = sin(time * 3f + phase) * 1.5f
    val cx = center.x
    val cy = center.y + bob
    val d = Path().apply {
        moveTo(cx, cy - r)
        lineTo(cx + r * 0.7f, cy)
        lineTo(cx, cy + r)
        lineTo(cx - r * 0.7f, cy)
        close()
    }
    drawPath(d, Color(0xFF1A0E1F))
    drawPath(d, color, style = Stroke(width = 2.5f))
    // Bow arc on the side
    drawArc(
        color = color,
        startAngle = -60f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(cx + r * 0.2f, cy - r),
        size = Size(r * 1.3f, r * 2f),
        style = Stroke(width = 2f)
    )
    drawCircle(Color.White, r * 0.12f, Offset(cx, cy))
}

private fun DrawScope.drawHerald(center: Offset, r: Float, color: Color, time: Float, phase: Float) {
    // Rotating runic ring
    rotate(time * 30f, center) {
        drawCircle(color.copy(alpha = 0.5f), r * 1.5f, center, style = Stroke(width = 2f))
        for (k in 0..7) {
            val a = (PI.toFloat() / 4f) * k
            drawCircle(color, 2.5f, Offset(center.x + cos(a) * r * 1.5f, center.y + sin(a) * r * 1.5f))
        }
    }
    // Crown of spikes
    val crown = Path()
    val spikes = 8
    for (i in 0 until spikes) {
        val a = (TAU / spikes) * i - PI.toFloat() / 2f
        val ox = center.x + cos(a) * r * 1.15f
        val oy = center.y + sin(a) * r * 1.15f
        val a2 = a + (PI.toFloat() / spikes)
        val ix = center.x + cos(a2) * r * 0.75f
        val iy = center.y + sin(a2) * r * 0.75f
        if (i == 0) crown.moveTo(ox, oy) else crown.lineTo(ox, oy)
        crown.lineTo(ix, iy)
    }
    crown.close()
    drawPath(crown, Color(0xFF200A2A))
    drawPath(crown, color, style = Stroke(width = 2.5f))
    // Central slit eye
    val blink = sin(time * 2f + phase) * 0.5f + 0.5f
    drawCircle(Color(0xFF120612), r * 0.6f, center)
    drawCircle(MoonGold.copy(alpha = 0.45f), r * 0.45f, center)
    drawRoundRect(
        color = Color(0xFF120612),
        topLeft = Offset(center.x - r * 0.08f, center.y - r * 0.4f * blink),
        size = Size(r * 0.16f, r * 0.8f * blink),
        cornerRadius = CornerRadius(r * 0.08f, r * 0.08f)
    )
    // Dangling roots
    for (k in -1..1) {
        val rx = center.x + k * r * 0.5f
        drawLine(
            color.copy(alpha = 0.6f),
            Offset(rx, center.y + r),
            Offset(rx + sin(time * 3f + k) * 4f, center.y + r * 1.8f),
            strokeWidth = 2f
        )
    }
}

fun DrawScope.drawHeroSprite(
    center: Offset,
    facingRight: Boolean,
    dashing: Boolean,
    lanternActive: Boolean,
    downed: Boolean,
    time: Float,
    heroAngle: Float
) {
    if (downed) {
        drawCircle(Color.Gray.copy(alpha = 0.45f), 13f, center)
        val ring = 16f + (sin(time * 5f) * 3f)
        drawCircle(CosmicTeal.copy(alpha = 0.5f), ring, center, style = Stroke(width = 2f))
        // X eyes
        drawLine(Color.White.copy(alpha = 0.7f), Offset(center.x - 5f, center.y - 3f), Offset(center.x - 1f, center.y + 1f), strokeWidth = 1.5f)
        drawLine(Color.White.copy(alpha = 0.7f), Offset(center.x - 1f, center.y - 3f), Offset(center.x - 5f, center.y + 1f), strokeWidth = 1.5f)
        drawLine(Color.White.copy(alpha = 0.7f), Offset(center.x + 1f, center.y - 3f), Offset(center.x + 5f, center.y + 1f), strokeWidth = 1.5f)
        drawLine(Color.White.copy(alpha = 0.7f), Offset(center.x + 5f, center.y - 3f), Offset(center.x + 1f, center.y + 1f), strokeWidth = 1.5f)
        return
    }

    val faceSign = if (facingRight) 1f else -1f
    val bob = if (dashing) 0f else sin(time * 4f) * 1.5f
    val cx = center.x
    val cy = center.y + bob

    // Lantern light field
    drawCircle(
        brush = Brush.radialGradient(
            listOf((if (lanternActive) NeonAmber else CosmicTeal).copy(alpha = 0.22f), Color.Transparent),
            center = Offset(cx, cy), radius = 46f
        ),
        radius = 46f, center = Offset(cx, cy)
    )
    // Moonlit sweep arc
    drawArc(
        color = CosmicTeal.copy(alpha = 0.35f),
        startAngle = heroAngle,
        sweepAngle = 120f,
        useCenter = false,
        size = Size(48f, 48f),
        topLeft = Offset(cx - 24f, cy - 24f),
        style = Stroke(width = 2f)
    )
    if (lanternActive) {
        drawCircle(NeonAmber.copy(alpha = 0.4f), 22f, Offset(cx, cy), style = Stroke(width = 1.5f))
    }
    // Cloak body
    val cloak = Path().apply {
        moveTo(cx, cy - 6f)
        lineTo(cx - 9f, cy + 12f)
        lineTo(cx + 9f, cy + 12f)
        close()
    }
    drawPath(cloak, Color(0xFF1B2B45))
    drawPath(cloak, CosmicTeal.copy(alpha = 0.85f), style = Stroke(width = 1.5f))
    // Head
    drawCircle(Color(0xFFEAF6FF), 5.5f, Offset(cx, cy - 8f))
    // Lantern in hand on the facing side
    val lx = cx + faceSign * 12f
    val ly = cy + 2f
    drawLine(Color(0xFF6B5436), Offset(cx + faceSign * 5f, cy - 2f), Offset(lx, ly - 4f), strokeWidth = 1.5f)
    val lanternColor = if (lanternActive) NeonAmber else MoonGold
    drawCircle(lanternColor.copy(alpha = 0.5f), 7f, Offset(lx, ly))
    drawCircle(lanternColor, 3.5f, Offset(lx, ly))
    // Dash streaks
    if (dashing) {
        for (i in 1..3) {
            drawLine(
                CosmicTeal.copy(alpha = 0.4f / i),
                Offset(cx, cy + i * 4f),
                Offset(cx, cy + i * 4f + 8f),
                strokeWidth = 2f
            )
        }
    }
}

fun DrawScope.drawBuildingSprite(b: BuildingInstance, center: Offset, color: Color, time: Float) {
    val r = if (b.type == BuildingType.THORN_WALL) 18f else 15f
    // Ambient glow
    drawCircle(
        brush = Brush.radialGradient(
            listOf(color.copy(alpha = 0.32f), Color.Transparent),
            center = center, radius = r * 2.1f
        ),
        radius = r * 2.1f, center = center
    )
    when (b.type) {
        BuildingType.WATCHTOWER -> {
            val bw = r * 1.1f
            val bh = r * 1.8f
            drawRoundRect(
                Color(0xFF13303A),
                topLeft = Offset(center.x - bw / 2, center.y - bh / 2),
                size = Size(bw, bh), cornerRadius = CornerRadius(3f, 3f)
            )
            drawRoundRect(
                color,
                topLeft = Offset(center.x - bw / 2, center.y - bh / 2),
                size = Size(bw, bh), cornerRadius = CornerRadius(3f, 3f),
                style = Stroke(width = 2f)
            )
            for (i in -1..1) {
                drawRect(
                    color,
                    topLeft = Offset(center.x + i * r * 0.45f - r * 0.15f, center.y - bh / 2 - r * 0.3f),
                    size = Size(r * 0.3f, r * 0.3f)
                )
            }
            val pulse = sin(time * 4f) * 0.5f + 0.5f
            drawCircle(color.copy(alpha = 0.6f + 0.4f * pulse), r * 0.3f, center)
        }
        BuildingType.MOONWELL -> {
            drawCircle(color, r, center, style = Stroke(width = 2.5f))
            drawCircle(color.copy(alpha = 0.9f), r * 0.55f, Offset(center.x - r * 0.1f, center.y))
            drawCircle(Color(0xFF0C0E17), r * 0.5f, Offset(center.x + r * 0.15f, center.y))
            drawCircle(Color.White.copy(alpha = 0.5f), 2f, Offset(center.x - r * 0.3f, center.y - r * 0.3f))
        }
        BuildingType.EMBER_BRAZIER -> {
            drawArc(
                color, startAngle = 20f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(center.x - r * 0.7f, center.y - r * 0.2f),
                size = Size(r * 1.4f, r * 1.2f), style = Stroke(width = 3f)
            )
            val flick = sin(time * 9f) * 2f
            val flame = Path().apply {
                moveTo(center.x, center.y - r * 0.9f + flick)
                lineTo(center.x - r * 0.4f, center.y)
                lineTo(center.x + r * 0.4f, center.y)
                close()
            }
            drawPath(flame, NeonAmber)
            drawPath(flame, MoonGold.copy(alpha = 0.8f), style = Stroke(width = 1.5f))
            drawCircle(MoonGold, r * 0.2f, Offset(center.x, center.y - r * 0.2f))
        }
        BuildingType.THORN_WALL -> {
            drawRoundRect(
                Color(0xFF3A3F4A),
                topLeft = Offset(center.x - r, center.y - r * 0.45f),
                size = Size(r * 2, r * 0.9f), cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color,
                topLeft = Offset(center.x - r, center.y - r * 0.45f),
                size = Size(r * 2, r * 0.9f), cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = 2f)
            )
            for (i in -2..2) {
                val sx = center.x + i * r * 0.45f
                val sp = Path().apply {
                    moveTo(sx - r * 0.18f, center.y - r * 0.45f)
                    lineTo(sx, center.y - r * 0.95f)
                    lineTo(sx + r * 0.18f, center.y - r * 0.45f)
                    close()
                }
                drawPath(sp, color)
            }
        }
        BuildingType.GRAVE_SNARE -> {
            drawCircle(Color(0xFF1A0A12), r * 0.8f, center)
            val pulse = sin(time * 6f) * 0.5f + 0.5f
            drawCircle(color.copy(alpha = 0.4f + 0.4f * pulse), r * 0.8f, center, style = Stroke(width = 2f))
            for (i in 0 until 8) {
                val a = (TAU / 8) * i
                val bx = center.x + cos(a) * r * 0.5f
                val by = center.y + sin(a) * r * 0.5f
                val tx = center.x + cos(a) * r * 0.85f
                val ty = center.y + sin(a) * r * 0.85f
                val perp = a + PI.toFloat() / 2
                val w = r * 0.12f
                val tri = Path().apply {
                    moveTo(bx + cos(perp) * w, by + sin(perp) * w)
                    lineTo(bx - cos(perp) * w, by - sin(perp) * w)
                    lineTo(tx, ty)
                    close()
                }
                drawPath(tri, color)
            }
        }
        BuildingType.BELL_SHRINE -> {
            val sway = sin(time * 3f) * 4f
            rotate(sway, Offset(center.x, center.y - r * 0.7f)) {
                val bell = Path().apply {
                    moveTo(center.x - r * 0.6f, center.y + r * 0.4f)
                    cubicTo(
                        center.x - r * 0.6f, center.y - r * 0.3f,
                        center.x - r * 0.3f, center.y - r * 0.7f,
                        center.x, center.y - r * 0.7f
                    )
                    cubicTo(
                        center.x + r * 0.3f, center.y - r * 0.7f,
                        center.x + r * 0.6f, center.y - r * 0.3f,
                        center.x + r * 0.6f, center.y + r * 0.4f
                    )
                    close()
                }
                drawPath(bell, color)
                drawPath(bell, Color.White.copy(alpha = 0.4f), style = Stroke(width = 1.5f))
                drawCircle(MoonGold, r * 0.12f, Offset(center.x, center.y + r * 0.45f))
            }
        }
        else -> {
            drawCircle(color, r, center)
            drawCircle(Color.White, r * 0.4f, center)
        }
    }
}
