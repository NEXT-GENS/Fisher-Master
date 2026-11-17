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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ActiveEventInfo
import com.example.domain.GameCatalog
import com.example.domain.LocationInfo
import com.example.domain.PlayMode
import com.example.domain.PlayerState
import com.example.domain.WeatherForecastSlot
import com.example.domain.WeatherType
import com.example.ui.components.AvatarBadge
import com.example.ui.components.FishIllustrationCanvas
import com.example.ui.components.WeatherForecastDialog
import com.example.ui.components.weatherIcon
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LobbyScreen(
    player: PlayerState,
    weather: WeatherType,
    activeEvent: ActiveEventInfo,
    forecast: List<WeatherForecastSlot>,
    isOnlineAccount: Boolean,
    pingMs: Int,
    claimableMissionsCount: Int,
    onPlayClick: () -> Unit,
    onLocationsClick: () -> Unit,
    onMultiplayerClick: () -> Unit,
    onShopClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onFishdexClick: () -> Unit,
    onMissionsClick: () -> Unit,
    onDailyLoginClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRequestGoogleSignIn: () -> Unit,
    onSimulateWeather: () -> Unit
) {
    val activeLoc = GameCatalog.locationById(player.activeLocationId)
    var showForecastDialog by remember { mutableStateOf(false) }
    var showWeatherDock by remember { mutableStateOf(true) }
    var showNotificationDock by remember { mutableStateOf(true) }
    var notificationsCleared by remember { mutableStateOf(false) }

    val formattedCoins = remember(player.coins) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.coins)
    }
    val formattedGems = remember(player.gems) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.gems)
    }
    val maxXp = (player.level * 120).coerceAtLeast(120)
    val notifCount = remember(claimableMissionsCount, notificationsCleared) {
        if (notificationsCleared) 0 else (claimableMissionsCount + 2).coerceAtLeast(1)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-bleed vibrant location scene
        Image(
            painter = painterResource(id = activeLoc.backgroundRes),
            contentDescription = activeLoc.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Subtle marine atmospheric vignette for panel legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x66062359),
                        0.38f to Color(0x28061C45),
                        0.75f to Color(0x5506183D),
                        1.0f to Color(0x9904122E)
                    )
                )
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            val compactHeight = maxHeight < 420.dp
            val tileHeight = if (compactHeight) 48.dp else 62.dp
            val dockHeight = if (compactHeight) 70.dp else 94.dp

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. TOP HUD BAR: Profile + Coins/Gems + Weather/Ping + Bell + Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile & XP Glossy Blue Capsule
                    LobbyHeaderPill(
                        modifier = Modifier
                            .weight(1.35f)
                            .height(48.dp)
                            .clickable(onClick = onSettingsClick)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarBadge(avatarId = player.avatarId, size = 36.dp)
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = player.username,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit Profil",
                                        tint = Color(0xFFBAE6FD),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFF0EA5E9),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Lv.${player.level}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0x99041538))
                                            .border(0.8.dp, Color(0x667DD3FC), RoundedCornerShape(50))
                                    ) {
                                        val xpFrac = (player.xp.toFloat() / maxXp.toFloat()).coerceIn(0.06f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(xpFrac)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(50))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                                                    )
                                                )
                                        )
                                        Text(
                                            text = "${player.xp}/$maxXp XP",
                                            fontSize = 8.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Coins Pill + '+' Button
                    LobbyCurrencyPill(
                        icon = Icons.Filled.MonetizationOn,
                        iconTint = Color(0xFFFACC15),
                        amountText = formattedCoins,
                        contentDesc = "Koin",
                        onPlusClick = onShopClick,
                        modifier = Modifier.height(48.dp)
                    )

                    // Gems Pill + '+' Button
                    LobbyCurrencyPill(
                        icon = Icons.Filled.Diamond,
                        iconTint = Color(0xFFC084FC),
                        amountText = formattedGems,
                        contentDesc = "Gem",
                        onPlusClick = onShopClick,
                        modifier = Modifier.height(48.dp)
                    )

                    // Weather + Temp + Ping Capsule
                    LobbyHeaderPill(
                        modifier = Modifier
                            .height(48.dp)
                            .clickable {
                                showWeatherDock = true
                                if (compactHeight) showForecastDialog = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = weatherIcon(weather),
                                contentDescription = weather.displayNameId,
                                tint = Color(0xFFFACC15),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = weather.displayNameId.substringBefore("/"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 11.sp
                                )
                                Text(
                                    text = "${activeLoc.baseTempC}°C",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFBAE6FD),
                                    lineHeight = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(22.dp)
                                    .background(Color.White.copy(alpha = 0.25f))
                            )
                            Icon(
                                imageVector = if (isOnlineAccount) Icons.Filled.Wifi else Icons.Filled.CloudOff,
                                contentDescription = "Status Koneksi",
                                tint = if (isOnlineAccount) EmeraldSuccess else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isOnlineAccount) "${pingMs}ms" else "Offline",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOnlineAccount) EmeraldSuccess else Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bell Notification Square Button with Badge
                    Box(modifier = Modifier.size(48.dp)) {
                        LobbyHeaderPill(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    notificationsCleared = false
                                    showNotificationDock = !showNotificationDock
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        if (notifCount > 0) {
                            Surface(
                                color = CoralDanger,
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$notifCount",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Settings Square Button
                    LobbyHeaderPill(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(onClick = onSettingsClick)
                            .testTag("settings_button")
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Pengaturan",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // 2. MIDDLE SECTION: Left (Special Event + 7 Colorful Tiles) | Center Space | Right (Location + Mulai Memancing)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = if (compactHeight) 2.dp else 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side (~50% width): Special Event Banner + 2 Rows of Colorful Menu Tiles
                    Column(
                        modifier = Modifier
                            .weight(0.50f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(if (compactHeight) 4.dp else 8.dp, Alignment.CenterVertically)
                    ) {
                        // Event Spesial Card
                        SpecialEventLobbyBanner(
                            activeEvent = activeEvent,
                            compact = compactHeight,
                            onClick = onMissionsClick
                        )

                        // Row 1: Multiplayer, Pilih Lokasi, Toko
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LobbyColorfulTile(
                                title = "Multiplayer",
                                icon = Icons.Filled.Groups,
                                gradient = listOf(Color(0xFF2563EB), Color(0xFF1E3A8A)),
                                borderColor = Color(0xFF60A5FA),
                                locked = !isOnlineAccount,
                                badgeText = null,
                                subTag = if (!isOnlineAccount) "Tersedia saat online" else null,
                                onClick = onMultiplayerClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(tileHeight)
                                    .testTag("multiplayer_button")
                            )
                            LobbyColorfulTile(
                                title = "Pilih Lokasi",
                                icon = Icons.Filled.LocationOn,
                                gradient = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                                borderColor = Color(0xFFBAE6FD),
                                locked = false,
                                badgeText = null,
                                subTag = null,
                                onClick = onLocationsClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(tileHeight)
                                    .testTag("locations_button")
                            )
                            LobbyColorfulTile(
                                title = "Toko",
                                icon = Icons.Filled.Storefront,
                                gradient = listOf(Color(0xFFFBBF24), Color(0xFFD97706)),
                                borderColor = Color(0xFFFEF08A),
                                locked = false,
                                badgeText = "!",
                                subTag = null,
                                onClick = onShopClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(tileHeight)
                                    .testTag("shop_button")
                            )
                        }

                        // Row 2: Inventori, Fishdex, Misi & Pass, Login Harian
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LobbyColorfulTile(
                                title = "Inventori",
                                icon = Icons.Filled.Backpack,
                                gradient = listOf(Color(0xFF34D399), Color(0xFF059669)),
                                borderColor = Color(0xFFA7F3D0),
                                locked = false,
                                badgeText = null,
                                subTag = null,
                                onClick = onInventoryClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(tileHeight)
                                    .testTag("inventory_button")
                            )
                            LobbyColorfulTile(
                                title = "Fishdex",
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                gradient = listOf(Color(0xFFA855F7), Color(0xFF6D28D9)),
                                borderColor = Color(0xFFE9D5FF),
                                locked = false,
                                badgeText = null,
                                subTag = null,
                                onClick = onFishdexClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(tileHeight)
                                    .testTag("fishdex_button")
                            )
                            LobbyColorfulTile(
                                title = "Misi & Pass",
                                icon = Icons.Filled.EmojiEvents,
                                gradient = listOf(Color(0xFF2DD4BF), Color(0xFF0F766E)),
                                borderColor = Color(0xFF99F6E4),
                                locked = false,
                                badgeText = if (claimableMissionsCount > 0) "$claimableMissionsCount" else null,
                                subTag = null,
                                onClick = onMissionsClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(tileHeight)
                                    .testTag("missions_button")
                            )
                            LobbyColorfulTile(
                                title = "Login Harian",
                                icon = Icons.Filled.CalendarMonth,
                                gradient = listOf(Color(0xFFFB7185), Color(0xFFE11D48)),
                                borderColor = Color(0xFFFECDD3),
                                locked = false,
                                badgeText = null,
                                subTag = null,
                                onClick = onDailyLoginClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(tileHeight)
                                    .testTag("daily_login_button")
                            )
                        }
                    }

                    // Center Breathing Space so scenic illustration shines
                    Spacer(modifier = Modifier.weight(0.18f))

                    // Right Side (~32% width): Location Card + Primary "Mulai Memancing >>" CTA
                    Column(
                        modifier = Modifier
                            .weight(0.32f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        // Top-Right Active Location Mini Card
                        Surface(
                            color = Color(0xCC0A2E75),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF56CCF2)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onLocationsClick)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = activeLoc.backgroundRes),
                                    contentDescription = activeLoc.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(if (compactHeight) 46.dp else 54.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, Color(0xFF7DD3FC), RoundedCornerShape(10.dp))
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(3.dp))
                                        Text(
                                            text = activeLoc.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    val starCount = when {
                                        activeLoc.difficulty.contains("Sangat", ignoreCase = true) -> 5
                                        activeLoc.difficulty.contains("Sulit", ignoreCase = true) -> 4
                                        activeLoc.difficulty.contains("Menengah", ignoreCase = true) -> 3
                                        else -> 2
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Kesulitan: ",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFE0F2FE)
                                        )
                                        repeat(5) { idx ->
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = if (idx < starCount) Color(0xFFFACC15) else Color(0x55FFFFFF),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Rekomendasi: ${activeLoc.recommendedBait}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFBAE6FD),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Pilih Lokasi",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Primary Gold "Mulai Memancing >>" Button
                        Button(
                            onClick = onPlayClick,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (compactHeight) 62.dp else 72.dp)
                                .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = GoldAccent, spotColor = GoldAccent)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFDE047), Color(0xFFFACC15), Color(0xFFD97706))
                                    )
                                )
                                .border(2.dp, Color(0xFFFEF9C3), RoundedCornerShape(18.dp))
                                .testTag("play_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Phishing,
                                    contentDescription = null,
                                    tint = Color(0xFF0A2A66),
                                    modifier = Modifier.size(28.dp)
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Mulai",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color(0xFF072356),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontStyle = FontStyle.Italic,
                                        lineHeight = 20.sp
                                    )
                                    Text(
                                        text = "Memancing",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color(0xFF072356),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontStyle = FontStyle.Italic,
                                        lineHeight = 20.sp
                                    )
                                }
                                Text(
                                    text = ">>",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color(0xFF072356),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // 3. BOTTOM DOCK: Prakiraan Cuaca Card + Pusat Notifikasi Card + Chat Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (showWeatherDock || showNotificationDock) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Bottom-Left: Prakiraan Cuaca Widget
                            if (showWeatherDock) {
                                Surface(
                                    color = Color(0xCC0A2E75),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.5.dp, Color(0xFF56CCF2)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(dockHeight)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(0.95f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = weatherIcon(weather),
                                                    contentDescription = null,
                                                    tint = GoldAccent,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(Modifier.width(5.dp))
                                                Text(
                                                    text = "Prakiraan Cuaca",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            val nextWeather = forecast.firstOrNull()?.weather?.displayNameId?.substringBefore("/") ?: weather.displayNameId.substringBefore("/")
                                            Text(
                                                text = "Besok: $nextWeather",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFBAE6FD),
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "Suhu: ${activeLoc.baseTempC - 2}°C – ${activeLoc.baseTempC + 3}°C",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFE0F2FE),
                                                maxLines = 1
                                            )
                                        }

                                        // Forecast hourly mini-cards
                                        Row(
                                            modifier = Modifier.weight(1.35f),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            val labels = listOf("06:00", "12:00", "18:00", "24:00")
                                            labels.forEachIndexed { idx, timeLbl ->
                                                val slotWeather = forecast.getOrNull(idx % forecast.size.coerceAtLeast(1))?.weather ?: weather
                                                Surface(
                                                    color = Color(0x66061C47),
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = BorderStroke(0.8.dp, Color(0x447DD3FC)),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable { showForecastDialog = true }
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Icon(
                                                            imageVector = weatherIcon(slotWeather),
                                                            contentDescription = slotWeather.displayNameId,
                                                            tint = if (slotWeather == WeatherType.CERAH) GoldAccent else Color(0xFF7DD3FC),
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Spacer(Modifier.height(2.dp))
                                                        Text(
                                                            text = timeLbl,
                                                            fontSize = 9.sp,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Simulasi Cuaca button + close X
                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxHeight()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Tutup Cuaca",
                                                tint = Color.White.copy(alpha = 0.8f),
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clickable { showWeatherDock = false }
                                            )
                                            Button(
                                                onClick = onSimulateWeather,
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF0EA5E9),
                                                    contentColor = Color.White
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Text(
                                                    text = "Simulasi",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(Modifier.width(2.dp))
                                                Icon(
                                                    imageVector = Icons.Filled.PlayArrow,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Bottom-Right: Pusat Notifikasi Widget
                            if (showNotificationDock) {
                                Surface(
                                    color = Color(0xCC0A2E75),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.5.dp, Color(0xFF56CCF2)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(dockHeight)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.Notifications,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(Modifier.width(5.dp))
                                                Text(
                                                    text = "Pusat Notifikasi",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Surface(
                                                color = Color(0x55FFFFFF),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.clickable {
                                                    notificationsCleared = true
                                                    showNotificationDock = false
                                                }
                                            ) {
                                                Text(
                                                    text = "Hapus Semua",
                                                    fontSize = 10.sp,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        HorizontalDivider(color = Color.White.copy(alpha = 0.16f))

                                        if (notificationsCleared) {
                                            Text(
                                                text = "Semua notifikasi telah dibaca.",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFBAE6FD)
                                            )
                                        } else {
                                            NotificationMiniLine(
                                                icon = Icons.Filled.CardGiftcard,
                                                iconTint = CoralDanger,
                                                text = if (claimableMissionsCount > 0) "$claimableMissionsCount Misi Harian siap diklaim!" else "Cek Misi & Battle Pass hari ini!",
                                                timeLabel = "Baru saja",
                                                onClick = onMissionsClick
                                            )
                                            NotificationMiniLine(
                                                icon = Icons.Filled.EmojiEvents,
                                                iconTint = Color(0xFFC084FC),
                                                text = "${activeEvent.title} sedang berlangsung!",
                                                timeLabel = "${activeEvent.remainingSeconds / 60}m lagi",
                                                onClick = onMissionsClick
                                            )
                                            if (!compactHeight) {
                                                NotificationMiniLine(
                                                    icon = Icons.Filled.Info,
                                                    iconTint = Color(0xFF38BDF8),
                                                    text = "Bonus Login Hari ke-${player.loginStreakDay} menanti!",
                                                    timeLabel = "Harian",
                                                    onClick = onDailyLoginClick
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Mini Chat Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = Color(0xB8061B44),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, Color(0x6656CCF2)),
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .height(28.dp)
                                .clickable {
                                    if (isOnlineAccount) onMultiplayerClick() else onRequestGoogleSignIn()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Chat",
                                    tint = Color(0xFFBAE6FD),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isOnlineAccount) "Ketuk untuk membuka chat multiplayer..." else "Masuk Google untuk membuka chat...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFE0F2FE),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForecastDialog) {
        WeatherForecastDialog(
            currentWeather = weather,
            forecast = forecast,
            onDismiss = { showForecastDialog = false },
            onSimulateWeather = onSimulateWeather
        )
    }
}

@Composable
private fun LobbyHeaderPill(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = Color(0xCC0A2E75),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.3.dp, Color(0xFF56CCF2)),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
private fun LobbyCurrencyPill(
    icon: ImageVector,
    iconTint: Color,
    amountText: String,
    contentDesc: String,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LobbyHeaderPill(
        modifier = modifier.clickable(onClick = onPlusClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = amountText,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = Color(0xFF0EA5E9),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Tambah $contentDesc",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialEventLobbyBanner(
    activeEvent: ActiveEventInfo,
    compact: Boolean,
    onClick: () -> Unit
) {
    val mins = activeEvent.remainingSeconds / 60
    val secs = activeEvent.remainingSeconds % 60

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp), ambientColor = Color(0xFFA855F7), spotColor = Color(0xFF38BDF8))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xE86D28D9), Color(0xE82563EB), Color(0xE80284C7))
                )
            )
            .border(1.6.dp, Color(0xFF7DD3FC), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = if (compact) 5.dp else 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFFEF4444),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Event Spesial",
                            fontSize = 9.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = activeEvent.title,
                    style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = activeEvent.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE0F2FE),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Berakhir dalam: ${mins}m ${secs}d",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFDE047),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Surface(
                color = Color(0x33FFFFFF),
                shape = CircleShape,
                border = BorderStroke(1.2.dp, Color(0x887DD3FC)),
                modifier = Modifier.size(if (compact) 46.dp else 54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Phishing,
                        contentDescription = activeEvent.title,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(if (compact) 26.dp else 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LobbyColorfulTile(
    title: String,
    icon: ImageVector,
    gradient: List<Color>,
    borderColor: Color,
    locked: Boolean,
    badgeText: String?,
    subTag: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(6.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(gradient))
                .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(if (subTag != null) 18.dp else 21.dp)
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                if (subTag != null) {
                    Text(
                        text = subTag,
                        fontSize = 8.sp,
                        color = Color(0xFFE0F2FE),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (locked) {
            Surface(
                color = Color(0xCC061626),
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Terkunci Offline",
                        tint = GoldAccent,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        } else if (badgeText != null) {
            Surface(
                color = CoralDanger,
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationMiniLine(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    timeLabel: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(13.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = timeLabel,
            fontSize = 9.sp,
            color = Color(0xFFBAE6FD)
        )
    }
}

@Composable
fun ModeAndLocationScreen(
    player: PlayerState,
    playMode: PlayMode,
    isOnlineAccount: Boolean,
    pingMs: Int,
    onSelectMode: (PlayMode) -> Unit,
    onSelectLocationAndPlay: (String) -> Unit,
    onRequestSignIn: () -> Unit,
    onBack: () -> Unit,
    weather: WeatherType = WeatherType.CERAH,
    onShopClick: () -> Unit = {}
) {
    var selectedLocationId by remember(player.activeLocationId) { mutableStateOf(player.activeLocationId) }
    val selectedLoc = remember(selectedLocationId) { GameCatalog.locationById(selectedLocationId) }
    val selectedUnlocked = player.level >= selectedLoc.unlockLevel

    val formattedCoins = remember(player.coins) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.coins)
    }
    val formattedGems = remember(player.gems) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.gems)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Dynamic full-bleed scenic background matching selected fishing spot
        Image(
            painter = painterResource(id = selectedLoc.backgroundRes),
            contentDescription = selectedLoc.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x88062359),
                        0.38f to Color(0x4D061C45),
                        0.72f to Color(0x7705173B),
                        1.0f to Color(0xCC04122E)
                    )
                )
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            val compactHeight = maxHeight < 430.dp
            val modeHeight = if (compactHeight) 56.dp else 74.dp
            val bottomHeight = if (compactHeight) 62.dp else 78.dp
            val cardWidth = if (compactHeight) 172.dp else 198.dp

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(if (compactHeight) 5.dp else 8.dp)
            ) {
                // 1. TOP HUD BAR: Back + Title Pill | Coins + Gems + Weather/Ping
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LobbyHeaderPill(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable(onClick = onBack)
                                .testTag("back_button")
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        LobbyHeaderPill(modifier = Modifier.height(44.dp)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Phishing,
                                    contentDescription = null,
                                    tint = Color(0xFF7DD3FC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Pemilihan Mode & Lokasi",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LobbyCurrencyPill(
                            icon = Icons.Filled.MonetizationOn,
                            iconTint = Color(0xFFFACC15),
                            amountText = formattedCoins,
                            contentDesc = "Koin",
                            onPlusClick = onShopClick,
                            modifier = Modifier.height(44.dp)
                        )
                        LobbyCurrencyPill(
                            icon = Icons.Filled.Diamond,
                            iconTint = Color(0xFFC084FC),
                            amountText = formattedGems,
                            contentDesc = "Gem",
                            onPlusClick = onShopClick,
                            modifier = Modifier.height(44.dp)
                        )
                        LobbyHeaderPill(modifier = Modifier.height(44.dp)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = weatherIcon(weather),
                                    contentDescription = weather.displayNameId,
                                    tint = Color(0xFFFACC15),
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = weather.displayNameId.substringBefore("/"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 11.sp
                                    )
                                    Text(
                                        text = "${selectedLoc.baseTempC}°C",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFBAE6FD),
                                        lineHeight = 11.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(20.dp)
                                        .background(Color.White.copy(alpha = 0.25f))
                                )
                                Icon(
                                    imageVector = if (isOnlineAccount) Icons.Filled.Wifi else Icons.Filled.CloudOff,
                                    contentDescription = "Status Jaringan",
                                    tint = if (isOnlineAccount) EmeraldSuccess else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isOnlineAccount) "${pingMs}ms" else "Offline",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isOnlineAccount) EmeraldSuccess else Color(0xFFCBD5E1),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 2. MODE SELECTOR ROW: Offline (Solo) & Online (Multiplayer)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(modeHeight),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModeSelectTile(
                        title = "Offline (Solo)",
                        subtitle = "Bermain sendiri tanpa koneksi internet.",
                        leadingIcon = Icons.Filled.SportsEsports,
                        selected = playMode == PlayMode.OFFLINE_SOLO,
                        badgeLabel = null,
                        pingLabel = null,
                        onClick = { onSelectMode(PlayMode.OFFLINE_SOLO) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("mode_offline_card")
                    )
                    ModeSelectTile(
                        title = "Online (Multiplayer)",
                        subtitle = "Bermain bersama pemain lain di seluruh dunia.",
                        leadingIcon = Icons.Filled.Public,
                        selected = playMode == PlayMode.ONLINE_MULTIPLAYER,
                        badgeLabel = if (isOnlineAccount) "Region: Asia Tenggara" else "Perlu Login Google",
                        pingLabel = if (isOnlineAccount) "Ping: ${pingMs}ms" else "Ketuk untuk masuk",
                        onClick = {
                            if (isOnlineAccount) onSelectMode(PlayMode.ONLINE_MULTIPLAYER) else onRequestSignIn()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("mode_online_card")
                    )
                }

                // 3. HORIZONTAL LOCATION CARDS CAROUSEL
                LazyRow(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(GameCatalog.locations, key = { it.id }) { loc ->
                        LocationSelectCard(
                            location = loc,
                            playerLevel = player.level,
                            isSelected = selectedLocationId == loc.id,
                            compact = compactHeight,
                            cardWidth = cardWidth,
                            onHighlight = { selectedLocationId = loc.id },
                            onSelectAndPlay = { onSelectLocationAndPlay(loc.id) }
                        )
                    }
                }

                // 4. BOTTOM DOCK: Info Lokasi + Hadiah Potensial + Konfirmasi CTA
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomHeight),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Info Lokasi
                    Surface(
                        color = Color(0xCC0A2E75),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.3.dp, Color(0xFF56CCF2)),
                        modifier = Modifier
                            .weight(1.35f)
                            .fillMaxHeight()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = selectedLoc.backgroundRes),
                                contentDescription = selectedLoc.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(if (compactHeight) 68.dp else 88.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFF7DD3FC), RoundedCornerShape(10.dp))
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF7DD3FC),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Info Lokasi • ${selectedLoc.name}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = selectedLoc.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFE0F2FE),
                                    maxLines = if (compactHeight) 2 else 3,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }

                    // Center: Hadiah Potensial
                    Surface(
                        color = Color(0xCC0A2E75),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.3.dp, Color(0xFF56CCF2)),
                        modifier = Modifier
                            .weight(0.95f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "Hadiah Potensial",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PotentialRewardBox(
                                    label = "x${selectedLoc.unlockLevel * 500}",
                                    bgColors = listOf(Color(0xFFB45309), Color(0xFF78350F)),
                                    borderColor = Color(0xFFFACC15),
                                    compact = compactHeight
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MonetizationOn,
                                        contentDescription = "Hadiah Koin",
                                        tint = Color(0xFFFDE047),
                                        modifier = Modifier.size(if (compactHeight) 16.dp else 20.dp)
                                    )
                                }
                                PotentialRewardBox(
                                    label = "x${selectedLoc.unlockLevel * 15}",
                                    bgColors = listOf(Color(0xFF7E22CE), Color(0xFF4C1D95)),
                                    borderColor = Color(0xFFD8B4FE),
                                    compact = compactHeight
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Diamond,
                                        contentDescription = "Hadiah Gem",
                                        tint = Color(0xFFE9D5FF),
                                        modifier = Modifier.size(if (compactHeight) 16.dp else 20.dp)
                                    )
                                }
                                PotentialRewardBox(
                                    label = "x1",
                                    bgColors = listOf(Color(0xFF0369A1), Color(0xFF1E3A8A)),
                                    borderColor = Color(0xFF7DD3FC),
                                    compact = compactHeight
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Phishing,
                                        contentDescription = "Ikan Langka",
                                        tint = Color(0xFFBAE6FD),
                                        modifier = Modifier.size(if (compactHeight) 16.dp else 20.dp)
                                    )
                                }
                                PotentialRewardBox(
                                    label = "x1",
                                    bgColors = listOf(Color(0xFF1E40AF), Color(0xFF0F172A)),
                                    borderColor = Color(0xFF93C5FD),
                                    compact = compactHeight
                                ) {
                                    Text(
                                        text = "?",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = if (compactHeight) 13.sp else 16.sp,
                                        color = Color(0xFFE0F2FE)
                                    )
                                }
                            }
                        }
                    }

                    // Right: Confirm CTA Button
                    Button(
                        onClick = { onSelectLocationAndPlay(selectedLoc.id) },
                        enabled = selectedUnlocked,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .width(if (compactHeight) 146.dp else 172.dp)
                            .fillMaxHeight()
                            .shadow(
                                elevation = if (selectedUnlocked) 10.dp else 0.dp,
                                shape = RoundedCornerShape(18.dp),
                                ambientColor = Color(0xFF38BDF8),
                                spotColor = Color(0xFF38BDF8)
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (selectedUnlocked) {
                                        listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF1D4ED8))
                                    } else {
                                        listOf(Color(0xFF475569), Color(0xFF334155))
                                    }
                                )
                            )
                            .border(
                                1.8.dp,
                                if (selectedUnlocked) Color(0xFFBAE6FD) else Color(0xFF64748B),
                                RoundedCornerShape(18.dp)
                            )
                            .testTag("confirm_location_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedUnlocked) Icons.Filled.Phishing else Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(if (compactHeight) 18.dp else 22.dp)
                            )
                            Text(
                                text = if (selectedUnlocked) "Konfirmasi" else "Lv.${selectedLoc.unlockLevel}",
                                style = if (compactHeight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeSelectTile(
    title: String,
    subtitle: String,
    leadingIcon: ImageVector,
    selected: Boolean,
    badgeLabel: String?,
    pingLabel: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = if (selected) 10.dp else 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = if (selected) Color(0xFF38BDF8) else Color.Black,
                    spotColor = if (selected) Color(0xFF38BDF8) else Color.Black
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        if (selected) {
                            listOf(Color(0xE01D4ED8), Color(0xE00284C7))
                        } else {
                            listOf(Color(0xCC0A2E75), Color(0xCC061C47))
                        }
                    )
                )
                .border(
                    width = if (selected) 2.2.dp else 1.2.dp,
                    color = if (selected) Color(0xFF7DD3FC) else Color(0x8856CCF2),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = title,
                    tint = if (selected) Color.White else Color(0xFFBAE6FD),
                    modifier = Modifier.size(34.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE0F2FE),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (badgeLabel != null && pingLabel != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = badgeLabel,
                            fontSize = 10.sp,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.SignalCellularAlt,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = pingLabel,
                                fontSize = 10.sp,
                                color = Color(0xFFE0F2FE),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        if (selected) {
            Surface(
                color = Color(0xFF0EA5E9),
                shape = CircleShape,
                border = BorderStroke(1.5.dp, Color.White),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = (-4).dp)
                    .size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Mode Dipilih",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationSelectCard(
    location: LocationInfo,
    playerLevel: Int,
    isSelected: Boolean,
    compact: Boolean,
    cardWidth: Dp,
    onHighlight: () -> Unit,
    onSelectAndPlay: () -> Unit
) {
    val unlocked = playerLevel >= location.unlockLevel
    val species = remember(location.id) { GameCatalog.speciesForLocation(location.id).take(4) }
    val starCount = when {
        location.difficulty.contains("Ekstrem", ignoreCase = true) -> 5
        location.difficulty.contains("Sulit", ignoreCase = true) -> 4
        location.difficulty.contains("Menengah", ignoreCase = true) -> 3
        else -> 2
    }

    Box(
        modifier = Modifier
            .width(cardWidth)
            .fillMaxHeight()
    ) {
        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (unlocked) 1f else 0.84f)
                .shadow(
                    elevation = if (isSelected) 10.dp else 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = if (isSelected) Color(0xFF38BDF8) else Color.Black,
                    spotColor = if (isSelected) Color(0xFF38BDF8) else Color.Black
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xDD0B3984), Color(0xEE08245B))
                    )
                )
                .border(
                    width = if (isSelected) 2.2.dp else 1.2.dp,
                    color = if (isSelected) Color(0xFF7DD3FC) else Color(0x8856CCF2),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onHighlight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 7.dp, vertical = if (compact) 4.dp else 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header: Pin + Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Scenic Photo Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 44.dp else 78.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0x66BAE6FD), RoundedCornerShape(10.dp))
                ) {
                    Image(
                        painter = painterResource(id = location.backgroundRes),
                        contentDescription = location.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(location.tintColor)
                    )
                }

                // Metadata rows (scrollable fallback on ultra-narrow viewports)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(if (compact) 1.dp else 3.dp, Alignment.CenterVertically)
                ) {
                    // Weather + Temp range
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = weatherIcon(location.defaultWeather),
                            contentDescription = null,
                            tint = if (location.defaultWeather == WeatherType.CERAH) GoldAccent else Color(0xFF7DD3FC),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${location.defaultWeather.displayNameId.substringBefore("/")} ${location.baseTempC - 2}–${location.baseTempC + 4}°C",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // 5-Star Difficulty
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Kesulitan: ",
                            fontSize = 10.sp,
                            color = Color(0xFFE0F2FE)
                        )
                        repeat(5) { idx ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (idx < starCount) GoldAccent else Color(0x44FFFFFF),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }

                    Text(
                        text = if (compact) {
                            "Habitat: ${location.habitat.substringBefore(" ")} • 1–${location.depthMeters}m"
                        } else {
                            "Habitat: ${location.habitat}"
                        },
                        fontSize = 10.sp,
                        color = Color(0xFFBAE6FD),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!compact) {
                        Text(
                            text = "Kedalaman: 1–${location.depthMeters}m",
                            fontSize = 10.sp,
                            color = Color(0xFFBAE6FD),
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "Umpan: ${location.recommendedBait.substringBefore(" /")}",
                        fontSize = 10.sp,
                        color = GoldAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Primary Species Fish Icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = if (compact) "Ikan:" else "Spesies:",
                            fontSize = 9.sp,
                            color = Color(0xFFE0F2FE)
                        )
                        species.forEach { fish ->
                            FishIllustrationCanvas(
                                species = fish,
                                discovered = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(if (compact) 16.dp else 20.dp)
                            )
                        }
                    }
                }

                // Bottom Action Button ("Mancing Di Sini" / "Level X")
                Button(
                    onClick = {
                        onHighlight()
                        if (unlocked) onSelectAndPlay()
                    },
                    enabled = unlocked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        contentColor = OceanDeep
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 32.dp else 40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (unlocked) {
                                    listOf(Color(0xFFFDE047), Color(0xFFF59E0B))
                                } else {
                                    listOf(Color(0xFF475569), Color(0xFF334155))
                                }
                            )
                        )
                        .border(
                            1.dp,
                            if (unlocked) Color(0xFFFEF08A) else Color(0xFF64748B),
                            RoundedCornerShape(10.dp)
                        )
                        .testTag("select_location_${location.id}")
                ) {
                    if (!unlocked) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Color(0xFFE2E8F0),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Level ${location.unlockLevel}",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Phishing,
                            contentDescription = null,
                            tint = Color(0xFF072356),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Mancing Di Sini",
                            fontSize = 11.sp,
                            color = Color(0xFF072356),
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        if (isSelected && unlocked) {
            Surface(
                color = Color(0xFF0EA5E9),
                shape = CircleShape,
                border = BorderStroke(1.5.dp, Color.White),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Lokasi Dipilih",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PotentialRewardBox(
    label: String,
    bgColors: List<Color>,
    borderColor: Color,
    compact: Boolean,
    iconContent: @Composable () -> Unit
) {
    val boxSize = if (compact) 32.dp else 40.dp
    Box(
        modifier = Modifier
            .size(boxSize)
            .clip(RoundedCornerShape(9.dp))
            .background(Brush.verticalGradient(bgColors))
            .border(1.2.dp, borderColor, RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center
    ) {
        iconContent()
        Text(
            text = label,
            fontSize = 8.sp,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color(0x9904122E), RoundedCornerShape(4.dp))
                .padding(horizontal = 3.dp)
        )
    }
}
