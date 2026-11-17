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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.CaughtFish
import com.example.domain.FishRarity
import com.example.domain.FishSpecies
import com.example.domain.GameCatalog
import com.example.domain.LocationInfo
import com.example.domain.PlayerState
import com.example.domain.TimeOfDay
import com.example.ui.components.FishIllustrationCanvas
import com.example.ui.components.weatherIcon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FishdexScreen(
    catches: List<CaughtFish>,
    onBack: () -> Unit,
    player: PlayerState = PlayerState(),
    onOpenShop: () -> Unit = {},
    onOpenLocation: (String) -> Unit = {}
) {
    var selectedLocId by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    var selectedSpeciesId by remember { mutableStateOf<String?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    val discoveredIds = remember(catches) { catches.map { it.fishId }.toSet() }
    val filteredSpecies = remember(selectedLocId) {
        GameCatalog.fishSpecies.filter { selectedLocId == null || selectedLocId in it.locationIds }
    }

    val itemsPerPage = 12
    val totalPages = ((filteredSpecies.size + itemsPerPage - 1) / itemsPerPage).coerceAtLeast(1)
    val safePage = currentPage.coerceIn(0, totalPages - 1)
    val pagedSpecies = remember(filteredSpecies, safePage) {
        filteredSpecies.drop(safePage * itemsPerPage).take(itemsPerPage)
    }

    LaunchedEffect(selectedLocId, filteredSpecies) {
        currentPage = 0
        if (selectedSpeciesId == null || filteredSpecies.none { it.id == selectedSpeciesId }) {
            selectedSpeciesId = filteredSpecies.firstOrNull()?.id
        }
    }

    val selectedSpecies = remember(filteredSpecies, selectedSpeciesId) {
        filteredSpecies.find { it.id == selectedSpeciesId } ?: filteredSpecies.firstOrNull()
    }

    val totalSpeciesCount = GameCatalog.fishSpecies.size.coerceAtLeast(1)
    val collectionPct = ((discoveredIds.size.toFloat() / totalSpeciesCount) * 100f).toInt().coerceIn(0, 100)

    val formattedCoins = remember(player.coins) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.coins)
    }
    val formattedGems = remember(player.gems) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.gems)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_location_lake),
            contentDescription = "Latar Fishdex",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xA6032B66),
                            Color(0x66073D87),
                            Color(0xB8021D4A)
                        )
                    )
                )
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            val isCompactHeight = maxHeight < 430.dp

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(if (isCompactHeight) 5.dp else 8.dp)
            ) {
                FishdexTopHud(
                    discoveredCount = discoveredIds.size,
                    totalCount = totalSpeciesCount,
                    collectionPct = collectionPct,
                    formattedCoins = formattedCoins,
                    formattedGems = formattedGems,
                    isCompact = isCompactHeight,
                    onBack = onBack,
                    onOpenShop = onOpenShop
                )

                FishdexHabitatStrip(
                    selectedLocId = selectedLocId,
                    locations = GameCatalog.locations,
                    isCompact = isCompactHeight,
                    onSelectLoc = { selectedLocId = it }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FishdexGridPane(
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxHeight(),
                        pagedSpecies = pagedSpecies,
                        discoveredIds = discoveredIds,
                        selectedSpeciesId = selectedSpecies?.id,
                        currentPage = safePage,
                        totalPages = totalPages,
                        isCompact = isCompactHeight,
                        onSelectSpecies = { selectedSpeciesId = it.id },
                        onPrevPage = { if (safePage > 0) currentPage = safePage - 1 },
                        onNextPage = { if (safePage < totalPages - 1) currentPage = safePage + 1 },
                        onOpenAllDetails = { showDetailDialog = true }
                    )

                    selectedSpecies?.let { sp ->
                        FishdexInspectorPane(
                            modifier = Modifier
                                .weight(0.42f)
                                .fillMaxHeight(),
                            species = sp,
                            discovered = sp.id in discoveredIds,
                            catches = catches,
                            isCompact = isCompactHeight,
                            onOpenLocation = onOpenLocation
                        )
                    }
                }
            }
        }
    }

    if (showDetailDialog && selectedSpecies != null) {
        FishdexFullDetailDialog(
            species = selectedSpecies,
            discovered = selectedSpecies.id in discoveredIds,
            catches = catches,
            onOpenLocation = { locId ->
                showDetailDialog = false
                onOpenLocation(locId)
            },
            onDismiss = { showDetailDialog = false }
        )
    }
}

@Composable
private fun FishdexTopHud(
    discoveredCount: Int,
    totalCount: Int,
    collectionPct: Int,
    formattedCoins: String,
    formattedGems: String,
    isCompact: Boolean,
    onBack: () -> Unit,
    onOpenShop: () -> Unit
) {
    val circleSize = if (isCompact) 42.dp else 48.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlossyCircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                size = circleSize,
                testTag = "fishdex_back_button",
                onClick = onBack
            )

            Box(
                modifier = Modifier
                    .size(if (isCompact) 36.dp else 42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
                    )
                    .border(1.5.dp, Color(0xFFBAE6FD), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "Ikon Fishdex",
                    tint = Color.White,
                    modifier = Modifier.size(if (isCompact) 22.dp else 26.dp)
                )
            }

            Column {
                Text(
                    text = "Fishdex",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (isCompact) 17.sp else 20.sp,
                    maxLines = 1
                )
                Text(
                    text = "Ensiklopedia Ikan Terlengkap!",
                    color = Color(0xFFD6EAFF),
                    fontSize = if (isCompact) 10.sp else 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FishdexCurrencyPill(
                icon = Icons.Filled.MonetizationOn,
                iconTint = GoldAccent,
                amountText = formattedCoins,
                testTag = "fishdex_coins_pill",
                onClick = onOpenShop
            )
            FishdexCurrencyPill(
                icon = Icons.Filled.Diamond,
                iconTint = Color(0xFFC084FC),
                amountText = formattedGems,
                testTag = "fishdex_gems_pill",
                onClick = onOpenShop
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xCC0B2C5C),
                border = BorderStroke(1.dp, Color(0xFF60A5FA)),
                modifier = Modifier.widthIn(min = 148.dp, max = 190.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Koleksi Total",
                                color = Color(0xFFBAE6FD),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Text(
                                text = "$collectionPct%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "$discoveredCount / $totalCount Spesies",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF1E3A5F))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(
                                        (discoveredCount.toFloat() / totalCount).coerceIn(0.03f, 1f)
                                    )
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(GoldAccent, Color(0xFFF59E0B))
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            GlossyCircleIconButton(
                icon = Icons.Filled.Home,
                contentDescription = "Beranda",
                size = circleSize,
                testTag = "fishdex_home_button",
                onClick = onBack
            )
        }
    }
}

@Composable
private fun GlossyCircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    size: androidx.compose.ui.unit.Dp,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8)))
            )
            .border(1.5.dp, Color(0xFFBFDBFE), CircleShape)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size * 0.52f)
        )
    }
}

@Composable
private fun FishdexCurrencyPill(
    icon: ImageVector,
    iconTint: Color,
    amountText: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xCC0A2855))
            .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(start = 7.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        Text(
            text = amountText,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1
        )
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(EmeraldSuccess)
                .border(1.dp, Color(0xFFD1FAE5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Tambah",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun FishdexHabitatStrip(
    selectedLocId: String?,
    locations: List<LocationInfo>,
    isCompact: Boolean,
    onSelectLoc: (String?) -> Unit
) {
    val pillHeight = if (isCompact) 32.dp else 36.dp

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            val active = selectedLocId == null
            Box(
                modifier = Modifier
                    .height(pillHeight)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (active) {
                            Brush.verticalGradient(listOf(Color(0xFFFFE066), Color(0xFFF59E0B)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xCC1E4E9B), Color(0xCC0F2E66)))
                        }
                    )
                    .border(
                        width = if (active) 1.5.dp else 1.dp,
                        color = if (active) Color(0xFFFFF3B0) else Color(0x8893C5FD),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectLoc(null) }
                    .padding(horizontal = 12.dp)
                    .testTag("fishdex_filter_all"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Public,
                        contentDescription = null,
                        tint = if (active) Color(0xFF1E3A8A) else Color(0xFF7DD3FC),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Semua Habitat",
                        color = if (active) Color(0xFF0F265C) else Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }

        items(locations, key = { it.id }) { loc ->
            val active = selectedLocId == loc.id
            Box(
                modifier = Modifier
                    .height(pillHeight)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (active) {
                            Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xCC1D488F), Color(0xCC0D2857)))
                        }
                    )
                    .border(
                        width = if (active) 1.5.dp else 1.dp,
                        color = if (active) GoldAccent else Color(0x8893C5FD),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectLoc(if (active) null else loc.id) }
                    .padding(start = 4.dp, end = 10.dp, top = 3.dp, bottom = 3.dp)
                    .testTag("fishdex_filter_${loc.id}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(id = loc.backgroundRes),
                        contentDescription = loc.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 34.dp, height = pillHeight - 8.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .border(1.dp, Color(0x88FFFFFF), RoundedCornerShape(7.dp))
                    )
                    Text(
                        text = loc.name,
                        color = if (active) Color(0xFFFEF08A) else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun FishdexGridPane(
    modifier: Modifier,
    pagedSpecies: List<FishSpecies>,
    discoveredIds: Set<String>,
    selectedSpeciesId: String?,
    currentPage: Int,
    totalPages: Int,
    isCompact: Boolean,
    onSelectSpecies: (FishSpecies) -> Unit,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onOpenAllDetails: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xBD15458E), Color(0xD90A275A)))
                )
                .border(1.5.dp, Color(0x997DD3FC), RoundedCornerShape(14.dp))
                .padding(if (isCompact) 6.dp else 8.dp)
        ) {
            if (pagedSpecies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Belum ada spesies untuk habitat ini.",
                        color = Color(0xFFBAE6FD),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(1.dp),
                    verticalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 5.dp else 7.dp)
                ) {
                    items(pagedSpecies, key = { it.id }) { species ->
                        FishdexSpeciesCard(
                            species = species,
                            discovered = species.id in discoveredIds,
                            selected = species.id == selectedSpeciesId,
                            isCompact = isCompact,
                            onClick = { onSelectSpecies(species) }
                        )
                    }
                }
            }
        }

        // Bottom Footer Bar: Pagination + Lihat Semua Detail
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC0A2855))
                    .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(10.dp))
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 30.dp else 34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (currentPage > 0) Color(0xFF2563EB) else Color(0xFF1E3A5F)
                        )
                        .clickable(enabled = currentPage > 0, onClick = onPrevPage)
                        .testTag("fishdex_prev_page"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Halaman Sebelumnya",
                        tint = if (currentPage > 0) Color.White else Color(0xFF64748B)
                    )
                }

                Text(
                    text = "${currentPage + 1} / $totalPages",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .size(if (isCompact) 30.dp else 34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (currentPage < totalPages - 1) Color(0xFF2563EB) else Color(0xFF1E3A5F)
                        )
                        .clickable(enabled = currentPage < totalPages - 1, onClick = onNextPage)
                        .testTag("fishdex_next_page"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Halaman Berikutnya",
                        tint = if (currentPage < totalPages - 1) Color.White else Color(0xFF64748B)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(if (isCompact) 36.dp else 42.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8)))
                    )
                    .border(1.5.dp, Color(0xFFBAE6FD), RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenAllDetails)
                    .padding(horizontal = 16.dp)
                    .testTag("fishdex_all_details_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Lihat Semua Detail",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun FishdexSpeciesCard(
    species: FishSpecies,
    discovered: Boolean,
    selected: Boolean,
    isCompact: Boolean,
    onClick: () -> Unit
) {
    val cardHeight = if (isCompact) 86.dp else 102.dp
    val starCount = rarityStarCount(species.rarity)

    val cardBackground = when {
        selected -> Brush.verticalGradient(listOf(Color(0xFFEAB308), Color(0xFF92400E)))
        discovered -> Brush.verticalGradient(listOf(Color(0xFF1D5BB8), Color(0xFF0C2D6B)))
        else -> Brush.verticalGradient(listOf(Color(0xFF16356B), Color(0xFF0B1C3E)))
    }
    val borderColor = when {
        selected -> Color(0xFFFEF08A)
        discovered -> Color(0x997DD3FC)
        else -> Color(0x5564748B)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .shadow(if (selected) 8.dp else 2.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(cardBackground)
            .border(if (selected) 2.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 5.dp, vertical = 4.dp)
            .testTag("fishdex_card_${species.id}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (discovered) species.name else "???",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (isCompact) 10.sp else 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            FishIllustrationCanvas(
                species = species,
                discovered = discovered,
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .fillMaxHeight(0.88f)
            )
            if (!discovered) {
                Text(
                    text = "?",
                    color = Color(0xCC94A3B8),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (isCompact) 15.sp else 18.sp
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < starCount) GoldAccent else Color(0xFF475569),
                    modifier = Modifier.size(if (isCompact) 9.dp else 10.dp)
                )
            }
        }

        Spacer(Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (discovered) {
                        Brush.horizontalGradient(listOf(Color(0xFFFDE047), Color(0xFFF59E0B)))
                    } else {
                        Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                    }
                )
                .padding(horizontal = 4.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = if (discovered) Icons.Filled.Check else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (discovered) Color(0xFF0F265C) else Color(0xFFCBD5E1),
                    modifier = Modifier.size(9.dp)
                )
                Text(
                    text = if (discovered) "Sudah Didapat" else "Belum Ditemukan",
                    color = if (discovered) Color(0xFF0F265C) else Color(0xFFCBD5E1),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 8.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FishdexInspectorPane(
    modifier: Modifier,
    species: FishSpecies,
    discovered: Boolean,
    catches: List<CaughtFish>,
    isCompact: Boolean,
    onOpenLocation: (String) -> Unit
) {
    val matchingCatches = remember(catches, species.id) { catches.filter { it.fishId == species.id } }
    val maxWeight = remember(matchingCatches) { matchingCatches.maxOfOrNull { it.weightKg } }
    val maxLength = remember(matchingCatches) { matchingCatches.maxOfOrNull { it.lengthCm } }

    val habitats = remember(species.locationIds) {
        species.locationIds.map { GameCatalog.locationById(it) }
    }
    val primaryLocation = habitats.firstOrNull() ?: GameCatalog.locations.first()
    val habitatNames = habitats.joinToString(", ") { it.name }
    val starCount = rarityStarCount(species.rarity)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xE60C2F6B), Color(0xF206193E)))
            )
            .border(1.5.dp, Color(0xAA7DD3FC), RoundedCornerShape(14.dp))
    ) {
        // Header Name + Rarity Crown Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF1E4E9B), Color(0xFF0E3470)))
                )
                .padding(horizontal = 12.dp, vertical = if (isCompact) 6.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (discovered) species.name else "??? (Belum Ditemukan)",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (isCompact) 14.sp else 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(species.rarity.color.copy(alpha = 0.22f))
                    .border(1.dp, species.rarity.color, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.WorkspacePremium,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = species.rarity.label,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        // Scenic Habitat + Fish Showcase Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCompact) 88.dp else 118.dp)
                .background(OceanDeep),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = primaryLocation.backgroundRes),
                contentDescription = primaryLocation.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x22000000), Color(0xAA06193E))
                        )
                    )
            )
            FishIllustrationCanvas(
                species = species,
                discovered = discovered,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight(0.82f)
            )
            if (!discovered) {
                Text(
                    text = "?",
                    color = Color(0xDD94A3B8),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp
                )
            }
        }

        // Scrollable Stats & Description
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = if (isCompact) 6.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { idx ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (idx < starCount) GoldAccent else Color(0xFF475569),
                        modifier = Modifier.size(if (isCompact) 14.dp else 16.dp)
                    )
                }
            }

            InspectorSpecRow(
                icon = Icons.Filled.EmojiEvents,
                iconTint = GoldAccent,
                label = "Rekor Berat Saya",
                value = if (maxWeight != null) "${formatStat(maxWeight)} kg" else "Belum tercatat"
            )
            InspectorSpecRow(
                icon = Icons.Filled.Straighten,
                iconTint = Color(0xFFFDE047),
                label = "Rekor Panjang Saya",
                value = if (maxLength != null) "${formatStat(maxLength)} cm" else "Belum tercatat"
            )
            InspectorSpecRow(
                icon = weatherIcon(species.favoriteWeather),
                iconTint = Color(0xFF38BDF8),
                label = "Cuaca Favorit",
                value = species.favoriteWeather.displayNameId.replace("/", ", ")
            )
            InspectorSpecRow(
                icon = Icons.Filled.Schedule,
                iconTint = GoldAccent,
                label = "Waktu Terbaik",
                value = formatBestTimeWindow(species.bestTime)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x7704122B))
                    .border(1.dp, Color(0x337DD3FC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (discovered) {
                        species.description
                    } else {
                        "Pancing di $habitatNames saat cuaca ${species.favoriteWeather.displayNameId} (${species.bestTime.labelId}) untuk membuka entri ini!"
                    },
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }

        // Bottom Habitat Card + Lihat Lokasi Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xAA041738))
                .padding(horizontal = 10.dp, vertical = if (isCompact) 6.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x88133770))
                    .border(1.dp, Color(0x557DD3FC), RoundedCornerShape(10.dp))
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = primaryLocation.backgroundRes),
                    contentDescription = primaryLocation.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 48.dp, height = 34.dp)
                        .clip(RoundedCornerShape(7.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Habitat Utama",
                        color = Color(0xFFBAE6FD),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = habitatNames,
                        color = Color.White,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 12.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(if (isCompact) 40.dp else 44.dp)
                    .shadow(4.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8)))
                    )
                    .border(1.5.dp, Color(0xFFBAE6FD), RoundedCornerShape(10.dp))
                    .clickable { onOpenLocation(primaryLocation.id) }
                    .padding(horizontal = 14.dp)
                    .testTag("fishdex_view_location_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lihat Lokasi",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun InspectorSpecRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(122.dp),
            maxLines = 1
        )
        Text(
            text = ": ",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FishdexFullDetailDialog(
    species: FishSpecies,
    discovered: Boolean,
    catches: List<CaughtFish>,
    onOpenLocation: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val matchingCatches = remember(catches, species.id) { catches.filter { it.fishId == species.id } }
    val maxW = matchingCatches.maxOfOrNull { it.weightKg }
    val maxL = matchingCatches.maxOfOrNull { it.lengthCm }
    val primaryLoc = GameCatalog.locationById(species.locationIds.first())
    val habitats = species.locationIds.joinToString(", ") { GameCatalog.locationById(it).name }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0A2454),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (discovered) species.name else "Spesies Misterius",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Tutup",
                    tint = Color(0xFFBAE6FD),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onDismiss)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FishIllustrationCanvas(
                    species = species,
                    discovered = discovered,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                )
                Text("Rarity: ${species.rarity.label}", color = species.rarity.color, fontWeight = FontWeight.Bold)
                Text("Habitat: $habitats", color = OceanCyan)
                Text(
                    "Kisaran Berat: ${formatStat(species.minWeightKg)} - ${formatStat(species.maxWeightKg)} kg",
                    color = Color.White
                )
                Text(
                    "Kisaran Panjang: ${formatStat(species.minLengthCm)} - ${formatStat(species.maxLengthCm)} cm",
                    color = Color.White
                )
                Text("Harga Dasar: ${species.baseSellPrice} Koin", color = GoldAccent)
                Text(
                    "Cuaca Favorit: ${species.favoriteWeather.displayNameId} • Waktu: ${formatBestTimeWindow(species.bestTime)}",
                    color = Color(0xFFBAE6FD)
                )
                if (discovered) {
                    Text(
                        "Rekor Saya: ${maxW?.let { formatStat(it) } ?: "-"} kg • ${maxL?.let { formatStat(it) } ?: "-"} cm (${matchingCatches.size}x tertangkap)",
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Bold
                    )
                    Text(species.description, color = Color(0xFFE2E8F0))
                } else {
                    Text(
                        "Gunakan umpan rekomendasi (${primaryLoc.recommendedBait}) di $habitats saat cuaca ${species.favoriteWeather.displayNameId}.",
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onOpenLocation(primaryLoc.id) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Text("Pergi ke ${primaryLoc.name}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = OceanCyan)
            }
        }
    )
}

private fun rarityStarCount(rarity: FishRarity): Int = when (rarity) {
    FishRarity.COMMON -> 1
    FishRarity.UNCOMMON -> 2
    FishRarity.RARE -> 3
    FishRarity.EPIC -> 4
    FishRarity.LEGENDARY -> 5
}

private fun formatBestTimeWindow(timeOfDay: TimeOfDay): String = when (timeOfDay) {
    TimeOfDay.PAGI -> "Pagi (06:00 - 10:00)"
    TimeOfDay.SIANG -> "Siang (10:00 - 15:00)"
    TimeOfDay.SORE -> "Sore (15:00 - 18:30)"
    TimeOfDay.MALAM -> "Malam (18:30 - 05:30)"
}

private fun formatStat(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }
}
