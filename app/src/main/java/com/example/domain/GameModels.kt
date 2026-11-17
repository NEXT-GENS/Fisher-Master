package com.example.domain

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CommonGray
import com.example.ui.theme.EpicPurple
import com.example.ui.theme.LegendaryMagenta
import com.example.ui.theme.RareBlue
import com.example.ui.theme.UncommonGreen

enum class FishRarity(val label: String, val color: Color, val xpMultiplier: Double) {
    COMMON("Common", CommonGray, 1.0),
    UNCOMMON("Uncommon", UncommonGreen, 1.4),
    RARE("Rare", RareBlue, 2.0),
    EPIC("Epic", EpicPurple, 3.2),
    LEGENDARY("Legendary", LegendaryMagenta, 5.0)
}

enum class WeatherType(
    val displayNameId: String,
    val displayNameEn: String,
    val effectDescId: String,
    val effectDescEn: String,
    val rareMultiplier: Float,
    val tensionMultiplier: Float,
    val windBias: Float
) {
    CERAH(
        "Cerah/Panas", "Sunny/Hot",
        "Panas: stamina lebih cepat berkurang, visibilitas penuh.",
        "Hot: stamina drains faster, clear visibility.",
        1.0f, 1.08f, 0f
    ),
    HUJAN(
        "Hujan", "Rain",
        "Hujan: ikan air tawar & permukaan lebih aktif menggigit.",
        "Rain: surface and river fish bite more frequently.",
        1.25f, 1.05f, 0.05f
    ),
    SALJU(
        "Salju", "Snow",
        "Salju: suhu dingin! Risiko senar membeku jika terlalu tegang.",
        "Snow: freezing air! Line tension spikes faster.",
        1.3f, 1.22f, -0.05f
    ),
    BADAI(
        "Badai", "Storm",
        "Badai: peluang ikan Epic & Legendary melonjak, risiko senar putus tinggi!",
        "Storm: rare fish chance surges, high line snap risk!",
        1.65f, 1.32f, 0.18f
    ),
    KABUT(
        "Kabut", "Fog",
        "Kabut: jarak pandang berkurang, ikan misterius muncul di kedalaman.",
        "Fog: reduced visibility, elusive deep fish appear.",
        1.35f, 1.05f, 0f
    ),
    ANGIN(
        "Angin", "Windy",
        "Angin: memengaruhi arah dan jarak lemparan pelampung.",
        "Wind: affects cast direction and distance.",
        1.15f, 1.12f, 0.25f
    )
}

enum class TimeOfDay(val labelId: String, val labelEn: String, val isNight: Boolean) {
    PAGI("Pagi", "Morning", false),
    SIANG("Siang", "Day", false),
    SORE("Sore", "Evening", false),
    MALAM("Malam", "Night", true)
}

enum class GearCategory(val labelId: String, val labelEn: String) {
    ROD("Joran", "Rod"),
    REEL("Reel", "Reel"),
    LINE("Senar", "Line"),
    BAIT("Umpan", "Bait"),
    SKIN("Skin", "Skin"),
    ITEM("Item", "Item")
}

enum class MissionCategory { DAILY, WEEKLY, ACHIEVEMENT }

enum class PlayMode { OFFLINE_SOLO, ONLINE_MULTIPLAYER }

enum class GraphicsQuality(val label: String) { RENDAH("Rendah"), SEDANG("Sedang"), TINGGI("Tinggi"), ULTRA("Ultra") }

enum class AudioMode(val label: String) { NORMAL("Normal"), HIFI("Hi-Fi"), KUSTOM("Kustom") }

enum class ControlLayoutMode(val label: String) { KLASIK("Klasik"), MODERN("Modern"), KUSTOM("Kustom") }

enum class AppLanguage(val code: String, val label: String) { ID("id", "Indonesia"), EN("en", "English") }

data class LocationInfo(
    val id: String,
    val name: String,
    val habitat: String,
    val difficulty: String,
    val baseTempC: Int,
    val recommendedBait: String,
    val unlockLevel: Int,
    val description: String,
    val defaultWeather: WeatherType,
    val depthMeters: Int,
    @param:DrawableRes val backgroundRes: Int,
    val tintColor: Color
)

data class FishSpecies(
    val id: String,
    val name: String,
    val rarity: FishRarity,
    val minWeightKg: Double,
    val maxWeightKg: Double,
    val minLengthCm: Double,
    val maxLengthCm: Double,
    val locationIds: List<String>,
    val favoriteWeather: WeatherType,
    val bestTime: TimeOfDay,
    val baseSellPrice: Int,
    val description: String,
    val primaryColor: Color,
    val accentColor: Color,
    val bodyType: Int // 0=streamlined, 1=deep/round, 2=eel/long, 3=marlin/sword
)

data class ShopProduct(
    val id: String,
    val name: String,
    val category: GearCategory,
    val rarity: FishRarity,
    val coinPrice: Int,
    val gemPrice: Int,
    val power: Int,
    val control: Int,
    val durability: Int,
    val description: String,
    val isEventOffer: Boolean = false
)

data class CraftingRecipe(
    val id: String,
    val outputItemId: String,
    val outputName: String,
    val outputCategory: GearCategory,
    val outputRarity: FishRarity,
    val outputQty: Int,
    val requiredMaterialId: String,
    val requiredMaterialName: String,
    val requiredMaterialQty: Int,
    val coinCost: Int,
    val description: String
)

data class MissionDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: MissionCategory,
    val target: Int,
    val rewardCoins: Int,
    val rewardGems: Int,
    val rewardXp: Int
)

data class DailyRewardDefinition(
    val day: Int,
    val title: String,
    val coins: Int,
    val gems: Int,
    val bonusItemId: String? = null
)

data class BattlePassTier(
    val tier: Int,
    val requiredXp: Int,
    val freeRewardLabel: String,
    val freeCoins: Int,
    val freeGems: Int,
    val premiumRewardLabel: String,
    val premiumCoins: Int,
    val premiumGems: Int,
    val premiumSkinId: String? = null
)

data class CaughtFish(
    val id: String,
    val fishId: String,
    val fishName: String,
    val rarity: FishRarity,
    val weightKg: Double,
    val lengthCm: Double,
    val locationId: String,
    val weather: WeatherType,
    val sellPrice: Int,
    val isKept: Boolean,
    val isRecord: Boolean,
    val caughtAtMillis: Long
)

data class OwnedGear(
    val itemId: String,
    val category: GearCategory,
    val name: String,
    val rarity: FishRarity,
    val level: Int = 1,
    val power: Int = 10,
    val control: Int = 10,
    val durability: Int = 100,
    val quantity: Int = 1
)

data class PlayerState(
    val userId: String = "offline_local",
    val username: String = "Kapten Nusantara",
    val avatarId: String = "avatar_marlin",
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 650,
    val gems: Int = 25,
    val totalCatches: Int = 0,
    val biggestCatchWeightKg: Double = 0.0,
    val biggestCatchFishName: String = "-",
    val equippedRodId: String = "rod_bambu",
    val equippedReelId: String = "reel_dasar",
    val equippedLineId: String = "line_nilon",
    val equippedBaitId: String = "bait_cacing",
    val equippedSkinId: String = "skin_klasik",
    val activeLocationId: String = "danau_tenang",
    val loginStreakDay: Int = 1,
    val lastLoginClaimDate: String = "",
    val claimedDailyDays: Set<Int> = emptySet(),
    val battlePassXp: Int = 0,
    val isPremiumPass: Boolean = false,
    val claimedFreePassTiers: Set<Int> = emptySet(),
    val claimedPremiumPassTiers: Set<Int> = emptySet(),
    val inventoryCapacity: Int = 50,
    val luckBuffRemaining: Int = 0,
    val tensionBuffRemaining: Int = 0
)

data class GameSettings(
    val bgmVolume: Float = 0.7f,
    val sfxVolume: Float = 0.85f,
    val audioMode: AudioMode = AudioMode.NORMAL,
    val graphicsQuality: GraphicsQuality = GraphicsQuality.TINGGI,
    val targetFps: Int = 60,
    val cameraSensitivity: Float = 0.6f,
    val controlLayout: ControlLayoutMode = ControlLayoutMode.MODERN,
    val language: AppLanguage = AppLanguage.ID,
    val notifyEvents: Boolean = true,
    val notifyFriends: Boolean = true,
    val notifyMessages: Boolean = true,
    val reduceWeatherEffects: Boolean = false,
    val colorblindMode: Boolean = false,
    val hapticFeedback: Boolean = true,
    val textScale: Float = 1.0f
)

enum class FishingPhase {
    IDLE,
    CASTING,
    WAITING,
    BITE,
    REELING,
    CATCH_RESULT
}

data class ActiveFishingSession(
    val phase: FishingPhase = FishingPhase.IDLE,
    val anglerX: Float = 0.5f,          // 0.1..0.9 controlled by virtual joystick
    val castPower: Float = 0.5f,        // 0..1 oscillating meter
    val castAngleDeg: Float = 0f,       // -25..25
    val castDistanceM: Float = 0f,      // 5..45m
    val waterDepthM: Float = 6f,
    val bobberProgress: Float = 0f,     // 0..1
    val biteCountdownSec: Float = 0f,
    val hookWindowRemaining: Float = 0f,
    val lineTension: Float = 0.25f,     // 0..1 (green 0..0.5, yellow 0.5..0.8, red 0.8..1.0)
    val fishStamina: Float = 1f,        // 1..0
    val fishDistanceM: Float = 20f,
    val anglerStamina: Float = 1f,
    val fishSurging: Boolean = false,
    val activeHookedSpecies: FishSpecies? = null,
    val lastCatchResult: CaughtFish? = null,
    val statusBanner: String? = null
)

data class WeatherForecastSlot(
    val timeLabel: String,
    val weather: WeatherType,
    val tempC: Int
)

data class ActiveEventInfo(
    val id: String,
    val title: String,
    val description: String,
    val targetWeather: WeatherType,
    val remainingSeconds: Int,
    val rewardSummary: String
)

data class RoomSummary(
    val id: String,
    val hostId: String,
    val roomCode: String,
    val name: String,
    val region: String,
    val locationId: String,
    val status: String,
    val maxPlayers: Int,
    val playerCount: Int,
    val participantIds: List<String>
)

data class RoomPlayerInfo(
    val userId: String,
    val username: String,
    val avatarId: String,
    val level: Int,
    val isReady: Boolean,
    val score: Int,
    val lastCatchName: String
)

data class RoomChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val type: String,
    val timestampLabel: String
)

data class LeaderboardPlayer(
    val userId: String,
    val username: String,
    val avatarId: String,
    val level: Int,
    val biggestFishName: String,
    val biggestWeightKg: Double,
    val tournamentScore: Int,
    val totalCatches: Int
)

data class FriendInfo(
    val uid: String,
    val name: String,
    val level: Int,
    val avatarId: String,
    val isOnline: Boolean
)

data class GiftInfo(
    val id: String,
    val senderId: String,
    val senderName: String,
    val itemId: String,
    val itemName: String,
    val quantity: Int,
    val coinsGift: Int,
    val status: String
)
