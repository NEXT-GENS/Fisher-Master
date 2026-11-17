package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.attemptAutoSignIn
import com.example.auth.onGoogleSignInClicked
import com.example.domain.GameCatalog
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.84f) }
    var tipIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, animationSpec = tween(550, easing = FastOutSlowInEasing))
        progress.animateTo(1f, animationSpec = tween(2200, easing = FastOutSlowInEasing))
        onFinished()
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1100L)
            tipIndex = (tipIndex + 1) % GameCatalog.fishingTips.size
        }
    }

    val percent = (progress.value * 100).toInt().coerceIn(0, 100)

    Box(modifier = Modifier.fillMaxSize()) {
        // Vibrant daytime ocean & hero fishing backdrop
        Image(
            painter = painterResource(id = R.drawable.img_splash_hero),
            contentDescription = "Latar Fishing Master",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Cool azure marine tint & bottom contrast vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x440369A1),
                        0.45f to Color(0x300284C7),
                        0.72f to Color(0x8807255B),
                        1.0f to Color(0xE0041436)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top-Left: Angled Ribbon Version Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VersionPillBadge(versionText = "Versi 1.1.1")
            }

            // Center: Stylized FISHING MASTER Emblem & Crest
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                FishingMasterTitleCrest(
                    scale = logoScale.value,
                    compact = false
                )
            }

            // Bottom: Percentage + Glowing Cyan Progress Bar + Tips
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .padding(bottom = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedGameText(
                    text = "$percent%",
                    fontSize = 22.sp,
                    brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFE0F2FE))),
                    strokeColor = Color(0xFF072356),
                    strokeWidth = 7f
                )
                Spacer(Modifier.height(4.dp))

                // Glowing Cyan Progress Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .shadow(10.dp, RoundedCornerShape(50), ambientColor = Color(0xFF38BDF8), spotColor = Color(0xFF38BDF8))
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xD905173B))
                        .border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(50))
                        .padding(3.dp)
                ) {
                    val fraction = progress.value.coerceIn(0.04f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0284C7), Color(0xFF22D3EE), Color(0xFFBAE6FD))
                                )
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.95f))
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Tips row flanked by horizontal divider lines
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(0.16f),
                        color = Color.White.copy(alpha = 0.45f),
                        thickness = 1.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFACC15),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFFFACC15),
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic
                                )
                            ) {
                                append("Tips: ")
                            }
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Medium)) {
                                append(GameCatalog.fishingTips[tipIndex])
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(0.16f),
                        color = Color.White.copy(alpha = 0.45f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun AuthScreen(
    onGoogleAuthSuccess: () -> Unit,
    onContinueOffline: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var legalDialogTitle by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = onGoogleAuthSuccess,
            onUnauthenticated = { /* stay on AuthScreen for explicit choice */ },
            scope = scope
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Bright sunny lakeside dock with sakura blossoms & blue mountains
        Image(
            painter = painterResource(id = R.drawable.img_location_lake),
            contentDescription = "Latar Login Fishing Master",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0.0f to Color(0x1805193D),
                        0.40f to Color(0x44062152),
                        1.0f to Color(0xB804153A)
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Scenic Column: Compact FISHING MASTER Logo top-left + Version Pill bottom-left
            Column(
                modifier = Modifier
                    .weight(0.84f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Box(modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                    FishingMasterTitleCrest(scale = 1f, compact = true)
                }

                Box(modifier = Modifier.padding(bottom = 2.dp)) {
                    VersionPillBadge(versionText = "Versi 1.1.1")
                }
            }

            // Right Column: Tagline + Close button + Glowing Royal-Blue "Akun Angler" Card
            Column(
                modifier = Modifier
                    .weight(1.22f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // Tagline + Close shortcut (48dp touch area, crisp 36dp visual circle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(48.dp))
                    Box(modifier = Modifier.rotate(-2f)) {
                        OutlinedGameText(
                            text = "Memancing Lebih Seru,\nBersama Angler!",
                            fontSize = 14.sp,
                            brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFE0F2FE))),
                            strokeColor = Color(0xFF0A3278),
                            strokeWidth = 5f,
                            italic = true,
                            textAlign = TextAlign.Center
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(enabled = !isLoading, onClick = onContinueOffline)
                            .testTag("close_auth_offline_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color(0xD90E4DA4),
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, Color(0xFF7DD3FC)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Lanjut Mode Offline",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Glowing Royal-Blue Glass Panel: Akun Angler
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .shadow(16.dp, RoundedCornerShape(20.dp), ambientColor = Color(0xFF38BDF8), spotColor = Color(0xFF38BDF8))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xE0144CB2), Color(0xED082563))
                            )
                        )
                        .border(1.8.dp, Color(0xFF56CCF2), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Header: User icon + Akun Angler + Subtitle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color(0x44FFFFFF),
                                shape = CircleShape,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Akun Angler",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Masuk untuk menyimpan progres dan nikmati fitur lengkap!",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFBAE6FD),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Dual Feature Comparison Cards: Cloud Save & Mode Offline
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AuthFeatureComparisonCard(
                                modifier = Modifier.weight(1f),
                                title = "Cloud Save",
                                headerIcon = Icons.Filled.CloudUpload,
                                headerTint = Color(0xFF38BDF8),
                                bullets = listOf(
                                    "Simpan progres di cloud",
                                    "Main di banyak perangkat",
                                    "Backup data aman"
                                ),
                                bottomEmblem = Icons.Filled.CloudUpload,
                                borderColor = Color(0xAA38BDF8)
                            )
                            AuthFeatureComparisonCard(
                                modifier = Modifier.weight(1f),
                                title = "Mode Offline",
                                headerIcon = Icons.Filled.AirplanemodeActive,
                                headerTint = Color.White,
                                bullets = listOf(
                                    "Bermain tanpa internet",
                                    "Akses mode solo",
                                    "Progres tetap tersimpan"
                                ),
                                bottomEmblem = Icons.Filled.WifiOff,
                                borderColor = Color(0x887DD3FC)
                            )
                        }

                        if (errorMessage != null) {
                            Surface(
                                color = Color(0x33EF4444),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFEF4444))
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFECACA),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Primary Button 1: Masuk dengan Google (White Pill)
                        Button(
                            onClick = {
                                errorMessage = null
                                isLoading = true
                                onGoogleSignInClicked(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = {
                                        isLoading = false
                                        onGoogleAuthSuccess()
                                    },
                                    onAuthError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                    },
                                    scope = scope,
                                    onAuthCancelled = { isLoading = false }
                                )
                            },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF0B224B),
                                disabledContainerColor = Color.White.copy(alpha = 0.7f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("google_signin_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF0B224B),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                GoogleGMark(modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(10.dp))
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Masuk dengan Google",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0B224B)
                                    )
                                    Text(
                                        text = "(Credential Manager + Firebase Auth)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF475569)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color(0xFF0B224B)
                                )
                            }
                        }

                        // "atau" divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color.White.copy(alpha = 0.35f)
                            )
                            Text(
                                text = "  atau  ",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFBAE6FD),
                                fontStyle = FontStyle.Italic
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color.White.copy(alpha = 0.35f)
                            )
                        }

                        // Primary Button 2: Main Offline (Solo) (Gold Gradient Pill)
                        Button(
                            onClick = onContinueOffline,
                            enabled = !isLoading,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFF0B224B)
                            ),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .shadow(6.dp, RoundedCornerShape(50))
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFACC15), Color(0xFFFBBF24), Color(0xFFF59E0B))
                                    )
                                )
                                .border(1.2.dp, Color(0xFFFEF08A), RoundedCornerShape(50))
                                .testTag("offline_play_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SportsEsports,
                                    contentDescription = null,
                                    tint = Color(0xFF0F2D6B),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Main Offline (Solo)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0B224B)
                                    )
                                    Text(
                                        text = "Langsung bermain tanpa login",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF3B2F0B),
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color(0xFF0B224B)
                                )
                            }
                        }

                        // Footer Terms & Privacy
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = Color(0xFFBAE6FD),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "Dengan masuk, Anda menyetujui ",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE0F2FE)
                            )
                            Text(
                                text = "Syarat & Ketentuan",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { legalDialogTitle = "Syarat & Ketentuan" }
                            )
                            Text(
                                text = " & ",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE0F2FE)
                            )
                            Text(
                                text = "Privasi",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { legalDialogTitle = "Kebijakan Privasi" }
                            )
                        }
                    }
                }
            }
        }
    }

    legalDialogTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { legalDialogTitle = null },
            containerColor = OceanDeep,
            title = { Text(title, color = GoldAccent) },
            text = {
                Text(
                    text = if (title == "Syarat & Ketentuan") {
                        "Fishing Master menyediakan mode Offline Solo (penyimpanan lokal di perangkat) dan mode Online Multiplayer (Google Sign-In & Cloud Firestore). Pemain wajib menjaga sportivitas dan etika obrolan di dalam room turnamen."
                    } else {
                        "Akun Google hanya digunakan untuk autentikasi Firebase, menyimpan profil Angler, koleksi Fishdex, dan skor leaderboard Anda. Mode Offline Solo menyimpan progres sepenuhnya di perangkat tanpa unggahan ke server."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            },
            confirmButton = {
                TextButton(onClick = { legalDialogTitle = null }) {
                    Text("Tutup", color = OceanCyan)
                }
            }
        )
    }
}

@Composable
private fun VersionPillBadge(versionText: String) {
    Surface(
        color = Color(0xE0092B68),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.2.dp, Color(0xFF38BDF8))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Surface(
                color = Color(0xFF1E40AF),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFF7DD3FC)),
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Phishing,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
            Text(
                text = versionText,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FishingMasterTitleCrest(
    scale: Float,
    compact: Boolean
) {
    val fishingSize = if (compact) 34.sp else 58.sp
    val masterSize = if (compact) 30.sp else 50.sp

    Box(
        modifier = Modifier
            .scale(scale)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ocean splash crest + fishing rod & jumping fish silhouette behind logo
        Canvas(
            modifier = Modifier.size(
                width = if (compact) 210.dp else 340.dp,
                height = if (compact) 105.dp else 160.dp
            )
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x770EA5E9), Color(0x330284C7), Color.Transparent)
                ),
                radius = size.maxDimension * 0.54f
            )
            // Curved fishing rod arc sweeping across top-left to right
            val rodPath = Path().apply {
                moveTo(size.width * 0.12f, size.height * 0.72f)
                quadraticTo(
                    size.width * 0.44f,
                    -size.height * 0.18f,
                    size.width * 0.88f,
                    size.height * 0.28f
                )
            }
            drawPath(
                path = rodPath,
                color = Color(0xDD7DD3FC),
                style = Stroke(width = if (compact) 4f else 6f, cap = StrokeCap.Round)
            )
            // Stylized leaping fish tail crest above center
            val fishBody = Path().apply {
                moveTo(size.width * 0.42f, size.height * 0.22f)
                quadraticTo(size.width * 0.54f, size.height * 0.02f, size.width * 0.66f, size.height * 0.22f)
                quadraticTo(size.width * 0.54f, size.height * 0.34f, size.width * 0.42f, size.height * 0.22f)
                close()
            }
            drawPath(fishBody, color = Color(0x8838BDF8))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedGameText(
                text = "FISHING",
                fontSize = fishingSize,
                brush = Brush.verticalGradient(listOf(Color.White, Color(0xFF7DD3FC), Color(0xFF0284C7))),
                strokeColor = Color(0xFF072356),
                strokeWidth = if (compact) 9f else 14f,
                italic = true
            )
            Box(modifier = Modifier.offset(y = if (compact) (-6).dp else (-10).dp)) {
                OutlinedGameText(
                    text = "MASTER",
                    fontSize = masterSize,
                    brush = Brush.verticalGradient(listOf(Color(0xFFFEF08A), Color(0xFFFACC15), Color(0xFFD97706))),
                    strokeColor = Color(0xFF072356),
                    strokeWidth = if (compact) 9f else 14f,
                    italic = true
                )
            }
        }
    }
}

@Composable
private fun OutlinedGameText(
    text: String,
    fontSize: TextUnit,
    brush: Brush,
    strokeColor: Color,
    strokeWidth: Float,
    italic: Boolean = false,
    textAlign: TextAlign = TextAlign.Center
) {
    val baseStyle = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = fontSize,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        lineHeight = fontSize * 1.08f,
        textAlign = textAlign
    )
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = baseStyle.copy(
                color = strokeColor,
                drawStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round)
            ),
            textAlign = textAlign
        )
        Text(
            text = text,
            style = baseStyle.copy(brush = brush),
            textAlign = textAlign
        )
    }
}

@Composable
private fun AuthFeatureComparisonCard(
    modifier: Modifier = Modifier,
    title: String,
    headerIcon: ImageVector,
    headerTint: Color,
    bullets: List<String>,
    bottomEmblem: ImageVector,
    borderColor: Color
) {
    Surface(
        color = Color(0x90061C47),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.2.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = headerIcon,
                    contentDescription = null,
                    tint = headerTint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.16f))
            bullets.forEach { bullet ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = bullet,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE2E8F0),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0x5538BDF8), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = bottomEmblem,
                        contentDescription = null,
                        tint = Color(0xFF7DD3FC),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleGMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.22f
        val inset = stroke / 2f
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val topLeft = Offset(inset, inset)

        drawArc(
            color = Color(0xFFEA4335),
            startAngle = -145f,
            sweepAngle = 110f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 140f,
            sweepAngle = 75f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 95f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -35f,
            sweepAngle = 82f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(size.width * 0.52f, size.height * 0.5f),
            end = Offset(size.width * 0.92f, size.height * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}
