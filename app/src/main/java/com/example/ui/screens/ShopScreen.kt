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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.ActiveEventInfo
import com.example.domain.GameCatalog
import com.example.domain.GearCategory
import com.example.domain.OwnedGear
import com.example.domain.PlayerState
import com.example.domain.ShopProduct
import com.example.domain.WeatherType
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OceanDeep
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ShopScreen(
    player: PlayerState,
    ownedGear: List<OwnedGear>,
    onBuyProduct: (ShopProduct) -> Unit,
    onBack: () -> Unit,
    activeEvent: ActiveEventInfo = ActiveEventInfo(
        id = "evt_festival",
        title = "Festival Memancing",
        description = "Dapatkan Joran Legendaris & Skin Eksklusif!",
        targetWeather = WeatherType.BADAI,
        remainingSeconds = 540,
        rewardSummary = "Peti Event Badai +60 Gem"
    ),
    onOpenEvents: () -> Unit = {}
) {
    val categories = listOf(
        "Semua" to Icons.Filled.Star,
        "Joran" to Icons.Filled.Phishing,
        "Reel" to Icons.Filled.Hardware,
        "Senar" to Icons.Filled.Shield,
        "Umpan" to Icons.Filled.AutoAwesome,
        "Skin" to Icons.Filled.EmojiEvents,
        "Item & Gem" to Icons.Filled.Diamond,
        "Penawaran Event" to Icons.Filled.LocalFireDepartment
    )
    var selectedCat by remember { mutableIntStateOf(0) }

    val products = remember(selectedCat) {
        GameCatalog.shopProducts.filter { p ->
            when (selectedCat) {
                0 -> p.coinPrice > 0 || p.gemPrice > 0
                1 -> p.category == GearCategory.ROD && (p.coinPrice > 0 || p.gemPrice > 0)
                2 -> p.category == GearCategory.REEL && (p.coinPrice > 0 || p.gemPrice > 0)
                3 -> p.category == GearCategory.LINE && (p.coinPrice > 0 || p.gemPrice > 0)
                4 -> p.category == GearCategory.BAIT
                5 -> p.category == GearCategory.SKIN && (p.coinPrice > 0 || p.gemPrice > 0)
                6 -> p.category == GearCategory.ITEM
                else -> p.isEventOffer
            }
        }
    }

    val dailyOffer = remember {
        GameCatalog.shopProducts.find { it.id == "gem_pack_event" } ?: GameCatalog.shopProducts.last()
    }
    val eventTicketCount = remember(ownedGear) {
        ownedGear.find { it.itemId == "bait_kristal" }?.quantity ?: 0
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
            contentDescription = "Toko Perlengkapan",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x99062359), Color(0x88061C45), Color(0xC204122E))
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
                // 1. TOP HUD BAR: Back + Title | Coins + Gems + Tickets + Home
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ShopCircleBtn(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDesc = "Kembali",
                            onClick = onBack,
                            modifier = Modifier.size(44.dp)
                        )
                        Surface(
                            color = Color(0xCC0A2E75),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.3.dp, Color(0xFF56CCF2)),
                            modifier = Modifier.height(if (compactHeight) 40.dp else 44.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(18.dp))
                                Column {
                                    Text(
                                        text = "Toko Perlengkapan & Event",
                                        style = if (compactHeight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontStyle = FontStyle.Italic
                                    )
                                    if (!compactHeight) {
                                        Text(
                                            text = "Lengkapi perlengkapanmu untuk memancing lebih maksimal!",
                                            fontSize = 10.sp,
                                            color = Color(0xFFBAE6FD)
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
                        ShopCurrencyPill(
                            icon = Icons.Filled.MonetizationOn,
                            iconTint = GoldAccent,
                            text = formattedCoins,
                            onPlusClick = { selectedCat = 6 }
                        )
                        ShopCurrencyPill(
                            icon = Icons.Filled.Diamond,
                            iconTint = Color(0xFFC084FC),
                            text = formattedGems,
                            onPlusClick = { selectedCat = 6 }
                        )
                        ShopCurrencyPill(
                            icon = Icons.Filled.ConfirmationNumber,
                            iconTint = Color(0xFF38BDF8),
                            text = "$eventTicketCount",
                            onPlusClick = { selectedCat = 7 }
                        )
                        ShopCircleBtn(
                            icon = Icons.Filled.Home,
                            contentDesc = "Lobby",
                            onClick = onBack,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // 2. GLOSSY 8-TAB CATEGORY BAR
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compactHeight) 32.dp else 38.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    itemsIndexed(categories) { idx, (label, icon) ->
                        val isActive = selectedCat == idx
                        val isEventTab = idx == 7
                        val gradientColors = when {
                            isActive && isEventTab -> listOf(Color(0xFFEF4444), Color(0xFFB91C1C))
                            isActive -> listOf(Color(0xFFFDE047), Color(0xFFF59E0B))
                            isEventTab -> listOf(Color(0xCCDC2626), Color(0xCC991B1B))
                            else -> listOf(Color(0xCC0A2E75), Color(0xCC061C47))
                        }
                        val contentColor = if (isActive && !isEventTab) OceanDeep else Color.White

                        Box {
                            Surface(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(if (compactHeight) 32.dp else 38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.verticalGradient(gradientColors))
                                    .border(
                                        width = if (isActive) 1.6.dp else 1.dp,
                                        color = if (isActive) Color(0xFFFEF08A) else Color(0x8856CCF2),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedCat = idx }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = label,
                                        fontSize = if (compactHeight) 10.sp else 11.sp,
                                        color = contentColor,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            if (isEventTab) {
                                Surface(
                                    color = CoralDanger,
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, Color.White),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .size(14.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("!", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. MAIN SPLIT WORKSPACE: Left 4-Column Product Grid + Right Event/Daily Deal Sidebar
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left 66%: Product Cards Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .weight(0.66f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(bottom = 2.dp)
                    ) {
                        items(products, key = { it.id }) { prod ->
                            val alreadyOwnedUnique = ownedGear.any { it.itemId == prod.id } &&
                                prod.category in listOf(GearCategory.ROD, GearCategory.REEL, GearCategory.LINE, GearCategory.SKIN)
                            val canAfford = player.coins >= prod.coinPrice && player.gems >= prod.gemPrice
                            ShopProductCard(
                                product = prod,
                                alreadyOwnedUnique = alreadyOwnedUnique,
                                canAfford = canAfford,
                                compact = compactHeight,
                                onBuy = { onBuyProduct(prod) }
                            )
                        }
                    }

                    // Right 34%: Event Spesial + Penawaran Harian
                    Column(
                        modifier = Modifier
                            .weight(0.34f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(if (compactHeight) 5.dp else 8.dp)
                    ) {
                        // Event Spesial Promo Banner
                        val mins = activeEvent.remainingSeconds / 60
                        val secs = activeEvent.remainingSeconds % 60
                        val eventProgress = (player.battlePassXp % 250).coerceAtLeast(35)

                        Surface(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF7DD3FC)),
                            modifier = Modifier
                                .weight(0.56f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xE84C1D95), Color(0xE81D4ED8), Color(0xE80284C7))
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp, vertical = if (compactHeight) 6.dp else 8.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(color = Color(0xFFEF4444), shape = RoundedCornerShape(6.dp)) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(11.dp))
                                            Text("Event Spesial", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(
                                        text = "Sisa: ${mins}m ${secs}d",
                                        fontSize = 9.sp,
                                        color = Color(0xFFFDE047),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = activeEvent.title,
                                            style = if (compactHeight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                                            color = GoldAccent,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontStyle = FontStyle.Italic,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = activeEvent.description,
                                            fontSize = 9.sp,
                                            color = Color(0xFFE0F2FE),
                                            maxLines = 2,
                                            lineHeight = 11.sp,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Surface(
                                        color = CoralDanger,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Limited!",
                                            fontSize = 8.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Progress Event Bar
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Progress Event:", fontSize = 9.sp, color = Color(0xFFBAE6FD))
                                        Text("$eventProgress / 250", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(7.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0x9904122E))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth((eventProgress / 250f).coerceIn(0.08f, 1f))
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(50))
                                                .background(Brush.horizontalGradient(listOf(Color(0xFFFACC15), Color(0xFFF59E0B))))
                                        )
                                    }
                                }

                                Button(
                                    onClick = { selectedCat = 7 },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = OceanDeep),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(if (compactHeight) 28.dp else 34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Brush.horizontalGradient(listOf(Color(0xFFFDE047), Color(0xFFF59E0B))))
                                        .border(1.dp, Color(0xFFFEF08A), RoundedCornerShape(10.dp))
                                ) {
                                    Text("Ikut Event Sekarang!", fontSize = 10.sp, color = Color(0xFF072356), fontWeight = FontWeight.ExtraBold)
                                    Spacer(Modifier.width(3.dp))
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF072356), modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        // Penawaran Harian Card
                        val canAffordDaily = player.coins >= dailyOffer.coinPrice && player.gems >= dailyOffer.gemPrice
                        Surface(
                            color = Color(0xCC0A2E75),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.4.dp, Color(0xFF56CCF2)),
                            modifier = Modifier
                                .weight(0.44f)
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp, vertical = if (compactHeight) 5.dp else 7.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = CoralDanger, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Penawaran Harian", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Surface(color = CoralDanger, shape = RoundedCornerShape(5.dp)) {
                                        Text("-50%", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFF1E3A8A),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, GoldAccent),
                                        modifier = Modifier.size(if (compactHeight) 36.dp else 44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Filled.Diamond, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(22.dp))
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = dailyOffer.name,
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = "4.400",
                                                fontSize = 9.sp,
                                                color = Color(0xFF94A3B8),
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                            Text(
                                                text = "${dailyOffer.coinPrice} Koin",
                                                fontSize = 10.sp,
                                                color = GoldAccent,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = { onBuyProduct(dailyOffer) },
                                        enabled = canAffordDaily,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9), contentColor = Color.White),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(if (compactHeight) 28.dp else 34.dp)
                                    ) {
                                        Text("Beli", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. BOTTOM TIPS & QUICK SHORTCUT STRIP
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compactHeight) 38.dp else 44.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xCC0A2E75),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF56CCF2)),
                        modifier = Modifier
                            .weight(1.45f)
                            .fillMaxHeight()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(16.dp))
                            Text(
                                text = "Tips: Kumpulkan lebih banyak koin dan gem untuk mendapatkan perlengkapan terbaik!",
                                fontSize = 10.sp,
                                color = Color(0xFFE0F2FE),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Button(
                        onClick = { selectedCat = 6 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(0.85f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF0EA5E9))))
                            .border(1.3.dp, Color(0xFFBAE6FD), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Filled.Diamond, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Tukar Gem", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Button(
                        onClick = onOpenEvents,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(0.75f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF1D4ED8), Color(0xFF0369A1))))
                            .border(1.3.dp, Color(0xFF7DD3FC), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Misi Event", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopProductCard(
    product: ShopProduct,
    alreadyOwnedUnique: Boolean,
    canAfford: Boolean,
    compact: Boolean,
    onBuy: () -> Unit
) {
    val cardHeight = if (compact) 98.dp else 132.dp
    val formattedCoinPrice = remember(product.coinPrice) {
        NumberFormat.getIntegerInstance(Locale.GERMANY).format(product.coinPrice)
    }

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .shadow(4.dp, RoundedCornerShape(12.dp), ambientColor = product.rarity.color)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(product.rarity.color.copy(alpha = 0.28f), Color(0xDD08245B))
                )
            )
            .border(
                width = 1.4.dp,
                color = if (alreadyOwnedUnique) EmeraldSuccess else product.rarity.color,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rarity Tag + Product Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = product.rarity.color.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = product.rarity.label,
                        fontSize = 8.sp,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
                Text(
                    text = product.name,
                    fontSize = if (compact) 10.sp else 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            // Icon Showcase + Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = gearCategoryIcon(product.category),
                    contentDescription = product.name,
                    tint = if (alreadyOwnedUnique) EmeraldSuccess else GoldAccent,
                    modifier = Modifier.size(if (compact) 22.dp else 28.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    if (product.power > 0 || product.control > 0) {
                        Text("Power +${product.power}", fontSize = 8.sp, color = Color(0xFFE0F2FE), maxLines = 1)
                        Text("Kontrol +${product.control}", fontSize = 8.sp, color = Color(0xFFBAE6FD), maxLines = 1)
                    } else {
                        Text(
                            text = product.description,
                            fontSize = 8.sp,
                            color = Color(0xFFE0F2FE),
                            maxLines = 2,
                            lineHeight = 9.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Buy / Owned Button (preserving exact testTag)
            val isGemOnly = product.coinPrice == 0 && product.gemPrice > 0
            Button(
                onClick = onBuy,
                enabled = !alreadyOwnedUnique && canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    contentColor = OceanDeep
                ),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 26.dp else 32.dp)
                    .alpha(if (!alreadyOwnedUnique && !canAfford) 0.55f else 1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            when {
                                alreadyOwnedUnique -> listOf(Color(0xFF15803D), Color(0xFF065F46))
                                isGemOnly -> listOf(Color(0xFF9333EA), Color(0xFF6D28D9))
                                else -> listOf(Color(0xFFFDE047), Color(0xFFF59E0B))
                            }
                        )
                    )
                    .border(
                        1.dp,
                        when {
                            alreadyOwnedUnique -> EmeraldSuccess
                            isGemOnly -> Color(0xFFE9D5FF)
                            else -> Color(0xFFFEF08A)
                        },
                        RoundedCornerShape(8.dp)
                    )
                    .testTag("buy_${product.id}")
            ) {
                when {
                    alreadyOwnedUnique -> {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Sudah Dimiliki", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    isGemOnly -> {
                        Icon(Icons.Filled.Diamond, contentDescription = null, tint = Color(0xFFE9D5FF), modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("${product.gemPrice}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                    else -> {
                        Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = Color(0xFF072356), modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(2.dp))
                        val priceLabel = if (product.gemPrice > 0) "$formattedCoinPrice + ${product.gemPrice}G" else formattedCoinPrice
                        Text(priceLabel, fontSize = 10.sp, color = Color(0xFF072356), fontWeight = FontWeight.ExtraBold, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopCircleBtn(
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
private fun ShopCurrencyPill(
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
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            Text(text, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Surface(
                color = Color(0xFF0EA5E9),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Add, contentDescription = "Buka Tab", tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}
