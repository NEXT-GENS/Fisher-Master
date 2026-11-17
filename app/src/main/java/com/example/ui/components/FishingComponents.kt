package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.FishRarity
import com.example.domain.FishSpecies
import com.example.domain.WeatherForecastSlot
import com.example.domain.WeatherType
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalHudTheme
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val hudTheme = LocalHudTheme.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor ?: hudTheme.panelBackground,
        border = BorderStroke(1.dp, borderColor ?: hudTheme.panelBorder),
        tonalElevation = 4.dp
    ) {
        Column(content = content)
    }
}

@Composable
fun RarityPill(rarity: FishRarity, modifier: Modifier = Modifier) {
    val bg = rarity.color.copy(alpha = 0.22f)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, rarity.color, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = rarity.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = rarity.color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AvatarBadge(
    avatarId: String,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = CircleShape
) {
    val (drawableRes, accent) = when (avatarId) {
        "avatar_koral" -> com.example.R.drawable.img_avatar_koral to Color(0xFF67E8F9)
        "avatar_aurora" -> com.example.R.drawable.img_avatar_aurora to Color(0xFFC4B5FD)
        "avatar_samurai" -> com.example.R.drawable.img_avatar_samurai to GoldAccent
        else -> com.example.R.drawable.img_avatar_marlin to OceanCyan
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(OceanDeep)
            .border(2.dp, accent, shape),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = drawableRes),
            contentDescription = "Avatar Pemancing",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun FishIllustrationCanvas(
    species: FishSpecies,
    discovered: Boolean = true,
    modifier: Modifier = Modifier
) {
    val bodyColor = if (discovered) species.primaryColor else Color(0xFF1E293B)
    val accentColor = if (discovered) species.accentColor else Color(0xFF334155)
    val eyeColor = if (discovered) Color.White else Color(0xFF475569)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cy = h * 0.5f

        if (discovered && species.rarity >= FishRarity.EPIC) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(species.rarity.color.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(w * 0.5f, cy),
                    radius = w * 0.52f
                ),
                radius = w * 0.52f,
                center = Offset(w * 0.5f, cy)
            )
        }

        // Tail fin
        val tailPath = Path().apply {
            moveTo(w * 0.24f, cy)
            lineTo(w * 0.06f, cy - h * 0.24f)
            lineTo(w * 0.13f, cy)
            lineTo(w * 0.06f, cy + h * 0.24f)
            close()
        }
        drawPath(tailPath, color = accentColor)

        // Body shape by bodyType
        when (species.bodyType) {
            1 -> { // Deep / Round
                drawOval(
                    brush = Brush.verticalGradient(listOf(bodyColor, accentColor.copy(alpha = 0.8f))),
                    topLeft = Offset(w * 0.2f, cy - h * 0.28f),
                    size = Size(w * 0.6f, h * 0.56f)
                )
            }
            2 -> { // Eel / Long Dragon
                val eelPath = Path().apply {
                    moveTo(w * 0.18f, cy)
                    cubicTo(w * 0.35f, cy - h * 0.22f, w * 0.62f, cy + h * 0.22f, w * 0.85f, cy - h * 0.02f)
                }
                drawPath(eelPath, color = bodyColor, style = Stroke(width = h * 0.2f, cap = StrokeCap.Round))
            }
            3 -> { // Marlin / Swordfish / Shark
                drawOval(
                    brush = Brush.horizontalGradient(listOf(bodyColor, accentColor)),
                    topLeft = Offset(w * 0.2f, cy - h * 0.18f),
                    size = Size(w * 0.58f, h * 0.36f)
                )
                // Sword bill
                val bill = Path().apply {
                    moveTo(w * 0.74f, cy - h * 0.04f)
                    lineTo(w * 0.96f, cy)
                    lineTo(w * 0.74f, cy + h * 0.04f)
                    close()
                }
                drawPath(bill, color = accentColor)
            }
            else -> { // Streamlined fish
                drawOval(
                    brush = Brush.horizontalGradient(listOf(bodyColor, accentColor.copy(alpha = 0.85f))),
                    topLeft = Offset(w * 0.2f, cy - h * 0.2f),
                    size = Size(w * 0.62f, h * 0.4f)
                )
            }
        }

        // Dorsal fin
        val dorsal = Path().apply {
            moveTo(w * 0.42f, cy - h * 0.16f)
            lineTo(w * 0.52f, cy - h * 0.36f)
            lineTo(w * 0.62f, cy - h * 0.14f)
            close()
        }
        drawPath(dorsal, color = accentColor)

        // Eye
        val eyeCenter = Offset(w * 0.72f, cy - h * 0.05f)
        drawCircle(color = eyeColor, radius = h * 0.055f, center = eyeCenter)
        if (discovered) {
            drawCircle(color = Color.Black, radius = h * 0.028f, center = eyeCenter)
        }
    }
}

fun weatherIcon(weather: WeatherType) = when (weather) {
    WeatherType.CERAH -> Icons.Filled.WbSunny
    WeatherType.HUJAN -> Icons.Filled.WaterDrop
    WeatherType.SALJU -> Icons.Filled.AcUnit
    WeatherType.BADAI -> Icons.Filled.Bolt
    WeatherType.KABUT -> Icons.Filled.Cloud
    WeatherType.ANGIN -> Icons.Filled.Air
}

@Composable
fun WeatherForecastDialog(
    currentWeather: WeatherType,
    forecast: List<WeatherForecastSlot>,
    onDismiss: () -> Unit,
    onSimulateWeather: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OceanDeep,
        title = {
            Text(
                text = "Prakiraan Cuaca & Efek",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(weatherIcon(currentWeather), contentDescription = currentWeather.displayNameId, tint = GoldAccent)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Saat Ini: ${currentWeather.displayNameId}",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldAccent
                    )
                }
                Text(
                    text = currentWeather.effectDescId,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(Modifier.height(4.dp))
                Text("Jadwal Cuaca Berikutnya:", style = MaterialTheme.typography.labelLarge, color = OceanCyan)
                forecast.forEach { slot ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x331E3A5F))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(slot.timeLabel, style = MaterialTheme.typography.labelMedium, color = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Icon(weatherIcon(slot.weather), contentDescription = null, tint = OceanCyan, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(slot.weather.displayNameId, style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1f))
                        Text("${slot.tempC}°C", style = MaterialTheme.typography.labelMedium, color = GoldAccent)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = OceanCyan)
            }
        },
        dismissButton = {
            TextButton(onClick = onSimulateWeather) {
                Text("Uji Ganti Cuaca", color = GoldAccent)
            }
        }
    )
}

@Composable
fun OfflineFeatureGateOverlay(
    isOnline: Boolean,
    tooltipText: String,
    onRequestSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (isOnline) {
        Box(modifier = modifier) { content() }
    } else {
        Box(
            modifier = modifier
                .alpha(0.58f)
                .clickable(onClick = onRequestSignIn)
        ) {
            content()
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                color = Color(0xDD1E293B),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, GoldAccent)
            ) {
                Text(
                    text = tooltipText,
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
