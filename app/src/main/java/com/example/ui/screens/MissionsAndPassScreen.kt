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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.GameCatalog
import com.example.domain.MissionCategory
import com.example.domain.PlayerState
import com.example.domain.WeatherType
import com.example.ui.components.AvatarBadge
import com.example.ui.components.weatherIcon
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MissionsAndPassScreen(
    player: PlayerState,
    missionProgress: Map<String, Pair<Int, Boolean>>,
    initialTab: Int = 0,
    onClaimMission: (String) -> Unit,
    onClaimDailyReward: (Int) -> Unit,
    onUnlockPremiumPass: () -> Unit,
    onClaimPassTier: (Int, Boolean) -> Unit,
    onBack: () -> Unit,
    weather: WeatherType = WeatherType.CERAH,
    pingMs: Int = 32,
    onOpenShop: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onSimulateWeather: () -> Unit = {},
    onPlayFishing: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(initialTab.coerceIn(0, 4)) }
    var showNotifDialog by remember { mutableStateOf(false) }

    val claimableDailyMissions = remember(missionProgress) {
        GameCatalog.missions.count { def ->
            def.category == MissionCategory.DAILY &&
                (missionProgress[def.id]?.let { !it.second && it.first >= def.target } == true)
        }
    }
    val claimableAllMissions = remember(missionProgress) {
        GameCatalog.missions.count { def ->
            missionProgress[def.id]?.let { !it.second && it.first >= def.target } == true
        }
    }
    val claimableDailyLoginCount = remember(player.claimedDailyDays, player.loginStreakDay) {
        GameCatalog.dailyRewards.count { it.day !in player.claimedDailyDays && it.day <= player.loginStreakDay }
    }
    val claimablePassCount = remember(player.battlePassXp, player.isPremiumPass, player.claimedFreePassTiers, player.claimedPremiumPassTiers) {
        GameCatalog.battlePassTiers.sumOf { t ->
            val unlocked = player.battlePassXp >= t.requiredXp
            val f = if (unlocked && t.tier !in player.claimedFreePassTiers) 1 else 0
            val p = if (unlocked && player.isPremiumPass && t.tier !in player.claimedPremiumPassTiers) 1 else 0
            f + p
        }
    }
    val totalPending = claimableAllMissions + claimableDailyLoginCount + claimablePassCount

    val formattedCoins = remember(player.coins) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.coins)
    }
    val formattedGems = remember(player.gems) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.gems)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_location_ocean),
            contentDescription = "Latar Misi dan Battle Pass",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xA6032B66), Color(0x66063C85), Color(0xB8021D4A))
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
                MissionsTopHud(
                    player = player,
                    formattedCoins = formattedCoins,
                    formattedGems = formattedGems,
                    weather = weather,
                    pingMs = pingMs,
                    notifCount = totalPending,
                    isCompact = isCompact,
                    onBack = onBack,
                    onOpenShop = onOpenShop,
                    onSimulateWeather = onSimulateWeather,
                    onShowNotifications = { showNotifDialog = true },
                    onOpenSettings = onOpenSettings
                )

                MissionsFolderTabs(
                    selectedTab = selectedTab,
                    claimableDailyBadge = claimableDailyMissions,
                    claimableLoginBadge = claimableDailyLoginCount,
                    claimablePassBadge = claimablePassCount,
                    isCompact = isCompact,
                    onSelectTab = { selectedTab = it }
                )

                val leftWeight = when (selectedTab) {
                    0, 1, 2 -> 0.35f
                    else -> 0.29f
                }
                val centerWeight = if (selectedTab == 3) 0.42f else 0.34f
                val rightWeight = if (selectedTab == 4) 0.42f else 0.31f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MissionsLeftColumn(
                        modifier = Modifier
                            .weight(leftWeight)
                            .fillMaxHeight(),
                        selectedTab = selectedTab,
                        missionProgress = missionProgress,
                        isCompact = isCompact,
                        onClaimMission = onClaimMission,
                        onPlayFishing = onPlayFishing
                    )

                    DailyLoginCenterPanel(
                        modifier = Modifier
                            .weight(centerWeight)
                            .fillMaxHeight(),
                        player = player,
                        highlighted = selectedTab == 3,
                        isCompact = isCompact,
                        onClaimDailyReward = onClaimDailyReward
                    )

                    BattlePassRightPanel(
                        modifier = Modifier
                            .weight(rightWeight)
                            .fillMaxHeight(),
                        player = player,
                        highlighted = selectedTab == 4,
                        isCompact = isCompact,
                        onUnlockPremiumPass = onUnlockPremiumPass,
                        onClaimPassTier = onClaimPassTier,
                        onClaimAllAvailable = {
                            GameCatalog.battlePassTiers.forEach { t ->
                                val unlocked = player.battlePassXp >= t.requiredXp
                                if (unlocked && t.tier !in player.claimedFreePassTiers) {
                                    onClaimPassTier(t.tier, false)
                                }
                                if (unlocked && player.isPremiumPass && t.tier !in player.claimedPremiumPassTiers) {
                                    onClaimPassTier(t.tier, true)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showNotifDialog) {
        AlertDialog(
            onDismissRequest = { showNotifDialog = false },
            containerColor = Color(0xFF0A2454),
            title = { Text("Ringkasan Hadiah", color = Color.White, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• Misi siap diklaim: $claimableAllMissions", color = GoldAccent)
                    Text("• Login Harian tersedia: $claimableDailyLoginCount", color = OceanCyan)
                    Text("• Tier Battle Pass tersedia: $claimablePassCount", color = Color(0xFFE9D5FF))
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotifDialog = false }) {
                    Text("Tutup", color = OceanCyan, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun MissionsTopHud(
    player: PlayerState,
    formattedCoins: String,
    formattedGems: String,
    weather: WeatherType,
    pingMs: Int,
    notifCount: Int,
    isCompact: Boolean,
    onBack: () -> Unit,
    onOpenShop: () -> Unit,
    onSimulateWeather: () -> Unit,
    onShowNotifications: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val circleSize = if (isCompact) 38.dp else 44.dp
    val maxXp = (player.level * 120).coerceAtLeast(120)
    val xpRatio = (player.xp.toFloat() / maxXp).coerceIn(0.05f, 1f)
    val tempC = GameCatalog.locationById(player.activeLocationId).baseTempC

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HudRoundButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                size = circleSize,
                testTag = "missions_back_button",
                onClick = onBack
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AvatarBadge(avatarId = player.avatarId, size = if (isCompact) 38.dp else 44.dp)
                Column(modifier = Modifier.widthIn(max = 155.dp)) {
                    Text(
                        text = player.username,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0284C7),
                            border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                        ) {
                            Text(
                                text = "Lv.${player.level}",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.5.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(84.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF0A2454))
                                .border(1.dp, Color(0x6693C5FD), RoundedCornerShape(50))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(xpRatio)
                                    .background(Brush.horizontalGradient(listOf(GoldAccent, Color(0xFFF59E0B))))
                            )
                            Text(
                                text = "${player.xp}/$maxXp",
                                color = Color.White,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MissionsCurrencyPill(
                icon = Icons.Filled.MonetizationOn,
                iconTint = GoldAccent,
                text = formattedCoins,
                testTag = "missions_coins_pill",
                onClick = onOpenShop
            )
            MissionsCurrencyPill(
                icon = Icons.Filled.Diamond,
                iconTint = Color(0xFFC084FC),
                text = formattedGems,
                testTag = "missions_gems_pill",
                onClick = onOpenShop
            )

            // Weather pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xCC0A2855))
                    .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(50))
                    .clickable(onClick = onSimulateWeather)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = weatherIcon(weather),
                    contentDescription = weather.displayNameId,
                    tint = GoldAccent,
                    modifier = Modifier.size(16.dp)
                )
                Column {
                    Text("$tempC°C", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                    Text(
                        weather.displayNameId.substringBefore("/"),
                        color = Color(0xFFBAE6FD),
                        fontSize = 8.5.sp,
                        maxLines = 1
                    )
                }
            }

            // Ping pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xCC0A2855))
                    .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Filled.SignalCellularAlt, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                Text("${pingMs}ms", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            }

            // Notification Bell + Badge
            Box {
                HudRoundButton(
                    icon = Icons.Filled.Notifications,
                    contentDescription = "Notifikasi Hadiah",
                    size = circleSize,
                    testTag = "missions_notif_button",
                    onClick = onShowNotifications
                )
                if (notifCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(CoralDanger)
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notifCount.coerceAtMost(9).toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            HudRoundButton(
                icon = Icons.Filled.Settings,
                contentDescription = "Pengaturan",
                size = circleSize,
                testTag = "missions_settings_button",
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
private fun HudRoundButton(
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
private fun MissionsCurrencyPill(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xCC0A2855))
            .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(start = 6.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, maxLines = 1)
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(EmeraldSuccess)
                .border(1.dp, Color(0xFFD1FAE5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(11.dp))
        }
    }
}

@Composable
private fun MissionsFolderTabs(
    selectedTab: Int,
    claimableDailyBadge: Int,
    claimableLoginBadge: Int,
    claimablePassBadge: Int,
    isCompact: Boolean,
    onSelectTab: (Int) -> Unit
) {
    val tabItems = listOf(
        Triple("Misi Harian", Icons.AutoMirrored.Filled.Assignment, claimableDailyBadge),
        Triple("Mingguan", Icons.Filled.CalendarMonth, 0),
        Triple("Achievement", Icons.Filled.EmojiEvents, 0),
        Triple("Login Harian", Icons.Filled.CardGiftcard, claimableLoginBadge),
        Triple("Battle Pass", Icons.Filled.WorkspacePremium, claimablePassBadge)
    )
    val tabHeight = if (isCompact) 34.dp else 40.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tabItems.forEachIndexed { idx, (label, icon, badgeCount) ->
            val selected = selectedTab == idx
            val isPassTab = idx == 4
            val bg = when {
                selected && isPassTab -> Brush.verticalGradient(listOf(Color(0xFFD946EF), Color(0xFF7E22CE)))
                selected -> Brush.verticalGradient(listOf(Color(0xFFFFE066), Color(0xFFF59E0B)))
                isPassTab -> Brush.verticalGradient(listOf(Color(0xE6A855F7), Color(0xE66D28D9)))
                else -> Brush.verticalGradient(listOf(Color(0xCC1E4E9B), Color(0xCC0F2E66)))
            }
            val borderColor = when {
                selected -> Color(0xFFFFFBEB)
                isPassTab -> Color(0xFFE879F9)
                else -> Color(0x8893C5FD)
            }
            val fgColor = if (selected && !isPassTab) Color(0xFF0F265C) else Color.White

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(tabHeight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(if (selected) 5.dp else 1.dp, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg)
                        .border(if (selected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
                        .clickable { onSelectTab(idx) }
                        .padding(horizontal = 8.dp)
                        .testTag("missions_tab_$idx"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isPassTab) GoldAccent else fgColor,
                        modifier = Modifier.size(if (isCompact) 16.dp else 18.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = label,
                        color = fgColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = (-3).dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(CoralDanger)
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "!",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
