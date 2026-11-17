package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.WeatherType

data class HudThemeColors(
    val panelBackground: Color = HudBlueStandard,
    val panelBorder: Color = GlassBorder,
    val skyTop: Color = Color(0xFF0A4DA2),
    val skyBottom: Color = Color(0xFF38BDF8),
    val waterTint: Color = Color(0x550096C7),
    val accent: Color = OceanCyan
)

val LocalHudTheme = staticCompositionLocalOf { HudThemeColors() }

fun hudColorsForWeather(weather: WeatherType, isNight: Boolean): HudThemeColors {
    val base = when (weather) {
        WeatherType.CERAH -> HudThemeColors(
            panelBackground = HudBlueStandard,
            panelBorder = Color(0x8872DDF7),
            skyTop = Color(0xFF0A58CA),
            skyBottom = Color(0xFF48CAE4),
            waterTint = Color(0x3300B4D8),
            accent = OceanCyan
        )
        WeatherType.HUJAN -> HudThemeColors(
            panelBackground = HudBlueDark,
            panelBorder = Color(0x8856CFE1),
            skyTop = Color(0xFF0F172A),
            skyBottom = Color(0xFF1E3A5F),
            waterTint = Color(0x550B2545),
            accent = Color(0xFF64DFDF)
        )
        WeatherType.SALJU -> HudThemeColors(
            panelBackground = HudBlueCold,
            panelBorder = Color(0x99BAE6FD),
            skyTop = Color(0xFF164E63),
            skyBottom = Color(0xFF7DD3FC),
            waterTint = Color(0x4438BDF8),
            accent = Color(0xFFBAE6FD)
        )
        WeatherType.BADAI -> HudThemeColors(
            panelBackground = HudPurpleDark,
            panelBorder = Color(0x99C084FC),
            skyTop = Color(0xFF1E1B4B),
            skyBottom = Color(0xFF4C1D95),
            waterTint = Color(0x663B0764),
            accent = Color(0xFFC084FC)
        )
        WeatherType.KABUT -> HudThemeColors(
            panelBackground = HudMistGray,
            panelBorder = Color(0x88CBD5E1),
            skyTop = Color(0xFF334155),
            skyBottom = Color(0xFF94A3B8),
            waterTint = Color(0x5564748B),
            accent = Color(0xFFCBD5E1)
        )
        WeatherType.ANGIN -> HudThemeColors(
            panelBackground = HudBlueStandard,
            panelBorder = Color(0x885EEAD4),
            skyTop = Color(0xFF0F766E),
            skyBottom = Color(0xFF2DD4BF),
            waterTint = Color(0x440D9488),
            accent = Color(0xFF5EEAD4)
        )
    }
    return if (isNight) {
        base.copy(
            skyTop = Color(0xFF030712),
            skyBottom = base.skyTop.copy(alpha = 0.85f),
            waterTint = Color(0x77020617)
        )
    } else base
}

private val FishingColorScheme = darkColorScheme(
    primary = OceanCyan,
    onPrimary = OceanDeep,
    primaryContainer = OceanPrimary,
    onPrimaryContainer = Color.White,
    secondary = GoldAccent,
    onSecondary = OceanDeep,
    secondaryContainer = Color(0xFF4A3800),
    onSecondaryContainer = GoldAccent,
    tertiary = EpicPurple,
    onTertiary = Color.White,
    background = OceanDeep,
    onBackground = Color.White,
    surface = GlassSurface,
    onSurface = Color.White,
    surfaceVariant = OceanLightPanel,
    onSurfaceVariant = Color(0xFFD0E6FA),
    error = CoralDanger,
    onError = Color.White,
    outline = GlassBorder
)

private val FishingShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun FishingMasterTheme(
    weather: WeatherType = WeatherType.CERAH,
    isNight: Boolean = false,
    textScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val hudTheme = hudColorsForWeather(weather, isNight)
    CompositionLocalProvider(LocalHudTheme provides hudTheme) {
        MaterialTheme(
            colorScheme = FishingColorScheme,
            typography = buildFishingTypography(textScale),
            shapes = FishingShapes,
            content = content
        )
    }
}
