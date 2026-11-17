package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun RoomBrowserLeftPanel(
    modifier: Modifier,
    rooms: List<RoomSummary>,
    selectedRegion: String,
    joinCodeInput: String,
    pingMs: Int,
    isCompact: Boolean,
    onJoinCodeChange: (String) -> Unit,
    onSelectRegion: (String) -> Unit,
    onJoinByCode: (String) -> Unit,
    onJoinRoom: (RoomSummary) -> Unit,
    onRefreshRooms: () -> Unit,
    onOpenCreateDialog: () -> Unit
) {
    val visibleRooms = remember(rooms, selectedRegion) {
        rooms.filter { selectedRegion == "Semua" || it.region.equals(selectedRegion, ignoreCase = true) }
    }
    val regions = listOf("Semua", "Indonesia", "Asia", "Global", "Eropa", "Amerika")

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xE0134292), Color(0xEE08214F))))
            .border(1.5.dp, Color(0x997DD3FC), RoundedCornerShape(14.dp))
            .padding(if (isCompact) 7.dp else 9.dp),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp)
    ) {
        Text(
            text = "Cari Room / Kode Room",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp
        )

        // Compact Glossy Search Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (isCompact) 32.dp else 36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF071A3D))
                    .border(1.dp, Color(0x667DD3FC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (joinCodeInput.isEmpty()) {
                    Text("Masukkan kode room...", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
                BasicTextField(
                    value = joinCodeInput,
                    onValueChange = { onJoinCodeChange(it.uppercase()) },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold),
                    cursorBrush = SolidColor(GoldAccent),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier
                    .size(if (isCompact) 32.dp else 36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8))))
                    .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
                    .clickable { if (joinCodeInput.isNotBlank()) onJoinByCode(joinCodeInput.trim()) }
                    .testTag("join_by_code_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Gabung Kode", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        // Filter Region 2x3 Grid with Globe Badge
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Filter Region:", color = Color(0xFFBAE6FD), fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 32.dp else 38.dp)
                        .clip(CircleShape)
                        .background(Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7))))
                        .border(1.5.dp, Color(0xFFBAE6FD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    regions.chunked(3).forEach { rowRegions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowRegions.forEach { reg ->
                                val active = selectedRegion == reg
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(
                                            if (active) Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFF59E0B)))
                                            else Brush.verticalGradient(listOf(Color(0xFF1E4E9B), Color(0xFF0F2E66)))
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (active) Color(0xFFFFFBEB) else Color(0x6693C5FD),
                                            shape = RoundedCornerShape(7.dp)
                                        )
                                        .clickable { onSelectRegion(reg) }
                                        .padding(vertical = 3.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = reg,
                                        color = if (active) Color(0xFF0F265C) else Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Rooms Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Daftar Room Aktif (${visibleRooms.size})",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.5.sp
            )
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Muat Ulang Room",
                tint = OceanCyan,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onRefreshRooms)
            )
        }

        // Room Cards List
        if (visibleRooms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x66061A3D))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada room aktif di region ini. Ketuk '+ Buat Room' untuk membuat room baru!",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(visibleRooms, key = { it.id }) { room ->
                    val loc = GameCatalog.locationById(room.locationId)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xCC0D2C66))
                            .border(1.dp, Color(0x667DD3FC), RoundedCornerShape(10.dp))
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = loc.backgroundRes),
                            contentDescription = loc.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 48.dp, height = 38.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(7.dp))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${room.name} [${room.roomCode}]",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(room.region, color = Color(0xFFBAE6FD), fontSize = 9.sp, maxLines = 1)
                                Icon(Icons.Filled.SignalCellularAlt, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(10.dp))
                                Text("${pingMs}ms", color = EmeraldSuccess, fontSize = 9.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${room.playerCount} / ${room.maxPlayers}",
                                color = Color(0xFFFEF08A),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.5.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Brush.verticalGradient(listOf(Color(0xFF4ADE80), Color(0xFF16A34A))))
                                    .border(1.dp, Color(0xFFDCFCE7), RoundedCornerShape(7.dp))
                                    .clickable { onJoinRoom(room) }
                                    .padding(horizontal = 9.dp, vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Gabung", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp)
                            }
                        }
                    }
                }
            }
        }

        // + Buat Room Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCompact) 36.dp else 42.dp)
                .shadow(4.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8))))
                .border(1.5.dp, Color(0xFFBAE6FD), RoundedCornerShape(10.dp))
                .clickable(onClick = onOpenCreateDialog)
                .testTag("create_room_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Text("Buat Room", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ActiveTournamentCenterPanel(
    modifier: Modifier,
    currentUserId: String,
    activeRoom: RoomSummary?,
    roomPlayers: List<RoomPlayerInfo>,
    roomMessages: List<RoomChatMessage>,
    mutedSenderIds: Set<String>,
    isCompact: Boolean,
    onToggleReady: (Boolean, Int, String) -> Unit,
    onStartTournament: () -> Unit,
    onStartFishingInRoom: () -> Unit,
    onLeaveRoom: () -> Unit,
    onSendChat: (String, String) -> Unit,
    onAddFriend: (String, String, Int) -> Unit,
    onReportAndMute: (RoomChatMessage) -> Unit,
    onOpenCreateDialog: () -> Unit
) {
    var showQuickChat by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xE6103678), Color(0xF0071C45))))
            .border(1.5.dp, Color(0x997DD3FC), RoundedCornerShape(14.dp))
            .padding(if (isCompact) 6.dp else 8.dp),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp)
    ) {
        // Top Hero Banner: Turnamen Mingguan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(11.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF3B1D7A), Color(0xFF7E22CE), Color(0xFFB45309))))
                .border(1.5.dp, Color(0xFFFDE047), RoundedCornerShape(11.dp))
                .padding(horizontal = 10.dp, vertical = if (isCompact) 5.dp else 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 34.dp else 40.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color(0xFF451A03), modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Turnamen Mingguan",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (isCompact) 14.sp else 16.sp,
                    maxLines = 1
                )
                Text(
                    text = "Tantangan Para Angler Terbaik!",
                    color = Color(0xFFFEF08A),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x991E1B4B),
                border = BorderStroke(1.dp, Color(0x66FDE047))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(11.dp))
                    Text("2j 15m", color = Color(0xFFFEF08A), fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (activeRoom == null) {
            // Empty Room Selection State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x9908214F))
                    .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Groups, contentDescription = null, tint = OceanCyan, modifier = Modifier.size(38.dp))
                    Text(
                        text = "Pilih Room atau Buat Room Turnamen",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Bergabunglah dengan angler lain melalui daftar room di kiri, masukkan kode room, atau buat room turnamen baru.",
                        color = Color(0xFFBAE6FD),
                        fontSize = 11.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9.dp))
                                .background(Color(0x991E3A8A))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Text("x1.000", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Icon(Icons.Filled.Diamond, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(16.dp))
                            Text("x100", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))))
                                .clickable(onClick = onOpenCreateDialog)
                                .padding(horizontal = 16.dp, vertical = 9.dp)
                        ) {
                            Text("Buat Room Sekarang", color = Color(0xFF0F265C), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            val loc = GameCatalog.locationById(activeRoom.locationId)
            val me = roomPlayers.find { it.userId == currentUserId }
            val isHost = activeRoom.hostId == currentUserId

            // Active Room Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(17.dp))
                    Text(
                        text = "${activeRoom.name} [${activeRoom.roomCode}]",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFD97706)) {
                        Text(
                            text = "Turnamen",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(7.dp))
                        .background(CoralDanger.copy(alpha = 0.85f))
                        .clickable(onClick = onLeaveRoom)
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }

            // Room Location Banner + Status / Actions Split
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                // Left: Scenic Spot + Player Standings List
                Column(
                    modifier = Modifier
                        .weight(0.62f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isCompact) 48.dp else 60.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0x887DD3FC), RoundedCornerShape(10.dp))
                    ) {
                        Image(
                            painter = painterResource(id = loc.backgroundRes),
                            contentDescription = loc.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.horizontalGradient(listOf(Color(0x22000000), Color(0xBB041738))))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(loc.name, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Filled.WbSunny, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(11.dp))
                                Text("${loc.defaultWeather.displayNameId.substringBefore("/")} • ${loc.baseTempC}°C", color = Color(0xFFE2E8F0), fontSize = 9.5.sp)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x99061A3D))
                            .padding(5.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(roomPlayers.mapIndexed { idx, p -> idx to p }, key = { it.second.userId }) { (idx, p) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(if (p.userId == currentUserId) Color(0x881E40AF) else Color(0x6613326B))
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        color = if (idx < 3) GoldAccent else Color(0xFF94A3B8),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                    AvatarBadge(p.avatarId, size = 22.dp)
                                    Text(
                                        text = p.username,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = if (p.isReady) "✓ Siap" else "Belum Siap",
                                        color = if (p.isReady) EmeraldSuccess else GoldAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    )
                                    Text(
                                        text = if (p.score > 0) "${p.score} pts" else "-",
                                        color = Color(0xFFBAE6FD),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    )
                                    if (p.userId != currentUserId) {
                                        Icon(
                                            imageVector = Icons.Filled.PersonAdd,
                                            contentDescription = "Tambah Teman",
                                            tint = OceanCyan,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { onAddFriend(p.userId, p.username, p.level) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Right: Player Count, Rewards, Siap! & Mulai Turnamen / Mancing!
                Column(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color(0xAA08214F))
                            .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(9.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Pemain: ${roomPlayers.size}/${activeRoom.maxPlayers}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Status: ${if (activeRoom.status == "playing") "Berlangsung" else "Menunggu"}",
                            color = if (activeRoom.status == "playing") GoldAccent else EmeraldSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color(0xAA103170))
                            .border(1.dp, Color(0x667DD3FC), RoundedCornerShape(9.dp))
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Hadiah Turnamen", color = Color(0xFFBAE6FD), fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                Text("x1.000", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Diamond, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(18.dp))
                                Text("x100", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Filled.Inventory2, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isCompact) 32.dp else 36.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF4ADE80), Color(0xFF16A34A))))
                            .border(1.dp, Color(0xFFDCFCE7), RoundedCornerShape(9.dp))
                            .clickable { onToggleReady(me?.isReady ?: false, me?.score ?: 0, me?.lastCatchName ?: "-") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (me?.isReady == true) "Batal Siap" else "Siap!",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }

                    val showStart = isHost && activeRoom.status == "waiting"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isCompact) 32.dp else 36.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                if (showStart) Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706)))
                                else Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
                            )
                            .border(1.dp, Color(0xFFFFFBEB), RoundedCornerShape(9.dp))
                            .clickable {
                                if (showStart) onStartTournament() else onStartFishingInRoom()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (!showStart) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            }
                            Text(
                                text = if (showStart) "Mulai Turnamen" else "Mancing!",
                                color = if (showStart) Color(0xFF0F265C) else Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Bottom Live Chat / Quick Chat Strip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC06183A))
                    .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(10.dp))
                    .padding(horizontal = 7.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!showQuickChat) GoldAccent else Color(0xFF1E3A8A))
                                .clickable { showQuickChat = false }
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Chat Room",
                                color = if (!showQuickChat) Color(0xFF0F265C) else Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.5.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (showQuickChat) GoldAccent else Color(0xFF1E3A8A))
                                .clickable { showQuickChat = true }
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Quick Chat",
                                color = if (showQuickChat) Color(0xFF0F265C) else Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.5.sp
                            )
                        }
                    }

                    val latestMsg = roomMessages.lastOrNull { it.senderId !in mutedSenderIds }
                    if (latestMsg != null) {
                        Text(
                            text = "${latestMsg.senderName}: ${latestMsg.text}",
                            color = if (latestMsg.type == "system") GoldAccent else Color(0xFFBAE6FD),
                            fontSize = 9.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                    }
                }

                if (showQuickChat) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        items(GameCatalog.emotePresets) { emote ->
                            Surface(
                                color = Color(0xFF1E3A8A),
                                shape = RoundedCornerShape(6.dp),
                                onClick = { onSendChat(emote, "emote") }
                            ) {
                                Text(emote, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                        items(GameCatalog.quickChatPresets) { qc ->
                            Surface(
                                color = Color(0xFF0E7490),
                                shape = RoundedCornerShape(6.dp),
                                onClick = { onSendChat(qc, "quick_chat") }
                            ) {
                                Text(qc, color = Color.White, fontSize = 9.5.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0B224D))
                                .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.EmojiEmotions,
                                    contentDescription = "Emote",
                                    tint = GoldAccent,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { showQuickChat = true }
                                )
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                    if (chatInput.isEmpty()) {
                                        Text("Ketik pesan...", color = Color(0xFF94A3B8), fontSize = 10.5.sp)
                                    }
                                    BasicTextField(
                                        value = chatInput,
                                        onValueChange = { chatInput = it },
                                        singleLine = true,
                                        textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                                        cursorBrush = SolidColor(GoldAccent),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(width = 34.dp, height = 30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8))))
                                .clickable {
                                    if (chatInput.isNotBlank()) {
                                        onSendChat(chatInput.trim(), "chat")
                                        chatInput = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim", tint = Color.White, modifier = Modifier.size(15.dp))
                        }

                        val lastOtherMsg = roomMessages.lastOrNull { it.senderId != currentUserId && it.type != "system" && it.senderId !in mutedSenderIds }
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF7F1D1D))
                                .clickable(enabled = lastOtherMsg != null) {
                                    lastOtherMsg?.let { onReportAndMute(it) }
                                }
                                .padding(horizontal = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Icon(Icons.Filled.Flag, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Text("Lapor", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E3A8A))
                                .clickable(enabled = lastOtherMsg != null) {
                                    lastOtherMsg?.let { onReportAndMute(it) }
                                }
                                .padding(horizontal = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Icon(Icons.Filled.VolumeOff, contentDescription = null, tint = Color(0xFFBAE6FD), modifier = Modifier.size(12.dp))
                                Text("Bisukan", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarLeaderboardAndFriendsPanel(
    modifier: Modifier,
    leaderboard: List<LeaderboardPlayer>,
    friends: List<FriendInfo>,
    incomingGifts: List<GiftInfo>,
    currentUserId: String,
    playerCoins: Int,
    isCompact: Boolean,
    onOpenFullLeaderboard: () -> Unit,
    onOpenFriendsTab: () -> Unit,
    onSendGift: (FriendInfo) -> Unit,
    onClaimGift: (GiftInfo) -> Unit
) {
    var boardMode by remember { mutableStateOf("Terbesar") }
    val friendIds = remember(friends, currentUserId) { friends.map { it.uid }.toSet() + currentUserId }
    val sorted = remember(leaderboard, boardMode, friendIds) {
        val base = if (boardMode == "Teman") leaderboard.filter { it.userId in friendIds } else leaderboard
        when (boardMode) {
            "Turnamen" -> base.sortedByDescending { it.tournamentScore }
            "Total" -> base.sortedByDescending { it.totalCatches }
            else -> base.sortedByDescending { it.biggestWeightKg }
        }.take(5)
    }
    val pendingGifts = remember(incomingGifts) { incomingGifts.filter { it.status == "pending" } }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp)
    ) {
        // Top Card: Compact Leaderboard
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.54f)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(listOf(Color(0xE0134292), Color(0xEE08214F))))
                .border(1.5.dp, Color(0x997DD3FC), RoundedCornerShape(14.dp))
                .padding(if (isCompact) 6.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                Text("Leaderboard", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                listOf("Terbesar", "Turnamen", "Total", "Teman").forEach { mode ->
                    val active = boardMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (active) GoldAccent else Color(0xFF173A7A))
                            .clickable { boardMode = mode }
                            .padding(vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode,
                            color = if (active) Color(0xFF0F265C) else Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 8.5.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            if (sorted.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Belum ada skor papan peringkat.", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(sorted.mapIndexed { idx, entry -> idx to entry }, key = { it.second.userId }) { (idx, entry) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(7.dp))
                                .background(if (entry.userId == currentUserId) Color(0x881E40AF) else Color(0x660B224D))
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    color = if (idx < 3) GoldAccent else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                                AvatarBadge(entry.avatarId, size = 18.dp)
                                Text(
                                    text = entry.username,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = when (boardMode) {
                                    "Turnamen" -> "${entry.tournamentScore}"
                                    "Total" -> "${entry.totalCatches}"
                                    else -> "${entry.biggestWeightKg} kg"
                                },
                                color = Color(0xFFFEF08A),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF0284C7))))
                    .clickable(onClick = onOpenFullLeaderboard)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lihat Lengkap", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Bottom Card: Teman & Hadiah Sidebar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.46f)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(listOf(Color(0xE0134292), Color(0xEE08214F))))
                .border(1.5.dp, Color(0x997DD3FC), RoundedCornerShape(14.dp))
                .padding(if (isCompact) 6.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(15.dp))
                    Text("Teman & Hadiah", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp)
                }
                Text(
                    text = "${friends.size} Teman",
                    color = Color(0xFFBAE6FD),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (friends.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Tambahkan teman di tab Teman & Hadiah.", color = Color(0xFFCBD5E1), fontSize = 9.5.sp)
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    friends.take(2).forEach { f ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color(0x660B224D))
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                AvatarBadge(f.avatarId, size = 20.dp)
                                Column {
                                    Text(f.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1)
                                    Text(
                                        text = if (f.isOnline) "● Online" else "● Offline",
                                        color = if (f.isOnline) EmeraldSuccess else Color(0xFF94A3B8),
                                        fontSize = 8.5.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (playerCoins >= 50) Color(0xFF0284C7) else Color(0xFF334155))
                                    .clickable(enabled = playerCoins >= 50) { onSendGift(f) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.CardGiftcard, contentDescription = "Kirim Hadiah", tint = GoldAccent, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Purple Kirim Hadiah Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9))))
                    .border(1.dp, Color(0xFFE9D5FF), RoundedCornerShape(8.dp))
                    .clickable(onClick = onOpenFriendsTab)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                    Text("Kirim Hadiah", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 10.5.sp)
                }
            }

            // Golden Hadiah Masuk Chest Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(9.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))))
                    .border(1.dp, Color(0xFFFFFBEB), RoundedCornerShape(9.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Inventory2, contentDescription = null, tint = Color(0xFF78350F), modifier = Modifier.size(16.dp))
                    Column {
                        Text("Hadiah Masuk!", color = Color(0xFF451A03), fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, maxLines = 1)
                        Text(
                            text = if (pendingGifts.isNotEmpty()) "Klaim ${pendingGifts.size} hadiah baru" else "Belum ada hadiah",
                            color = Color(0xFF78350F),
                            fontSize = 8.5.sp,
                            maxLines = 1
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (pendingGifts.isNotEmpty()) Color(0xFF0F265C) else Color(0x66451A03))
                        .clickable {
                            val first = pendingGifts.firstOrNull()
                            if (first != null) onClaimGift(first) else onOpenFriendsTab()
                        }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (pendingGifts.isNotEmpty()) "Klaim" else "Lihat",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
