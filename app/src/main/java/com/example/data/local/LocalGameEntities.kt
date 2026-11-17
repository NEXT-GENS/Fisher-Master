package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class LocalPlayerEntity(
    @PrimaryKey val id: Int = 1,
    val userId: String,
    val username: String,
    val avatarId: String,
    val level: Int,
    val xp: Int,
    val coins: Int,
    val gems: Int,
    val totalCatches: Int,
    val biggestCatchWeightKg: Double,
    val biggestCatchFishName: String,
    val equippedRodId: String,
    val equippedReelId: String,
    val equippedLineId: String,
    val equippedBaitId: String,
    val equippedSkinId: String,
    val activeLocationId: String,
    val loginStreakDay: Int,
    val lastLoginClaimDate: String,
    val claimedDailyDaysCsv: String,
    val battlePassXp: Int,
    val isPremiumPass: Boolean,
    val claimedFreeTiersCsv: String,
    val claimedPremiumTiersCsv: String,
    val luckBuffRemaining: Int,
    val tensionBuffRemaining: Int
)

@Entity(tableName = "caught_fish")
data class LocalCatchEntity(
    @PrimaryKey val id: String,
    val fishId: String,
    val fishName: String,
    val rarity: String,
    val weightKg: Double,
    val lengthCm: Double,
    val locationId: String,
    val weather: String,
    val sellPrice: Int,
    val isKept: Boolean,
    val isRecord: Boolean,
    val caughtAtMillis: Long
)

@Entity(tableName = "owned_gear")
data class LocalGearEntity(
    @PrimaryKey val itemId: String,
    val category: String,
    val name: String,
    val rarity: String,
    val level: Int,
    val power: Int,
    val control: Int,
    val durability: Int,
    val quantity: Int
)

@Entity(tableName = "mission_progress")
data class LocalMissionEntity(
    @PrimaryKey val missionId: String,
    val progress: Int,
    val claimed: Boolean
)
