package com.example.data.remote

import android.content.Context
import com.example.R
import com.example.domain.CaughtFish
import com.example.domain.FishRarity
import com.example.domain.FriendInfo
import com.example.domain.GearCategory
import com.example.domain.GiftInfo
import com.example.domain.LeaderboardPlayer
import com.example.domain.OwnedGear
import com.example.domain.PlayerState
import com.example.domain.RoomChatMessage
import com.example.domain.RoomPlayerInfo
import com.example.domain.RoomSummary
import com.example.domain.WeatherType
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Pengguna harus masuk dengan Google terlebih dahulu.")
    }

    suspend fun savePlayerProfile(player: PlayerState): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val existingSnap = docRef.get().await()
        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "username" to player.username.take(30).ifBlank { "Angler" },
            "avatarId" to player.avatarId,
            "level" to player.level.toLong(),
            "xp" to player.xp.toLong(),
            "coins" to player.coins.toLong(),
            "gems" to player.gems.toLong(),
            "totalCatches" to player.totalCatches.toLong(),
            "biggestCatchWeightKg" to player.biggestCatchWeightKg,
            "biggestCatchFishName" to player.biggestCatchFishName,
            "equippedRodId" to player.equippedRodId,
            "equippedReelId" to player.equippedReelId,
            "equippedLineId" to player.equippedLineId,
            "equippedBaitId" to player.equippedBaitId,
            "equippedSkinId" to player.equippedSkinId,
            "activeLocationId" to player.activeLocationId,
            "loginStreakDay" to player.loginStreakDay.coerceIn(1, 7).toLong(),
            "lastLoginDate" to player.lastLoginClaimDate,
            "battlePassXp" to player.battlePassXp.toLong(),
            "isPremiumPass" to player.isPremiumPass,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!existingSnap.exists()) {
            payload["createdAt"] = FieldValue.serverTimestamp()
            docRef.set(payload).await()
        } else {
            payload.remove("userId")
            docRef.update(payload).await()
        }
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.WRITE, "users/${auth.currentUser?.uid}")
    }

    suspend fun loadPlayerProfile(): Result<PlayerState?> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val snap = docRef.get().await()
        if (!snap.exists()) return@runCatching null
        val fs = snap.toObject(FsPlayerProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            ?: return@runCatching null
        PlayerState(
            userId = fs.userId.ifBlank { uid },
            username = fs.username,
            avatarId = fs.avatarId,
            level = fs.level.toInt().coerceAtLeast(1),
            xp = fs.xp.toInt().coerceAtLeast(0),
            coins = fs.coins.toInt().coerceAtLeast(0),
            gems = fs.gems.toInt().coerceAtLeast(0),
            totalCatches = fs.totalCatches.toInt().coerceAtLeast(0),
            biggestCatchWeightKg = fs.biggestCatchWeightKg,
            biggestCatchFishName = fs.biggestCatchFishName,
            equippedRodId = fs.equippedRodId,
            equippedReelId = fs.equippedReelId,
            equippedLineId = fs.equippedLineId,
            equippedBaitId = fs.equippedBaitId,
            equippedSkinId = fs.equippedSkinId,
            activeLocationId = fs.activeLocationId,
            loginStreakDay = fs.loginStreakDay.toInt().coerceIn(1, 7),
            lastLoginClaimDate = fs.lastLoginDate,
            battlePassXp = fs.battlePassXp.toInt().coerceAtLeast(0),
            isPremiumPass = fs.isPremiumPass
        )
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.GET, "users/${auth.currentUser?.uid}")
    }

    suspend fun saveCatchRecord(catch: CaughtFish): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docId = catch.id.ifBlank { UUID.randomUUID().toString() }
        val docRef = db.collection("users").document(uid).collection("catches").document(docId)
        val payload = mapOf(
            "userId" to uid,
            "fishId" to catch.fishId,
            "fishName" to catch.fishName,
            "rarity" to catch.rarity.label,
            "weightKg" to catch.weightKg,
            "lengthCm" to catch.lengthCm,
            "locationId" to catch.locationId,
            "weather" to catch.weather.displayNameId,
            "sellPrice" to catch.sellPrice.toLong(),
            "isKept" to catch.isKept,
            "isRecord" to catch.isRecord,
            "caughtAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.CREATE, "users/${auth.currentUser?.uid}/catches")
    }

    suspend fun updateCatchKeptStatus(catchId: String, isKept: Boolean): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("catches").document(catchId)
        docRef.update(mapOf("isKept" to isKept)).await()
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.UPDATE, "users/${auth.currentUser?.uid}/catches/$catchId")
    }

    suspend fun loadCatches(): Result<List<CaughtFish>> = runCatching {
        val uid = requireUserId()
        val snaps = db.collection("users").document(uid).collection("catches").get().await()
        snaps.documents.mapNotNull { doc ->
            val fs = doc.toObject(FsCatchRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?: return@mapNotNull null
            CaughtFish(
                id = fs.id,
                fishId = fs.fishId,
                fishName = fs.fishName,
                rarity = FishRarity.entries.find { it.label.equals(fs.rarity, ignoreCase = true) } ?: FishRarity.COMMON,
                weightKg = fs.weightKg,
                lengthCm = fs.lengthCm,
                locationId = fs.locationId,
                weather = WeatherType.entries.find { it.displayNameId == fs.weather } ?: WeatherType.CERAH,
                sellPrice = fs.sellPrice.toInt(),
                isKept = fs.isKept,
                isRecord = fs.isRecord,
                caughtAtMillis = fs.caughtAt?.toDate()?.time ?: System.currentTimeMillis()
            )
        }
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.LIST, "users/${auth.currentUser?.uid}/catches")
    }

    suspend fun saveGearItem(gear: OwnedGear): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("gear").document(gear.itemId)
        val existingSnap = docRef.get().await()
        if (!existingSnap.exists()) {
            val payload = mapOf(
                "userId" to uid,
                "itemId" to gear.itemId,
                "category" to gear.category.name.lowercase().replaceFirstChar { it.uppercase() },
                "name" to gear.name,
                "rarity" to gear.rarity.label,
                "level" to gear.level.toLong(),
                "power" to gear.power.toLong(),
                "control" to gear.control.toLong(),
                "durability" to gear.durability.toLong(),
                "quantity" to gear.quantity.toLong(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } else {
            docRef.update(
                mapOf(
                    "level" to gear.level.toLong(),
                    "power" to gear.power.toLong(),
                    "control" to gear.control.toLong(),
                    "durability" to gear.durability.toLong(),
                    "quantity" to gear.quantity.toLong(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.WRITE, "users/${auth.currentUser?.uid}/gear/${gear.itemId}")
    }

    suspend fun loadGear(): Result<List<OwnedGear>> = runCatching {
        val uid = requireUserId()
        val snaps = db.collection("users").document(uid).collection("gear").get().await()
        snaps.documents.mapNotNull { doc ->
            val fs = doc.toObject(FsGearItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?: return@mapNotNull null
            OwnedGear(
                itemId = fs.itemId,
                category = GearCategory.entries.find { it.name.equals(fs.category, ignoreCase = true) } ?: GearCategory.ITEM,
                name = fs.name,
                rarity = FishRarity.entries.find { it.label.equals(fs.rarity, ignoreCase = true) } ?: FishRarity.COMMON,
                level = fs.level.toInt(),
                power = fs.power.toInt(),
                control = fs.control.toInt(),
                durability = fs.durability.toInt(),
                quantity = fs.quantity.toInt()
            )
        }
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.LIST, "users/${auth.currentUser?.uid}/gear")
    }

    suspend fun saveMissionProgress(missionId: String, progress: Int, claimed: Boolean): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("missions").document(missionId)
        val existingSnap = docRef.get().await()
        if (!existingSnap.exists()) {
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "missionId" to missionId,
                    "progress" to progress.toLong(),
                    "claimed" to claimed,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } else {
            docRef.update(
                mapOf(
                    "progress" to progress.toLong(),
                    "claimed" to claimed,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.WRITE, "users/${auth.currentUser?.uid}/missions/$missionId")
    }

    suspend fun publishLeaderboardScore(player: PlayerState, tournamentScore: Int): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("leaderboard").document(uid)
        val existingSnap = docRef.get().await()
        if (!existingSnap.exists()) {
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "username" to player.username.take(30).ifBlank { "Angler" },
                    "avatarId" to player.avatarId,
                    "level" to player.level.toLong(),
                    "biggestFishName" to player.biggestCatchFishName,
                    "biggestWeightKg" to player.biggestCatchWeightKg,
                    "tournamentScore" to tournamentScore.toLong(),
                    "totalCatches" to player.totalCatches.toLong(),
                    "isPublic" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } else {
            docRef.update(
                mapOf(
                    "username" to player.username.take(30).ifBlank { "Angler" },
                    "avatarId" to player.avatarId,
                    "level" to player.level.toLong(),
                    "biggestFishName" to player.biggestCatchFishName,
                    "biggestWeightKg" to player.biggestCatchWeightKg,
                    "tournamentScore" to tournamentScore.toLong(),
                    "totalCatches" to player.totalCatches.toLong(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.WRITE, "leaderboard/${auth.currentUser?.uid}")
    }

    fun observeLeaderboard(): Flow<List<LeaderboardPlayer>> = flow {
        requireUserId()
        val path = "leaderboard"
        emitAll(
            db.collection(path)
                .whereEqualTo("isPublic", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FsLeaderboardEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .map {
                            LeaderboardPlayer(
                                userId = it.userId,
                                username = it.username,
                                avatarId = it.avatarId,
                                level = it.level.toInt(),
                                biggestFishName = it.biggestFishName,
                                biggestWeightKg = it.biggestWeightKg,
                                tournamentScore = it.tournamentScore.toInt(),
                                totalCatches = it.totalCatches.toInt()
                            )
                        }
                        .sortedByDescending { it.biggestWeightKg }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    if (auth.currentUser != null) {
                        handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeOpenRooms(): Flow<List<RoomSummary>> = flow {
        requireUserId()
        val path = "rooms"
        emitAll(
            db.collection(path)
                .whereIn("status", listOf("waiting", "playing"))
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FsMultiplayerRoom::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .map {
                            RoomSummary(
                                id = it.id,
                                hostId = it.hostId,
                                roomCode = it.roomCode,
                                name = it.name,
                                region = it.region,
                                locationId = it.locationId,
                                status = it.status,
                                maxPlayers = it.maxPlayers.toInt(),
                                playerCount = it.playerCount.toInt(),
                                participantIds = it.participantIds
                            )
                        }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    if (auth.currentUser != null) {
                        handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createRoom(
        name: String,
        region: String,
        locationId: String,
        player: PlayerState
    ): Result<RoomSummary> = runCatching {
        val uid = requireUserId()
        val roomId = "room_${UUID.randomUUID().toString().take(8)}"
        val code = "FM${(1000..9999).random()}"
        val roomRef = db.collection("rooms").document(roomId)
        val payload = mapOf(
            "hostId" to uid,
            "roomCode" to code,
            "name" to name.take(50).ifBlank { "Room ${player.username}" },
            "region" to region,
            "locationId" to locationId,
            "status" to "waiting",
            "maxPlayers" to 6L,
            "playerCount" to 1L,
            "participantIds" to listOf(uid),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        roomRef.set(payload).await()
        joinRoomPlayerDoc(roomId, player, isReady = true)
        RoomSummary(roomId, uid, code, name, region, locationId, "waiting", 6, 1, listOf(uid))
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.CREATE, "rooms")
    }

    suspend fun joinRoom(room: RoomSummary, player: PlayerState): Result<Unit> = runCatching {
        val uid = requireUserId()
        val roomRef = db.collection("rooms").document(room.id)
        val newParticipants = (room.participantIds + uid).distinct().take(room.maxPlayers)
        roomRef.update(
            mapOf(
                "participantIds" to newParticipants,
                "playerCount" to newParticipants.size.toLong(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        joinRoomPlayerDoc(room.id, player, isReady = false)
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.UPDATE, "rooms/${room.id}")
    }

    suspend fun joinRoomByCode(code: String, player: PlayerState): Result<RoomSummary?> = runCatching {
        requireUserId()
        val snaps = db.collection("rooms")
            .whereIn("status", listOf("waiting", "playing"))
            .get()
            .await()
        val match = snaps.toObjects(FsMultiplayerRoom::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .find { it.roomCode.equals(code.trim(), ignoreCase = true) }
            ?: return@runCatching null
        val summary = RoomSummary(
            id = match.id,
            hostId = match.hostId,
            roomCode = match.roomCode,
            name = match.name,
            region = match.region,
            locationId = match.locationId,
            status = match.status,
            maxPlayers = match.maxPlayers.toInt(),
            playerCount = match.playerCount.toInt(),
            participantIds = match.participantIds
        )
        joinRoom(summary, player).getOrThrow()
        summary
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.LIST, "rooms")
    }

    private suspend fun joinRoomPlayerDoc(roomId: String, player: PlayerState, isReady: Boolean) {
        val uid = requireUserId()
        val playerRef = db.collection("rooms").document(roomId).collection("players").document(uid)
        val snap = playerRef.get().await()
        val data = mapOf(
            "roomId" to roomId,
            "userId" to uid,
            "username" to player.username.take(30).ifBlank { "Angler" },
            "avatarId" to player.avatarId,
            "level" to player.level.toLong(),
            "isReady" to isReady,
            "score" to 0L,
            "lastCatchName" to "-",
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!snap.exists()) {
            playerRef.set(data).await()
        } else {
            playerRef.update(
                mapOf(
                    "username" to player.username.take(30).ifBlank { "Angler" },
                    "isReady" to isReady,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun updateRoomPlayerState(
        roomId: String,
        isReady: Boolean,
        score: Int,
        lastCatchName: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val playerRef = db.collection("rooms").document(roomId).collection("players").document(uid)
        playerRef.update(
            mapOf(
                "isReady" to isReady,
                "score" to score.toLong(),
                "lastCatchName" to lastCatchName.take(64),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.UPDATE, "rooms/$roomId/players/${auth.currentUser?.uid}")
    }

    suspend fun updateRoomStatus(roomId: String, status: String): Result<Unit> = runCatching {
        requireUserId()
        db.collection("rooms").document(roomId)
            .update(mapOf("status" to status, "updatedAt" to FieldValue.serverTimestamp()))
            .await()
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.UPDATE, "rooms/$roomId")
    }

    fun observeRoomPlayers(roomId: String): Flow<List<RoomPlayerInfo>> = flow {
        requireUserId()
        val path = "rooms/$roomId/players"
        emitAll(
            db.collection("rooms").document(roomId).collection("players")
                .whereEqualTo("roomId", roomId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FsRoomPlayer::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .map {
                            RoomPlayerInfo(
                                userId = it.userId,
                                username = it.username,
                                avatarId = it.avatarId,
                                level = it.level.toInt(),
                                isReady = it.isReady,
                                score = it.score.toInt(),
                                lastCatchName = it.lastCatchName
                            )
                        }
                        .sortedByDescending { it.score }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    if (auth.currentUser != null) {
                        handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeRoomMessages(roomId: String): Flow<List<RoomChatMessage>> = flow {
        requireUserId()
        val path = "rooms/$roomId/messages"
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        emitAll(
            db.collection("rooms").document(roomId).collection("messages")
                .whereEqualTo("roomId", roomId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FsRoomMessage::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedBy { it.createdAt?.toDate()?.time ?: 0L }
                        .map {
                            RoomChatMessage(
                                id = it.id,
                                senderId = it.senderId,
                                senderName = it.senderName,
                                text = it.text,
                                type = it.type,
                                timestampLabel = formatter.format(it.createdAt?.toDate() ?: Date())
                            )
                        }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    if (auth.currentUser != null) {
                        handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun sendRoomMessage(
        roomId: String,
        senderName: String,
        text: String,
        type: String = "chat"
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val msgId = "msg_${UUID.randomUUID().toString().take(10)}"
        val msgRef = db.collection("rooms").document(roomId).collection("messages").document(msgId)
        msgRef.set(
            mapOf(
                "roomId" to roomId,
                "senderId" to uid,
                "senderName" to senderName.take(30).ifBlank { "Angler" },
                "text" to text.take(300),
                "type" to type,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.CREATE, "rooms/$roomId/messages")
    }

    fun observeFriends(): Flow<List<FriendInfo>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/friends"
        emitAll(
            db.collection("users").document(uid).collection("friends")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FsFriendEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .map {
                            FriendInfo(
                                uid = it.friendUid,
                                name = it.friendName,
                                level = it.friendLevel.toInt(),
                                avatarId = it.avatarId,
                                isOnline = it.isOnline
                            )
                        }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun addFriend(friendUid: String, friendName: String, level: Int = 1): Result<Unit> = runCatching {
        val uid = requireUserId()
        val safeId = friendUid.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
        val ref = db.collection("users").document(uid).collection("friends").document(safeId)
        ref.set(
            mapOf(
                "userId" to uid,
                "friendUid" to safeId,
                "friendName" to friendName.take(30).ifBlank { "Angler" },
                "friendLevel" to level.toLong(),
                "avatarId" to "avatar_marlin",
                "isOnline" to true,
                "addedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.CREATE, "users/${auth.currentUser?.uid}/friends")
    }

    fun observeIncomingGifts(): Flow<List<GiftInfo>> = flow {
        val uid = requireUserId()
        val path = "gifts"
        emitAll(
            db.collection(path)
                .whereEqualTo("recipientId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FsPlayerGift::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .map {
                            GiftInfo(
                                id = it.id,
                                senderId = it.senderId,
                                senderName = it.senderName,
                                itemId = it.itemId,
                                itemName = it.itemName,
                                quantity = it.quantity.toInt(),
                                coinsGift = it.coinsGift.toInt(),
                                status = it.status
                            )
                        }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    if (auth.currentUser?.uid == uid) {
                        handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun sendGift(
        recipientId: String,
        senderName: String,
        itemId: String,
        itemName: String,
        quantity: Int,
        coinsGift: Int
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val giftId = "gift_${UUID.randomUUID().toString().take(10)}"
        db.collection("gifts").document(giftId).set(
            mapOf(
                "senderId" to uid,
                "senderName" to senderName.take(30).ifBlank { "Angler" },
                "recipientId" to recipientId,
                "itemId" to itemId,
                "itemName" to itemName,
                "quantity" to quantity.toLong(),
                "coinsGift" to coinsGift.toLong(),
                "status" to "pending",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.CREATE, "gifts")
    }

    suspend fun claimGift(giftId: String): Result<Unit> = runCatching {
        requireUserId()
        db.collection("gifts").document(giftId).update(
            mapOf(
                "status" to "claimed",
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Unit
    }.onFailure { e ->
        if (e is CancellationException) throw e
        handleFirestoreError(e, OperationType.UPDATE, "gifts/$giftId")
    }
}
