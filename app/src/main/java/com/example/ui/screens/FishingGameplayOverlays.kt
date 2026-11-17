package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ActiveFishingSession
import com.example.domain.CaughtFish
import com.example.domain.FishingPhase
import com.example.domain.GameCatalog
import com.example.domain.LocationInfo
import com.example.domain.OwnedGear
import com.example.domain.PlayerState
import com.example.domain.RoomChatMessage
import com.example.ui.components.FishIllustrationCanvas
import com.example.ui.components.RarityPill
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.WarningAmber
import java.util.Locale
import kotlin.math.roundToInt

@Composable
internal fun TopHudActionCircle(
    icon: ImageVector,
    label: String,
    active: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 3.dp, vertical = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = Color.Transparent,
            shape = CircleShape,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        if (active) listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                        else listOf(Color(0xDD1E3A8A), Color(0xDD0A2156))
                    )
                )
                .border(
                    width = 1.3.dp,
                    color = if (active) Color(0xFFBAE6FD) else Color(0xFF56CCF2),
                    shape = CircleShape
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = label,
            fontSize = 8.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
internal fun RadarMiniMapWidget(
    anglerX: Float,
    phase: FishingPhase,
    fishDistanceM: Float,
    sizeDp: Dp,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(sizeDp)
    ) {
        Surface(
            color = Color(0xCC062863),
            shape = CircleShape,
            border = BorderStroke(2.dp, Color(0xFF56CCF2)),
            modifier = Modifier
                .fillMaxSize()
                .shadow(8.dp, CircleShape, ambientColor = Color(0xFF38BDF8))
                .clickable(onClick = onExpandClick)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val r = size.minDimension / 2f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF0284C7), Color(0xFF0A2E75), Color(0xFF051840)),
                            center = Offset(cx, cy),
                            radius = r
                        ),
                        radius = r
                    )
                    // Depth contour rings
                    drawCircle(Color(0x447DD3FC), radius = r * 0.68f, style = Stroke(1.2f))
                    drawCircle(Color(0x337DD3FC), radius = r * 0.36f, style = Stroke(1.2f))

                    // Shoreline arc at bottom-left
                    drawArc(
                        color = Color(0xAA10B981),
                        startAngle = 110f,
                        sweepAngle = 95f,
                        useCenter = true,
                        topLeft = Offset(-r * 0.3f, r * 0.45f),
                        size = Size(r * 1.4f, r * 1.4f)
                    )

                    // Boat marker (red chevron)
                    val bx = (0.24f + anglerX * 0.52f) * size.width
                    val by = size.height * 0.58f
                    val boatPath = Path().apply {
                        moveTo(bx, by - 9f)
                        lineTo(bx + 7f, by + 6f)
                        lineTo(bx, by + 2f)
                        lineTo(bx - 7f, by + 6f)
                        close()
                    }
                    drawPath(boatPath, color = CoralDanger)

                    // Bobber sonar ping if cast
                    if (phase in listOf(FishingPhase.WAITING, FishingPhase.BITE, FishingPhase.REELING)) {
                        val bobY = (by - 12f - (fishDistanceM.coerceIn(2f, 40f) / 40f) * (r * 0.6f))
                            .coerceIn(size.height * 0.16f, by - 10f)
                        drawCircle(
                            color = GoldAccent.copy(alpha = 0.45f),
                            radius = 10f,
                            center = Offset(bx, bobY)
                        )
                        drawCircle(color = GoldAccent, radius = 4f, center = Offset(bx, bobY))
                    }
                }

                // North indicator
                Text(
                    text = "N",
                    fontSize = 9.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                )
            }
        }

        // Bottom-right '+' zoom trigger
        Surface(
            color = Color(0xFF0EA5E9),
            shape = CircleShape,
            border = BorderStroke(1.2.dp, Color.White),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(20.dp)
                .clickable(onClick = onExpandClick)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Detail Mini-Map",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
internal fun CastingArcGauge(
    castPower: Float,
    modifier: Modifier = Modifier
) {
    val pct = (castPower * 100).roundToInt().coerceIn(0, 100)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(width = 168.dp, height = 62.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeW = 14f
                val arcSize = Size(size.width - 20f, size.height * 1.9f)
                val topLeft = Offset(10f, 8f)
                drawArc(
                    color = Color(0xAA061C47),
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeW + 6f, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF38BDF8), Color(0xFF10B981), Color(0xFFFACC15), Color(0xFFEF4444))
                    ),
                    startAngle = 200f,
                    sweepAngle = (140f * castPower.coerceIn(0.03f, 1f)),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 18.dp)
            ) {
                Text(
                    text = "Casting",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$pct%",
                    fontSize = 18.sp,
                    color = GoldAccent,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Surface(
            color = Color(0xBB061C47),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0x667DD3FC))
        ) {
            Text(
                text = "Perkiraan Jarak: ${(8 + castPower * 32).toInt()} m",
                fontSize = 10.sp,
                color = Color(0xFFE0F2FE),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
internal fun FishStaminaTensionHud(
    session: ActiveFishingSession,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val tension = session.lineTension.coerceIn(0f, 1f)
    val zoneColor = when {
        tension < 0.52f -> EmeraldSuccess
        tension < 0.80f -> WarningAmber
        else -> CoralDanger
    }
    val zoneLabel = when {
        tension < 0.52f -> "AMAN"
        tension < 0.80f -> "WASPADA"
        else -> "BAHAYA PUTUS!"
    }
    val zoneIcon = when {
        tension < 0.52f -> Icons.Filled.CheckCircle
        tension < 0.80f -> Icons.Filled.Warning
        else -> Icons.Filled.Bolt
    }

    Surface(
        color = Color(0xCC092A6B),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.3.dp, Color(0xFF56CCF2)),
        modifier = modifier.width(if (compact) 215.dp else 248.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Stamina Ikan + Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Phishing,
                        contentDescription = null,
                        tint = Color(0xFF7DD3FC),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Stamina Ikan",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Jarak: ${String.format(Locale.US, "%.1f", session.fishDistanceM)} m",
                    fontSize = 11.sp,
                    color = GoldAccent,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x9904122E))
                    .border(0.8.dp, Color(0x667DD3FC), RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(session.fishStamina.coerceIn(0.03f, 1f))
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFEF4444), Color(0xFFFACC15), Color(0xFF22C55E))
                            )
                        )
                )
            }

            // Line Tension (colorblind safe with icon & label)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(zoneIcon, contentDescription = zoneLabel, tint = zoneColor, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "Ketegangan: $zoneLabel",
                        fontSize = 10.sp,
                        color = zoneColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${(tension * 100).roundToInt()}%",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { tension },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50)),
                color = zoneColor,
                trackColor = Color(0x5504122E)
            )
        }
    }
}

@Composable
internal fun VirtualBoatJoystick(
    sizeDp: Dp,
    onMoveAngler: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var knobX by remember { mutableFloatStateOf(0f) }
    var knobY by remember { mutableFloatStateOf(0f) }
    val maxOffsetPx = 36f

    Surface(
        color = Color(0xCC092A6B),
        shape = CircleShape,
        border = BorderStroke(2.dp, Color(0xFF7DD3FC)),
        modifier = modifier
            .size(sizeDp)
            .shadow(8.dp, CircleShape, ambientColor = Color(0xFF38BDF8))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        knobX = 0f
                        knobY = 0f
                    },
                    onDragCancel = {
                        knobX = 0f
                        knobY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    knobX = (knobX + dragAmount.x).coerceIn(-maxOffsetPx, maxOffsetPx)
                    knobY = (knobY + dragAmount.y).coerceIn(-maxOffsetPx, maxOffsetPx)
                    onMoveAngler((dragAmount.x / 20f).coerceIn(-1f, 1f))
                }
            }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Directional arrows on D-pad rim
            Text("▲", fontSize = 9.sp, color = Color(0xFFBAE6FD), modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp))
            Text("▼", fontSize = 9.sp, color = Color(0xFFBAE6FD), modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp))
            Text("◄", fontSize = 9.sp, color = Color(0xFFBAE6FD), modifier = Modifier.align(Alignment.CenterStart).padding(start = 5.dp))
            Text("►", fontSize = 9.sp, color = Color(0xFFBAE6FD), modifier = Modifier.align(Alignment.CenterEnd).padding(end = 5.dp))

            // Thumbstick sphere
            Surface(
                color = Color.Transparent,
                shape = CircleShape,
                border = BorderStroke(1.8.dp, Color.White),
                modifier = Modifier
                    .offset { IntOffset(knobX.roundToInt(), knobY.roundToInt()) }
                    .size(sizeDp * 0.48f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF7DD3FC), Color(0xFF0284C7), Color(0xFF1D4ED8))
                        )
                    )
            ) {}
        }
    }
}

@Composable
internal fun QuickSlotBar(
    depthMeters: Int,
    activeBait: OwnedGear?,
    luckBuffQty: Int,
    stabilizerQty: Int,
    luckItem: OwnedGear?,
    stabilizerItem: OwnedGear?,
    compact: Boolean,
    onOpenBaitPicker: () -> Unit,
    onUseBuff: (OwnedGear) -> Unit,
    modifier: Modifier = Modifier
) {
    val slotSize = if (compact) 46.dp else 54.dp

    Surface(
        color = Color(0xCC092863),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.4.dp, Color(0xFF56CCF2)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quick-Slot",
                fontSize = 10.sp,
                color = Color(0xFFE0F2FE),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text("◄", fontSize = 11.sp, color = Color(0xFF7DD3FC), fontWeight = FontWeight.Bold)

                QuickSlotItemTile(
                    title = "Kedalaman",
                    value = "${depthMeters} m",
                    icon = Icons.Filled.WaterDrop,
                    iconTint = Color(0xFF38BDF8),
                    sizeDp = slotSize,
                    onClick = {}
                )
                QuickSlotItemTile(
                    title = "Umpan Aktif",
                    value = "x${activeBait?.quantity ?: 0}",
                    icon = Icons.Filled.Phishing,
                    iconTint = Color(0xFFFB923C),
                    sizeDp = slotSize,
                    highlighted = true,
                    onClick = onOpenBaitPicker
                )
                QuickSlotItemTile(
                    title = "Luck Charm",
                    value = "x$luckBuffQty",
                    icon = Icons.Filled.AutoAwesome,
                    iconTint = Color(0xFF4ADE80),
                    sizeDp = slotSize,
                    onClick = { if (luckItem != null && luckBuffQty > 0) onUseBuff(luckItem) }
                )
                QuickSlotItemTile(
                    title = "Stabilizer",
                    value = "x$stabilizerQty",
                    icon = Icons.Filled.Bolt,
                    iconTint = Color(0xFFC084FC),
                    sizeDp = slotSize,
                    onClick = { if (stabilizerItem != null && stabilizerBuffExists(stabilizerQty)) onUseBuff(stabilizerItem) }
                )

                Text("►", fontSize = 11.sp, color = Color(0xFF7DD3FC), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun stabilizerBuffExists(qty: Int): Boolean = qty > 0

@Composable
private fun QuickSlotItemTile(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    sizeDp: Dp,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xAA061C47),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (highlighted) 1.5.dp else 1.dp,
            color = if (highlighted) GoldAccent else Color(0x8856CCF2)
        ),
        modifier = Modifier
            .size(width = sizeDp + 8.dp, height = sizeDp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontSize = 7.sp,
                color = Color(0xFFBAE6FD),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                fontSize = 9.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }
    }
}

@Composable
internal fun CircularFishingButton(
    label: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    borderColor: Color,
    textColor: Color,
    sizeDp: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        shape = CircleShape,
        modifier = modifier
            .size(sizeDp)
            .shadow(10.dp, CircleShape, ambientColor = borderColor, spotColor = borderColor)
            .clip(CircleShape)
            .background(Brush.radialGradient(gradientColors))
            .border(2.2.dp, borderColor, CircleShape)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(sizeDp * 0.34f)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = if (sizeDp < 72.dp) 9.sp else 10.sp,
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
internal fun CircularHoldReelButton(
    sizeDp: Dp,
    onReelPressChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }

    Surface(
        color = Color.Transparent,
        shape = CircleShape,
        modifier = modifier
            .size(sizeDp)
            .shadow(14.dp, CircleShape, ambientColor = GoldAccent, spotColor = GoldAccent)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    if (pressed) listOf(Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFFB45309))
                    else listOf(Color(0xFFFDE047), Color(0xFFFACC15), Color(0xFFD97706))
                )
            )
            .border(2.5.dp, Color(0xFFFEF9C3), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        onReelPressChange(true)
                        tryAwaitRelease()
                        pressed = false
                        onReelPressChange(false)
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Phishing,
                contentDescription = "Tahan Tarik Reel",
                tint = Color(0xFF072356),
                modifier = Modifier.size(sizeDp * 0.36f)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Tahan:\nTARIK / REEL",
                fontSize = 10.sp,
                color = Color(0xFF072356),
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp
            )
        }
    }
}

@Composable
internal fun CatchResultShowcaseCard(
    caught: CaughtFish,
    location: LocationInfo,
    compact: Boolean,
    onResolveCatch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val species = remember(caught.fishId) {
        GameCatalog.fishSpecies.find { it.id == caught.fishId } ?: GameCatalog.fishSpecies.first()
    }

    Surface(
        color = Color(0xEE0A2E75),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, Color(0xFF7DD3FC)),
        modifier = modifier
            .width(if (compact) 256.dp else 290.dp)
            .shadow(14.dp, RoundedCornerShape(18.dp), ambientColor = caught.rarity.color)
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 8.dp else 10.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 3.dp else 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ikan Hasil Tangkapan!",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Surface(
                    color = Color(0x44FFFFFF),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onResolveCatch("keep") }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Simpan & Tutup",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 60.dp else 102.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0x667DD3FC), RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = location.backgroundRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x4404122E))
                )
                FishIllustrationCanvas(
                    species = species,
                    discovered = true,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(if (compact) 118.dp else 156.dp, if (compact) 52.dp else 80.dp)
                )
                if (caught.isRecord) {
                    Surface(
                        color = GoldAccent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = OceanDeep,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "REKOR BARU!",
                                fontSize = 9.sp,
                                color = OceanDeep,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Text(
                text = caught.fishName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RarityPill(rarity = caught.rarity)
                Text(
                    text = "Berat: ${caught.weightKg} kg | ${caught.lengthCm} cm",
                    fontSize = 10.sp,
                    color = Color(0xFFE0F2FE),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = location.name,
                    fontSize = 10.sp,
                    color = Color(0xFFBAE6FD)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onResolveCatch("keep") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("catch_keep_button")
                ) {
                    Text("Simpan", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
                Button(
                    onClick = { onResolveCatch("release") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9), contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("catch_release_button")
                ) {
                    Text("Lepas +XP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onResolveCatch("sell") },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = OceanDeep),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("catch_sell_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.MonetizationOn, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(2.dp))
                        Text("+${caught.sellPrice}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
internal fun FloatingBaitPickerCard(
    baits: List<OwnedGear>,
    equippedBaitId: String,
    onSelectBait: (OwnedGear) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xEE0A2E75),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, Color(0xFF7DD3FC)),
        modifier = modifier
            .width(214.dp)
            .shadow(10.dp, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pilih Umpan",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Tutup Pilih Umpan",
                    tint = Color.White,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(onClick = onDismiss)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                baits.take(3).forEach { bait ->
                    val isSelected = bait.itemId == equippedBaitId
                    Surface(
                        color = if (isSelected) Color(0xFF1D4ED8) else Color(0xAA061C47),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            width = if (isSelected) 1.8.dp else 1.dp,
                            color = if (isSelected) GoldAccent else Color(0x667DD3FC)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectBait(bait) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Phishing,
                                contentDescription = bait.name,
                                tint = if (isSelected) GoldAccent else Color(0xFF7DD3FC),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = bait.name.substringBefore(" "),
                                fontSize = 9.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "x${bait.quantity}",
                                fontSize = 9.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = OceanDeep),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                Text("Gunakan", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
internal fun GameplaySideChatDrawer(
    visible: Boolean,
    initialTabEmote: Boolean,
    isOnlineAccount: Boolean,
    inMultiplayerRoom: Boolean,
    roomMessages: List<RoomChatMessage>,
    onSendQuickChat: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTabEmote by remember(initialTabEmote, visible) { mutableStateOf(initialTabEmote) }
    var draftMessage by remember { mutableStateOf("") }

    AnimatedVisibility(visible = visible, modifier = modifier) {
        Surface(
            color = Color(0xDD082256),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.4.dp, Color(0xFF56CCF2)),
            modifier = Modifier
                .width(236.dp)
                .height(198.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Tabs: Chat | Emote | X
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Surface(
                            color = if (!activeTabEmote) Color(0xFF0EA5E9) else Color(0x44061626),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { activeTabEmote = false }
                        ) {
                            Text(
                                text = "Chat",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = if (activeTabEmote) Color(0xFF0EA5E9) else Color(0x44061626),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { activeTabEmote = true }
                        ) {
                            Text(
                                text = "Emote",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Tutup Chat",
                        tint = Color.White,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onClose)
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.16f))

                if (!isOnlineAccount || !inMultiplayerRoom) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!isOnlineAccount) {
                                "Masuk akun Google untuk membuka chat & emote online."
                            } else {
                                "Gabung Room Multiplayer untuk mengobrol secara real-time."
                            },
                            fontSize = 11.sp,
                            color = Color(0xFFBAE6FD),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                } else if (activeTabEmote) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GameCatalog.emotePresets.forEach { emote ->
                                Surface(
                                    color = Color(0x660EA5E9),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.clickable { onSendQuickChat(emote) }
                                ) {
                                    Text(
                                        text = emote,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Ayo!", "Nice!", "Hore!").forEach { phrase ->
                                Surface(
                                    color = Color(0x661E3A8A),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.8.dp, Color(0x887DD3FC)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSendQuickChat(phrase) }
                                ) {
                                    Text(
                                        text = phrase,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(roomMessages.takeLast(12)) { msg ->
                            Text(
                                text = "${msg.senderName}: ${msg.text}",
                                fontSize = 10.sp,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Ayo!", "Nice!", "Hore!").forEach { phrase ->
                            Surface(
                                color = Color(0x661E3A8A),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.8.dp, Color(0x887DD3FC)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSendQuickChat(phrase) }
                            ) {
                                Text(
                                    text = phrase,
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedTextField(
                            value = draftMessage,
                            onValueChange = { draftMessage = it },
                            placeholder = { Text("Ketik pesan...", fontSize = 10.sp, color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.labelSmall.copy(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OceanCyan,
                                unfocusedBorderColor = Color(0x667DD3FC)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        )
                        Surface(
                            color = Color(0xFF0EA5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .size(34.dp)
                                .clickable {
                                    val clean = draftMessage.trim()
                                    if (clean.isNotEmpty()) {
                                        onSendQuickChat(clean)
                                        draftMessage = ""
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Kirim Pesan",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun GameplayDialogsHost(
    showMiniMapDialog: Boolean,
    showPauseDialog: Boolean,
    location: LocationInfo,
    activeRod: OwnedGear?,
    player: PlayerState,
    onDismissMiniMap: () -> Unit,
    onDismissPause: () -> Unit,
    onExitToLobby: () -> Unit
) {
    if (showMiniMapDialog) {
        AlertDialog(
            onDismissRequest = onDismissMiniMap,
            containerColor = OceanDeep,
            title = { Text("Mini-Map: ${location.name}", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Habitat: ${location.habitat}", color = OceanCyan)
                    Text("Kedalaman Air: ${location.depthMeters}m", color = Color.White)
                    Text("Joran Aktif: ${activeRod?.name ?: "-"} (Power ${activeRod?.power ?: 0})", color = GoldAccent)
                    Text("Buff Aktif: Luck (${player.luckBuffRemaining}), Stabilizer (${player.tensionBuffRemaining})", color = EmeraldSuccess)
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissMiniMap) { Text("Tutup", color = OceanCyan) }
            }
        )
    }

    if (showPauseDialog) {
        AlertDialog(
            onDismissRequest = onDismissPause,
            containerColor = OceanDeep,
            title = { Text("Permainan Dijeda", color = Color.White) },
            text = { Text("Lanjutkan memancing di ${location.name} atau kembali ke Lobby?", color = Color.White.copy(alpha = 0.85f)) },
            confirmButton = {
                Button(
                    onClick = onDismissPause,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = OceanDeep)
                ) {
                    Text("Lanjutkan")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissPause()
                    onExitToLobby()
                }) {
                    Text("Keluar ke Lobby", color = CoralDanger)
                }
            }
        )
    }
}
