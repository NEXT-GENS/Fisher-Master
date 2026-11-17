package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.GameSettings
import com.example.domain.PlayerState
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SettingsScreen(
    player: PlayerState,
    settings: GameSettings,
    isOnlineAccount: Boolean,
    syncStatus: String,
    onUpdateProfile: (String, String) -> Unit,
    onUpdateSettings: ((GameSettings) -> GameSettings) -> Unit,
    onResetControls: () -> Unit,
    onSyncNow: () -> Unit,
    onResetOfflineData: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit,
    pingMs: Int = 32,
    onOpenShop: () -> Unit = {}
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    var usernameInput by remember(player.username) { mutableStateOf(player.username) }
    var selectedAvatar by remember(player.avatarId) { mutableStateOf(player.avatarId) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val formattedCoins = remember(player.coins) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.coins)
    }
    val formattedGems = remember(player.gems) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.gems)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_location_lake),
            contentDescription = "Latar Pengaturan",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xA6032B66), Color(0x66063C85), Color(0xC2021D4A))
                    )
                )
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            val isCompact = maxHeight < 430.dp

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp)
            ) {
                SettingsTopHud(
                    formattedCoins = formattedCoins,
                    formattedGems = formattedGems,
                    pingMs = pingMs,
                    isCompact = isCompact,
                    onBack = onBack,
                    onOpenShop = onOpenShop
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left Sidebar Navigation + Angler Cat Mascot
                    SettingsSidebar(
                        modifier = Modifier
                            .weight(0.20f)
                            .fillMaxHeight(),
                        selectedCategory = selectedCategory,
                        isCompact = isCompact,
                        onSelectCategory = { selectedCategory = it }
                    )

                    // Right Content Column (Scrollable glossy cards + footer bar)
                    Column(
                        modifier = Modifier
                            .weight(0.80f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp)
                    ) {
                        val orderedCategories = when (selectedCategory) {
                            1 -> listOf(1, 2, 0, 3)
                            2 -> listOf(2, 1, 0, 3)
                            3 -> listOf(3, 0, 1, 2)
                            else -> listOf(0, 1, 2, 3)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
                        ) {
                            orderedCategories.forEach { cat ->
                                when (cat) {
                                    0 -> AccountProfileSettingsCard(
                                        player = player,
                                        usernameInput = usernameInput,
                                        selectedAvatar = selectedAvatar,
                                        isOnlineAccount = isOnlineAccount,
                                        syncStatus = syncStatus,
                                        highlighted = selectedCategory == 0,
                                        isCompact = isCompact,
                                        onUsernameChange = { usernameInput = it },
                                        onSelectAvatar = { selectedAvatar = it },
                                        onSaveProfile = { onUpdateProfile(usernameInput, selectedAvatar) },
                                        onSyncNow = onSyncNow,
                                        onSignInWithGoogle = onSignInWithGoogle,
                                        onSignOut = onSignOut,
                                        onRequestResetData = { showResetConfirm = true }
                                    )

                                    1 -> AudioGraphicsControlsCard(
                                        settings = settings,
                                        highlighted = selectedCategory == 1,
                                        isCompact = isCompact,
                                        onUpdateSettings = onUpdateSettings,
                                        onResetControls = onResetControls
                                    )

                                    2 -> NotificationsAccessibilityCard(
                                        settings = settings,
                                        highlighted = selectedCategory == 2,
                                        isCompact = isCompact,
                                        onUpdateSettings = onUpdateSettings
                                    )

                                    else -> GameInfoCard(
                                        player = player,
                                        isOnlineAccount = isOnlineAccount,
                                        syncStatus = syncStatus,
                                        highlighted = selectedCategory == 3,
                                        isCompact = isCompact
                                    )
                                }
                            }
                        }

                        // Bottom Save & Auto-save Info Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xCC061A3D))
                                    .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Info, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Pengaturan akan tersimpan secara otomatis.",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .height(if (isCompact) 36.dp else 42.dp)
                                    .shadow(4.dp, RoundedCornerShape(10.dp))
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))))
                                    .border(1.5.dp, Color(0xFFFFFBEB), RoundedCornerShape(10.dp))
                                    .clickable {
                                        onUpdateProfile(usernameInput, selectedAvatar)
                                        onBack()
                                    }
                                    .padding(horizontal = 16.dp)
                                    .testTag("save_settings_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF0F265C), modifier = Modifier.size(16.dp))
                                    Text("Simpan Pengaturan", color = Color(0xFF0F265C), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = OceanDeep,
            title = { Text("Reset Data Lokal?", color = CoralDanger, fontWeight = FontWeight.ExtraBold) },
            text = {
                Text(
                    text = "Semua data lokal di perangkat ini akan dikembalikan ke awal. Akun Google yang sudah disinkronkan tetap aman di cloud.",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetOfflineData()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralDanger, contentColor = Color.White)
                ) {
                    Text("Ya, Reset", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Batal", color = OceanCyan)
                }
            }
        )
    }
}

@Composable
private fun SettingsTopHud(
    formattedCoins: String,
    formattedGems: String,
    pingMs: Int,
    isCompact: Boolean,
    onBack: () -> Unit,
    onOpenShop: () -> Unit
) {
    val circleSize = if (isCompact) 38.dp else 44.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsCircleButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                size = circleSize,
                testTag = "settings_back_button",
                onClick = onBack
            )

            Box(
                modifier = Modifier
                    .size(if (isCompact) 34.dp else 40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7))))
                    .border(1.5.dp, Color(0xFFBAE6FD), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }

            Text(
                text = "Pengaturan",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (isCompact) 18.sp else 21.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsCurrencyPill(
                icon = Icons.Filled.MonetizationOn,
                iconTint = GoldAccent,
                text = formattedCoins,
                onClick = onOpenShop
            )
            SettingsCurrencyPill(
                icon = Icons.Filled.Diamond,
                iconTint = Color(0xFFC084FC),
                text = formattedGems,
                onClick = onOpenShop
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xCC0A2855))
                    .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(50))
                    .padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.Filled.Wifi, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(15.dp))
                Text("${pingMs}ms", color = EmeraldSuccess, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Icon(Icons.Filled.BatteryChargingFull, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(15.dp))
            }

            SettingsCircleButton(
                icon = Icons.Filled.Home,
                contentDescription = "Beranda",
                size = circleSize,
                testTag = "settings_home_button",
                onClick = onBack
            )
        }
    }
}

@Composable
private fun SettingsCircleButton(
    icon: ImageVector,
    contentDescription: String,
    size: Dp,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8))))
            .border(1.5.dp, Color(0xFFBFDBFE), CircleShape)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = Color.White, modifier = Modifier.size(size * 0.54f))
    }
}

@Composable
private fun SettingsCurrencyPill(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xCC0A2855))
            .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(start = 7.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(EmeraldSuccess)
                .border(1.dp, Color(0xFFD1FAE5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
private fun SettingsSidebar(
    modifier: Modifier,
    selectedCategory: Int,
    isCompact: Boolean,
    onSelectCategory: (Int) -> Unit
) {
    val navItems = listOf(
        "Akun &\nProfil" to Icons.Filled.People,
        "Audio, Grafis &\nKontrol" to Icons.AutoMirrored.Filled.VolumeUp,
        "Notifikasi &\nAksesibilitas" to Icons.Filled.Notifications,
        "Info Game" to Icons.Filled.Info
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp)
        ) {
            navItems.forEachIndexed { idx, (label, icon) ->
                val active = selectedCategory == idx
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isCompact) 42.dp else 50.dp)
                        .shadow(if (active) 5.dp else 1.dp, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (active) Brush.verticalGradient(listOf(Color(0xFFFFE066), Color(0xFFF59E0B)))
                            else Brush.verticalGradient(listOf(Color(0xCC1E4E9B), Color(0xCC0D2857)))
                        )
                        .border(
                            width = if (active) 1.5.dp else 1.dp,
                            color = if (active) Color(0xFFFFFBEB) else Color(0x887DD3FC),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectCategory(idx) }
                        .padding(horizontal = 8.dp)
                        .testTag("settings_nav_$idx"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (active) Color(0xFF451A03) else Color(0xFFBAE6FD),
                        modifier = Modifier.size(if (isCompact) 18.dp else 21.dp)
                    )
                    Text(
                        text = label,
                        color = if (active) Color(0xFF0F265C) else Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (isCompact) 10.sp else 11.sp,
                        lineHeight = 12.sp,
                        maxLines = 2
                    )
                }
            }
        }

        // Cute Cat Mascot + Speech Bubble at bottom-left
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SettingsCatMascotCanvas(
                modifier = Modifier.size(if (isCompact) 52.dp else 68.dp)
            )
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFDF8E8),
                border = BorderStroke(1.5.dp, Color(0xFFFDE047))
            ) {
                Text(
                    text = "Selamat memancing!",
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 9.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
