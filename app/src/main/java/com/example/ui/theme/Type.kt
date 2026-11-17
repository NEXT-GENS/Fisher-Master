package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val FredokaFontFamily = FontFamily(
    Font(R.font.fredoka, FontWeight.Normal),
    Font(R.font.fredoka, FontWeight.Medium),
    Font(R.font.fredoka, FontWeight.Bold)
)

val PlusJakartaFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

val JetBrainsMonoFontFamily = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal),
    Font(R.font.jetbrains_mono, FontWeight.Bold)
)

fun buildFishingTypography(scale: Float = 1f): Typography {
    val s = scale.coerceIn(0.85f, 1.35f)
    return Typography(
        displayLarge = TextStyle(
            fontFamily = FredokaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (36 * s).sp,
            lineHeight = (42 * s).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = FredokaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (26 * s).sp,
            lineHeight = (32 * s).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = FredokaFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (20 * s).sp,
            lineHeight = (26 * s).sp
        ),
        titleLarge = TextStyle(
            fontFamily = FredokaFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (18 * s).sp,
            lineHeight = (24 * s).sp
        ),
        titleMedium = TextStyle(
            fontFamily = PlusJakartaFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (15 * s).sp,
            lineHeight = (20 * s).sp
        ),
        bodyLarge = TextStyle(
            fontFamily = PlusJakartaFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (15 * s).sp,
            lineHeight = (21 * s).sp
        ),
        bodyMedium = TextStyle(
            fontFamily = PlusJakartaFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (13 * s).sp,
            lineHeight = (18 * s).sp
        ),
        labelLarge = TextStyle(
            fontFamily = PlusJakartaFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (13 * s).sp,
            lineHeight = (16 * s).sp
        ),
        labelMedium = TextStyle(
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (12 * s).sp,
            lineHeight = (16 * s).sp
        ),
        labelSmall = TextStyle(
            fontFamily = PlusJakartaFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * s).sp,
            lineHeight = (14 * s).sp
        )
    )
}
