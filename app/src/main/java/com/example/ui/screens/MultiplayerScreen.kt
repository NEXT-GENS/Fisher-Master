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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.domain.FriendInfo
import com.example.domain.GameCatalog
import com.example.domain.GiftInfo
import com.example.domain.LeaderboardPlayer
import com.example.domain.PlayerState
import com.example.domain.RoomChatMessage
import com.example.domain.RoomPlayerInfo
import com.example.domain.RoomSummary
import com.example.ui.components.AvatarBadge
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MultiplayerScreen(
    player: PlayerState,
    currentUserId: String,
    rooms: List<RoomSummary>,
    activeRoom: RoomSummary?,
    roomPlayers: List<RoomPlayerInfo>,
    roomMessages: List<RoomChatMessage>,
    leaderboard: List<LeaderboardPlayer>,
    friends: List<FriendInfo>,
    incomingGifts: List<GiftInfo>,
    connectionState: String,
    onCreateRoom: (name: String, region: String, locationId: String) -> Unit,
    onJoinRoom: (RoomSummary) -> Unit,
    onJoinByCode: (String) -> Unit,
    onLeaveRoom: () -> Unit,
    onReconnect: () -> Unit,
    onToggleReady: (Boolean, Int, String) -> Unit,
    onStartTournament: () -> Unit,
    onSendChat: (String, String) -> Unit,
    onStartFishingInRoom: () -> Unit,
    onAddFriend: (String, String, Int) -> Unit,
    onSendGift: (String, String, Int, Int) -> Unit,
    onClaimGift: (GiftInfo) -> Unit,
    onBack: () -> Unit,
    pingMs: Int = 32,
    onOpenShop: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedRegion by remember { mutableStateOf("Semua") }
    var joinCodeInput by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var mutedSenderIds by remember { mutableStateOf(setOf<String>()) }
    var reportNotice by remember { mutableStateOf<String?>(null) }

    val pendingGiftCount = remember(incomingGifts) {
        incomingGifts.count { it.status == "pending" }
    }
    val formattedCoins = remember(player.coins) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.coins)
    }
    val formattedGems = remember(player.gems) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.gems)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_location_ocean),
            contentDescription = "Latar Multiplayer",
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
                MultiplayerTopHud(
                    player = player,
                    formattedCoins = formattedCoins,
                    formattedGems = formattedGems,
                    connectionState = connectionState,
                    pendingGiftCount = pendingGiftCount,
                    isCompact = isCompact,
                    onBack = onBack,
                    onReconnect = onReconnect,
                    onOpenShop = onOpenShop,
                    onOpenFriends = { selectedTab = 2 },
                    onOpenSettings = onOpenSettings
                )

                MultiplayerFolderTabs(
                    selectedTab = selectedTab,
                    pendingGiftCount = pendingGiftCount,
                    isCompact = isCompact,
                    onSelectTab = { selectedTab = it }
                )

                when (selectedTab) {
                    0 -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RoomBrowserLeftPanel(
                                modifier = Modifier
                                    .weight(0.30f)
                                    .fillMaxHeight(),
                                rooms = rooms,
                                selectedRegion = selectedRegion,
                                joinCodeInput = joinCodeInput,
                                pingMs = pingMs,
                                isCompact = isCompact,
                                onJoinCodeChange = { joinCodeInput = it },
                                onSelectRegion = { selectedRegion = it },
                                onJoinByCode = onJoinByCode,
                                onJoinRoom = onJoinRoom,
                                onRefreshRooms = onReconnect,
                                onOpenCreateDialog = { showCreateDialog = true }
                            )

                            ActiveTournamentCenterPanel(
                                modifier = Modifier
                                    .weight(0.43f)
                                    .fillMaxHeight(),
                                currentUserId = currentUserId,
                                activeRoom = activeRoom,
                                roomPlayers = roomPlayers,
                                roomMessages = roomMessages,
                                mutedSenderIds = mutedSenderIds,
                                isCompact = isCompact,
                                onToggleReady = onToggleReady,
                                onStartTournament = onStartTournament,
                                onStartFishingInRoom = onStartFishingInRoom,
                                onLeaveRoom = onLeaveRoom,
                                onSendChat = onSendChat,
                                onAddFriend = onAddFriend,
                                onReportAndMute = { msg ->
                                    mutedSenderIds = mutedSenderIds + msg.senderId
                                    reportNotice = "Pesan dari ${msg.senderName} disembunyikan & dilaporkan ke moderasi."
                                },
                                onOpenCreateDialog = { showCreateDialog = true }
                            )

                            SidebarLeaderboardAndFriendsPanel(
                                modifier = Modifier
                                    .weight(0.27f)
                                    .fillMaxHeight(),
                                leaderboard = leaderboard,
                                friends = friends,
                                incomingGifts = incomingGifts,
                                currentUserId = currentUserId,
                                playerCoins = player.coins,
                                isCompact = isCompact,
                                onOpenFullLeaderboard = { selectedTab = 1 },
                                onOpenFriendsTab = { selectedTab = 2 },
                                onSendGift = { f -> onSendGift(f.uid, "bait_cacing", 3, 50) },
                                onClaimGift = onClaimGift
                            )
                        }
                    }

                    1 -> FullLeaderboardWorkspace(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        leaderboard = leaderboard,
                        friends = friends,
                        currentUserId = currentUserId
                    )

                    else -> FullFriendsAndGiftsWorkspace(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        friends = friends,
                        incomingGifts = incomingGifts,
                        playerCoins = player.coins,
                        onAddFriend = onAddFriend,
                        onSendGift = onSendGift,
                        onClaimGift = onClaimGift
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        var roomName by remember { mutableStateOf("Room ${player.username}") }
        var region by remember { mutableStateOf("Indonesia") }
        var locationId by remember { mutableStateOf(player.activeLocationId) }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = OceanDeep,
            title = { Text("Buat Room Multiplayer", color = Color.White, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = roomName, onValueChange = { roomName = it }, label = { Text("Nama Room") }, singleLine = true)
                    Text("Region:", color = OceanCyan, style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("Indonesia", "Asia", "Global", "Eropa", "Amerika")) { reg ->
                            FilterChip(selected = region == reg, onClick = { region = reg }, label = { Text(reg) })
                        }
                    }
                    Text("Lokasi Turnamen:", color = GoldAccent, style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(GameCatalog.locations) { loc ->
                            FilterChip(selected = locationId == loc.id, onClick = { locationId = loc.id }, label = { Text(loc.name) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateRoom(roomName, region, locationId)
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = OceanDeep)
                ) { Text("Buat & Masuk", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Batal", color = OceanCyan) } }
        )
    }

    reportNotice?.let { notice ->
        AlertDialog(
            onDismissRequest = { reportNotice = null },
            containerColor = OceanDeep,
            title = { Text("Moderasi Chat", color = GoldAccent, fontWeight = FontWeight.ExtraBold) },
            text = { Text(notice, color = Color.White) },
            confirmButton = { TextButton(onClick = { reportNotice = null }) { Text("Oke", color = OceanCyan) } }
        )
    }
}

@Composable
private fun MultiplayerTopHud(
    player: PlayerState,
    formattedCoins: String,
    formattedGems: String,
    connectionState: String,
    pendingGiftCount: Int,
    isCompact: Boolean,
    onBack: () -> Unit,
    onReconnect: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val circleSize = if (isCompact) 38.dp else 44.dp
    val maxXp = (player.level * 120).coerceAtLeast(120)
    val xpRatio = (player.xp.toFloat() / maxXp).coerceIn(0.05f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MpRoundButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                size = circleSize,
                testTag = "mp_back_button",
                onClick = onBack
            )

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MpCurrencyPill(
                icon = Icons.Filled.MonetizationOn,
                iconTint = GoldAccent,
                text = formattedCoins,
                onClick = onOpenShop
            )
            MpCurrencyPill(
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
                    .clickable(onClick = onReconnect)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Reconnect", tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                Text(
                    text = connectionState,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            MpRoundButton(
                icon = Icons.Filled.People,
                contentDescription = "Teman",
                size = circleSize,
                testTag = "mp_friends_button",
                onClick = onOpenFriends
            )

            Box {
                MpRoundButton(
                    icon = Icons.Filled.Mail,
                    contentDescription = "Kotak Hadiah",
                    size = circleSize,
                    testTag = "mp_gifts_button",
                    onClick = onOpenFriends
                )
                if (pendingGiftCount > 0) {
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
                            text = pendingGiftCount.coerceAtMost(9).toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            MpRoundButton(
                icon = Icons.Filled.Settings,
                contentDescription = "Pengaturan",
                size = circleSize,
                testTag = "mp_settings_button",
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
private fun MpRoundButton(
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
private fun MpCurrencyPill(
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
            .padding(start = 6.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
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
private fun MultiplayerFolderTabs(
    selectedTab: Int,
    pendingGiftCount: Int,
    isCompact: Boolean,
    onSelectTab: (Int) -> Unit
) {
    val tabs = listOf(
        Triple("Lobby & Room", Icons.Filled.Groups, 0),
        Triple("Leaderboard", Icons.Filled.EmojiEvents, 0),
        Triple("Teman & Hadiah", Icons.Filled.People, pendingGiftCount)
    )
    val tabHeight = if (isCompact) 34.dp else 40.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { idx, (label, icon, badge) ->
            val selected = selectedTab == idx
            val bg = if (selected) {
                Brush.verticalGradient(listOf(Color(0xFFFFE066), Color(0xFFF59E0B)))
            } else {
                Brush.verticalGradient(listOf(Color(0xCC1E4E9B), Color(0xCC0F2E66)))
            }
            val borderColor = if (selected) Color(0xFFFFFBEB) else Color(0x8893C5FD)
            val fgColor = if (selected) Color(0xFF0F265C) else Color.White

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
                        .testTag("mp_tab_$idx"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(icon, contentDescription = null, tint = fgColor, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(label, color = fgColor, fontWeight = FontWeight.ExtraBold, fontSize = 12.5.sp)
                }
                if (badge > 0) {
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
                        Text("!", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FullLeaderboardWorkspace(
    modifier: Modifier,
    leaderboard: List<LeaderboardPlayer>,
    friends: List<FriendInfo>,
    currentUserId: String
) {
    var boardMode by remember { mutableStateOf("Terbesar") }
    val friendIds = remember(friends, currentUserId) { friends.map { it.uid }.toSet() + currentUserId }
    val sorted = remember(leaderboard, boardMode, friendIds) {
        val base = if (boardMode == "Teman") leaderboard.filter { it.userId in friendIds } else leaderboard
        when (boardMode) {
            "Turnamen" -> base.sortedByDescending { it.tournamentScore }
            "Total" -> base.sortedByDescending { it.totalCatches }
            else -> base.sortedByDescending { it.biggestWeightKg }
        }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xE0134292), Color(0xEE08214F))))
            .border(1.5.dp, Color(0x997DD3FC), RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Terbesar", "Turnamen", "Total", "Teman").forEach { mode ->
                FilterChip(selected = boardMode == mode, onClick = { boardMode = mode }, label = { Text(mode) })
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(sorted.mapIndexed { idx, entry -> idx to entry }, key = { it.second.userId }) { (idx, entry) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (entry.userId == currentUserId) Color(0x991E40AF) else Color(0x990D2C66))
                        .border(1.dp, if (entry.userId == currentUserId) GoldAccent else Color(0x557DD3FC), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("#${idx + 1}", style = MaterialTheme.typography.titleMedium, color = GoldAccent, modifier = Modifier.width(38.dp))
                        AvatarBadge(entry.avatarId, size = 34.dp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("${entry.username} (Lv.${entry.level})", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Ikan Terbesar: ${entry.biggestFishName} • ${entry.totalCatches} Tangkapan", color = OceanCyan, fontSize = 11.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${entry.biggestWeightKg} kg", color = GoldAccent, fontWeight = FontWeight.ExtraBold)
                        Text("Skor ${entry.tournamentScore}", color = EmeraldSuccess, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FullFriendsAndGiftsWorkspace(
    modifier: Modifier,
    friends: List<FriendInfo>,
    incomingGifts: List<GiftInfo>,
    playerCoins: Int,
    onAddFriend: (String, String, Int) -> Unit,
    onSendGift: (String, String, Int, Int) -> Unit,
    onClaimGift: (GiftInfo) -> Unit
) {
    var friendUidInput by remember { mutableStateOf("") }
    var friendNameInput by remember { mutableStateOf("") }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(listOf(Color(0xE0134292), Color(0xEE08214F))))
                .border(1.5.dp, Color(0x997DD3FC), RoundedCornerShape(14.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Daftar Teman (${friends.size})", color = GoldAccent, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = friendNameInput,
                    onValueChange = { friendNameInput = it },
                    label = { Text("Nama Teman") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = friendUidInput,
                    onValueChange = { friendUidInput = it },
                    label = { Text("ID Pemain") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (friendNameInput.isNotBlank() && friendUidInput.isNotBlank()) {
                            onAddFriend(friendUidInput.trim(), friendNameInput.trim(), 1)
                            friendUidInput = ""
                            friendNameInput = ""
                        }
                    },
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = "Tambah")
                }
            }
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(friends, key = { it.uid }) { f ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x880D2C66))
                            .padding(9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${f.name} (Lv.${f.level})", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(if (f.isOnline) "Online" else "Offline", color = if (f.isOnline) EmeraldSuccess else Color.Gray, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { onSendGift(f.uid, "bait_cacing", 3, 50) },
                            enabled = playerCoins >= 50
                        ) {
                            Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Kirim 50 Koin", color = GoldAccent)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(listOf(Color(0xE0134292), Color(0xEE08214F))))
                .border(1.5.dp, Color(0x997DD3FC), RoundedCornerShape(14.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Kotak Hadiah & Trade Masuk", color = OceanCyan, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            val pendingGifts = incomingGifts.filter { it.status == "pending" }
            if (pendingGifts.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Belum ada hadiah masuk.", color = Color(0xFFCBD5E1))
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(pendingGifts, key = { it.id }) { g ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x880D2C66))
                                .padding(9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Dari: ${g.senderName}", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("${g.itemName} (x${g.quantity}) + ${g.coinsGift} Koin", color = GoldAccent, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onClaimGift(g) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess, contentColor = OceanDeep)
                            ) {
                                Text("Klaim", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
