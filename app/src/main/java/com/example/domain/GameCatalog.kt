package com.example.domain

import androidx.compose.ui.graphics.Color
import com.example.R

object GameCatalog {

    val fishingTips = listOf(
        "Gunakan umpan yang sesuai dengan habitat ikan untuk hasil maksimal!",
        "Perhatikan warna bar ketegangan! Lepaskan Tarik saat mendekati zona merah agar senar tidak putus.",
        "Ikan Epic dan Legendary lebih sering muncul saat cuaca Badai atau Malam hari.",
        "Upgrade Joran di menu Inventori untuk meningkatkan Power, Kontrol, dan Durability.",
        "Cek Prakiraan Cuaca di HUD atas untuk bersiap sebelum Badai tiba!"
    )

    val avatars = listOf(
        "avatar_marlin" to "Kapten Marlin",
        "avatar_koral" to "Penyelam Koral",
        "avatar_aurora" to "Pemburu Es",
        "avatar_samurai" to "Angler Naga"
    )

    val locations: List<LocationInfo> = listOf(
        LocationInfo(
            id = "danau_tenang",
            name = "Danau Tenang",
            habitat = "Danau Air Tawar",
            difficulty = "Mudah",
            baseTempC = 24,
            recommendedBait = "Cacing Tanah / Roti",
            unlockLevel = 1,
            description = "Danau pegunungan tenang berair jernih dengan bunga sakura dan ikan air tawar melimpah.",
            defaultWeather = WeatherType.CERAH,
            depthMeters = 8,
            backgroundRes = R.drawable.img_location_lake,
            tintColor = Color(0x2200B4D8)
        ),
        LocationInfo(
            id = "sungai_hijau",
            name = "Sungai Hijau",
            habitat = "Sungai Hutan Tropis",
            difficulty = "Menengah",
            baseTempC = 27,
            recommendedBait = "Jangkrik / Umpan Lumut",
            unlockLevel = 1,
            description = "Aliran sungai deras di tengah rimba zamrud, rumah bagi Arwana Emas dan Baung Sungai.",
            defaultWeather = WeatherType.HUJAN,
            depthMeters = 10,
            backgroundRes = R.drawable.img_location_lake,
            tintColor = Color(0x4410B981)
        ),
        LocationInfo(
            id = "laut_biru",
            name = "Laut Biru",
            habitat = "Samudra Terbuka",
            difficulty = "Menengah-Sulit",
            baseTempC = 28,
            recommendedBait = "Udang Segar / Minnow",
            unlockLevel = 2,
            description = "Lautan biru dalam dengan ombak dinamis dan kawanan Tuna Sirip Kuning serta Marlin.",
            defaultWeather = WeatherType.ANGIN,
            depthMeters = 28,
            backgroundRes = R.drawable.img_location_ocean,
            tintColor = Color(0x220284C7)
        ),
        LocationInfo(
            id = "pulau_tropis",
            name = "Pulau Tropis",
            habitat = "Laguna Terumbu Karang",
            difficulty = "Sulit",
            baseTempC = 31,
            recommendedBait = "Cumi Kilau / Popper Emas",
            unlockLevel = 3,
            description = "Laguna tropis penuh warna tempat Napoleon Wrasse, Pari Manta, dan Hiu Karang berenang.",
            defaultWeather = WeatherType.CERAH,
            depthMeters = 22,
            backgroundRes = R.drawable.img_location_ocean,
            tintColor = Color(0x22F59E0B)
        ),
        LocationInfo(
            id = "air_terjun",
            name = "Air Terjun",
            habitat = "Jeram Berkabut",
            difficulty = "Sulit",
            baseTempC = 19,
            recommendedBait = "Katak Mini / Spinner Perak",
            unlockLevel = 4,
            description = "Air terjun mistis berarus kuat dengan kabut alami dan ikan predator legendaris.",
            defaultWeather = WeatherType.KABUT,
            depthMeters = 15,
            backgroundRes = R.drawable.img_location_ice_falls,
            tintColor = Color(0x3314B8A6)
        ),
        LocationInfo(
            id = "danau_es",
            name = "Danau Es",
            habitat = "Gletser Kutub",
            difficulty = "Ekstrem",
            baseTempC = -4,
            recommendedBait = "Udang Arktik / Umpan Kristal",
            unlockLevel = 5,
            description = "Danau es bercahaya aurora dengan suhu beku. Hanya angler tangguh yang sanggup menaklukkan Leviathan Es!",
            defaultWeather = WeatherType.SALJU,
            depthMeters = 35,
            backgroundRes = R.drawable.img_location_ice_falls,
            tintColor = Color(0x3360A5FA)
        )
    )

    val fishSpecies: List<FishSpecies> = listOf(
        // Danau Tenang
        FishSpecies("fish_nila", "Ikan Nila Perak", FishRarity.COMMON, 0.4, 2.2, 22.0, 42.0, listOf("danau_tenang"), WeatherType.CERAH, TimeOfDay.PAGI, 45, "Ikan air tawar populer yang mudah dipancing di tepi dermaga.", Color(0xFF94A3B8), Color(0xFFE2E8F0), 1),
        FishSpecies("fish_mas", "Ikan Mas Koi", FishRarity.UNCOMMON, 1.0, 4.8, 30.0, 65.0, listOf("danau_tenang"), WeatherType.CERAH, TimeOfDay.SIANG, 95, "Ikan hias berwarna jingga cerah dengan sisik berkilau.", Color(0xFFFF6B35), Color(0xFFFFCA28), 1),
        FishSpecies("fish_gurame", "Gurame Raksasa", FishRarity.RARE, 3.5, 11.0, 48.0, 88.0, listOf("danau_tenang", "sungai_hijau"), WeatherType.HUJAN, TimeOfDay.SORE, 210, "Penghuni danau tenang dengan tarikan kuat di dasar air.", Color(0xFF475569), Color(0xFF38BDF8), 1),
        FishSpecies("fish_naga_danau", "Naga Danau Sakura", FishRarity.EPIC, 8.0, 18.5, 95.0, 150.0, listOf("danau_tenang"), WeatherType.HUJAN, TimeOfDay.MALAM, 520, "Makhluk langka berona sakura yang hanya muncul saat senja atau malam.", Color(0xFFA855F7), Color(0xFFF472B6), 2),

        // Sungai Hijau
        FishSpecies("fish_lele", "Lele Kumis Emas", FishRarity.COMMON, 0.8, 3.5, 32.0, 60.0, listOf("sungai_hijau"), WeatherType.HUJAN, TimeOfDay.MALAM, 55, "Aktif berburu di dasar sungai berlumpur saat hujan turun.", Color(0xFF6B7280), Color(0xFFFBBF24), 2),
        FishSpecies("fish_toman", "Toman Rimba", FishRarity.UNCOMMON, 2.5, 9.2, 55.0, 105.0, listOf("sungai_hijau"), WeatherType.CERAH, TimeOfDay.SIANG, 140, "Predator sungai agresif berpola loreng zamrud.", Color(0xFF15803D), Color(0xFF86EFAC), 0),
        FishSpecies("fish_patin", "Patin Raja", FishRarity.RARE, 6.0, 19.0, 75.0, 130.0, listOf("sungai_hijau", "air_terjun"), WeatherType.HUJAN, TimeOfDay.SORE, 280, "Perenang cepat yang menguji ketahanan senar di arus sungai.", Color(0xFF334155), Color(0xFF60A5FA), 0),
        FishSpecies("fish_arwana", "Arwana Emas Keramat", FishRarity.LEGENDARY, 4.5, 12.8, 80.0, 135.0, listOf("sungai_hijau"), WeatherType.BADAI, TimeOfDay.MALAM, 1350, "Simbol keberuntungan tertinggi Sungai Hijau dengan sisik emas murni.", Color(0xFFF59E0B), Color(0xFFE040FB), 0),

        // Laut Biru
        FishSpecies("fish_tongkol", "Tongkol Samudra", FishRarity.COMMON, 1.5, 6.0, 40.0, 75.0, listOf("laut_biru"), WeatherType.CERAH, TimeOfDay.PAGI, 75, "Ikan pelagis lincah yang bergerak dalam kawanan besar.", Color(0xFF1D4ED8), Color(0xFF93C5FD), 0),
        FishSpecies("fish_kakap", "Kakap Merah Karang", FishRarity.UNCOMMON, 2.2, 8.5, 42.0, 82.0, listOf("laut_biru", "pulau_tropis"), WeatherType.ANGIN, TimeOfDay.SIANG, 165, "Ikan favorit nelayan dengan warna merah menyala.", Color(0xFFEF4444), Color(0xFFFCA5A5), 1),
        FishSpecies("fish_tuna", "Tuna Sirip Kuning", FishRarity.RARE, 18.0, 58.0, 110.0, 195.0, listOf("laut_biru"), WeatherType.ANGIN, TimeOfDay.PAGI, 420, "Petarung laut lepas yang melaju sangat cepat saat terpancing.", Color(0xFF0F172A), Color(0xFFFACC15), 0),
        FishSpecies("fish_marlin", "Marlin Biru Agung", FishRarity.LEGENDARY, 45.0, 180.0, 210.0, 380.0, listOf("laut_biru"), WeatherType.BADAI, TimeOfDay.SORE, 1850, "Raja samudra berparuh tombak yang melompat menembus badai.", Color(0xFF0284C7), Color(0xFFE040FB), 3),

        // Pulau Tropis
        FishSpecies("fish_badut", "Ikan Badut Pelangi", FishRarity.COMMON, 0.2, 0.9, 12.0, 24.0, listOf("pulau_tropis"), WeatherType.CERAH, TimeOfDay.SIANG, 65, "Penghuni anemon karang yang cerah dan lincah.", Color(0xFFF97316), Color(0xFFFFFFFF), 1),
        FishSpecies("fish_napoleon", "Ikan Napoleon", FishRarity.RARE, 12.0, 42.0, 85.0, 160.0, listOf("pulau_tropis"), WeatherType.CERAH, TimeOfDay.SIANG, 460, "Ikan karang raksasa berpola biru-toska yang anggun.", Color(0xFF0D9488), Color(0xFF5EEAD4), 1),
        FishSpecies("fish_pari", "Pari Manta Bintang", FishRarity.EPIC, 35.0, 95.0, 180.0, 320.0, listOf("pulau_tropis", "laut_biru"), WeatherType.ANGIN, TimeOfDay.MALAM, 880, "Meluncur bak sayap di laguna tropis saat malam berbintang.", Color(0xFF312E81), Color(0xFF38BDF8), 1),
        FishSpecies("fish_hiu_karang", "Hiu Karang Zamrud", FishRarity.LEGENDARY, 55.0, 145.0, 190.0, 310.0, listOf("pulau_tropis"), WeatherType.BADAI, TimeOfDay.MALAM, 2100, "Predator puncak laguna yang membutuhkan joran berdaya tinggi.", Color(0xFF047857), Color(0xFFE040FB), 3),

        // Air Terjun
        FishSpecies("fish_trout", "Trout Pelangi Jeram", FishRarity.COMMON, 0.9, 3.8, 28.0, 58.0, listOf("air_terjun"), WeatherType.KABUT, TimeOfDay.PAGI, 85, "Menyukai air dingin beroksigen tinggi di bawah air terjun.", Color(0xFF0EA5E9), Color(0xFFF472B6), 0),
        FishSpecies("fish_salmon", "Salmon Perak", FishRarity.UNCOMMON, 3.0, 9.5, 52.0, 90.0, listOf("air_terjun", "danau_tenang"), WeatherType.HUJAN, TimeOfDay.PAGI, 195, "Mampu melompati arus air terjun dengan tenaga luar biasa.", Color(0xFF64748B), Color(0xFFFB7185), 0),
        FishSpecies("fish_belut_listrik", "Belut Petir Kabut", FishRarity.EPIC, 6.5, 16.0, 120.0, 210.0, listOf("air_terjun"), WeatherType.KABUT, TimeOfDay.MALAM, 790, "Memancarkan kilatan biru di balik kabut air terjun.", Color(0xFF6D28D9), Color(0xFFFDE047), 2),
        FishSpecies("fish_phoenix_koi", "Koi Roh Air Terjun", FishRarity.LEGENDARY, 9.0, 24.0, 95.0, 155.0, listOf("air_terjun"), WeatherType.BADAI, TimeOfDay.SORE, 1980, "Koi legendaris bercahaya emas-ungu yang menjaga Air Terjun.", Color(0xFFEA580C), Color(0xFFE040FB), 1),

        // Danau Es
        FishSpecies("fish_cod_es", "Cod Kutub", FishRarity.COMMON, 2.0, 7.5, 45.0, 85.0, listOf("danau_es"), WeatherType.SALJU, TimeOfDay.SIANG, 110, "Ikan air dingin tangguh di bawah lapisan es kristal.", Color(0xFF0284C7), Color(0xFFE0F2FE), 0),
        FishSpecies("fish_pike_salju", "Pike Salju", FishRarity.RARE, 8.0, 22.0, 85.0, 145.0, listOf("danau_es"), WeatherType.SALJU, TimeOfDay.SORE, 490, "Pemburu cepat dengan tubuh ramping sewarna gletser.", Color(0xFF38BDF8), Color(0xFFFFFFFF), 3),
        FishSpecies("fish_sturgeon", "Sturgeon Berlian", FishRarity.EPIC, 25.0, 78.0, 140.0, 240.0, listOf("danau_es"), WeatherType.KABUT, TimeOfDay.MALAM, 1120, "Ikan purba berlapis pelindung kristal es berkilauan.", Color(0xFF4338CA), Color(0xFF7DD3FC), 3),
        FishSpecies("fish_leviathan", "Leviathan Aurora", FishRarity.LEGENDARY, 85.0, 260.0, 260.0, 450.0, listOf("danau_es"), WeatherType.BADAI, TimeOfDay.MALAM, 3200, "Ikan mitos raksasa Danau Es yang memancarkan cahaya Aurora Borealis!", Color(0xFF7C3AED), Color(0xFF2DD4BF), 3)
    )

    val shopProducts: List<ShopProduct> = listOf(
        // Joran
        ShopProduct("rod_bambu", "Joran Bambu Klasik", GearCategory.ROD, FishRarity.COMMON, 0, 0, 12, 14, 100, "Joran pemula yang ringan dan seimbang untuk danau."),
        ShopProduct("rod_karbon", "Joran Karbon Angin", GearCategory.ROD, FishRarity.UNCOMMON, 450, 0, 24, 22, 140, "Ringan namun kuat menghadapi arus sungai dan laut dangkal."),
        ShopProduct("rod_samudra", "Joran Baja Samudra", GearCategory.ROD, FishRarity.RARE, 1400, 0, 42, 34, 180, "Dirancang khusus menaklukkan Tuna dan Kakap besar."),
        ShopProduct("rod_aurora", "Joran Kristal Aurora", GearCategory.ROD, FishRarity.EPIC, 3500, 45, 68, 52, 240, "Mengurangi lonjakan ketegangan saat cuaca Salju & Badai."),
        ShopProduct("rod_poseidon", "Trisula Poseidon", GearCategory.ROD, FishRarity.LEGENDARY, 0, 120, 98, 80, 350, "Joran legendaris dengan kontrol maksimum untuk ikan raksasa.", isEventOffer = true),

        // Reel
        ShopProduct("reel_dasar", "Reel Standar", GearCategory.REEL, FishRarity.COMMON, 0, 0, 10, 12, 100, "Reel mekanis sederhana untuk memancing santai."),
        ShopProduct("reel_turbo", "Reel Turbo Karang", GearCategory.REEL, FishRarity.RARE, 950, 0, 28, 36, 160, "Kecepatan gulungan +30% untuk menarik ikan agresif."),
        ShopProduct("reel_titan", "Reel Titan Magnetik", GearCategory.REEL, FishRarity.EPIC, 2600, 30, 50, 58, 220, "Sistem rem magnetik mencegah senar putus di zona merah."),

        // Senar
        ShopProduct("line_nilon", "Senar Nilon 10kg", GearCategory.LINE, FishRarity.COMMON, 0, 0, 10, 10, 100, "Senar nilon lentur untuk tangkapan ringan."),
        ShopProduct("line_braid", "Senar Braided 50kg", GearCategory.LINE, FishRarity.RARE, 800, 0, 30, 25, 180, "Senar anyam tahan gesekan karang."),
        ShopProduct("line_titanium", "Senar Serat Titanium", GearCategory.LINE, FishRarity.LEGENDARY, 3200, 40, 72, 55, 300, "Tahan suhu beku Danau Es dan hentakan ikan Legendary."),

        // Umpan
        ShopProduct("bait_cacing", "Cacing Tanah (x10)", GearCategory.BAIT, FishRarity.COMMON, 80, 0, 5, 5, 100, "Umpan universal paling disukai ikan air tawar."),
        ShopProduct("bait_udang", "Udang Segar (x10)", GearCategory.BAIT, FishRarity.UNCOMMON, 220, 0, 15, 10, 100, "Meningkatkan peluang ikan Rare di Laut Biru & Pulau Tropis."),
        ShopProduct("bait_kristal", "Umpan Kristal (x5)", GearCategory.BAIT, FishRarity.EPIC, 650, 10, 35, 25, 100, "Memikat ikan Epic dan Legendary di segala cuaca.", isEventOffer = true),

        // Skin
        ShopProduct("skin_klasik", "Set Nelayan Nusantara", GearCategory.SKIN, FishRarity.COMMON, 0, 0, 0, 0, 100, "Kostum nelayan klasik biru laut."),
        ShopProduct("skin_badai", "Kostum Pemburu Badai", GearCategory.SKIN, FishRarity.EPIC, 1800, 25, 5, 10, 100, "Mantel tahan badai berkilau ungu neon.", isEventOffer = true),
        ShopProduct("skin_aurora", "Zirah Penakluk Es", GearCategory.SKIN, FishRarity.LEGENDARY, 0, 90, 10, 15, 100, "Skin eksklusif dengan efek partikel salju kristal."),

        // Item & Buff
        ShopProduct("item_luck_charm", "Jimat Keberuntungan", GearCategory.ITEM, FishRarity.RARE, 300, 0, 20, 0, 100, "Meningkatkan peluang ikan langka untuk 5 lemparan berikutnya."),
        ShopProduct("item_stabilizer", "Penstabil Senar", GearCategory.ITEM, FishRarity.RARE, 280, 0, 0, 25, 100, "Mengurangi ketegangan senar 25% selama 5 tangkapan."),
        ShopProduct("mat_serat_baja", "Material Serat Baja", GearCategory.ITEM, FishRarity.UNCOMMON, 180, 0, 0, 0, 100, "Bahan utama untuk upgrade joran dan crafting senar."),
        ShopProduct("mat_inti_kristal", "Inti Kristal Laut", GearCategory.ITEM, FishRarity.EPIC, 480, 8, 0, 0, 100, "Bahan langka untuk crafting Umpan Kristal & enhancement."),

        // Paket Gem (Koin -> Gem / Penawaran Event)
        ShopProduct("gem_pack_small", "Kantong 25 Gem", GearCategory.ITEM, FishRarity.RARE, 1000, 0, 0, 0, 100, "Tukarkan 1.000 Koin menjadi 25 Gem berkilau."),
        ShopProduct("gem_pack_event", "Peti Event Badai (+60 Gem)", GearCategory.ITEM, FishRarity.LEGENDARY, 2200, 0, 0, 0, 100, "Penawaran event terbatas: 60 Gem + bonus Umpan Kristal!", isEventOffer = true)
    )

    val craftingRecipes: List<CraftingRecipe> = listOf(
        CraftingRecipe(
            id = "craft_umpan_kristal",
            outputItemId = "bait_kristal",
            outputName = "Umpan Kristal (x5)",
            outputCategory = GearCategory.BAIT,
            outputRarity = FishRarity.EPIC,
            outputQty = 5,
            requiredMaterialId = "mat_inti_kristal",
            requiredMaterialName = "Inti Kristal Laut",
            requiredMaterialQty = 1,
            coinCost = 150,
            description = "Racik umpan bercahaya untuk memancing ikan Epic & Legendary."
        ),
        CraftingRecipe(
            id = "craft_jimat_luck",
            outputItemId = "item_luck_charm",
            outputName = "Jimat Keberuntungan",
            outputCategory = GearCategory.ITEM,
            outputRarity = FishRarity.RARE,
            outputQty = 1,
            requiredMaterialId = "mat_serat_baja",
            requiredMaterialName = "Material Serat Baja",
            requiredMaterialQty = 2,
            coinCost = 120,
            description = "Memberikan buff peluang ikan langka (+40%) selama 5 lemparan."
        ),
        CraftingRecipe(
            id = "craft_stabilizer",
            outputItemId = "item_stabilizer",
            outputName = "Penstabil Senar",
            outputCategory = GearCategory.ITEM,
            outputRarity = FishRarity.RARE,
            outputQty = 1,
            requiredMaterialId = "mat_serat_baja",
            requiredMaterialName = "Material Serat Baja",
            requiredMaterialQty = 1,
            coinCost = 100,
            description = "Menjaga ketegangan senar tetap stabil di zona hijau."
        )
    )

    val missions: List<MissionDefinition> = listOf(
        MissionDefinition("m_daily_catch5", "Pemanasan Pagi", "Tangkap 5 ekor ikan di lokasi mana saja.", MissionCategory.DAILY, 5, 250, 5, 120),
        MissionDefinition("m_daily_sell3", "Pedagang Dermaga", "Jual 3 ekor ikan hasil tangkapan.", MissionCategory.DAILY, 3, 200, 3, 90),
        MissionDefinition("m_daily_rare1", "Pemburu Langka", "Tangkap 1 ikan Rare, Epic, atau Legendary.", MissionCategory.DAILY, 1, 400, 10, 180),
        MissionDefinition("m_weekly_catch25", "Nelayan Tangguh", "Tangkap 25 ekor ikan minggu ini.", MissionCategory.WEEKLY, 25, 1200, 25, 500),
        MissionDefinition("m_weekly_upgrade2", "Pandai Joran", "Lakukan upgrade perlengkapan 2 kali.", MissionCategory.WEEKLY, 2, 800, 15, 300),
        MissionDefinition("m_ach_legendary", "Sang Penakluk Legenda", "Tangkap ikan bertingkat Legendary pertama kamu.", MissionCategory.ACHIEVEMENT, 1, 2500, 60, 1000),
        MissionDefinition("m_ach_fishdex10", "Kolektor Fishdex", "Temukan 10 spesies ikan berbeda di Fishdex.", MissionCategory.ACHIEVEMENT, 10, 1500, 40, 750)
    )

    val dailyRewards: List<DailyRewardDefinition> = listOf(
        DailyRewardDefinition(1, "Hari 1", 250, 5, "bait_cacing"),
        DailyRewardDefinition(2, "Hari 2", 350, 8, "mat_serat_baja"),
        DailyRewardDefinition(3, "Hari 3", 500, 12, "bait_udang"),
        DailyRewardDefinition(4, "Hari 4", 650, 15, "item_luck_charm"),
        DailyRewardDefinition(5, "Hari 5", 850, 20, "mat_inti_kristal"),
        DailyRewardDefinition(6, "Hari 6", 1100, 28, "bait_kristal"),
        DailyRewardDefinition(7, "Hari 7 Spesial", 2000, 50, "item_stabilizer")
    )

    val battlePassTiers: List<BattlePassTier> = (1..8).map { tier ->
        BattlePassTier(
            tier = tier,
            requiredXp = tier * 150,
            freeRewardLabel = "+${tier * 150} Koin",
            freeCoins = tier * 150,
            freeGems = if (tier % 2 == 0) 5 else 0,
            premiumRewardLabel = if (tier == 8) "Skin Zirah Es + 50 Gem" else "+${tier * 250} Koin & ${tier * 5} Gem",
            premiumCoins = tier * 250,
            premiumGems = if (tier == 8) 50 else tier * 5,
            premiumSkinId = if (tier == 8) "skin_aurora" else null
        )
    }

    val quickChatPresets = listOf(
        "Halo semuanya! Selamat memancing!",
        "Strike!! Ikan besar menyambar!",
        "Cuaca akan segera berubah, siap-siap!",
        "Mantap tangkapannya!",
        "Ayo gabung ke Laut Biru!"
    )

    val emotePresets = listOf("🎣", "🐟", "🏆", "⚡", "🔥", "👏")

    fun locationById(id: String): LocationInfo =
        locations.find { it.id == id } ?: locations.first()

    fun speciesForLocation(locationId: String): List<FishSpecies> =
        fishSpecies.filter { locationId in it.locationIds }

    fun starterGear(): List<OwnedGear> = listOf(
        OwnedGear("rod_bambu", GearCategory.ROD, "Joran Bambu Klasik", FishRarity.COMMON, 1, 12, 14, 100, 1),
        OwnedGear("reel_dasar", GearCategory.REEL, "Reel Standar", FishRarity.COMMON, 1, 10, 12, 100, 1),
        OwnedGear("line_nilon", GearCategory.LINE, "Senar Nilon 10kg", FishRarity.COMMON, 1, 10, 10, 100, 1),
        OwnedGear("bait_cacing", GearCategory.BAIT, "Cacing Tanah", FishRarity.COMMON, 1, 5, 5, 100, 15),
        OwnedGear("skin_klasik", GearCategory.SKIN, "Set Nelayan Nusantara", FishRarity.COMMON, 1, 0, 0, 100, 1),
        OwnedGear("mat_serat_baja", GearCategory.ITEM, "Material Serat Baja", FishRarity.UNCOMMON, 1, 0, 0, 100, 2)
    )
}
