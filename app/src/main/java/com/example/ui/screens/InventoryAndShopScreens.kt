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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.CaughtFish
import com.example.domain.CraftingRecipe
import com.example.domain.FishRarity
import com.example.domain.GameCatalog
import com.example.domain.GearCategory
import com.example.domain.OwnedGear
import com.example.domain.PlayerState
import com.example.domain.ShopProduct
import com.example.ui.components.FishIllustrationCanvas
import com.example.ui.components.GlassPanel
import com.example.ui.components.RarityPill
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InventoryScreen(
    player: PlayerState,
    catches: List<CaughtFish>,
    ownedGear: List<OwnedGear>,
    onEquipGear: (OwnedGear) -> Unit,
    onUpgradeGear: (OwnedGear) -> Unit,
    onCraftRecipe: (CraftingRecipe) -> Unit,
    onSellFish: (CaughtFish) -> Unit,
    onBack: () -> Unit,
    onOpenShop: () -> Unit = {}
) {
    val tabs = listOf(
        "Ikan" to Icons.Filled.Phishing,
        "Umpan" to Icons.Filled.ShoppingCart,
        "Joran & Gear" to Icons.Filled.Hardware,
        "Item & Bahan" to Icons.Filled.Science,
        "Upgrade & Crafting" to Icons.Filled.Build
    )
    var selectedTab by remember { mutableIntStateOf(0) }
    var rarityFilter by remember { mutableStateOf<FishRarity?>(null) }
    var sortMode by remember { mutableStateOf("Terbaru") }
    var selectedFishId by remember { mutableStateOf<String?>(null) }
    var selectedGearId by remember { mutableStateOf<String?>(player.equippedRodId) }
    var currentPage by remember { mutableIntStateOf(0) }
    var selectedRecipeIdx by remember { mutableIntStateOf(0) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val keptCount = remember(catches) { catches.count { it.isKept } }
    val keptCatches = remember(catches, rarityFilter, sortMode) {
        catches.filter { it.isKept }
            .filter { rarityFilter == null || it.rarity == rarityFilter }
            .let { list ->
                when (sortMode) {
                    "Berat" -> list.sortedByDescending { it.weightKg }
                    "Nama" -> list.sortedBy { it.fishName }
                    else -> list.sortedByDescending { it.caughtAtMillis }
                }
            }
    }

    val filteredGear = remember(ownedGear, selectedTab) {
        ownedGear.filter {
            when (selectedTab) {
                1 -> it.category == GearCategory.BAIT
                2 -> it.category in listOf(GearCategory.ROD, GearCategory.REEL, GearCategory.LINE, GearCategory.SKIN)
                3 -> it.category == GearCategory.ITEM
                else -> it.category in listOf(GearCategory.ROD, GearCategory.REEL, GearCategory.LINE)
            }
        }
    }

    val upgradables = remember(ownedGear) {
        ownedGear.filter { it.category in listOf(GearCategory.ROD, GearCategory.REEL, GearCategory.LINE) }
    }

    val inspectedFish = remember(keptCatches, selectedFishId) {
        keptCatches.find { it.id == selectedFishId } ?: keptCatches.firstOrNull()
    }
    val inspectedGear = remember(ownedGear, filteredGear, selectedGearId, selectedTab) {
        ownedGear.find { it.itemId == selectedGearId } ?: filteredGear.firstOrNull()
    }

    val pageSize = 9
    val totalPages = ((keptCatches.size - 1).coerceAtLeast(0) / pageSize) + 1
    val safePage = currentPage.coerceIn(0, totalPages - 1)
    val pagedFish = remember(keptCatches, safePage) {
        keptCatches.drop(safePage * pageSize).take(pageSize)
    }

    val formattedCoins = remember(player.coins) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.coins)
    }
    val formattedGems = remember(player.gems) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(player.gems)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scenic lake backdrop with marine gradient vignette
        Image(
            painter = painterResource(id = R.drawable.img_location_lake),
            contentDescription = "Bengkel Joran",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x99062359), Color(0x88061C45), Color(0xBB04122E))
                    )
                )
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            val compactHeight = maxHeight < 430.dp

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(if (compactHeight) 4.dp else 7.dp)
            ) {
                // 1. TOP HUD BAR: Back + Title Pill + '?' | Coins + Gems + Capacity + Home
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InventoryTopCircleBtn(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDesc = "Kembali",
                            onClick = onBack,
                            modifier = Modifier.size(44.dp)
                        )
                        Surface(
                            color = Color(0xCC0A2E75),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.3.dp, Color(0xFF56CCF2)),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Inventori & Bengkel Joran",
                                    style = if (compactHeight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontStyle = FontStyle.Italic
                                )
                                Icon(
                                    imageVector = Icons.Filled.HelpOutline,
                                    contentDescription = "Panduan Bengkel",
                                    tint = Color(0xFFBAE6FD),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { showHelpDialog = true }
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InventoryHeaderCurrencyPill(
                            icon = Icons.Filled.MonetizationOn,
                            iconTint = GoldAccent,
                            text = formattedCoins,
                            onPlusClick = onOpenShop
                        )
                        InventoryHeaderCurrencyPill(
                            icon = Icons.Filled.Diamond,
                            iconTint = Color(0xFFC084FC),
                            text = formattedGems,
                            onPlusClick = onOpenShop
                        )
                        Surface(
                            color = Color(0xCC0A2E75),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, Color(0xFF56CCF2)),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(Icons.Filled.Backpack, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Kapasitas: $keptCount/${player.inventoryCapacity}",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        InventoryTopCircleBtn(
                            icon = Icons.Filled.Home,
                            contentDesc = "Lobby",
                            onClick = onBack,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // 2. GLOSSY 5-TAB BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compactHeight) 32.dp else 38.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    tabs.forEachIndexed { idx, (label, icon) ->
                        val isActive = selectedTab == idx
                        val isCraftTab = idx == 4
                        val gradientColors = when {
                            isActive && isCraftTab -> listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                            isActive -> listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                            isCraftTab -> listOf(Color(0xCCB45309), Color(0xCC78350F))
                            else -> listOf(Color(0xCC0A2E75), Color(0xCC061C47))
                        }
                        Surface(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(if (compactHeight) 32.dp else 38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.verticalGradient(gradientColors))
                                .border(
                                    width = if (isActive) 1.6.dp else 1.dp,
                                    color = if (isActive) Color(0xFFFEF08A) else Color(0x8856CCF2),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedTab = idx }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    fontSize = if (compactHeight) 10.sp else 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // 3. MAIN 3-PANE WORKSPACE
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // LEFT PANE: Inventory Grid (Ikan / Umpan / Joran & Gear / Item & Bahan / Daftar Upgrade)
                    Surface(
                        color = Color(0xCC0A2E75),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.4.dp, Color(0xFF56CCF2)),
                        modifier = Modifier
                            .weight(0.36f)
                            .fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (selectedTab == 0) {
                                Text(
                                    text = "Kapasitas Ikan: $keptCount / ${player.inventoryCapacity}",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                // Rarity Filters
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    item {
                                        MiniFilterPill(
                                            label = "Semua",
                                            selected = rarityFilter == null,
                                            accent = Color(0xFF38BDF8),
                                            onClick = { rarityFilter = null; currentPage = 0 }
                                        )
                                    }
                                    items(FishRarity.entries) { rarity ->
                                        MiniFilterPill(
                                            label = rarity.label,
                                            selected = rarityFilter == rarity,
                                            accent = rarity.color,
                                            onClick = {
                                                rarityFilter = if (rarityFilter == rarity) null else rarity
                                                currentPage = 0
                                            }
                                        )
                                    }
                                }
                                // Sort Pills
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf("Terbaru", "Berat", "Nama").forEach { sort ->
                                        MiniFilterPill(
                                            label = sort,
                                            selected = sortMode == sort,
                                            accent = GoldAccent,
                                            onClick = { sortMode = sort; currentPage = 0 }
                                        )
                                    }
                                }

                                if (pagedFish.isEmpty()) {
                                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "Belum ada ikan tersimpan.\nAyo memancing!",
                                            fontSize = 11.sp,
                                            color = Color(0xFFBAE6FD),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(5.dp),
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        items(pagedFish, key = { it.id }) { fish ->
                                            val sp = GameCatalog.fishSpecies.find { it.id == fish.fishId } ?: GameCatalog.fishSpecies.first()
                                            val isSelected = inspectedFish?.id == fish.id
                                            Surface(
                                                color = Color(0xAA061C47),
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(
                                                    width = if (isSelected) 2.dp else 1.2.dp,
                                                    color = if (isSelected) GoldAccent else fish.rarity.color
                                                ),
                                                modifier = Modifier
                                                    .height(if (compactHeight) 62.dp else 82.dp)
                                                    .clickable { selectedFishId = fish.id }
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(3.dp),
                                                    verticalArrangement = Arrangement.SpaceBetween,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Surface(
                                                        color = fish.rarity.color.copy(alpha = 0.28f),
                                                        shape = RoundedCornerShape(5.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = fish.rarity.label,
                                                            fontSize = 7.sp,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                            maxLines = 1
                                                        )
                                                    }
                                                    FishIllustrationCanvas(
                                                        species = sp,
                                                        discovered = true,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .weight(1f)
                                                            .padding(vertical = 1.dp)
                                                    )
                                                    Text(
                                                        text = "${fish.weightKg} kg",
                                                        fontSize = 9.sp,
                                                        color = GoldAccent,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Pagination Bar (< 1 / N >)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0x44FFFFFF),
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clickable { if (safePage > 0) currentPage = safePage - 1 }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("◄", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = "${safePage + 1} / $totalPages",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Surface(
                                        color = Color(0x44FFFFFF),
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clickable { if (safePage < totalPages - 1) currentPage = safePage + 1 }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("►", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                }
                            } else {
                                // Gear / Bait / Item Grid
                                val steelQty = ownedGear.find { it.itemId == "mat_serat_baja" }?.quantity ?: 0
                                val crystalQty = ownedGear.find { it.itemId == "mat_inti_kristal" }?.quantity ?: 0
                                Text(
                                    text = if (selectedTab == 4) "Baja: x$steelQty • Kristal: x$crystalQty" else tabs[selectedTab].first,
                                    fontSize = 11.sp,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(filteredGear, key = { it.itemId }) { item ->
                                        val isEquipped = item.itemId in listOf(
                                            player.equippedRodId, player.equippedReelId,
                                            player.equippedLineId, player.equippedBaitId, player.equippedSkinId
                                        )
                                        val isSelected = inspectedGear?.itemId == item.itemId
                                        Surface(
                                            color = Color(0xAA061C47),
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.2.dp,
                                                color = if (isSelected) GoldAccent else item.rarity.color
                                            ),
                                            modifier = Modifier
                                                .height(if (compactHeight) 68.dp else 82.dp)
                                                .clickable { selectedGearId = item.itemId }
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(5.dp),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = gearCategoryIcon(item.category),
                                                        contentDescription = null,
                                                        tint = item.rarity.color,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    if (isEquipped) {
                                                        Surface(color = EmeraldSuccess, shape = RoundedCornerShape(4.dp)) {
                                                            Row(
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Icon(Icons.Filled.Check, contentDescription = null, tint = OceanDeep, modifier = Modifier.size(9.dp))
                                                                Text("Aktif", fontSize = 7.sp, color = OceanDeep, fontWeight = FontWeight.ExtraBold)
                                                            }
                                                        }
                                                    } else {
                                                        Text(
                                                            text = if (item.category in listOf(GearCategory.BAIT, GearCategory.ITEM)) "x${item.quantity}" else "Lv.${item.level}",
                                                            fontSize = 9.sp,
                                                            color = GoldAccent,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = item.name,
                                                    fontSize = 10.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 2,
                                                    lineHeight = 11.sp,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // CENTER PANE: Detailed Inspector (Fish or Gear)
                    if (selectedTab == 0) {
                        InventoryFishInspectorPane(
                            fish = inspectedFish,
                            compact = compactHeight,
                            onSellFish = onSellFish,
                            modifier = Modifier
                                .weight(0.31f)
                                .fillMaxSize()
                        )
                    } else {
                        InventoryGearInspectorPane(
                            gear = inspectedGear,
                            player = player,
                            compact = compactHeight,
                            onEquipGear = onEquipGear,
                            onUpgradeGear = onUpgradeGear,
                            modifier = Modifier
                                .weight(0.31f)
                                .fillMaxSize()
                        )
                    }

                    // RIGHT PANE: Peningkatan Perlengkapan + Peracikan (Crafting)
                    WorkshopRightColumnPane(
                        upgradables = upgradables,
                        ownedGear = ownedGear,
                        selectedRecipeIdx = selectedRecipeIdx,
                        onChangeRecipeIdx = { selectedRecipeIdx = it },
                        onSelectGear = { gear ->
                            selectedGearId = gear.itemId
                            if (selectedTab == 0) selectedTab = 2
                        },
                        onUpgradeGear = onUpgradeGear,
                        onCraftRecipe = onCraftRecipe,
                        compact = compactHeight,
                        modifier = Modifier
                            .weight(0.33f)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = OceanDeep,
            title = { Text("Panduan Inventori & Bengkel", color = Color.White) },
            text = {
                Text(
                    "• Pilih ikan di panel kiri untuk melihat detail habitat & menjualnya.\n" +
                        "• Pilih Joran, Reel, Senar, atau Umpan untuk memasang langsung.\n" +
                        "• Gunakan Serat Baja dan Koin di panel kanan untuk menaikkan level gear dan meracik umpan/buff!",
                    color = Color(0xFFE0F2FE)
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Mengerti", color = OceanCyan)
                }
            }
        )
    }
}

@Composable
private fun InventoryTopCircleBtn(
    icon: ImageVector,
    contentDesc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xCC0A2E75),
        shape = CircleShape,
        border = BorderStroke(1.3.dp, Color(0xFF56CCF2)),
        modifier = modifier
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDesc, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun InventoryHeaderCurrencyPill(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    onPlusClick: () -> Unit
) {
    Surface(
        color = Color(0xCC0A2E75),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.2.dp, Color(0xFF56CCF2)),
        modifier = Modifier
            .height(38.dp)
            .clickable(onClick = onPlusClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(17.dp))
            Text(text, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Surface(
                color = Color(0xFF0EA5E9),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
private fun MiniFilterPill(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) accent else Color(0x88061C47),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (selected) Color.White else accent.copy(alpha = 0.5f)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (selected && accent == GoldAccent) OceanDeep else Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
