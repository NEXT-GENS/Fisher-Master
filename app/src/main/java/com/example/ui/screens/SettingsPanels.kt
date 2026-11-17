package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AppLanguage
import com.example.domain.AudioMode
import com.example.domain.ControlLayoutMode
import com.example.domain.GameCatalog
import com.example.domain.GameSettings
import com.example.domain.GraphicsQuality
import com.example.domain.PlayerState
import com.example.ui.components.AvatarBadge
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan

@Composable
fun AccountProfileSettingsCard(
    player: PlayerState,
    usernameInput: String,
    selectedAvatar: String,
    isOnlineAccount: Boolean,
    syncStatus: String,
    highlighted: Boolean,
    isCompact: Boolean,
    onUsernameChange: (String) -> Unit,
    onSelectAvatar: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onSyncNow: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onRequestResetData: () -> Unit
) {
    SettingsCardShell(
        icon = Icons.Filled.Person,
        title = "Akun & Profil Angler",
        highlighted = highlighted,
        isCompact = isCompact
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Avatar + Player Name + Level + Logout / Reset Data
            Column(
                modifier = Modifier.weight(1.1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AvatarBadge(selectedAvatar, size = if (isCompact) 54.dp else 62.dp)
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7))
                                .border(1.dp, Color.White, CircleShape)
                                .clickable(onClick = onSaveProfile),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Simpan Profil", tint = Color.White, modifier = Modifier.size(11.dp))
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Nama Pemain", color = Color(0xFFBAE6FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color(0xFF0B2454))
                                .border(1.dp, Color(0x667DD3FC), RoundedCornerShape(7.dp))
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = usernameInput,
                                onValueChange = onUsernameChange,
                                singleLine = true,
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                cursorBrush = SolidColor(GoldAccent),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Simpan Nama",
                                tint = Color(0xFF93C5FD),
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable(onClick = onSaveProfile)
                            )
                        }
                        Text(
                            text = "ID Akun: ${player.userId.take(12)}",
                            color = Color(0xFFBAE6FD),
                            fontSize = 9.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                            Text("Level: ${player.level}", color = Color(0xFFFEF08A), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isOnlineAccount) {
                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.verticalGradient(listOf(Color(0xFFEF4444), Color(0xFFB91C1C))))
                                .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                                .clickable(onClick = onSignOut)
                                .padding(horizontal = 6.dp)
                                .testTag("signout_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Text("Logout Akun", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, maxLines = 1)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))))
                                .border(1.dp, Color(0xFFFFFBEB), RoundedCornerShape(8.dp))
                                .clickable(onClick = onSignInWithGoogle)
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color(0xFF0F265C), modifier = Modifier.size(14.dp))
                                Text("Login Google", color = Color(0xFF0F265C), fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, maxLines = 1)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF64748B), Color(0xFF334155))))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                            .clickable(onClick = onRequestResetData)
                            .padding(horizontal = 6.dp)
                            .testTag("reset_local_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Column {
                                Text("Reset Data Lokal", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, maxLines = 1)
                                Text("Hapus data perangkat", color = Color(0xFFE2E8F0), fontSize = 7.5.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Middle: Avatar Selector Strip
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x880B2454))
                    .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(10.dp))
                    .padding(7.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text("Avatar", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    GameCatalog.avatars.forEach { (id, label) ->
                        val active = selectedAvatar == id
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) Color(0x991E40AF) else Color(0x88091C42))
                                .border(
                                    width = if (active) 2.dp else 1.dp,
                                    color = if (active) GoldAccent else Color(0x5593C5FD),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSelectAvatar(id)
                                    onSaveProfile()
                                }
                                .padding(vertical = 5.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            AvatarBadge(id, size = if (isCompact) 38.dp else 44.dp, shape = RoundedCornerShape(8.dp))
                            Text(
                                text = label.substringBefore(" "),
                                color = if (active) Color(0xFFFEF08A) else Color(0xFFBAE6FD),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Right: Sinkronkan Cloud & Hubungkan Google
            Column(
                modifier = Modifier
                    .weight(0.95f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x880B2454))
                    .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(10.dp))
                    .padding(7.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.CloudSync, contentDescription = null, tint = OceanCyan, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Sinkronkan Cloud", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.5.sp)
                        Text(syncStatus, color = Color(0xFFBAE6FD), fontSize = 8.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8))))
                        .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(7.dp))
                        .clickable {
                            if (isOnlineAccount) onSyncNow() else onSignInWithGoogle()
                        }
                        .testTag("sync_now_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isOnlineAccount) "Sinkron Sekarang" else "Aktifkan Cloud",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color(0xFF1D4ED8), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                    Column {
                        Text(
                            text = if (isOnlineAccount) "Google Terhubung" else "Hubungkan Google",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp
                        )
                        Text(
                            text = if (isOnlineAccount) "Akun aman & tersinkron" else "Amankan akun Anda",
                            color = if (isOnlineAccount) EmeraldSuccess else Color(0xFFBAE6FD),
                            fontSize = 8.5.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            if (isOnlineAccount) Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF047857)))
                            else Brush.verticalGradient(listOf(Color(0xFF4ADE80), Color(0xFF16A34A)))
                        )
                        .border(1.dp, Color(0xFFDCFCE7), RoundedCornerShape(7.dp))
                        .clickable(enabled = !isOnlineAccount, onClick = onSignInWithGoogle),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isOnlineAccount) "✓ Terhubung" else "Hubungkan",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AudioGraphicsControlsCard(
    settings: GameSettings,
    highlighted: Boolean,
    isCompact: Boolean,
    onUpdateSettings: ((GameSettings) -> GameSettings) -> Unit,
    onResetControls: () -> Unit
) {
    var langExpanded by remember { mutableStateOf(false) }

    SettingsCardShell(
        icon = Icons.AutoMirrored.Filled.VolumeUp,
        title = "Audio, Grafis & Kontrol",
        highlighted = highlighted,
        isCompact = isCompact
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Column 1: Audio (BGM, SFX, Mode Audio)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CompactSettingSlider(
                    label = "Volume Musik Latar (BGM)",
                    value = settings.bgmVolume,
                    valueRange = 0f..1f,
                    valueLabel = "${(settings.bgmVolume * 100).toInt()}%",
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    onValueChange = { v -> onUpdateSettings { it.copy(bgmVolume = v) } }
                )
                CompactSettingSlider(
                    label = "Volume Efek Suara (SFX)",
                    value = settings.sfxVolume,
                    valueRange = 0f..1f,
                    valueLabel = "${(settings.sfxVolume * 100).toInt()}%",
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    onValueChange = { v -> onUpdateSettings { it.copy(sfxVolume = v) } }
                )
                Text("Mode Audio", color = Color(0xFFBAE6FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AudioMode.entries.forEach { mode ->
                        SettingsSegmentChip(
                            label = mode.label,
                            selected = settings.audioMode == mode,
                            accentGold = false,
                            modifier = Modifier.weight(1f),
                            onClick = { onUpdateSettings { it.copy(audioMode = mode) } }
                        )
                    }
                }
            }

            // Column 2: Kualitas Grafis, FPS, Sensitivitas
            Column(modifier = Modifier.weight(1.05f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Kualitas Grafis", color = Color(0xFFBAE6FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    GraphicsQuality.entries.forEach { q ->
                        SettingsSegmentChip(
                            label = q.label,
                            selected = settings.graphicsQuality == q,
                            accentGold = q == GraphicsQuality.ULTRA,
                            modifier = Modifier.weight(1f),
                            onClick = { onUpdateSettings { it.copy(graphicsQuality = q) } }
                        )
                    }
                }

                Text("FPS", color = Color(0xFFBAE6FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(30, 60, 120).forEach { fps ->
                        SettingsSegmentChip(
                            label = "$fps FPS",
                            selected = settings.targetFps == fps,
                            accentGold = false,
                            modifier = Modifier.weight(1f),
                            onClick = { onUpdateSettings { it.copy(targetFps = fps) } }
                        )
                    }
                }

                CompactSettingSlider(
                    label = "Sensitivitas Kamera/Tarikan",
                    value = settings.cameraSensitivity,
                    valueRange = 0.2f..1.0f,
                    valueLabel = "${(settings.cameraSensitivity * 100).toInt()}%",
                    icon = Icons.Filled.Tune,
                    onValueChange = { v -> onUpdateSettings { it.copy(cameraSensitivity = v) } }
                )
            }

            // Column 3: Layout Kontrol, Reset Kontrol, Bahasa
            Column(modifier = Modifier.weight(0.95f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Layout Kontrol", color = Color(0xFFBAE6FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ControlLayoutMode.entries.forEach { mode ->
                        SettingsSegmentChip(
                            label = mode.label,
                            selected = settings.controlLayout == mode,
                            accentGold = false,
                            modifier = Modifier.weight(1f),
                            onClick = { onUpdateSettings { it.copy(controlLayout = mode) } }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8))))
                        .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
                        .clickable(onClick = onResetControls)
                        .testTag("reset_controls_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(Icons.Filled.RestartAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Text("Reset Kontrol", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }

                Text("Bahasa", color = Color(0xFFBAE6FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0B2454))
                            .border(1.dp, Color(0x667DD3FC), RoundedCornerShape(8.dp))
                            .clickable { langExpanded = true }
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Public, contentDescription = null, tint = OceanCyan, modifier = Modifier.size(14.dp))
                            Text(settings.language.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Pilih Bahasa", tint = Color(0xFFBAE6FD), modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = langExpanded,
                        onDismissRequest = { langExpanded = false }
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang.label, fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    onUpdateSettings { it.copy(language = lang) }
                                    langExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsAccessibilityCard(
    settings: GameSettings,
    highlighted: Boolean,
    isCompact: Boolean,
    onUpdateSettings: ((GameSettings) -> GameSettings) -> Unit
) {
    SettingsCardShell(
        icon = Icons.Filled.Notifications,
        iconTint = GoldAccent,
        title = "Notifikasi & Aksesibilitas",
        highlighted = highlighted,
        isCompact = isCompact
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.05f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CompactSwitchRow(
                    label = "Notifikasi Event & Teman",
                    checked = settings.notifyEvents,
                    onCheckedChange = { c -> onUpdateSettings { it.copy(notifyEvents = c, notifyFriends = c) } }
                )
                CompactSwitchRow(
                    label = "Kurangi Efek Partikel Cuaca",
                    checked = settings.reduceWeatherEffects,
                    onCheckedChange = { c -> onUpdateSettings { it.copy(reduceWeatherEffects = c) } }
                )
            }

            Column(modifier = Modifier.weight(1.05f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CompactSwitchRow(
                    label = "Mode Buta Warna",
                    checked = settings.colorblindMode,
                    onCheckedChange = { c -> onUpdateSettings { it.copy(colorblindMode = c) } }
                )
                CompactSwitchRow(
                    label = "Getaran Haptik saat Gigitan",
                    checked = settings.hapticFeedback,
                    onCheckedChange = { c -> onUpdateSettings { it.copy(hapticFeedback = c) } }
                )
            }

            Column(modifier = Modifier.weight(0.9f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Skala Ukuran Teks UI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Aa", color = Color(0xFFBAE6FD), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    Slider(
                        value = settings.textScale,
                        valueRange = 0.85f..1.25f,
                        onValueChange = { v -> onUpdateSettings { it.copy(textScale = v) } },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF38BDF8),
                            inactiveTrackColor = Color(0xFF0A2454)
                        ),
                        modifier = Modifier.weight(1f).height(26.dp)
                    )
                    Text("${(settings.textScale * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun GameInfoCard(
    player: PlayerState,
    isOnlineAccount: Boolean,
    syncStatus: String,
    highlighted: Boolean,
    isCompact: Boolean
) {
    SettingsCardShell(
        icon = Icons.Filled.Info,
        iconTint = OceanCyan,
        title = "Info Game & Keamanan Akun",
        highlighted = highlighted,
        isCompact = isCompact
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoBadgePill(
                icon = Icons.Filled.Phishing,
                title = "Fishing Master",
                subtitle = "Versi 1.1.1 • Build Rilis",
                modifier = Modifier.weight(1f)
            )
            InfoBadgePill(
                icon = Icons.Filled.Shield,
                title = if (isOnlineAccount) "Akun Google Aktif" else "Mode Tamu Lokal",
                subtitle = syncStatus,
                modifier = Modifier.weight(1f)
            )
            InfoBadgePill(
                icon = Icons.Filled.SportsEsports,
                title = "Total Tangkapan: ${player.totalCatches}",
                subtitle = "Rekor: ${player.biggestCatchWeightKg} kg",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun InfoBadgePill(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(Color(0x880B2454))
            .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(9.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(icon, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
            Text(subtitle, color = Color(0xFFBAE6FD), fontSize = 9.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SettingsCardShell(
    icon: ImageVector,
    title: String,
    highlighted: Boolean,
    isCompact: Boolean,
    iconTint: Color = Color(0xFFBAE6FD),
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(Brush.verticalGradient(listOf(Color(0xE0144496), Color(0xEE08214F))))
            .border(
                width = if (highlighted) 2.dp else 1.2.dp,
                color = if (highlighted) GoldAccent else Color(0x887DD3FC),
                shape = RoundedCornerShape(13.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF1E50A6), Color(0xFF0E3470))))
                .padding(horizontal = 10.dp, vertical = if (isCompact) 4.dp else 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.5.sp)
        }
        Box(modifier = Modifier.padding(if (isCompact) 7.dp else 9.dp)) {
            content()
        }
    }
}

@Composable
private fun CompactSettingSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueLabel: String,
    icon: ImageVector,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, tint = Color(0xFFBAE6FD), modifier = Modifier.size(14.dp))
            Slider(
                value = value,
                valueRange = valueRange,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF38BDF8),
                    inactiveTrackColor = Color(0xFF0A2454)
                ),
                modifier = Modifier.weight(1f).height(24.dp)
            )
            Text(valueLabel, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun SettingsSegmentChip(
    label: String,
    selected: Boolean,
    accentGold: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val bg = when {
        selected && accentGold -> Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706)))
        selected -> Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
        else -> Brush.verticalGradient(listOf(Color(0xFF173A7A), Color(0xFF0C224E)))
    }
    val textColor = if (selected && accentGold) Color(0xFF0F265C) else Color.White

    Box(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(bg)
            .border(1.dp, if (selected) Color(0xFFFFFBEB) else Color(0x5593C5FD), RoundedCornerShape(7.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, maxLines = 1)
    }
}

@Composable
private fun CompactSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x77091D42))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EmeraldSuccess,
                uncheckedThumbColor = Color(0xFFE2E8F0),
                uncheckedTrackColor = Color(0xFF475569)
            )
        )
    }
}

@Composable
fun SettingsCatMascotCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawArc(
            color = Color(0xFF1D4ED8),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.14f, h * 0.62f),
            size = Size(w * 0.72f, h * 0.65f)
        )
        val leftEar = Path().apply {
            moveTo(w * 0.22f, h * 0.36f)
            lineTo(w * 0.20f, h * 0.12f)
            lineTo(w * 0.40f, h * 0.26f)
            close()
        }
        val rightEar = Path().apply {
            moveTo(w * 0.78f, h * 0.36f)
            lineTo(w * 0.80f, h * 0.12f)
            lineTo(w * 0.60f, h * 0.26f)
            close()
        }
        drawPath(leftEar, color = Color(0xFFF59E0B))
        drawPath(rightEar, color = Color(0xFFF59E0B))
        drawCircle(color = Color(0xFFFBBF24), radius = w * 0.31f, center = Offset(w * 0.5f, h * 0.46f))
        drawOval(color = Color(0xFFFFFBEB), topLeft = Offset(w * 0.24f, h * 0.42f), size = Size(w * 0.52f, h * 0.30f))
        drawArc(
            color = Color(0xFF0284C7),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.22f, h * 0.12f),
            size = Size(w * 0.56f, h * 0.34f)
        )
        drawOval(color = Color(0xFF1E3A8A), topLeft = Offset(w * 0.30f, h * 0.26f), size = Size(w * 0.52f, h * 0.10f))
        drawArc(
            color = Color(0xFF1E293B),
            startAngle = 190f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.33f, h * 0.44f),
            size = Size(w * 0.11f, h * 0.08f),
            style = Stroke(width = w * 0.04f, cap = StrokeCap.Round)
        )
        drawArc(
            color = Color(0xFF1E293B),
            startAngle = 190f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.56f, h * 0.44f),
            size = Size(w * 0.11f, h * 0.08f),
            style = Stroke(width = w * 0.04f, cap = StrokeCap.Round)
        )
        drawCircle(color = Color(0xFFEC4899), radius = w * 0.035f, center = Offset(w * 0.5f, h * 0.55f))
    }
}
