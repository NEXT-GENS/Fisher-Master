package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundAndHapticsManager
import com.example.data.local.LocalGameRepository
import com.example.data.local.SettingsDataStore
import com.example.data.remote.FirestoreRepository
import com.example.domain.ActiveEventInfo
import com.example.domain.ActiveFishingSession
import com.example.domain.CaughtFish
import com.example.domain.CraftingRecipe
import com.example.domain.FishRarity
import com.example.domain.FishSpecies
import com.example.domain.FishingPhase
import com.example.domain.GameCatalog
import com.example.domain.GameSettings
import com.example.domain.GearCategory
import com.example.domain.OwnedGear
import com.example.domain.PlayMode
import com.example.domain.PlayerState
import com.example.domain.ShopProduct
import com.example.domain.TimeOfDay
import com.example.domain.WeatherForecastSlot
import com.example.domain.WeatherType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

class FishingMasterViewModel(
    private val localRepo: LocalGameRepository,
    private val settingsStore: SettingsDataStore,
    private val soundManager: SoundAndHapticsManager,
    private val remoteRepo: FirestoreRepository?, // Null in strictly Offline Solo mode
    private val currentUserId: String,
    private val initialDisplayName: String,
    val isOnlineAccount: Boolean
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = localRepo.observePlayer()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), PlayerState(userId = currentUserId, username = initialDisplayName))

    val catches: StateFlow<List<CaughtFish>> = localRepo.observeCatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val ownedGear: StateFlow<List<OwnedGear>> = localRepo.observeGear()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), GameCatalog.starterGear())

    val missionProgress: StateFlow<Map<String, Pair<Int, Boolean>>> = localRepo.observeMissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyMap())

    val settings: StateFlow<GameSettings> = settingsStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), GameSettings())

    private val _playMode = MutableStateFlow(if (isOnlineAccount) PlayMode.ONLINE_MULTIPLAYER else PlayMode.OFFLINE_SOLO)
    val playMode: StateFlow<PlayMode> = _playMode.asStateFlow()

    private val _currentWeather = MutableStateFlow(WeatherType.CERAH)
    val currentWeather: StateFlow<WeatherType> = _currentWeather.asStateFlow()

    private val _timeOfDay = MutableStateFlow(TimeOfDay.SIANG)
    val timeOfDay: StateFlow<TimeOfDay> = _timeOfDay.asStateFlow()

    private val _inGameClockText = MutableStateFlow("10:30")
    val inGameClockText: StateFlow<String> = _inGameClockText.asStateFlow()

    private val _forecast = MutableStateFlow<List<WeatherForecastSlot>>(emptyList())
    val forecast: StateFlow<List<WeatherForecastSlot>> = _forecast.asStateFlow()

    private val _activeEvent = MutableStateFlow(
        ActiveEventInfo(
            id = "evt_badai_samudra",
            title = "Event Badai Samudra",
            description = "Peluang Ikan Epic & Legendary meningkat +65% saat Badai!",
            targetWeather = WeatherType.BADAI,
            remainingSeconds = 300,
            rewardSummary = "Bonus +50% XP & Peluang Tropi Emas"
        )
    )
    val activeEvent: StateFlow<ActiveEventInfo> = _activeEvent.asStateFlow()

    private val _weatherAlert = MutableStateFlow<String?>(null)
    val weatherAlert: StateFlow<String?> = _weatherAlert.asStateFlow()

    private val _fishingSession = MutableStateFlow(ActiveFishingSession())
    val fishingSession: StateFlow<ActiveFishingSession> = _fishingSession.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _syncStatus = MutableStateFlow(if (isOnlineAccount) "Terhubung ke Cloud" else "Mode Offline Lokal")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _pingMs = MutableStateFlow(if (isOnlineAccount) 28 else 0)
    val pingMs: StateFlow<Int> = _pingMs.asStateFlow()

    private val _isReelingPressed = MutableStateFlow(false)
    val isReelingPressed: StateFlow<Boolean> = _isReelingPressed.asStateFlow()

    private var gameLoopJob: Job? = null
    private var worldClockMinutes = 630 // 10:30 AM

    init {
        viewModelScope.launch {
            localRepo.ensureInitialized(currentUserId, initialDisplayName)
            if (isOnlineAccount && remoteRepo != null) {
                syncFromCloudOnStart()
            }
        }
        rebuildForecast()
        startWorldLoop()
    }

    private fun startWorldLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var tickCount = 0
            while (isActive) {
                delay(50L) // 20 Hz simulation tick
                tickCount++
                stepFishingPhysics(0.05f, tickCount)
                if (tickCount % 20 == 0) {
                    stepWorldClockAndWeather()
                }
            }
        }
    }

    private fun stepWorldClockAndWeather() {
        worldClockMinutes = (worldClockMinutes + 2) % 1440
        val hour = worldClockMinutes / 60
        val minute = worldClockMinutes % 60
        _inGameClockText.value = String.format(Locale.US, "%02d:%02d", hour, minute)
        _timeOfDay.value = when (hour) {
            in 5..9 -> TimeOfDay.PAGI
            in 10..15 -> TimeOfDay.SIANG
            in 16..18 -> TimeOfDay.SORE
            else -> TimeOfDay.MALAM
        }

        val currentEvt = _activeEvent.value
        val nextRem = (currentEvt.remainingSeconds - 1).coerceAtLeast(0)
        if (nextRem == 60 && settings.value.notifyEvents) {
            _weatherAlert.value = "${currentEvt.targetWeather.displayNameId} 1 menit lagi!"
        } else if (nextRem == 0) {
            val nextWeather = currentEvt.targetWeather
            _currentWeather.value = nextWeather
            val nextTarget = WeatherType.entries.filter { it != nextWeather }.random()
            _activeEvent.value = ActiveEventInfo(
                id = "evt_${nextTarget.name.lowercase()}",
                title = "Cuaca Dinamis: ${nextTarget.displayNameId}",
                description = nextTarget.effectDescId,
                targetWeather = nextTarget,
                remainingSeconds = 240,
                rewardSummary = "Bonus Tangkapan Cuaca Aktif"
            )
            rebuildForecast()
            _weatherAlert.value = "Cuaca berubah menjadi ${nextWeather.displayNameId}!"
        } else {
            _activeEvent.value = currentEvt.copy(remainingSeconds = nextRem)
        }
        if (isOnlineAccount) {
            _pingMs.value = (22..48).random()
        }
    }

    private fun rebuildForecast() {
        val loc = GameCatalog.locationById(playerState.value.activeLocationId)
        val pool = WeatherType.entries
        val startHour = (worldClockMinutes / 60)
        _forecast.value = (1..5).map { idx ->
            val h = (startHour + idx * 3) % 24
            val w = if (idx == 1) _activeEvent.value.targetWeather else pool[(loc.name.length + idx + startHour) % pool.size]
            val tempOffset = when (w) {
                WeatherType.CERAH -> 3
                WeatherType.SALJU -> -6
                WeatherType.BADAI -> -2
                WeatherType.HUJAN -> -1
                else -> 0
            }
            WeatherForecastSlot(
                timeLabel = String.format(Locale.US, "%02d:00", h),
                weather = w,
                tempC = loc.baseTempC + tempOffset
            )
        }
    }

    fun dismissWeatherAlert() {
        _weatherAlert.value = null
    }

    fun selectPlayMode(mode: PlayMode) {
        if (mode == PlayMode.ONLINE_MULTIPLAYER && !isOnlineAccount) {
            _toastMessage.value = "Masuk dengan Google terlebih dahulu untuk Mode Online."
            return
        }
        _playMode.value = mode
    }

    fun selectLocation(locationId: String) {
        val loc = GameCatalog.locationById(locationId)
        if (playerState.value.level < loc.unlockLevel) {
            _toastMessage.value = "${loc.name} terbuka pada Level ${loc.unlockLevel}."
            return
        }
        viewModelScope.launch {
            val updated = playerState.value.copy(activeLocationId = locationId)
            savePlayerDual(updated)
            _currentWeather.value = loc.defaultWeather
            _fishingSession.value = ActiveFishingSession(waterDepthM = loc.depthMeters.toFloat())
            rebuildForecast()
        }
    }

    fun cycleWeatherManually() {
        val all = WeatherType.entries
        val next = all[(all.indexOf(_currentWeather.value) + 1) % all.size]
        _currentWeather.value = next
        _toastMessage.value = "Cuaca simulasi: ${next.displayNameId}"
    }

    // --- Fishing Mini-Game Mechanics ---

    fun moveAngler(deltaX: Float) {
        _fishingSession.update { s ->
            if (s.phase == FishingPhase.IDLE || s.phase == FishingPhase.CASTING) {
                s.copy(anglerX = (s.anglerX + deltaX * 0.03f).coerceIn(0.12f, 0.88f))
            } else s
        }
    }

    fun startCasting() {
        val gear = ownedGear.value
        val bait = gear.find { it.itemId == playerState.value.equippedBaitId }
        if (bait == null || bait.quantity <= 0) {
            _toastMessage.value = "Umpan habis! Beli atau racik umpan di Toko/Crafting."
            return
        }
        _fishingSession.update {
            it.copy(
                phase = FishingPhase.CASTING,
                castPower = 0.1f,
                statusBanner = "Lepas tombol Lempar saat kekuatan maksimal!"
            )
        }
    }

    fun releaseCast() {
        val s = _fishingSession.value
        if (s.phase != FishingPhase.CASTING) return
        val weather = _currentWeather.value
        val windDelta = weather.windBias * 12f
        val dist = (8f + s.castPower * 32f + windDelta).coerceIn(6f, 45f)
        val loc = GameCatalog.locationById(playerState.value.activeLocationId)
        val depth = (loc.depthMeters * (0.4f + 0.6f * s.castPower)).coerceAtLeast(2f)

        soundManager.playCastSfx(settings.value)
        val equippedBaitId = playerState.value.equippedBaitId
        viewModelScope.launch { consumeBait(equippedBaitId) }

        val waitSec = (2.6f + Random.nextFloat() * 3.0f) / weather.rareMultiplier.coerceAtLeast(1f)
        _fishingSession.value = s.copy(
            phase = FishingPhase.WAITING,
            castDistanceM = dist,
            waterDepthM = depth,
            bobberProgress = 1f,
            biteCountdownSec = waitSec.coerceIn(1.8f, 6.5f),
            statusBanner = "Menunggu ikan menyambar umpan..."
        )
    }

    fun triggerHook() {
        val s = _fishingSession.value
        if (s.phase != FishingPhase.BITE) return
        val species = pickFishSpecies(playerState.value.activeLocationId, _currentWeather.value, _timeOfDay.value)
        val dist = (14f + species.rarity.ordinal * 5f + Random.nextFloat() * 8f).coerceAtMost(42f)
        soundManager.playReelTick(settings.value)
        _fishingSession.value = s.copy(
            phase = FishingPhase.REELING,
            activeHookedSpecies = species,
            fishStamina = 1f,
            anglerStamina = 1f,
            lineTension = 0.35f,
            fishDistanceM = dist,
            fishSurging = false,
            statusBanner = "Tarik ikan! Jaga ketegangan di zona Hijau/Kuning!"
        )
    }

    fun setReelingPressed(pressed: Boolean) {
        _isReelingPressed.value = pressed
    }

    fun cancelFishing() {
        _isReelingPressed.value = false
        _fishingSession.update {
            ActiveFishingSession(
                anglerX = it.anglerX,
                waterDepthM = GameCatalog.locationById(playerState.value.activeLocationId).depthMeters.toFloat()
            )
        }
    }

    private fun stepFishingPhysics(dt: Float, tickCount: Int) {
        val s = _fishingSession.value
        when (s.phase) {
            FishingPhase.CASTING -> {
                val osc = ((tickCount % 30) / 15f).let { if (it <= 1f) it else 2f - it }
                val angle = ((tickCount % 40) / 20f - 0.5f) * 24f
                _fishingSession.value = s.copy(castPower = osc, castAngleDeg = angle)
            }
            FishingPhase.WAITING -> {
                val rem = s.biteCountdownSec - dt
                if (rem <= 0f) {
                    soundManager.playBiteAlert(settings.value)
                    _fishingSession.value = s.copy(
                        phase = FishingPhase.BITE,
                        hookWindowRemaining = 2.8f,
                        statusBanner = "GIGITAN! Tekan HOOK sekarang!"
                    )
                } else {
                    _fishingSession.value = s.copy(biteCountdownSec = rem)
                }
            }
            FishingPhase.BITE -> {
                val rem = s.hookWindowRemaining - dt
                if (rem <= 0f) {
                    _fishingSession.value = ActiveFishingSession(
                        anglerX = s.anglerX,
                        statusBanner = "Ikan lepas! Terlambat melakukan Hook."
                    )
                } else {
                    _fishingSession.value = s.copy(hookWindowRemaining = rem)
                }
            }
            FishingPhase.REELING -> {
                val gear = ownedGear.value
                val rod = gear.find { it.itemId == playerState.value.equippedRodId }
                val reel = gear.find { it.itemId == playerState.value.equippedReelId }
                val line = gear.find { it.itemId == playerState.value.equippedLineId }
                val powerBonus = ((rod?.power ?: 12) + (reel?.power ?: 10) + (line?.power ?: 10)) / 100f
                val controlBonus = ((rod?.control ?: 14) + (reel?.control ?: 12) + (line?.control ?: 10)) / 120f
                val hasStabilizer = playerState.value.tensionBuffRemaining > 0
                val weather = _currentWeather.value

                val surging = (tickCount / 28) % 3 == 1
                val species = s.activeHookedSpecies ?: GameCatalog.fishSpecies.first()
                val rarityResistance = 1f + species.rarity.ordinal * 0.28f

                val pulling = _isReelingPressed.value
                val tensionMod = weather.tensionMultiplier * (if (hasStabilizer) 0.75f else 1f) * (1f - controlBonus * 0.3f).coerceAtLeast(0.55f)
                val tensionDelta = if (pulling) {
                    (if (surging) 0.55f else 0.28f) * tensionMod * rarityResistance * dt
                } else {
                    -0.42f * dt
                }
                val newTension = (s.lineTension + tensionDelta).coerceIn(0.05f, 1.05f)

                if (newTension >= 1.0f) {
                    soundManager.playSnapAlert(settings.value)
                    _isReelingPressed.value = false
                    _fishingSession.value = ActiveFishingSession(
                        anglerX = s.anglerX,
                        statusBanner = "Senar putus! Ketegangan melebihi batas merah."
                    )
                    return
                }

                val staminaDrainWeather = if (weather == WeatherType.CERAH) 0.08f else 0.03f
                val newAnglerStamina = if (pulling) (s.anglerStamina - staminaDrainWeather * dt).coerceAtLeast(0.15f) else (s.anglerStamina + 0.14f * dt).coerceAtMost(1f)

                val pullSpeed = if (pulling) (5.2f + powerBonus * 6.5f) * newAnglerStamina else -1.8f * rarityResistance
                val newDist = (s.fishDistanceM - pullSpeed * dt).coerceAtLeast(0f)
                val newFishStamina = (s.fishStamina - (if (pulling) 0.15f + powerBonus * 0.1f else 0.04f) * dt).coerceIn(0f, 1f)

                if (pulling && tickCount % 6 == 0) {
                    soundManager.playReelTick(settings.value)
                }

                if (newDist <= 0.2f || newFishStamina <= 0.02f) {
                    _isReelingPressed.value = false
                    completeCatch(species)
                } else {
                    _fishingSession.value = s.copy(
                        lineTension = newTension,
                        fishDistanceM = newDist,
                        fishStamina = newFishStamina,
                        anglerStamina = newAnglerStamina,
                        fishSurging = surging,
                        statusBanner = if (surging) "Ikan memberontak! Kendurkan tarikan!" else "Terus gulung senar!"
                    )
                }
            }
            else -> Unit
        }
    }

    private fun pickFishSpecies(locationId: String, weather: WeatherType, time: TimeOfDay): FishSpecies {
        val candidates = GameCatalog.speciesForLocation(locationId)
        val hasLuck = playerState.value.luckBuffRemaining > 0
        val bait = playerState.value.equippedBaitId
        val baitBoost = when (bait) {
            "bait_kristal" -> 2.4f
            "bait_udang" -> 1.5f
            else -> 1.0f
        }
        val weighted = candidates.map { sp ->
            var w = when (sp.rarity) {
                FishRarity.COMMON -> 50f
                FishRarity.UNCOMMON -> 30f
                FishRarity.RARE -> 14f * weather.rareMultiplier * baitBoost
                FishRarity.EPIC -> 5f * weather.rareMultiplier * baitBoost * (if (hasLuck) 1.5f else 1f)
                FishRarity.LEGENDARY -> 2f * weather.rareMultiplier * baitBoost * (if (hasLuck) 1.8f else 1f)
            }
            if (sp.favoriteWeather == weather) w *= 1.45f
            if (sp.bestTime == time) w *= 1.35f
            sp to w
        }
        val total = weighted.sumOf { it.second.toDouble() }
        var roll = Random.nextDouble() * total
        for ((sp, w) in weighted) {
            roll -= w
            if (roll <= 0) return sp
        }
        return candidates.first()
    }

    private fun completeCatch(species: FishSpecies) {
        val weight = ((species.minWeightKg + Random.nextDouble() * (species.maxWeightKg - species.minWeightKg)) * 100.0).roundToInt() / 100.0
        val length = ((species.minLengthCm + Random.nextDouble() * (species.maxLengthCm - species.minLengthCm)) * 10.0).roundToInt() / 10.0
        val existingRecord = catches.value.filter { it.fishId == species.id }.maxOfOrNull { it.weightKg } ?: 0.0
        val isRecord = weight > existingRecord
        val sellPrice = (species.baseSellPrice * (0.85 + weight / species.maxWeightKg * 0.5)).roundToInt()

        val caught = CaughtFish(
            id = "catch_${UUID.randomUUID().toString().take(8)}",
            fishId = species.id,
            fishName = species.name,
            rarity = species.rarity,
            weightKg = weight,
            lengthCm = length,
            locationId = playerState.value.activeLocationId,
            weather = _currentWeather.value,
            sellPrice = sellPrice,
            isKept = true,
            isRecord = isRecord,
            caughtAtMillis = System.currentTimeMillis()
        )

        soundManager.playCatchFanfare(settings.value, species.rarity >= FishRarity.EPIC)

        viewModelScope.launch {
            val cur = playerState.value
            val gainedXp = (35 * species.rarity.xpMultiplier).roundToInt()
            var newXp = cur.xp + gainedXp
            var newLevel = cur.level
            while (newXp >= newLevel * 120) {
                newXp -= newLevel * 120
                newLevel++
            }
            val newBiggestW = maxOf(cur.biggestCatchWeightKg, weight)
            val newBiggestName = if (weight >= cur.biggestCatchWeightKg) species.name else cur.biggestCatchFishName

            val updatedPlayer = cur.copy(
                level = newLevel,
                xp = newXp,
                totalCatches = cur.totalCatches + 1,
                biggestCatchWeightKg = newBiggestW,
                biggestCatchFishName = newBiggestName,
                battlePassXp = cur.battlePassXp + gainedXp,
                luckBuffRemaining = (cur.luckBuffRemaining - 1).coerceAtLeast(0),
                tensionBuffRemaining = (cur.tensionBuffRemaining - 1).coerceAtLeast(0)
            )

            savePlayerDual(updatedPlayer)
            localRepo.saveCatch(caught)
            if (isOnlineAccount && remoteRepo != null) {
                remoteRepo.saveCatchRecord(caught)
                remoteRepo.publishLeaderboardScore(updatedPlayer, updatedPlayer.totalCatches * 50 + newBiggestW.roundToInt() * 10)
            }

            incrementMission("m_daily_catch5", 1)
            incrementMission("m_weekly_catch25", 1)
            if (species.rarity >= FishRarity.RARE) incrementMission("m_daily_rare1", 1)
            if (species.rarity == FishRarity.LEGENDARY) incrementMission("m_ach_legendary", 1)
            val uniqueCount = (catches.value.map { it.fishId } + species.id).distinct().size
            setMissionProgress("m_ach_fishdex10", uniqueCount)
        }

        _fishingSession.update {
            it.copy(
                phase = FishingPhase.CATCH_RESULT,
                lastCatchResult = caught,
                statusBanner = if (isRecord) "REKOR BARU! ${species.name}" else "Berhasil menangkap ${species.name}!"
            )
        }
    }

    fun resolveCatchResult(action: String) {
        val result = _fishingSession.value.lastCatchResult ?: return
        viewModelScope.launch {
            when (action) {
                "keep" -> {
                    val keptCount = catches.value.count { it.isKept }
                    if (keptCount >= playerState.value.inventoryCapacity) {
                        _toastMessage.value = "Inventori penuh! Ikan otomatis dijual seharga ${result.sellPrice} Koin."
                        sellFishInternal(result)
                    } else {
                        _toastMessage.value = "${result.fishName} disimpan ke Inventori & Fishdex!"
                    }
                }
                "release" -> {
                    localRepo.updateCatchKept(result.id, false)
                    if (isOnlineAccount) remoteRepo?.updateCatchKeptStatus(result.id, false)
                    val cur = playerState.value
                    savePlayerDual(cur.copy(xp = cur.xp + 25, battlePassXp = cur.battlePassXp + 25))
                    _toastMessage.value = "${result.fishName} dilepas kembali! +25 XP."
                }
                "sell" -> {
                    sellFishInternal(result)
                }
            }
        }
        _fishingSession.update {
            ActiveFishingSession(
                anglerX = it.anglerX,
                waterDepthM = GameCatalog.locationById(playerState.value.activeLocationId).depthMeters.toFloat()
            )
        }
    }

    private suspend fun sellFishInternal(fish: CaughtFish) {
        localRepo.updateCatchKept(fish.id, false)
        if (isOnlineAccount) remoteRepo?.updateCatchKeptStatus(fish.id, false)
        val cur = playerState.value
        savePlayerDual(cur.copy(coins = cur.coins + fish.sellPrice))
        incrementMission("m_daily_sell3", 1)
        _toastMessage.value = "Terjual ${fish.fishName} (+${fish.sellPrice} Koin)!"
    }

    fun sellKeptFish(fish: CaughtFish) {
        viewModelScope.launch { sellFishInternal(fish) }
    }

    // --- Shop, Inventory, Upgrade & Crafting ---

    fun buyProduct(product: ShopProduct) {
        viewModelScope.launch {
            val cur = playerState.value
            if (cur.coins < product.coinPrice || cur.gems < product.gemPrice) {
                _toastMessage.value = "Koin atau Gem tidak cukup!"
                return@launch
            }
            var updatedPlayer = cur.copy(
                coins = cur.coins - product.coinPrice,
                gems = cur.gems - product.gemPrice
            )

            when (product.id) {
                "gem_pack_small" -> {
                    updatedPlayer = updatedPlayer.copy(gems = updatedPlayer.gems + 25)
                    savePlayerDual(updatedPlayer)
                    _toastMessage.value = "Berhasil menukar +25 Gem!"
                    return@launch
                }
                "gem_pack_event" -> {
                    updatedPlayer = updatedPlayer.copy(gems = updatedPlayer.gems + 60)
                    addOrIncrementGear(
                        OwnedGear("bait_kristal", GearCategory.BAIT, "Umpan Kristal", FishRarity.EPIC, 1, 35, 25, 100, 5)
                    )
                    savePlayerDual(updatedPlayer)
                    _toastMessage.value = "Peti Event Badai terbuka! +60 Gem & 5 Umpan Kristal."
                    return@launch
                }
            }

            val qtyToAdd = when (product.id) {
                "bait_cacing", "bait_udang" -> 10
                "bait_kristal" -> 5
                else -> 1
            }
            val existing = ownedGear.value.find { it.itemId == product.id }
            if (existing != null && product.category in listOf(GearCategory.ROD, GearCategory.REEL, GearCategory.LINE, GearCategory.SKIN)) {
                _toastMessage.value = "Kamu sudah memiliki ${product.name}."
                return@launch
            }
            val newGear = if (existing != null) {
                existing.copy(quantity = existing.quantity + qtyToAdd)
            } else {
                OwnedGear(
                    itemId = product.id,
                    category = product.category,
                    name = product.name.replace(" (x10)", "").replace(" (x5)", ""),
                    rarity = product.rarity,
                    level = 1,
                    power = product.power,
                    control = product.control,
                    durability = product.durability,
                    quantity = qtyToAdd
                )
            }
            localRepo.saveGear(newGear)
            if (isOnlineAccount) remoteRepo?.saveGearItem(newGear)
            savePlayerDual(updatedPlayer)
            _toastMessage.value = "Berhasil membeli ${product.name}!"
        }
    }

    private suspend fun addOrIncrementGear(gear: OwnedGear) {
        val existing = ownedGear.value.find { it.itemId == gear.itemId }
        val merged = existing?.copy(quantity = existing.quantity + gear.quantity) ?: gear
        localRepo.saveGear(merged)
        if (isOnlineAccount) remoteRepo?.saveGearItem(merged)
    }

    fun equipGear(gear: OwnedGear) {
        viewModelScope.launch {
            val cur = playerState.value
            val updated = when (gear.category) {
                GearCategory.ROD -> cur.copy(equippedRodId = gear.itemId)
                GearCategory.REEL -> cur.copy(equippedReelId = gear.itemId)
                GearCategory.LINE -> cur.copy(equippedLineId = gear.itemId)
                GearCategory.BAIT -> cur.copy(equippedBaitId = gear.itemId)
                GearCategory.SKIN -> cur.copy(equippedSkinId = gear.itemId)
                GearCategory.ITEM -> {
                    useConsumableItem(gear)
                    return@launch
                }
            }
            savePlayerDual(updated)
            _toastMessage.value = "${gear.name} dipasang!"
        }
    }

    fun useConsumableItem(gear: OwnedGear) {
        viewModelScope.launch {
            if (gear.quantity <= 0) return@launch
            val cur = playerState.value
            val updatedPlayer = when (gear.itemId) {
                "item_luck_charm" -> cur.copy(luckBuffRemaining = cur.luckBuffRemaining + 5)
                "item_stabilizer" -> cur.copy(tensionBuffRemaining = cur.tensionBuffRemaining + 5)
                else -> {
                    _toastMessage.value = "${gear.name} digunakan sebagai bahan crafting/upgrade."
                    return@launch
                }
            }
            val updatedGear = gear.copy(quantity = gear.quantity - 1)
            localRepo.saveGear(updatedGear)
            if (isOnlineAccount) remoteRepo?.saveGearItem(updatedGear)
            savePlayerDual(updatedPlayer)
            _toastMessage.value = "Buff ${gear.name} aktif untuk 5 tangkapan!"
        }
    }

    fun upgradeGear(gear: OwnedGear) {
        viewModelScope.launch {
            if (gear.category !in listOf(GearCategory.ROD, GearCategory.REEL, GearCategory.LINE)) {
                _toastMessage.value = "Hanya Joran, Reel, dan Senar yang bisa di-upgrade."
                return@launch
            }
            val coinCost = gear.level * 220
            val mat = ownedGear.value.find { it.itemId == "mat_serat_baja" }
            val matQty = mat?.quantity ?: 0
            if (playerState.value.coins < coinCost || matQty < 1) {
                _toastMessage.value = "Butuh $coinCost Koin dan 1 Material Serat Baja."
                return@launch
            }
            val upgraded = gear.copy(
                level = gear.level + 1,
                power = gear.power + 6,
                control = gear.control + 5,
                durability = gear.durability + 15
            )
            val updatedMat = mat!!.copy(quantity = mat.quantity - 1)
            localRepo.saveGear(upgraded)
            localRepo.saveGear(updatedMat)
            if (isOnlineAccount) {
                remoteRepo?.saveGearItem(upgraded)
                remoteRepo?.saveGearItem(updatedMat)
            }
            savePlayerDual(playerState.value.copy(coins = playerState.value.coins - coinCost))
            incrementMission("m_weekly_upgrade2", 1)
            _toastMessage.value = "${gear.name} naik ke Lv.${upgraded.level}!"
        }
    }

    fun craftRecipe(recipe: CraftingRecipe) {
        viewModelScope.launch {
            val mat = ownedGear.value.find { it.itemId == recipe.requiredMaterialId }
            if ((mat?.quantity ?: 0) < recipe.requiredMaterialQty || playerState.value.coins < recipe.coinCost) {
                _toastMessage.value = "Bahan atau koin belum cukup untuk crafting."
                return@launch
            }
            val updatedMat = mat!!.copy(quantity = mat.quantity - recipe.requiredMaterialQty)
            localRepo.saveGear(updatedMat)
            if (isOnlineAccount) remoteRepo?.saveGearItem(updatedMat)

            addOrIncrementGear(
                OwnedGear(
                    itemId = recipe.outputItemId,
                    category = recipe.outputCategory,
                    name = recipe.outputName.replace(" (x5)", ""),
                    rarity = recipe.outputRarity,
                    level = 1,
                    power = 15,
                    control = 15,
                    durability = 100,
                    quantity = recipe.outputQty
                )
            )
            savePlayerDual(playerState.value.copy(coins = playerState.value.coins - recipe.coinCost))
            _toastMessage.value = "Berhasil meracik ${recipe.outputName}!"
        }
    }

    private suspend fun consumeBait(baitId: String) {
        val bait = ownedGear.value.find { it.itemId == baitId } ?: return
        if (bait.quantity > 0) {
            val updated = bait.copy(quantity = bait.quantity - 1)
            localRepo.saveGear(updated)
            if (isOnlineAccount) remoteRepo?.saveGearItem(updated)
        }
    }

    // --- Missions, Daily Login & Battle Pass ---

    private suspend fun incrementMission(missionId: String, delta: Int) {
        val cur = missionProgress.value[missionId]
        val nextProg = (cur?.first ?: 0) + delta
        val claimed = cur?.second ?: false
        localRepo.saveMission(missionId, nextProg, claimed)
        if (isOnlineAccount) remoteRepo?.saveMissionProgress(missionId, nextProg, claimed)
    }

    private suspend fun setMissionProgress(missionId: String, value: Int) {
        val cur = missionProgress.value[missionId]
        val nextProg = maxOf(cur?.first ?: 0, value)
        val claimed = cur?.second ?: false
        localRepo.saveMission(missionId, nextProg, claimed)
        if (isOnlineAccount) remoteRepo?.saveMissionProgress(missionId, nextProg, claimed)
    }

    fun claimMission(missionId: String) {
        val def = GameCatalog.missions.find { it.id == missionId } ?: return
        val state = missionProgress.value[missionId] ?: return
        if (state.second || state.first < def.target) return
        viewModelScope.launch {
            localRepo.saveMission(missionId, state.first, true)
            if (isOnlineAccount) remoteRepo?.saveMissionProgress(missionId, state.first, true)
            val cur = playerState.value
            savePlayerDual(
                cur.copy(
                    coins = cur.coins + def.rewardCoins,
                    gems = cur.gems + def.rewardGems,
                    xp = cur.xp + def.rewardXp,
                    battlePassXp = cur.battlePassXp + def.rewardXp
                )
            )
            _toastMessage.value = "Hadiah misi diklaim: +${def.rewardCoins} Koin, +${def.rewardGems} Gem!"
        }
    }

    fun claimDailyReward(day: Int) {
        val def = GameCatalog.dailyRewards.find { it.day == day } ?: return
        val cur = playerState.value
        if (day in cur.claimedDailyDays || day > cur.loginStreakDay) return
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val nextStreak = (cur.loginStreakDay + 1).coerceAtMost(7)
            val updated = cur.copy(
                coins = cur.coins + def.coins,
                gems = cur.gems + def.gems,
                loginStreakDay = nextStreak,
                lastLoginClaimDate = today,
                claimedDailyDays = cur.claimedDailyDays + day
            )
            savePlayerDual(updated)
            def.bonusItemId?.let { itemId ->
                val prod = GameCatalog.shopProducts.find { it.id == itemId }
                if (prod != null) {
                    addOrIncrementGear(
                        OwnedGear(prod.id, prod.category, prod.name.replace(" (x10)", "").replace(" (x5)", ""), prod.rarity, 1, prod.power, prod.control, prod.durability, 3)
                    )
                }
            }
            _toastMessage.value = "Hadiah Login Hari-$day diklaim!"
        }
    }

    fun unlockPremiumBattlePass() {
        val cur = playerState.value
        if (cur.isPremiumPass) return
        if (cur.gems < 40) {
            _toastMessage.value = "Butuh 40 Gem untuk mengaktifkan Premium Pass."
            return
        }
        viewModelScope.launch {
            savePlayerDual(cur.copy(gems = cur.gems - 40, isPremiumPass = true))
            _toastMessage.value = "Premium Battle Pass aktif!"
        }
    }

    fun claimBattlePassTier(tier: Int, isPremium: Boolean) {
        val def = GameCatalog.battlePassTiers.find { it.tier == tier } ?: return
        val cur = playerState.value
        if (cur.battlePassXp < def.requiredXp) return
        if (isPremium && !cur.isPremiumPass) return
        if (isPremium && tier in cur.claimedPremiumPassTiers) return
        if (!isPremium && tier in cur.claimedFreePassTiers) return

        viewModelScope.launch {
            val updated = if (isPremium) {
                cur.copy(
                    coins = cur.coins + def.premiumCoins,
                    gems = cur.gems + def.premiumGems,
                    claimedPremiumPassTiers = cur.claimedPremiumPassTiers + tier
                )
            } else {
                cur.copy(
                    coins = cur.coins + def.freeCoins,
                    gems = cur.gems + def.freeGems,
                    claimedFreePassTiers = cur.claimedFreePassTiers + tier
                )
            }
            savePlayerDual(updated)
            if (isPremium && def.premiumSkinId != null) {
                val skin = GameCatalog.shopProducts.find { it.id == def.premiumSkinId }
                if (skin != null) {
                    addOrIncrementGear(OwnedGear(skin.id, GearCategory.SKIN, skin.name, skin.rarity, 1, skin.power, skin.control, 100, 1))
                }
            }
            _toastMessage.value = "Hadiah Battle Pass Tier $tier diklaim!"
        }
    }

    // --- Profile, Settings, Offline & Cloud Sync ---

    fun updateProfile(username: String, avatarId: String) {
        viewModelScope.launch {
            val clean = username.trim().take(30).ifBlank { playerState.value.username }
            savePlayerDual(playerState.value.copy(username = clean, avatarId = avatarId))
            _toastMessage.value = "Profil diperbarui!"
        }
    }

    fun updateSettings(transform: (GameSettings) -> GameSettings) {
        viewModelScope.launch {
            settingsStore.updateSettings(transform)
        }
    }

    fun resetControlSettings() {
        updateSettings {
            it.copy(
                cameraSensitivity = 0.6f,
                controlLayout = com.example.domain.ControlLayoutMode.MODERN
            )
        }
        _toastMessage.value = "Pengaturan kontrol dikembalikan ke default."
    }

    fun syncNow() {
        if (!isOnlineAccount || remoteRepo == null) {
            _toastMessage.value = "Masuk dengan Google untuk menyinkronkan progres ke cloud."
            return
        }
        viewModelScope.launch {
            _syncStatus.value = "Menyinkronkan..."
            val p = playerState.value
            val pRes = remoteRepo.savePlayerProfile(p)
            ownedGear.value.forEach { remoteRepo.saveGearItem(it) }
            remoteRepo.publishLeaderboardScore(p, p.totalCatches * 50 + p.biggestCatchWeightKg.roundToInt() * 10)
            _syncStatus.value = if (pRes.isSuccess) "Tersinkronisasi ke Cloud" else "Gagal sinkronisasi"
            _toastMessage.value = if (pRes.isSuccess) "Progres berhasil disinkronkan ke cloud!" else "Sinkronisasi gagal, coba lagi."
        }
    }

    fun resetOfflineLocalData() {
        viewModelScope.launch {
            localRepo.resetOfflineData()
            _toastMessage.value = "Data lokal berhasil direset."
        }
    }

    private suspend fun syncFromCloudOnStart() {
        val remote = remoteRepo ?: return
        _syncStatus.value = "Menghubungkan ke Cloud..."
        val cloudProfile = remote.loadPlayerProfile().getOrNull()
        val local = playerState.value
        if (cloudProfile == null) {
            remote.savePlayerProfile(local)
            ownedGear.value.forEach { remote.saveGearItem(it) }
            _syncStatus.value = "Tersinkronisasi ke Cloud"
        } else {
            val merged = if (cloudProfile.totalCatches >= local.totalCatches && cloudProfile.level >= local.level) {
                cloudProfile
            } else {
                local.copy(userId = currentUserId)
            }
            localRepo.savePlayer(merged)
            remote.savePlayerProfile(merged)
            val cloudGear = remote.loadGear().getOrNull().orEmpty()
            if (cloudGear.isNotEmpty()) {
                localRepo.saveGearList(cloudGear)
            }
            val cloudCatches = remote.loadCatches().getOrNull().orEmpty()
            if (cloudCatches.isNotEmpty()) {
                localRepo.saveCatches(cloudCatches)
            }
            _syncStatus.value = "Tersinkronisasi ke Cloud"
        }
    }

    private suspend fun savePlayerDual(updated: PlayerState) {
        localRepo.savePlayer(updated)
        if (isOnlineAccount && remoteRepo != null) {
            val res = remoteRepo.savePlayerProfile(updated)
            if (res.isFailure) {
                Log.w("FishingVM", "Cloud save queued/failed: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun deductCoinsForGift(coins: Int) {
        if (coins <= 0) return
        viewModelScope.launch {
            val cur = playerState.value
            if (cur.coins >= coins) {
                savePlayerDual(cur.copy(coins = cur.coins - coins))
            }
        }
    }

    fun applyClaimedGift(itemId: String, itemName: String, quantity: Int, coinsGift: Int) {
        viewModelScope.launch {
            val cur = playerState.value
            if (coinsGift > 0) {
                savePlayerDual(cur.copy(coins = cur.coins + coinsGift))
            }
            if (itemId.isNotBlank() && quantity > 0) {
                val prod = GameCatalog.shopProducts.find { it.id == itemId }
                addOrIncrementGear(
                    OwnedGear(
                        itemId = itemId,
                        category = prod?.category ?: GearCategory.BAIT,
                        name = itemName.ifBlank { prod?.name ?: "Umpan Hadiah" },
                        rarity = prod?.rarity ?: FishRarity.COMMON,
                        level = 1,
                        power = prod?.power ?: 5,
                        control = prod?.control ?: 5,
                        durability = prod?.durability ?: 100,
                        quantity = quantity
                    )
                )
            }
        }
    }

    fun consumeToast() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
