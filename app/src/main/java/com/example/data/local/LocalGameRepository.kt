package com.example.data.local

import com.example.domain.CaughtFish
import com.example.domain.FishRarity
import com.example.domain.GameCatalog
import com.example.domain.GearCategory
import com.example.domain.OwnedGear
import com.example.domain.PlayerState
import com.example.domain.WeatherType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalGameRepository(private val dao: LocalGameDao) {

    fun observePlayer(): Flow<PlayerState> = dao.observePlayer().map { entity ->
        entity?.toDomain() ?: PlayerState()
    }

    fun observeCatches(): Flow<List<CaughtFish>> = dao.observeCatches().map { list ->
        list.map { it.toDomain() }
    }

    fun observeGear(): Flow<List<OwnedGear>> = dao.observeGear().map { list ->
        if (list.isEmpty()) GameCatalog.starterGear() else list.map { it.toDomain() }
    }

    fun observeMissions(): Flow<Map<String, Pair<Int, Boolean>>> = dao.observeMissions().map { list ->
        list.associate { it.missionId to (it.progress to it.claimed) }
    }

    suspend fun ensureInitialized(userId: String, username: String) {
        val existing = dao.getPlayer()
        if (existing == null) {
            val initial = PlayerState(userId = userId, username = username)
            dao.upsertPlayer(initial.toEntity())
            dao.upsertGearList(GameCatalog.starterGear().map { it.toEntity() })
        } else if (existing.userId != userId && userId != "offline_local") {
            dao.upsertPlayer(existing.copy(userId = userId, username = username))
        }
    }

    suspend fun savePlayer(player: PlayerState) {
        dao.upsertPlayer(player.toEntity())
    }

    suspend fun saveCatch(catch: CaughtFish) {
        dao.insertCatch(catch.toEntity())
    }

    suspend fun saveCatches(catches: List<CaughtFish>) {
        dao.insertCatches(catches.map { it.toEntity() })
    }

    suspend fun updateCatchKept(id: String, isKept: Boolean) {
        dao.updateCatchKept(id, isKept)
    }

    suspend fun saveGear(gear: OwnedGear) {
        dao.upsertGear(gear.toEntity())
    }

    suspend fun saveGearList(items: List<OwnedGear>) {
        dao.upsertGearList(items.map { it.toEntity() })
    }

    suspend fun saveMission(missionId: String, progress: Int, claimed: Boolean) {
        dao.upsertMission(LocalMissionEntity(missionId, progress, claimed))
    }

    suspend fun resetOfflineData() {
        dao.clearCatches()
        dao.clearGear()
        dao.clearMissions()
        dao.upsertPlayer(PlayerState().toEntity())
        dao.upsertGearList(GameCatalog.starterGear().map { it.toEntity() })
    }
}

private fun LocalPlayerEntity.toDomain(): PlayerState = PlayerState(
    userId = userId,
    username = username,
    avatarId = avatarId,
    level = level,
    xp = xp,
    coins = coins,
    gems = gems,
    totalCatches = totalCatches,
    biggestCatchWeightKg = biggestCatchWeightKg,
    biggestCatchFishName = biggestCatchFishName,
    equippedRodId = equippedRodId,
    equippedReelId = equippedReelId,
    equippedLineId = equippedLineId,
    equippedBaitId = equippedBaitId,
    equippedSkinId = equippedSkinId,
    activeLocationId = activeLocationId,
    loginStreakDay = loginStreakDay,
    lastLoginClaimDate = lastLoginClaimDate,
    claimedDailyDays = claimedDailyDaysCsv.split(",").mapNotNull { it.toIntOrNull() }.toSet(),
    battlePassXp = battlePassXp,
    isPremiumPass = isPremiumPass,
    claimedFreePassTiers = claimedFreeTiersCsv.split(",").mapNotNull { it.toIntOrNull() }.toSet(),
    claimedPremiumPassTiers = claimedPremiumTiersCsv.split(",").mapNotNull { it.toIntOrNull() }.toSet(),
    luckBuffRemaining = luckBuffRemaining,
    tensionBuffRemaining = tensionBuffRemaining
)

private fun PlayerState.toEntity(): LocalPlayerEntity = LocalPlayerEntity(
    id = 1,
    userId = userId,
    username = username,
    avatarId = avatarId,
    level = level,
    xp = xp,
    coins = coins,
    gems = gems,
    totalCatches = totalCatches,
    biggestCatchWeightKg = biggestCatchWeightKg,
    biggestCatchFishName = biggestCatchFishName,
    equippedRodId = equippedRodId,
    equippedReelId = equippedReelId,
    equippedLineId = equippedLineId,
    equippedBaitId = equippedBaitId,
    equippedSkinId = equippedSkinId,
    activeLocationId = activeLocationId,
    loginStreakDay = loginStreakDay,
    lastLoginClaimDate = lastLoginClaimDate,
    claimedDailyDaysCsv = claimedDailyDays.joinToString(","),
    battlePassXp = battlePassXp,
    isPremiumPass = isPremiumPass,
    claimedFreeTiersCsv = claimedFreePassTiers.joinToString(","),
    claimedPremiumTiersCsv = claimedPremiumPassTiers.joinToString(","),
    luckBuffRemaining = luckBuffRemaining,
    tensionBuffRemaining = tensionBuffRemaining
)

private fun LocalCatchEntity.toDomain(): CaughtFish = CaughtFish(
    id = id,
    fishId = fishId,
    fishName = fishName,
    rarity = FishRarity.entries.find { it.label.equals(rarity, ignoreCase = true) } ?: FishRarity.COMMON,
    weightKg = weightKg,
    lengthCm = lengthCm,
    locationId = locationId,
    weather = WeatherType.entries.find { it.name == weather || it.displayNameId == weather } ?: WeatherType.CERAH,
    sellPrice = sellPrice,
    isKept = isKept,
    isRecord = isRecord,
    caughtAtMillis = caughtAtMillis
)

private fun CaughtFish.toEntity(): LocalCatchEntity = LocalCatchEntity(
    id = id,
    fishId = fishId,
    fishName = fishName,
    rarity = rarity.label,
    weightKg = weightKg,
    lengthCm = lengthCm,
    locationId = locationId,
    weather = weather.name,
    sellPrice = sellPrice,
    isKept = isKept,
    isRecord = isRecord,
    caughtAtMillis = caughtAtMillis
)

private fun LocalGearEntity.toDomain(): OwnedGear = OwnedGear(
    itemId = itemId,
    category = GearCategory.entries.find { it.name == category } ?: GearCategory.ITEM,
    name = name,
    rarity = FishRarity.entries.find { it.label.equals(rarity, ignoreCase = true) } ?: FishRarity.COMMON,
    level = level,
    power = power,
    control = control,
    durability = durability,
    quantity = quantity
)

private fun OwnedGear.toEntity(): LocalGearEntity = LocalGearEntity(
    itemId = itemId,
    category = category.name,
    name = name,
    rarity = rarity.label,
    level = level,
    power = power,
    control = control,
    durability = durability,
    quantity = quantity
)
