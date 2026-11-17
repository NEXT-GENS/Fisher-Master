package com.example.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FsPlayerProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "Angler",
    val avatarId: String = "avatar_marlin",
    val level: Long = 1L,
    val xp: Long = 0L,
    val coins: Long = 650L,
    val gems: Long = 25L,
    val totalCatches: Long = 0L,
    val biggestCatchWeightKg: Double = 0.0,
    val biggestCatchFishName: String = "-",
    val equippedRodId: String = "rod_bambu",
    val equippedReelId: String = "reel_dasar",
    val equippedLineId: String = "line_nilon",
    val equippedBaitId: String = "bait_cacing",
    val equippedSkinId: String = "skin_klasik",
    val activeLocationId: String = "danau_tenang",
    val loginStreakDay: Long = 1L,
    val lastLoginDate: String = "",
    val battlePassXp: Long = 0L,
    @get:PropertyName("isPremiumPass")
    @set:PropertyName("isPremiumPass")
    var isPremiumPass: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsCatchRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fishId: String = "",
    val fishName: String = "",
    val rarity: String = "Common",
    val weightKg: Double = 0.0,
    val lengthCm: Double = 0.0,
    val locationId: String = "",
    val weather: String = "Cerah",
    val sellPrice: Long = 0L,
    @get:PropertyName("isKept")
    @set:PropertyName("isKept")
    var isKept: Boolean = true,
    @get:PropertyName("isRecord")
    @set:PropertyName("isRecord")
    var isRecord: Boolean = false,
    @ServerTimestamp val caughtAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsGearItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val itemId: String = "",
    val category: String = "Rod",
    val name: String = "",
    val rarity: String = "Common",
    val level: Long = 1L,
    val power: Long = 10L,
    val control: Long = 10L,
    val durability: Long = 100L,
    val quantity: Long = 1L,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsMissionProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val missionId: String = "",
    val progress: Long = 0L,
    val claimed: Boolean = false,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsFriendEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val friendUid: String = "",
    val friendName: String = "",
    val friendLevel: Long = 1L,
    val avatarId: String = "avatar_marlin",
    @get:PropertyName("isOnline")
    @set:PropertyName("isOnline")
    var isOnline: Boolean = true,
    @ServerTimestamp val addedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsLeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val avatarId: String = "avatar_marlin",
    val level: Long = 1L,
    val biggestFishName: String = "-",
    val biggestWeightKg: Double = 0.0,
    val tournamentScore: Long = 0L,
    val totalCatches: Long = 0L,
    @get:PropertyName("isPublic")
    @set:PropertyName("isPublic")
    var isPublic: Boolean = true,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsMultiplayerRoom(
    @DocumentId val id: String = "",
    val hostId: String = "",
    val roomCode: String = "",
    val name: String = "",
    val region: String = "Indonesia",
    val locationId: String = "danau_tenang",
    val status: String = "waiting",
    val maxPlayers: Long = 6L,
    val playerCount: Long = 1L,
    val participantIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsRoomPlayer(
    @DocumentId val id: String = "",
    val roomId: String = "",
    val userId: String = "",
    val username: String = "",
    val avatarId: String = "avatar_marlin",
    val level: Long = 1L,
    @get:PropertyName("isReady")
    @set:PropertyName("isReady")
    var isReady: Boolean = false,
    val score: Long = 0L,
    val lastCatchName: String = "-",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsRoomMessage(
    @DocumentId val id: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val type: String = "chat",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FsPlayerGift(
    @DocumentId val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val recipientId: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Long = 0L,
    val coinsGift: Long = 0L,
    val status: String = "pending",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
