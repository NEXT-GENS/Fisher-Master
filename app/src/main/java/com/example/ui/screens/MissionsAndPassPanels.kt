package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.BattlePassTier
import com.example.domain.DailyRewardDefinition
import com.example.domain.GameCatalog
import com.example.domain.MissionCategory
import com.example.domain.MissionDefinition
import com.example.domain.PlayerState
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EpicPurple
import com.example.ui.theme.GoldAccent

@Composable
fun MissionsLeftColumn(
    modifier: Modifier,
    selectedTab: Int,
    missionProgress: Map<String, Pair<Int, Boolean>>,
    isCompact: Boolean,
    onClaimMission: (String) -> Unit,
    onPlayFishing: () -> Unit
) {
    val dailyMissions = GameCatalog.missions.filter { it.category == MissionCategory.DAILY }
    val weeklyMissions = GameCatalog.missions.filter { it.category == MissionCategory.WEEKLY }
    val achievementMissions = GameCatalog.missions.filter { it.category == MissionCategory.ACHIEVEMENT }

    val totalDone = GameCatalog.missions.count { def ->
        val st = missionProgress[def.id] ?: (0 to false)
        st.second || st.first >= def.target
    }

    val orderedSections = when (selectedTab) {
        1 -> listOf(MissionCategory.WEEKLY, MissionCategory.DAILY, MissionCategory.ACHIEVEMENT)
        2 -> listOf(MissionCategory.ACHIEVEMENT, MissionCategory.DAILY, MissionCategory.WEEKLY)
        else -> listOf(MissionCategory.DAILY, MissionCategory.WEEKLY, MissionCategory.ACHIEVEMENT)
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
    ) {
        orderedSections.forEach { cat ->
            when (cat) {
                MissionCategory.DAILY -> MissionSectionCard(
                    title = "Misi Harian",
                    badgeText = "Reset dalam: 01:45:12",
                    headerIcon = Icons.Filled.Schedule,
                    highlighted = selectedTab == 0,
                    missions = dailyMissions,
                    missionProgress = missionProgress,
                    isCompact = isCompact,
                    onClaimMission = onClaimMission,
                    onPlayFishing = onPlayFishing
                )
                MissionCategory.WEEKLY -> MissionSectionCard(
                    title = "Misi Mingguan",
                    badgeText = "Reset dalam: 3h 45m",
                    headerIcon = Icons.Filled.Schedule,
                    highlighted = selectedTab == 1,
                    missions = weeklyMissions,
                    missionProgress = missionProgress,
                    isCompact = isCompact,
                    onClaimMission = onClaimMission,
                    onPlayFishing = onPlayFishing
                )
                MissionCategory.ACHIEVEMENT -> AchievementSectionCard(
                    totalDone = totalDone,
                    totalCount = GameCatalog.missions.size,
                    highlighted = selectedTab == 2,
                    missions = achievementMissions,
                    missionProgress = missionProgress,
                    isCompact = isCompact,
                    onClaimMission = onClaimMission,
                    onPlayFishing = onPlayFishing
                )
            }
        }
    }
}

@Composable
private fun MissionSectionCard(
    title: String,
    badgeText: String,
    headerIcon: ImageVector,
    highlighted: Boolean,
    missions: List<MissionDefinition>,
    missionProgress: Map<String, Pair<Int, Boolean>>,
    isCompact: Boolean,
    onClaimMission: (String) -> Unit,
    onPlayFishing: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xD9123B82), Color(0xEB08214F)))
            )
            .border(
                width = if (highlighted) 1.5.dp else 1.dp,
                color = if (highlighted) Color(0xFF93C5FD) else Color(0x667DD3FC),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF0E3470))))
                .padding(horizontal = 10.dp, vertical = if (isCompact) 5.dp else 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(GoldAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(headerIcon, contentDescription = null, tint = Color(0xFF0F265C), modifier = Modifier.size(13.dp))
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFFBAE6FD), modifier = Modifier.size(11.dp))
                Text(
                    text = badgeText,
                    color = Color(0xFFFEF08A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp,
                    maxLines = 1
                )
            }
        }

        Column(
            modifier = Modifier.padding(if (isCompact) 6.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 6.dp)
        ) {
            missions.forEach { m ->
                MissionItemRow(
                    mission = m,
                    progressPair = missionProgress[m.id] ?: (0 to false),
                    isCompact = isCompact,
                    onClaimMission = onClaimMission,
                    onPlayFishing = onPlayFishing
                )
            }
        }
    }
}

@Composable
private fun AchievementSectionCard(
    totalDone: Int,
    totalCount: Int,
    highlighted: Boolean,
    missions: List<MissionDefinition>,
    missionProgress: Map<String, Pair<Int, Boolean>>,
    isCompact: Boolean,
    onClaimMission: (String) -> Unit,
    onPlayFishing: () -> Unit
) {
    val ratio = (totalDone.toFloat() / totalCount.coerceAtLeast(1)).coerceIn(0.05f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xD9123B82), Color(0xEB08214F))))
            .border(
                width = if (highlighted) 1.5.dp else 1.dp,
                color = if (highlighted) GoldAccent else Color(0x667DD3FC),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF0E3470))))
                .padding(horizontal = 10.dp, vertical = if (isCompact) 5.dp else 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                Text(
                    text = "Achievement",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
            Text(
                text = "Progres Total: $totalDone / $totalCount",
                color = Color(0xFFFEF08A),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }

        Column(
            modifier = Modifier.padding(if (isCompact) 6.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))))
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color(0xFF0F265C), modifier = Modifier.size(16.dp))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF091D42))
                        .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(50))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(ratio)
                            .background(Brush.horizontalGradient(listOf(GoldAccent, Color(0xFFF59E0B))))
                    )
                }
            }

            missions.forEach { m ->
                MissionItemRow(
                    mission = m,
                    progressPair = missionProgress[m.id] ?: (0 to false),
                    isCompact = isCompact,
                    onClaimMission = onClaimMission,
                    onPlayFishing = onPlayFishing
                )
            }
        }
    }
}

@Composable
private fun MissionItemRow(
    mission: MissionDefinition,
    progressPair: Pair<Int, Boolean>,
    isCompact: Boolean,
    onClaimMission: (String) -> Unit,
    onPlayFishing: () -> Unit
) {
    val progress = progressPair.first.coerceAtMost(mission.target)
    val claimed = progressPair.second
    val canClaim = !claimed && progress >= mission.target
    val ratio = (progress.toFloat() / mission.target.coerceAtLeast(1)).coerceIn(0.04f, 1f)

    val icon = when {
        mission.id.contains("sell") -> Icons.Filled.MonetizationOn
        mission.id.contains("upgrade") -> Icons.Filled.Hardware
        mission.id.contains("legendary") -> Icons.Filled.EmojiEvents
        mission.id.contains("fishdex") -> Icons.Filled.AutoAwesome
        mission.id.contains("multi") -> Icons.Filled.Groups
        else -> Icons.Filled.Phishing
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (canClaim) Color(0xCC1E40AF) else Color(0xAA0C2654))
            .border(
                width = if (canClaim) 1.5.dp else 1.dp,
                color = if (canClaim) GoldAccent else Color(0x447DD3FC),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 7.dp, vertical = if (isCompact) 5.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isCompact) 28.dp else 32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E3A8A))
                .border(1.dp, Color(0x6693C5FD), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(17.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mission.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF071530))
                    .border(1.dp, Color(0x447DD3FC), RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(ratio)
                        .background(
                            Brush.horizontalGradient(
                                if (claimed) listOf(EmeraldSuccess, Color(0xFF059669))
                                else listOf(GoldAccent, Color(0xFFF59E0B))
                            )
                        )
                )
                Text(
                    text = "$progress / ${mission.target}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 8.5.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(11.dp))
                Text("${mission.rewardCoins}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Filled.Diamond, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(11.dp))
                Text("${mission.rewardGems}", color = Color(0xFFE9D5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .size(if (isCompact) 32.dp else 36.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(
                    when {
                        claimed -> Brush.verticalGradient(listOf(EmeraldSuccess, Color(0xFF047857)))
                        canClaim -> Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706)))
                        else -> Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8)))
                    }
                )
                .border(1.dp, Color(0xAAFFFFFF), RoundedCornerShape(9.dp))
                .clickable {
                    if (canClaim) onClaimMission(mission.id) else if (!claimed) onPlayFishing()
                }
                .testTag("claim_${mission.id}"),
            contentAlignment = Alignment.Center
        ) {
            if (claimed) {
                Icon(Icons.Filled.Check, contentDescription = "Diklaim", tint = Color.White, modifier = Modifier.size(18.dp))
            } else if (canClaim) {
                Text("Klaim", color = Color(0xFF0F265C), fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Main untuk selesaikan misi",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DailyLoginCenterPanel(
    modifier: Modifier,
    player: PlayerState,
    highlighted: Boolean,
    isCompact: Boolean,
    onClaimDailyReward: (Int) -> Unit
) {
    val topDays = GameCatalog.dailyRewards.take(4)
    val bottomDays = GameCatalog.dailyRewards.drop(4)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xE0134292), Color(0xEE08214F))))
            .border(
                width = if (highlighted) 2.dp else 1.5.dp,
                color = if (highlighted) GoldAccent else Color(0x997DD3FC),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF0E3470))))
                .padding(horizontal = 10.dp, vertical = if (isCompact) 6.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                Column {
                    Text(
                        text = "Login Harian 7 Hari",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "Login setiap hari untuk hadiah menarik!",
                        color = Color(0xFFBAE6FD),
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x99061A3D),
                border = BorderStroke(1.dp, Color(0x557DD3FC))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(11.dp))
                    Text("6j 45m", color = Color(0xFFFEF08A), fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(if (isCompact) 6.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                topDays.forEach { reward ->
                    DailyRewardTile(
                        reward = reward,
                        isClaimed = reward.day in player.claimedDailyDays,
                        isAvailable = reward.day !in player.claimedDailyDays && reward.day <= player.loginStreakDay,
                        isGrand = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onClaimDailyReward(reward.day) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.08f),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                bottomDays.forEach { reward ->
                    val isDay7 = reward.day == 7
                    DailyRewardTile(
                        reward = reward,
                        isClaimed = reward.day in player.claimedDailyDays,
                        isAvailable = reward.day !in player.claimedDailyDays && reward.day <= player.loginStreakDay,
                        isGrand = isDay7,
                        modifier = Modifier
                            .weight(if (isDay7) 1.25f else 1f)
                            .fillMaxHeight(),
                        onClick = { onClaimDailyReward(reward.day) }
                    )
                }
            }

            // Cute Angler Cat Mascot + Speech Bubble Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 50.dp else 62.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0x880C3A78), Color(0x440C3A78))))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnglerCatMascotCanvas(
                    modifier = Modifier.size(if (isCompact) 44.dp else 54.dp)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFDF8E8),
                    border = BorderStroke(1.5.dp, Color(0xFFFDE047)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Login 7 hari berturut-turut untuk hadiah spesial!",
                        color = Color(0xFF1E293B),
                        fontSize = if (isCompact) 10.sp else 11.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyRewardTile(
    reward: DailyRewardDefinition,
    isClaimed: Boolean,
    isAvailable: Boolean,
    isGrand: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val bgBrush = when {
        isGrand -> Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706)))
        isAvailable -> Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1E4E9B), Color(0xFF0C2A63)))
    }
    val borderColor = when {
        isAvailable -> Color(0xFFFEF08A)
        isGrand -> Color(0xFFFFF3B0)
        isClaimed -> EmeraldSuccess
        else -> Color(0x6693C5FD)
    }

    val rewardIcon = when (reward.day) {
        1 -> Icons.Filled.MonetizationOn
        2, 5 -> Icons.Filled.Diamond
        3 -> Icons.Filled.Phishing
        4, 7 -> Icons.Filled.Inventory2
        else -> Icons.Filled.Hardware
    }
    val iconTint = when (reward.day) {
        1, 7 -> GoldAccent
        2, 5 -> Color(0xFFD8B4FE)
        3 -> Color(0xFFF87171)
        else -> Color(0xFF7DD3FC)
    }

    Column(
        modifier = modifier
            .shadow(if (isAvailable || isGrand) 4.dp else 1.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(bgBrush)
            .border(if (isAvailable || isGrand) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = isAvailable, onClick = onClick)
            .padding(4.dp)
            .testTag("daily_reward_day_${reward.day}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Hari ${reward.day}",
            color = if (isGrand) Color(0xFF451A03) else Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.sp,
            maxLines = 1
        )

        Icon(
            imageVector = rewardIcon,
            contentDescription = reward.title,
            tint = if (isGrand) Color(0xFF78350F) else iconTint,
            modifier = Modifier.size(if (isGrand) 28.dp else 22.dp)
        )

        Text(
            text = if (reward.gems > 0 && reward.day != 1) "+${reward.gems} Gem" else "+${reward.coins}",
            color = if (isGrand) Color(0xFF451A03) else Color(0xFFFEF08A),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 9.5.sp,
            maxLines = 1
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(
                    when {
                        isClaimed -> EmeraldSuccess
                        isAvailable -> GoldAccent
                        else -> Color(0x99061A3D)
                    }
                )
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    isClaimed -> "✓ Diklaim"
                    isAvailable -> "Klaim!"
                    else -> "Hari ${reward.day}"
                },
                color = if (isAvailable) Color(0xFF0F265C) else Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 8.5.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun BattlePassRightPanel(
    modifier: Modifier,
    player: PlayerState,
    highlighted: Boolean,
    isCompact: Boolean,
    onUnlockPremiumPass: () -> Unit,
    onClaimPassTier: (Int, Boolean) -> Unit,
    onClaimAllAvailable: () -> Unit
) {
    val hasClaimable = GameCatalog.battlePassTiers.any { t ->
        val unlocked = player.battlePassXp >= t.requiredXp
        val freeReady = unlocked && t.tier !in player.claimedFreePassTiers
        val premReady = unlocked && player.isPremiumPass && t.tier !in player.claimedPremiumPassTiers
        freeReady || premReady
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xE63B1D7A), Color(0xF0151E54))))
            .border(
                width = if (highlighted) 2.dp else 1.5.dp,
                color = if (highlighted) Color(0xFFE879F9) else Color(0x99C084FC),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF6D28D9), Color(0xFF4338CA))))
                .padding(horizontal = 10.dp, vertical = if (isCompact) 6.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                Column {
                    Text(
                        text = "Battle Pass",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp
                    )
                    Text(
                        text = "${player.battlePassXp} XP • Klaim hadiah eksklusif!",
                        color = Color(0xFFE9D5FF),
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x991E1B4B),
                border = BorderStroke(1.dp, Color(0x66C084FC))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(11.dp))
                    Text("23j 45m", color = Color(0xFFFEF08A), fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Free / Premium Track Headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x991E3A8A))
                    .border(1.dp, Color(0x6693C5FD), RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFBAE6FD), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Free", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFFD97706))))
                    .border(1.dp, GoldAccent, RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = Color(0xFFFEF08A), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Premium", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
            }
        }

        // Tier Track
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(GameCatalog.battlePassTiers, key = { it.tier }) { tier ->
                BattlePassTierRow(
                    tier = tier,
                    unlocked = player.battlePassXp >= tier.requiredXp,
                    isPremiumActive = player.isPremiumPass,
                    freeClaimed = tier.tier in player.claimedFreePassTiers,
                    premClaimed = tier.tier in player.claimedPremiumPassTiers,
                    isCompact = isCompact,
                    onClaimFree = { onClaimPassTier(tier.tier, false) },
                    onClaimPrem = { onClaimPassTier(tier.tier, true) }
                )
            }
        }

        // Bottom Actions: Aktivasi Premium Pass + Klaim Semua
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xAA170E3B))
                .padding(horizontal = 8.dp, vertical = if (isCompact) 6.dp else 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .height(if (isCompact) 40.dp else 44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (player.isPremiumPass) {
                            Brush.verticalGradient(listOf(EmeraldSuccess, Color(0xFF047857)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFFC026D3), Color(0xFF7E22CE)))
                        }
                    )
                    .border(1.5.dp, Color(0xFFF5D0FE), RoundedCornerShape(10.dp))
                    .clickable(enabled = !player.isPremiumPass, onClick = onUnlockPremiumPass)
                    .padding(horizontal = 8.dp)
                    .testTag("activate_premium_pass_button"),
                contentAlignment = Alignment.Center
            ) {
                if (player.isPremiumPass) {
                    Text(
                        text = "Premium Aktif ✓",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Aktivasi Premium Pass",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Filled.Diamond, contentDescription = null, tint = Color(0xFFE9D5FF), modifier = Modifier.size(11.dp))
                            Text("40", color = Color(0xFFFEF08A), fontWeight = FontWeight.ExtraBold, fontSize = 10.5.sp)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .height(if (isCompact) 40.dp else 44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (hasClaimable) {
                            Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (hasClaimable) Color(0xFFFFFBEB) else Color(0x88CBD5E1),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(enabled = hasClaimable, onClick = onClaimAllAvailable)
                    .testTag("claim_all_pass_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Klaim Semua",
                    color = if (hasClaimable) Color(0xFF0F265C) else Color(0xFFE2E8F0),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.5.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun BattlePassTierRow(
    tier: BattlePassTier,
    unlocked: Boolean,
    isPremiumActive: Boolean,
    freeClaimed: Boolean,
    premClaimed: Boolean,
    isCompact: Boolean,
    onClaimFree: () -> Unit,
    onClaimPrem: () -> Unit
) {
    val freeReady = unlocked && !freeClaimed
    val premReady = unlocked && isPremiumActive && !premClaimed

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Tier Badge
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(if (unlocked) GoldAccent else Color(0xFF1E293B))
                .border(1.dp, if (unlocked) Color.White else Color(0xFF64748B), RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${tier.tier}",
                color = if (unlocked) Color(0xFF0F265C) else Color(0xFFCBD5E1),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp
            )
        }

        // Free Reward Card
        Box(
            modifier = Modifier
                .weight(1f)
                .height(if (isCompact) 44.dp else 50.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(if (freeReady) Color(0xCC1E40AF) else Color(0xAA0D2454))
                .border(
                    width = if (freeReady) 1.5.dp else 1.dp,
                    color = if (freeReady) GoldAccent else if (freeClaimed) EmeraldSuccess else Color(0x447DD3FC),
                    shape = RoundedCornerShape(9.dp)
                )
                .clickable(enabled = freeReady, onClick = onClaimFree)
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("bp_free_${tier.tier}"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = tier.freeRewardLabel,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = when {
                        freeClaimed -> "✓ Diklaim"
                        freeReady -> "Klaim!"
                        else -> "${tier.requiredXp} XP"
                    },
                    color = when {
                        freeClaimed -> EmeraldSuccess
                        freeReady -> GoldAccent
                        else -> Color(0xFF94A3B8)
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 8.5.sp
                )
            }
        }

        // Premium Reward Card
        Box(
            modifier = Modifier
                .weight(1f)
                .height(if (isCompact) 44.dp else 50.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(
                    Brush.horizontalGradient(
                        if (premReady) listOf(Color(0xFF9333EA), Color(0xFFD97706))
                        else listOf(Color(0xAA4C1D95), Color(0xAA78350F))
                    )
                )
                .border(
                    width = if (premReady) 1.5.dp else 1.dp,
                    color = if (premReady) Color(0xFFFEF08A) else if (premClaimed) EmeraldSuccess else Color(0x66C084FC),
                    shape = RoundedCornerShape(9.dp)
                )
                .clickable(enabled = premReady, onClick = onClaimPrem)
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("bp_prem_${tier.tier}"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!isPremiumActive || !unlocked) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Terkunci",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(12.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = tier.premiumRewardLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = when {
                            premClaimed -> "✓ Diklaim"
                            premReady -> "Klaim!"
                            !isPremiumActive -> "Premium"
                            else -> "${tier.requiredXp} XP"
                        },
                        color = when {
                            premClaimed -> EmeraldSuccess
                            premReady -> Color(0xFFFEF08A)
                            else -> Color(0xFFE9D5FF)
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 8.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AnglerCatMascotCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Body (blue angler vest)
        drawArc(
            color = Color(0xFF1D4ED8),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.14f, h * 0.62f),
            size = Size(w * 0.72f, h * 0.65f)
        )
        // Ears
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

        // Cat head
        drawCircle(
            color = Color(0xFFFBBF24),
            radius = w * 0.31f,
            center = Offset(w * 0.5f, h * 0.46f)
        )
        // Cream muzzle/cheeks
        drawOval(
            color = Color(0xFFFFFBEB),
            topLeft = Offset(w * 0.24f, h * 0.42f),
            size = Size(w * 0.52f, h * 0.30f)
        )
        // Angler cap
        drawArc(
            color = Color(0xFF0284C7),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.22f, h * 0.12f),
            size = Size(w * 0.56f, h * 0.34f)
        )
        // Cap visor
        drawOval(
            color = Color(0xFF1E3A8A),
            topLeft = Offset(w * 0.30f, h * 0.26f),
            size = Size(w * 0.52f, h * 0.10f)
        )
        // Happy eyes & smile
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
