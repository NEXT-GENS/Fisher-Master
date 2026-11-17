package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.domain.FishingPhase
import com.example.ui.theme.GoldAccent
import kotlin.math.sin

/**
 * High-detail cel-shaded boat, angler character, companion cat, and articulated fishing rod
 * drawn smoothly in Gameplay Canvas, reflecting avatar identity, equipped skin, and live phase.
 */
fun DrawScope.drawHdBoatAndAngler(
    boatX: Float,
    boatY: Float,
    wavePhase: Float,
    phase: FishingPhase,
    avatarId: String,
    equippedSkinId: String
): Offset {
    val bobOffset = sin(wavePhase * 1.4f) * 3.5f
    val by = boatY + bobOffset

    // Determine outfit & character palette from equippedSkinId + avatarId
    val (jacketMain, jacketTrim, capAccent, hairColor) = when {
        equippedSkinId == "skin_aurora" -> listOf(
            Color(0xFF0EA5E9), Color(0xFFE0F2FE), Color(0xFF38BDF8), Color(0xFF60A5FA)
        )
        equippedSkinId == "skin_badai" -> listOf(
            Color(0xFF6D28D9), Color(0xFFFDE047), Color(0xFFA855F7), Color(0xFF1E1B4B)
        )
        avatarId == "avatar_koral" -> listOf(
            Color(0xFF06B6D4), Color(0xFFFFFFFF), Color(0xFF0284C7), Color(0xFF5D4037)
        )
        avatarId == "avatar_aurora" -> listOf(
            Color(0xFF1D4ED8), Color(0xFFE0F2FE), Color(0xFF0EA5E9), Color(0xFF2563EB)
        )
        avatarId == "avatar_samurai" -> listOf(
            Color(0xFFB91C1C), GoldAccent, Color(0xFF1E3A8A), Color(0xFF1E293B)
        )
        else -> listOf(
            Color(0xFF1D4ED8), Color(0xFFFFFFFF), Color(0xFF0284C7), Color(0xFF3E2723)
        )
    }

    // 1. Shimmering Water Wake & Reflection under the Boat
    drawOval(
        brush = Brush.horizontalGradient(
            listOf(Color.Transparent, Color(0x66E0F2FE), Color(0x88FFFFFF), Color(0x66E0F2FE), Color.Transparent)
        ),
        topLeft = Offset(boatX - 135f, by + 26f),
        size = Size(270f, 16f)
    )
    drawOval(
        color = Color(0x330F172A),
        topLeft = Offset(boatX - 108f, by + 22f),
        size = Size(216f, 14f)
    )

    // 2. Outboard Engine at Stern (Left)
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF0F172A))),
        topLeft = Offset(boatX - 114f, by - 12f),
        size = Size(24f, 34f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = Color(0xFF38BDF8),
        topLeft = Offset(boatX - 110f, by - 6f),
        size = Size(16f, 5f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // 3. Sleek Fiberglass Sport Fishing Boat Hull
    val hullPath = Path().apply {
        moveTo(boatX - 96f, by - 2f)
        lineTo(boatX + 106f, by - 8f)
        lineTo(boatX + 124f, by + 6f)
        quadraticTo(boatX + 98f, by + 32f, boatX + 46f, by + 32f)
        lineTo(boatX - 88f, by + 30f)
        quadraticTo(boatX - 102f, by + 16f, boatX - 96f, by - 2f)
        close()
    }
    drawPath(
        hullPath,
        brush = Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFCBD5E1), Color(0xFF1E3A8A)))
    )
    drawPath(
        hullPath,
        color = Color(0xFF0F172A),
        style = Stroke(width = 2.2f)
    )

    // Sport Blue Hull Racing Stripe
    val stripePath = Path().apply {
        moveTo(boatX - 94f, by + 8f)
        lineTo(boatX + 114f, by + 4f)
        lineTo(boatX + 106f, by + 14f)
        lineTo(boatX - 92f, by + 16f)
        close()
    }
    drawPath(
        stripePath,
        brush = Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF38BDF8), GoldAccent))
    )

    // Cockpit Windshield / Console on Right-Mid
    val cabinPath = Path().apply {
        moveTo(boatX + 16f, by - 5f)
        lineTo(boatX + 38f, by - 28f)
        lineTo(boatX + 64f, by - 24f)
        lineTo(boatX + 74f, by - 6f)
        close()
    }
    drawPath(
        cabinPath,
        brush = Brush.verticalGradient(listOf(Color(0xCC7DD3FC), Color(0xCC0284C7)))
    )
    drawPath(cabinPath, color = Color(0xFFE0F2FE), style = Stroke(width = 2f))

    // Bow Companion Cat sitting near front windshield
    val catX = boatX + 80f
    val catY = by - 14f
    drawOval(color = Color(0xFFF59E0B), topLeft = Offset(catX - 9f, catY - 4f), size = Size(18f, 16f))
    drawCircle(color = Color(0xFFFBBF24), radius = 9.5f, center = Offset(catX, catY - 11f))
    drawCircle(color = Color(0xFFFFFBEB), radius = 5.5f, center = Offset(catX + 2f, catY - 9f))
    // Cat cap
    drawArc(
        color = Color(0xFF0284C7),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(catX - 9f, catY - 22f),
        size = Size(18f, 12f)
    )
    // Cat scarf
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(catX - 7f, catY - 3f), size = Size(14f, 4f), cornerRadius = CornerRadius(2f, 2f))

    // 4. HD Angler Character (Animated lean depending on Casting / Reeling)
    val leanOffset = when (phase) {
        FishingPhase.REELING -> -10f
        FishingPhase.CASTING -> 6f
        FishingPhase.BITE -> -5f
        else -> 0f
    }
    val ax = boatX - 24f + leanOffset
    val ay = by - 8f

    // Legs / Dark Fishing Trousers
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(ax - 12f, ay - 22f),
        size = Size(12f, 22f),
        cornerRadius = CornerRadius(5f, 5f)
    )
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(ax + 1f, ay - 22f),
        size = Size(12f, 22f),
        cornerRadius = CornerRadius(5f, 5f)
    )

    // Backpack / Tackle Sling behind Angler
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF334155))),
        topLeft = Offset(ax - 22f, ay - 56f),
        size = Size(14f, 28f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Torso: Shaded Sport Fishing Jacket
    val torsoPath = Path().apply {
        moveTo(ax - 15f, ay - 58f)
        lineTo(ax + 15f, ay - 58f)
        lineTo(ax + 18f, ay - 20f)
        lineTo(ax - 16f, ay - 20f)
        close()
    }
    drawPath(
        torsoPath,
        brush = Brush.verticalGradient(listOf(jacketTrim, jacketMain, Color(0xFF0F172A)))
    )
    drawPath(torsoPath, color = Color(0xFF0F172A), style = Stroke(width = 1.6f))
    // Jacket collar & zipper stripe
    drawLine(
        color = Color(0xFF38BDF8),
        start = Offset(ax + 3f, ay - 57f),
        end = Offset(ax + 3f, ay - 22f),
        strokeWidth = 3f
    )
    // Back fish emblem accent on hoodie
    drawOval(
        color = Color(0xFF0284C7),
        topLeft = Offset(ax - 10f, ay - 46f),
        size = Size(10f, 7f)
    )

    // Neck & Head (facing slightly right toward the water)
    val headCenter = Offset(ax + 1f, ay - 73f)
    drawRoundRect(
        color = Color(0xFFF3C6A5),
        topLeft = Offset(headCenter.x - 5f, ay - 63f),
        size = Size(10f, 8f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Head base
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0xFFFFE0C8), Color(0xFFF1B894))),
        radius = 15.5f,
        center = headCenter
    )
    // Jawline shadow
    drawCircle(
        color = Color(0x339A3412),
        radius = 15.5f,
        center = headCenter,
        style = Stroke(width = 1.4f)
    )
    // Focused Anime Eye & Brow (looking toward line/water)
    drawCircle(color = Color(0xFF0F172A), radius = 2.6f, center = Offset(headCenter.x + 6.5f, headCenter.y - 1f))
    drawCircle(color = Color.White, radius = 1f, center = Offset(headCenter.x + 7.2f, headCenter.y - 1.8f))
    // Hair tufts under cap
    val hairPath = Path().apply {
        moveTo(headCenter.x - 15f, headCenter.y - 3f)
        lineTo(headCenter.x - 18f, headCenter.y + 6f)
        lineTo(headCenter.x - 10f, headCenter.y + 2f)
        lineTo(headCenter.x - 4f, headCenter.y - 5f)
        lineTo(headCenter.x + 12f, headCenter.y - 8f)
        close()
    }
    drawPath(hairPath, color = hairColor)

    // Baseball Cap + Visor facing right + Mini Emblem
    drawArc(
        brush = Brush.verticalGradient(listOf(Color.White, capAccent)),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(headCenter.x - 16.5f, headCenter.y - 18f),
        size = Size(33f, 22f)
    )
    // Visor extending toward water
    val visorPath = Path().apply {
        moveTo(headCenter.x + 2f, headCenter.y - 8.5f)
        lineTo(headCenter.x + 25f, headCenter.y - 5.5f)
        lineTo(headCenter.x + 21f, headCenter.y - 2.5f)
        lineTo(headCenter.x + 2f, headCenter.y - 4.5f)
        close()
    }
    drawPath(visorPath, color = Color(0xFF1E3A8A))
    drawCircle(color = GoldAccent, radius = 2.5f, center = Offset(headCenter.x + 4f, headCenter.y - 12f))

    // 5. Articulated Arms + Cork Rod Handle + Spinning Reel
    val handGrip = Offset(ax + 34f, ay - 38f)
    // Back arm
    drawLine(
        color = jacketMain,
        start = Offset(ax + 7f, ay - 52f),
        end = Offset(handGrip.x - 8f, handGrip.y + 2f),
        strokeWidth = 9f,
        cap = StrokeCap.Round
    )
    // Front arm
    drawLine(
        color = jacketTrim,
        start = Offset(ax - 2f, ay - 50f),
        end = handGrip,
        strokeWidth = 9.5f,
        cap = StrokeCap.Round
    )
    // Gloves/Hands gripping the rod
    drawCircle(color = Color(0xFFF3C6A5), radius = 5f, center = handGrip)
    drawCircle(color = Color(0xFF1E293B), radius = 5.5f, center = Offset(handGrip.x - 7f, handGrip.y + 1f))

    // Cork grip + Spinning Reel Seat
    val rodBase = Offset(handGrip.x - 14f, handGrip.y + 8f)
    val rodHandleEnd = Offset(handGrip.x + 12f, handGrip.y - 8f)
    drawLine(color = Color(0xFFD97706), start = rodBase, end = rodHandleEnd, strokeWidth = 7f, cap = StrokeCap.Round)
    // Metallic Spinning Reel
    drawCircle(
        brush = Brush.radialGradient(listOf(GoldAccent, Color(0xFF475569))),
        radius = 7.5f,
        center = Offset(handGrip.x + 6f, handGrip.y + 3f)
    )

    // 6. Multi-Guide High-Detail Bent Fishing Rod
    val rodBend = when (phase) {
        FishingPhase.REELING -> 26f + sin(wavePhase * 4f) * 9f
        FishingPhase.BITE -> 14f + sin(wavePhase * 7f) * 6f
        else -> 6f
    }
    val rodTipX = boatX + 114f
    val rodTipY = by - 104f + rodBend
    val ctrlX = boatX + 54f
    val ctrlY = by - 96f

    val rodPath = Path().apply {
        moveTo(rodHandleEnd.x, rodHandleEnd.y)
        quadraticTo(ctrlX, ctrlY, rodTipX, rodTipY)
    }
    drawPath(rodPath, color = Color(0xFF0F172A), style = Stroke(width = 7.5f, cap = StrokeCap.Round))
    drawPath(
        rodPath,
        brush = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF7DD3FC), GoldAccent)),
        style = Stroke(width = 4.2f, cap = StrokeCap.Round)
    )
    // Golden Rod Tip Ring
    drawCircle(color = GoldAccent, radius = 5f, center = Offset(rodTipX, rodTipY))
    drawCircle(color = Color(0xFFE0F2FE), radius = 2.2f, center = Offset(rodTipX, rodTipY))

    return Offset(rodTipX, rodTipY)
}
