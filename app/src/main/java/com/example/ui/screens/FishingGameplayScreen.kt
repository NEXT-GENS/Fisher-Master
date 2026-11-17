package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ActiveEventInfo
import com.example.domain.ActiveFishingSession
import com.example.domain.ControlLayoutMode
import com.example.domain.FishingPhase
import com.example.domain.GameCatalog
import com.example.domain.GameSettings
import com.example.domain.GearCategory
import com.example.domain.OwnedGear
import com.example.domain.PlayerState
import com.example.domain.RoomChatMessage
import com.example.domain.TimeOfDay
import com.example.domain.WeatherForecastSlot
import com.example.domain.WeatherType
import com.example.ui.components.AvatarBadge
import com.example.ui.components.WeatherForecastDialog
import com.example.ui.components.drawHdBoatAndAngler
import com.example.ui.components.weatherIcon
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalHudTheme
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FishingGameplayScreen(
    player: PlayerState,
    ownedGear: List<OwnedGear>,
    session: ActiveFishingSession,
    weather: WeatherType,
    timeOfDay: TimeOfDay,
    clockText: String,
    activeEvent: ActiveEventInfo,
    forecast: List<WeatherForecastSlot>,
    weatherAlert: String?,
    settings: GameSettings,
    isOnlineAccount: Boolean,
    pingMs: Int,
    roomMessages: List<RoomChatMessage>,
    inMultiplayerRoom: Boolean,
    onMoveAngler: (Float) -> Unit,
    onStartCast: () -> Unit,
    onReleaseCast: () -> Unit,
    onHook: () -> Unit,
    onReelPressChange: (Boolean) -> Unit,
    onCancelFishing: () -> Unit,
    onResolveCatch: (String) -> Unit,
    onEquipGear: (OwnedGear) -> Unit,
    onSendQuickChat: (String) -> Unit,
    onSimulateWeather: () -> Unit,
    onOpenSettings: () -> Unit,
    onExitToLobby: () -> Unit,
    onDismissAlert: () -> Unit
) {
    val location = GameCatalog.locationById(player.activeLocationId)
    val hudTheme = LocalHudTheme.current
    var showPauseDialog by remember { mutableStateOf(false) }
    var showMiniMapRadar by remember { mutableStateOf(true) }
    var showMiniMapDialog by remember { mutableStateOf(false) }
    var showForecast by remember { mutableStateOf(false) }
    var showSideChat by remember { mutableStateOf(false) }
    var openEmoteTab by remember { mutableStateOf(false) }
    var showBaitPicker by remember { mutableStateOf(false) }

    val baits = remember(ownedGear) { ownedGear.filter { it.category == GearCategory.BAIT } }
    val activeBait = baits.find { it.itemId == player.equippedBaitId }
    val activeRod = ownedGear.find { it.itemId == player.equippedRodId }
    val luckItem = ownedGear.find { it.itemId == "item_luck_charm" }
    val stabilizerItem = ownedGear.find { it.itemId == "item_stabilizer" }

    val formattedCoins = remember(player.coins) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.coins)
    }
    val formattedGems = remember(player.gems) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.gems)
    }
    val maxXp = (player.level * 120).coerceAtLeast(120)

    val infiniteTransition = rememberInfiniteTransition(label = "water_weather")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Restart),
        label = "wave"
    )
    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart),
        label = "particles"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Dynamic Location Backdrop + Day/Night Sky + Weather Canvas
        Image(
            painter = painterResource(id = location.backgroundRes),
            contentDescription = location.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            hudTheme.skyTop.copy(alpha = if (timeOfDay.isNight) 0.72f else 0.22f),
                            hudTheme.waterTint
                        )
                    )
                )
        )

        // Interactive 2D Fishing Scene Canvas (waves, boat/angler, bent rod, neon line, bobber ripples, weather)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val waterLineY = h * 0.54f

            // Animated layered water crests
            val wavePath = Path().apply {
                moveTo(0f, waterLineY)
                var x = 0f
                while (x <= w) {
                    val y = waterLineY + sin(x * 0.015f + wavePhase) * 6f
                    lineTo(x, y)
                    x += 20f
                }
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(wavePath, color = hudTheme.waterTint.copy(alpha = 0.32f))

            // HD Boat, Angler Character (reflects avatar & equipped skin), Companion Cat, and Rod
            val boatX = w * session.anglerX
            val boatY = waterLineY + 14f
            val rodTip = drawHdBoatAndAngler(
                boatX = boatX,
                boatY = boatY,
                wavePhase = wavePhase,
                phase = session.phase,
                avatarId = player.avatarId,
                equippedSkinId = player.equippedSkinId
            )
            val rodTipX = rodTip.x
            val rodTipY = rodTip.y

            // Cast Direction Arc when CASTING
            if (session.phase == FishingPhase.CASTING) {
                val targetX = (rodTipX + 130f + session.castPower * 220f).coerceAtMost(w * 0.88f)
                val targetY = waterLineY + 34f + session.castAngleDeg * 1.5f
                val arcPath = Path().apply {
                    moveTo(rodTipX, rodTipY)
                    quadraticTo((rodTipX + targetX) * 0.5f, rodTipY - 40f, targetX, targetY)
                }
                drawPath(arcPath, color = Color(0xAA7DD3FC), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                drawCircle(color = GoldAccent, radius = 14f, center = Offset(targetX, targetY), style = Stroke(width = 3f))
            }

            // Fishing line + glowing water ripple + bobber during WAITING / BITE / REELING
            if (session.phase in listOf(FishingPhase.WAITING, FishingPhase.BITE, FishingPhase.REELING)) {
                val bobberX = (boatX + 135f + session.fishDistanceM * 5.0f).coerceIn(w * 0.28f, w * 0.84f)
                val bobberDip = when (session.phase) {
                    FishingPhase.BITE -> sin(wavePhase * 6f) * 14f + 8f
                    FishingPhase.REELING -> 10f
                    else -> sin(wavePhase * 2f) * 4f
                }
                val bobberY = waterLineY + 32f + bobberDip

                drawLine(
                    color = if (session.lineTension > 0.8f && session.phase == FishingPhase.REELING) CoralDanger else Color(0xFFE0F2FE),
                    start = Offset(rodTipX, rodTipY),
                    end = Offset(bobberX, bobberY),
                    strokeWidth = 2.6f
                )

                // Expanding sonar water ring
                drawOval(
                    color = Color(0x8838BDF8),
                    topLeft = Offset(bobberX - 24f - particleProgress * 14f, bobberY - 6f),
                    size = Size(48f + particleProgress * 28f, 14f),
                    style = Stroke(width = 2.5f)
                )
                drawCircle(color = CoralDanger, radius = 9f, center = Offset(bobberX, bobberY))
                drawCircle(color = Color.White, radius = 4.5f, center = Offset(bobberX, bobberY + 3f))

                if (session.phase == FishingPhase.BITE) {
                    drawCircle(
                        color = GoldAccent.copy(alpha = 0.8f),
                        radius = 24f + (particleProgress * 22f),
                        center = Offset(bobberX, bobberY),
                        style = Stroke(width = 3.2f)
                    )
                }
            }

            // Dynamic Weather Particle Effects (respects reduceWeatherEffects accessibility setting)
            if (!settings.reduceWeatherEffects) {
                val density = when (settings.graphicsQuality.name) {
                    "RENDAH" -> 12
                    "SEDANG" -> 22
                    else -> 36
                }
                when (weather) {
                    WeatherType.HUJAN, WeatherType.BADAI -> {
                        val color = if (weather == WeatherType.BADAI) Color(0xAA93C5FD) else Color(0x88BAE6FD)
                        for (i in 0 until density) {
                            val rx = ((i * 97 + particleProgress * w * 0.25f) % w)
                            val ry = ((i * 53 + particleProgress * h) % h)
                            drawLine(color, Offset(rx, ry), Offset(rx - 10f, ry + 26f), strokeWidth = 2.2f)
                        }
                        if (weather == WeatherType.BADAI && particleProgress > 0.92f) {
                            drawRect(Color(0x28FFFFFF))
                        }
                    }
                    WeatherType.SALJU -> {
                        for (i in 0 until density) {
                            val sx = ((i * 83 + sin(particleProgress * 6.28f + i) * 24f) % w)
                            val sy = ((i * 61 + particleProgress * h) % h)
                            drawCircle(Color.White.copy(alpha = 0.8f), radius = 4f, center = Offset(if (sx < 0) sx + w else sx, sy))
                        }
                    }
                    WeatherType.KABUT -> drawRect(Color(0x44CBD5E1))
                    WeatherType.ANGIN -> {
                        for (i in 0 until density / 2) {
                            val ax = ((i * 131 + particleProgress * w) % w)
                            val ay = (i * 47f) % h
                            drawLine(Color(0x44E2E8F0), Offset(ax, ay), Offset(ax + 65f, ay + 4f), strokeWidth = 2f)
                        }
                    }
                    WeatherType.CERAH -> {
                        drawCircle(
                            brush = Brush.radialGradient(listOf(Color(0x55FDE047), Color.Transparent), Offset(w * 0.68f, h * 0.18f), 170f),
                            radius = 170f,
                            center = Offset(w * 0.68f, h * 0.18f)
                        )
                    }
                }
            }
        }

        // 2. Landscape HUD Overlay
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            val compactHeight = maxHeight < 430.dp
            val joystickSize = if (compactHeight) 74.dp else 92.dp
            val primaryRingSize = if (compactHeight) 70.dp else 86.dp
            val secondaryRingSize = if (compactHeight) 56.dp else 66.dp

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP HUD ROW: Left (Avatar/XP + Coins + Gems), Center (Weather/Clock/DayNight), Right (Ping + 5 Circle Actions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Capsule
                    Surface(
                        color = Color(0xCC0A2E75),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.3.dp, Color(0xFF56CCF2)),
                        modifier = Modifier
                            .weight(1.15f)
                            .height(44.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarBadge(avatarId = player.avatarId, size = 34.dp)
                            Spacer(Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = player.username,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Lv.${player.level}",
                                        fontSize = 10.sp,
                                        color = Color(0xFFBAE6FD),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(9.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0x9904122E))
                                            .border(0.7.dp, Color(0x667DD3FC), RoundedCornerShape(50))
                                    ) {
                                        val xpFrac = (player.xp.toFloat() / maxXp.toFloat()).coerceIn(0.06f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(xpFrac)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(50))
                                                .background(Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF38BDF8))))
                                        )
                                        Text(
                                            text = "${player.xp}/$maxXp XP",
                                            fontSize = 7.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Coins & Gems Compact Pills
                    Surface(
                        color = Color(0xCC0A2E75),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF56CCF2)),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.MonetizationOn, contentDescription = "Koin", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(3.dp))
                                Text(formattedCoins, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Diamond, contentDescription = "Gem", tint = Color(0xFFC084FC), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(3.dp))
                                Text(formattedGems, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Weather + Temp + Clock + Day/Night Pill
                    Surface(
                        color = Color(0xCC0A2E75),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF56CCF2)),
                        modifier = Modifier
                            .height(40.dp)
                            .clickable { showForecast = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(weatherIcon(weather), contentDescription = weather.displayNameId, tint = GoldAccent, modifier = Modifier.size(17.dp))
                            Column {
                                Text(
                                    text = weather.displayNameId.substringBefore("/"),
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 11.sp
                                )
                                Text(
                                    text = "${location.baseTempC}°C",
                                    fontSize = 9.sp,
                                    color = Color(0xFFBAE6FD),
                                    lineHeight = 10.sp
                                )
                            }
                            Box(modifier = Modifier.width(1.dp).height(18.dp).background(Color.White.copy(alpha = 0.25f)))
                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFFBAE6FD), modifier = Modifier.size(14.dp))
                            Text(clockText, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Icon(Icons.Filled.WbSunny, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                            Text(timeOfDay.labelId, fontSize = 10.sp, color = Color(0xFFE0F2FE))
                        }
                    }

                    // Ping + 5 Labeled Circular Action Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (isOnlineAccount) Icons.Filled.SignalCellularAlt else Icons.Filled.CloudOff,
                                contentDescription = "Koneksi",
                                tint = if (isOnlineAccount) EmeraldSuccess else Color(0xFF94A3B8),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = if (isOnlineAccount) "${pingMs}ms" else "Off",
                                fontSize = 10.sp,
                                color = if (isOnlineAccount) EmeraldSuccess else Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TopHudActionCircle(
                            icon = Icons.Filled.Explore,
                            label = "Mini-Map",
                            active = showMiniMapRadar,
                            onClick = { showMiniMapRadar = !showMiniMapRadar }
                        )
                        TopHudActionCircle(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            label = "Chat",
                            active = showSideChat && !openEmoteTab,
                            onClick = {
                                if (showSideChat && !openEmoteTab) {
                                    showSideChat = false
                                } else {
                                    openEmoteTab = false
                                    showSideChat = true
                                }
                            }
                        )
                        TopHudActionCircle(
                            icon = Icons.Filled.Mood,
                            label = "Emote",
                            active = showSideChat && openEmoteTab,
                            onClick = {
                                if (showSideChat && openEmoteTab) {
                                    showSideChat = false
                                } else {
                                    openEmoteTab = true
                                    showSideChat = true
                                }
                            }
                        )
                        TopHudActionCircle(
                            icon = Icons.Filled.Settings,
                            label = "Pengaturan",
                            onClick = onOpenSettings
                        )
                        TopHudActionCircle(
                            icon = Icons.Filled.Pause,
                            label = "Jeda",
                            onClick = { showPauseDialog = true }
                        )
                    }
                }

                // MIDDLE ARENA OVERLAYS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    // Upper-Left: Compact Prakiraan Cuaca Card + Weather/Status Alerts
                    Column(
                        modifier = Modifier.align(Alignment.TopStart),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = Color(0xCC0A2E75),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, Color(0xFF56CCF2)),
                            modifier = Modifier
                                .width(156.dp)
                                .clickable { showForecast = true }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = weatherIcon(weather),
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "Prakiraan Cuaca",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "${weather.displayNameId.substringBefore("/")}, ${location.baseTempC - 2}–${location.baseTempC + 4}°C",
                                    fontSize = 9.sp,
                                    color = Color(0xFFE0F2FE)
                                )
                                val nextForecast = forecast.firstOrNull()?.weather?.displayNameId?.substringBefore("/") ?: activeEvent.targetWeather.displayNameId.substringBefore("/")
                                Text(
                                    text = "Besok: $nextForecast • Detail >",
                                    fontSize = 9.sp,
                                    color = Color(0xFF7DD3FC),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (weatherAlert != null) {
                            Surface(
                                color = GoldAccent,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.clickable(onClick = onDismissAlert)
                            ) {
                                Text(
                                    text = weatherAlert,
                                    fontSize = 10.sp,
                                    color = OceanDeep,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        session.statusBanner?.let { banner ->
                            Surface(
                                color = Color(0xCC061626),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, OceanCyan)
                            ) {
                                Text(
                                    text = banner,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Top-Center: Casting Power Arc Gauge (CASTING)
                    if (session.phase == FishingPhase.CASTING) {
                        CastingArcGauge(
                            castPower = session.castPower,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }

                    // Upper-Right: Fish Stamina & Line Tension HUD + Circular Radar Mini-Map
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (session.phase == FishingPhase.REELING) {
                            FishStaminaTensionHud(
                                session = session,
                                compact = compactHeight
                            )
                        }
                        if (showMiniMapRadar) {
                            RadarMiniMapWidget(
                                anglerX = session.anglerX,
                                phase = session.phase,
                                fishDistanceM = session.fishDistanceM,
                                sizeDp = if (compactHeight) 74.dp else 94.dp,
                                onExpandClick = { showMiniMapDialog = true }
                            )
                        }
                    }

                    // Left-Middle: Inline Catch Result Showcase Card (CATCH_RESULT)
                    val caught = session.lastCatchResult
                    if (session.phase == FishingPhase.CATCH_RESULT && caught != null) {
                        CatchResultShowcaseCard(
                            caught = caught,
                            location = location,
                            compact = compactHeight,
                            onResolveCatch = onResolveCatch,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }

                    // Bottom-Center Prompt: "! Sambaran!" + Glowing Gold "HOOK SEKARANG!" Pill (BITE)
                    if (session.phase == FishingPhase.BITE) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                color = Color(0xDD7F1D1D),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.5.dp, Color(0xFFFCA5A5))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "! Sambaran!",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Button(
                                onClick = onHook,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .height(if (compactHeight) 38.dp else 44.dp)
                                    .width(if (compactHeight) 196.dp else 224.dp)
                                    .shadow(12.dp, RoundedCornerShape(50), ambientColor = GoldAccent, spotColor = GoldAccent)
                                    .clip(RoundedCornerShape(50))
                                    .background(Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEA580C), Color(0xFFF59E0B))))
                                    .border(2.dp, Color(0xFFFEF08A), RoundedCornerShape(50))
                                    .testTag("hook_button")
                            ) {
                                Text(
                                    text = "HOOK SEKARANG!",
                                    fontSize = if (compactHeight) 13.sp else 15.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }

                    // Bottom-Center Popover: Pilih Umpan
                    if (showBaitPicker) {
                        FloatingBaitPickerCard(
                            baits = baits,
                            equippedBaitId = player.equippedBaitId,
                            onSelectBait = { bait ->
                                onEquipGear(bait)
                            },
                            onDismiss = { showBaitPicker = false },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 2.dp)
                        )
                    }

                    // Center-Right: Side Chat & Emote Drawer
                    GameplaySideChatDrawer(
                        visible = showSideChat,
                        initialTabEmote = openEmoteTab,
                        isOnlineAccount = isOnlineAccount,
                        inMultiplayerRoom = inMultiplayerRoom,
                        roomMessages = roomMessages,
                        onSendQuickChat = onSendQuickChat,
                        onClose = { showSideChat = false },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 2.dp, bottom = 4.dp)
                    )
                }

                // BOTTOM CONTROL DECK: Joystick (Left), Quick-Slot (Center), Circular Action Rings (Right)
                val swapControls = settings.controlLayout == ControlLayoutMode.KLASIK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (!swapControls) {
                        VirtualBoatJoystick(sizeDp = joystickSize, onMoveAngler = onMoveAngler)
                    } else {
                        FishingActionRingCluster(
                            phase = session.phase,
                            primaryRingSize = primaryRingSize,
                            secondaryRingSize = secondaryRingSize,
                            onStartCast = onStartCast,
                            onReleaseCast = onReleaseCast,
                            onHook = onHook,
                            onReelPressChange = onReelPressChange,
                            onCancelFishing = onCancelFishing
                        )
                    }

                    QuickSlotBar(
                        depthMeters = session.waterDepthM.toInt(),
                        activeBait = activeBait,
                        luckBuffQty = (luckItem?.quantity ?: 0) + player.luckBuffRemaining,
                        stabilizerQty = (stabilizerItem?.quantity ?: 0) + player.tensionBuffRemaining,
                        luckItem = luckItem,
                        stabilizerItem = stabilizerItem,
                        compact = compactHeight,
                        onOpenBaitPicker = { showBaitPicker = !showBaitPicker },
                        onUseBuff = onEquipGear
                    )

                    if (!swapControls) {
                        FishingActionRingCluster(
                            phase = session.phase,
                            primaryRingSize = primaryRingSize,
                            secondaryRingSize = secondaryRingSize,
                            onStartCast = onStartCast,
                            onReleaseCast = onReleaseCast,
                            onHook = onHook,
                            onReelPressChange = onReelPressChange,
                            onCancelFishing = onCancelFishing
                        )
                    } else {
                        VirtualBoatJoystick(sizeDp = joystickSize, onMoveAngler = onMoveAngler)
                    }
                }
            }
        }
    }

    if (showForecast) {
        WeatherForecastDialog(
            currentWeather = weather,
            forecast = forecast,
            onDismiss = { showForecast = false },
            onSimulateWeather = onSimulateWeather
        )
    }

    GameplayDialogsHost(
        showMiniMapDialog = showMiniMapDialog,
        showPauseDialog = showPauseDialog,
        location = location,
        activeRod = activeRod,
        player = player,
        onDismissMiniMap = { showMiniMapDialog = false },
        onDismissPause = { showPauseDialog = false },
        onExitToLobby = onExitToLobby
    )
}

@Composable
private fun FishingActionRingCluster(
    phase: FishingPhase,
    primaryRingSize: androidx.compose.ui.unit.Dp,
    secondaryRingSize: androidx.compose.ui.unit.Dp,
    onStartCast: () -> Unit,
    onReleaseCast: () -> Unit,
    onHook: () -> Unit,
    onReelPressChange: (Boolean) -> Unit,
    onCancelFishing: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        when (phase) {
            FishingPhase.IDLE -> {
                CircularFishingButton(
                    label = "LEMPAR",
                    icon = Icons.Filled.Phishing,
                    gradientColors = listOf(Color(0xFFFDE047), Color(0xFFFACC15), Color(0xFFD97706)),
                    borderColor = Color(0xFFFEF9C3),
                    textColor = Color(0xFF072356),
                    sizeDp = primaryRingSize,
                    onClick = onStartCast,
                    modifier = Modifier.testTag("cast_button")
                )
            }
            FishingPhase.CASTING -> {
                CircularFishingButton(
                    label = "LEPASKAN!",
                    icon = Icons.Filled.Phishing,
                    gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF1E3A8A)),
                    borderColor = Color(0xFFBAE6FD),
                    textColor = Color.White,
                    sizeDp = primaryRingSize,
                    onClick = onReleaseCast,
                    modifier = Modifier.testTag("release_cast_button")
                )
            }
            FishingPhase.WAITING -> {
                CircularFishingButton(
                    label = "TARIK\nULANG",
                    icon = Icons.Filled.Refresh,
                    gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1E3A8A), Color(0xFF0F172A)),
                    borderColor = Color(0xFF93C5FD),
                    textColor = Color.White,
                    sizeDp = primaryRingSize,
                    onClick = onCancelFishing
                )
            }
            FishingPhase.BITE -> {
                CircularFishingButton(
                    label = "TARIK\nULANG",
                    icon = Icons.Filled.Refresh,
                    gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1E3A8A), Color(0xFF0F172A)),
                    borderColor = Color(0xFF93C5FD),
                    textColor = Color.White,
                    sizeDp = secondaryRingSize,
                    onClick = onCancelFishing
                )
                CircularFishingButton(
                    label = "HOOK!",
                    icon = Icons.Filled.Bolt,
                    gradientColors = listOf(Color(0xFFF87171), Color(0xFFDC2626), Color(0xFF991B1B)),
                    borderColor = Color(0xFFFEF08A),
                    textColor = Color.White,
                    sizeDp = primaryRingSize,
                    onClick = onHook
                )
            }
            FishingPhase.REELING -> {
                CircularFishingButton(
                    label = "LEPASKAN!",
                    icon = Icons.Filled.Phishing,
                    gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF1E3A8A)),
                    borderColor = Color(0xFFBAE6FD),
                    textColor = Color.White,
                    sizeDp = secondaryRingSize,
                    onClick = onCancelFishing
                )
                CircularHoldReelButton(
                    sizeDp = primaryRingSize,
                    onReelPressChange = onReelPressChange,
                    modifier = Modifier.testTag("reel_button")
                )
            }
            FishingPhase.CATCH_RESULT -> {
                CircularFishingButton(
                    label = "LEMPAR",
                    icon = Icons.Filled.Phishing,
                    gradientColors = listOf(Color(0xFF64748B), Color(0xFF334155)),
                    borderColor = Color(0xFF94A3B8),
                    textColor = Color(0xFFE2E8F0),
                    sizeDp = primaryRingSize,
                    onClick = {}
                )
            }
        }
    }
}
