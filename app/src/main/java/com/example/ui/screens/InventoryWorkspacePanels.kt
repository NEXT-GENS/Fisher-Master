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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.domain.CaughtFish
import com.example.domain.CraftingRecipe
import com.example.domain.FishRarity
import com.example.domain.GameCatalog
import com.example.domain.GearCategory
import com.example.domain.OwnedGear
import com.example.domain.PlayerState
import com.example.ui.components.FishIllustrationCanvas
import com.example.ui.components.RarityPill
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanCyan
import com.example.ui.theme.OceanDeep
import java.text.NumberFormat
import java.util.Locale

internal fun gearCategoryIcon(category: GearCategory): ImageVector = when (category) {
    GearCategory.ROD -> Icons.Filled.Phishing
    GearCategory.REEL -> Icons.Filled.Hardware
    GearCategory.LINE -> Icons.Filled.Shield
    GearCategory.BAIT -> Icons.Filled.AutoAwesome
    GearCategory.SKIN -> Icons.Filled.EmojiEvents
    GearCategory.ITEM -> Icons.Filled.Science
}

@Composable
internal fun InventoryFishInspectorPane(
    fish: CaughtFish?,
    compact: Boolean,
    onSellFish: (CaughtFish) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xCC0A2E75),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.4.dp, Color(0xFF56CCF2)),
        modifier = modifier
    ) {
        if (fish == null) {
            Box(modifier = Modifier.fillMaxSize().padding(14.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Pilih ikan di daftar kiri untuk melihat detail lengkap & menjual tangkapan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFBAE6FD),
                    textAlign = TextAlign.Center
                )
            }
            return@Surface
        }

        val species = remember(fish.fishId) {
            GameCatalog.fishSpecies.find { it.id == fish.fishId } ?: GameCatalog.fishSpecies.first()
        }
        val location = remember(fish.locationId) { GameCatalog.locationById(fish.locationId) }
        val formattedPrice = remember(fish.sellPrice) {
            NumberFormat.getIntegerInstance(Locale.GERMANY).format(fish.sellPrice)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (compact) 8.dp else 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
            ) {
                // Header: Rarity + Name + New Record Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        RarityPill(rarity = fish.rarity)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = fish.fishName,
                            style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (fish.isRecord) {
                        Surface(
                            color = GoldAccent,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFEF08A))
                        ) {
                            Text(
                                text = "REKOR BARU!",
                                fontSize = 9.sp,
                                color = OceanDeep,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Scenic Water Showcase with FishIllustrationCanvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 68.dp else 94.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x667DD3FC), RoundedCornerShape(12.dp))
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
                            .background(Color(0x4404122E))
                    )
                    FishIllustrationCanvas(
                        species = species,
                        discovered = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(if (compact) 128.dp else 156.dp, if (compact) 56.dp else 76.dp)
                    )
                }

                // Weight & Length Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatMiniBadge("Berat: ${fish.weightKg} kg", GoldAccent, Modifier.weight(1f))
                    StatMiniBadge("Panjang: ${fish.lengthCm} cm", OceanCyan, Modifier.weight(1f))
                }

                // Metadata Key-Value Rows
                InspectorMetaRow(Icons.Filled.EmojiEvents, "Rarity", fish.rarity.label, fish.rarity.color)
                InspectorMetaRow(Icons.Filled.LocationOn, "Habitat", location.name, Color(0xFFBAE6FD))
                InspectorMetaRow(Icons.Filled.WbSunny, "Cuaca Favorit", species.favoriteWeather.displayNameId.substringBefore("/"), Color(0xFFE0F2FE))
                InspectorMetaRow(Icons.Filled.Schedule, "Waktu Terbaik", species.bestTime.labelId, Color(0xFFE0F2FE))
                InspectorMetaRow(Icons.Filled.MonetizationOn, "Harga Jual", "$formattedPrice Koin", GoldAccent)

                Text(
                    text = species.description,
                    fontSize = 10.sp,
                    color = Color(0xFFE0F2FE),
                    lineHeight = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(4.dp))

            // Primary Gold Sell Button
            Button(
                onClick = { onSellFish(fish) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = OceanDeep),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 36.dp else 42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFFDE047), Color(0xFFFACC15), Color(0xFFD97706))))
                    .border(1.4.dp, Color(0xFFFEF08A), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = Color(0xFF072356), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "Jual Ikan • $formattedPrice",
                    fontSize = 12.sp,
                    color = Color(0xFF072356),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
internal fun InventoryGearInspectorPane(
    gear: OwnedGear?,
    player: PlayerState,
    compact: Boolean,
    onEquipGear: (OwnedGear) -> Unit,
    onUpgradeGear: (OwnedGear) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xCC0A2E75),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.4.dp, Color(0xFF56CCF2)),
        modifier = modifier
    ) {
        if (gear == null) {
            Box(modifier = Modifier.fillMaxSize().padding(14.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Pilih perlengkapan atau umpan di daftar kiri untuk melihat statistik & memasang.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFBAE6FD),
                    textAlign = TextAlign.Center
                )
            }
            return@Surface
        }

        val isEquipped = gear.itemId in listOf(
            player.equippedRodId, player.equippedReelId,
            player.equippedLineId, player.equippedBaitId, player.equippedSkinId
        )
        val isUpgradable = gear.category in listOf(GearCategory.ROD, GearCategory.REEL, GearCategory.LINE)
        val catalogDesc = remember(gear.itemId) {
            GameCatalog.shopProducts.find { it.id == gear.itemId }?.description
                ?: "Perlengkapan memancing andalan Angler untuk meningkatkan peluang tangkapan."
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (compact) 8.dp else 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
            ) {
                RarityPill(rarity = gear.rarity)
                Text(
                    text = gear.name,
                    style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 64.dp else 88.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1D4ED8), Color(0xFF0A2156))
                            )
                        )
                        .border(1.2.dp, gear.rarity.color, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = gearCategoryIcon(gear.category),
                        contentDescription = gear.name,
                        tint = Color.White,
                        modifier = Modifier.size(if (compact) 38.dp else 48.dp)
                    )
                    Surface(
                        color = Color(0xAA04122E),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = if (gear.category in listOf(GearCategory.BAIT, GearCategory.ITEM)) "x${gear.quantity}" else "Lv.${gear.level}",
                            fontSize = 10.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (gear.power > 0 || gear.control > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        StatMiniBadge("Power +${gear.power}", GoldAccent, Modifier.weight(1f))
                        StatMiniBadge("Kontrol +${gear.control}", OceanCyan, Modifier.weight(1f))
                    }
                    InspectorMetaRow(Icons.Filled.Shield, "Durability", "${gear.durability} / 350", EmeraldSuccess)
                }

                InspectorMetaRow(Icons.Filled.Hardware, "Kategori", gear.category.labelId, Color(0xFFBAE6FD))
                Text(
                    text = catalogDesc,
                    fontSize = 10.sp,
                    color = Color(0xFFE0F2FE),
                    lineHeight = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onEquipGear(gear) },
                    enabled = !isEquipped && gear.quantity > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEquipped) EmeraldSuccess else Color(0xFF0EA5E9),
                        disabledContainerColor = if (isEquipped) Color(0xAA059669) else Color(0xFF334155),
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(if (compact) 36.dp else 42.dp)
                ) {
                    if (isEquipped) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = when {
                            isEquipped -> "Terpasang"
                            gear.category == GearCategory.ITEM -> "Gunakan"
                            else -> "Pasang"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isUpgradable) {
                    Button(
                        onClick = { onUpgradeGear(gear) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = OceanDeep),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(if (compact) 36.dp else 42.dp)
                    ) {
                        Icon(Icons.Filled.Upgrade, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Upgrade", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
internal fun WorkshopRightColumnPane(
    upgradables: List<OwnedGear>,
    ownedGear: List<OwnedGear>,
    selectedRecipeIdx: Int,
    onChangeRecipeIdx: (Int) -> Unit,
    onSelectGear: (OwnedGear) -> Unit,
    onUpgradeGear: (OwnedGear) -> Unit,
    onCraftRecipe: (CraftingRecipe) -> Unit,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val recipes = GameCatalog.craftingRecipes
    val safeIdx = selectedRecipeIdx.coerceIn(0, (recipes.size - 1).coerceAtLeast(0))
    val activeRecipe = recipes[safeIdx]
    val ownedMatQty = ownedGear.find { it.itemId == activeRecipe.requiredMaterialId }?.quantity ?: 0

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 8.dp)
    ) {
        // TOP CARD: Peningkatan Perlengkapan
        Surface(
            color = Color(0xCC0A2E75),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.4.dp, Color(0xFF56CCF2)),
            modifier = Modifier
                .weight(0.58f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Peningkatan Perlengkapan",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(upgradables, key = { "upg_${it.itemId}" }) { gear ->
                        val cost = gear.level * 220
                        val formattedCost = NumberFormat.getIntegerInstance(Locale.GERMANY).format(cost)
                        Surface(
                            color = Color(0xAA061C47),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0x667DD3FC)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectGear(gear) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Surface(
                                    color = Color(0xFF1E3A8A),
                                    shape = RoundedCornerShape(9.dp),
                                    border = BorderStroke(1.dp, gear.rarity.color),
                                    modifier = Modifier.size(if (compact) 34.dp else 40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = gearCategoryIcon(gear.category),
                                            contentDescription = gear.name,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = gear.name,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Lv. ${gear.level} / 10  •  Pow +${gear.power}→+${gear.power + 6}",
                                        fontSize = 9.sp,
                                        color = EmeraldSuccess,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0x8804122E))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth((gear.level / 10f).coerceIn(0.1f, 1f))
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(50))
                                                .background(Brush.horizontalGradient(listOf(GoldAccent, Color(0xFFF59E0B))))
                                        )
                                    }
                                }
                                Button(
                                    onClick = { onUpgradeGear(gear) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9), contentColor = Color.White),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(if (compact) 34.dp else 38.dp)
                                        .testTag("upgrade_${gear.itemId}")
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Upgrade", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 10.sp)
                                        Text(formattedCost, fontSize = 9.sp, color = Color(0xFFFDE047), fontWeight = FontWeight.Bold, lineHeight = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM CARD: Peracikan (Crafting)
        Surface(
            color = Color(0xCC0A2E75),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.4.dp, Color(0xFF56CCF2)),
            modifier = Modifier
                .weight(0.42f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Peracikan (Crafting)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = Color(0x44FFFFFF),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onChangeRecipeIdx((safeIdx - 1 + recipes.size) % recipes.size) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("◄", fontSize = 9.sp, color = Color.White)
                            }
                        }
                        Text("${safeIdx + 1}/${recipes.size}", fontSize = 10.sp, color = Color(0xFFBAE6FD), fontWeight = FontWeight.Bold)
                        Surface(
                            color = Color(0x44FFFFFF),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onChangeRecipeIdx((safeIdx + 1) % recipes.size) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("►", fontSize = 9.sp, color = Color.White)
                            }
                        }
                    }
                }

                // Recipe Material -> Output Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Color(0xAA061C47),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (ownedMatQty >= activeRecipe.requiredMaterialQty) EmeraldSuccess else Color(0x66F87171)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = activeRecipe.requiredMaterialName.replace("Material ", ""),
                                fontSize = 9.sp,
                                color = Color(0xFFBAE6FD),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "$ownedMatQty / ${activeRecipe.requiredMaterialQty}",
                                fontSize = 11.sp,
                                color = if (ownedMatQty >= activeRecipe.requiredMaterialQty) EmeraldSuccess else GoldAccent,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Surface(
                        color = Color(0xAA061C47),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.2.dp, activeRecipe.outputRarity.color),
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = activeRecipe.outputName,
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "x${activeRecipe.outputQty}",
                                fontSize = 10.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Button(
                        onClick = { onCraftRecipe(activeRecipe) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = OceanDeep),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(if (compact) 34.dp else 38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFFFDE047), Color(0xFFF59E0B))))
                            .border(1.dp, Color(0xFFFEF08A), RoundedCornerShape(10.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Build, contentDescription = null, tint = Color(0xFF072356), modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(3.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Craft", fontSize = 9.sp, color = Color(0xFF072356), fontWeight = FontWeight.ExtraBold, lineHeight = 10.sp)
                                Text("${activeRecipe.coinCost}", fontSize = 9.sp, color = Color(0xFF072356), fontWeight = FontWeight.Bold, lineHeight = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMiniBadge(
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0x88061C47),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.9.dp, accent.copy(alpha = 0.65f)),
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = accent,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun InspectorMetaRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$label:",
            fontSize = 10.sp,
            color = Color(0xFFBAE6FD),
            modifier = Modifier.width(74.dp)
        )
        Text(
            text = value,
            fontSize = 10.sp,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
