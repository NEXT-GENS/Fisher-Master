package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.audio.SoundAndHapticsManager
import com.example.auth.authStateFlow
import com.example.auth.signOutGoogle
import com.example.data.local.FishingDatabase
import com.example.data.local.LocalGameRepository
import com.example.data.local.SettingsDataStore
import com.example.data.remote.FirestoreRepository
import com.example.domain.GameCatalog
import com.example.domain.PlayMode
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FishdexScreen
import com.example.ui.screens.FishingGameplayScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.MissionsAndPassScreen
import com.example.ui.screens.ModeAndLocationScreen
import com.example.ui.screens.MultiplayerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.FishingMasterTheme
import com.example.viewmodel.FishingMasterViewModel
import com.example.viewmodel.MultiplayerViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

private object Routes {
    const val LOBBY = "lobby"
    const val LOCATIONS = "locations"
    const val GAMEPLAY = "gameplay"
    const val INVENTORY = "inventory"
    const val SHOP = "shop"
    const val FISHDEX = "fishdex"
    const val MISSIONS = "missions"
    const val DAILY_LOGIN = "daily_login"
    const val MULTIPLAYER = "multiplayer"
    const val SETTINGS = "settings"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FishingMasterRoot()
        }
    }
}

@Composable
fun FishingMasterRoot(auth: FirebaseAuth = Firebase.auth) {
    var splashDone by rememberSaveable { mutableStateOf(false) }
    var offlineSoloActive by rememberSaveable { mutableStateOf(false) }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)

    if (!splashDone) {
        FishingMasterTheme {
            SplashScreen(onFinished = { splashDone = true })
        }
        return
    }

    val user = currentUser
    if (user == null && !offlineSoloActive) {
        FishingMasterTheme {
            AuthScreen(
                onGoogleAuthSuccess = { offlineSoloActive = false },
                onContinueOffline = { offlineSoloActive = true }
            )
        }
    } else if (user != null) {
        // Authenticated session: key by uid so signing out or switching accounts resets state cleanly
        androidx.compose.runtime.key(user.uid) {
            FishingAppContent(
                uid = user.uid,
                displayName = user.displayName ?: "Kapten Nusantara",
                isOnlineAccount = true,
                onRequestGoogleSignIn = {},
                onSignOutComplete = { offlineSoloActive = false }
            )
        }
    } else {
        // Strictly local Offline Solo session: zero Firestore / Auth calls
        androidx.compose.runtime.key("offline_local") {
            FishingAppContent(
                uid = "offline_local",
                displayName = "Kapten Nusantara",
                isOnlineAccount = false,
                onRequestGoogleSignIn = { offlineSoloActive = false },
                onSignOutComplete = { offlineSoloActive = false }
            )
        }
    }
}

@Composable
private fun FishingAppContent(
    uid: String,
    displayName: String,
    isOnlineAccount: Boolean,
    onRequestGoogleSignIn: () -> Unit,
    onSignOutComplete: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val mainVm: FishingMasterViewModel = viewModel(
        key = "fm_$uid",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val db = FishingDatabase.getInstance(app)
                val localRepo = LocalGameRepository(db.gameDao())
                val settingsStore = SettingsDataStore(app)
                val soundManager = SoundAndHapticsManager(app)
                val remoteRepo = if (isOnlineAccount) {
                    val databaseId = app.getString(R.string.firestore_database_id)
                    FirestoreRepository(FirebaseFirestore.getInstance(databaseId))
                } else null
                FishingMasterViewModel(
                    localRepo = localRepo,
                    settingsStore = settingsStore,
                    soundManager = soundManager,
                    remoteRepo = remoteRepo,
                    currentUserId = uid,
                    initialDisplayName = displayName,
                    isOnlineAccount = isOnlineAccount
                )
            }
        }
    )

    // MultiplayerViewModel is instantiated ONLY when authenticated (auth-gated)
    val mpVm: MultiplayerViewModel? = if (isOnlineAccount) {
        viewModel(
            key = "mp_$uid",
            factory = viewModelFactory {
                initializer {
                    val app = checkNotNull(this[APPLICATION_KEY])
                    val databaseId = app.getString(R.string.firestore_database_id)
                    MultiplayerViewModel(
                        remoteRepo = FirestoreRepository(FirebaseFirestore.getInstance(databaseId)),
                        currentUserId = uid
                    )
                }
            }
        )
    } else null

    val player by mainVm.playerState.collectAsStateWithLifecycle()
    val catches by mainVm.catches.collectAsStateWithLifecycle()
    val gear by mainVm.ownedGear.collectAsStateWithLifecycle()
    val missions by mainVm.missionProgress.collectAsStateWithLifecycle()
    val settings by mainVm.settings.collectAsStateWithLifecycle()
    val playMode by mainVm.playMode.collectAsStateWithLifecycle()
    val weather by mainVm.currentWeather.collectAsStateWithLifecycle()
    val timeOfDay by mainVm.timeOfDay.collectAsStateWithLifecycle()
    val clockText by mainVm.inGameClockText.collectAsStateWithLifecycle()
    val forecast by mainVm.forecast.collectAsStateWithLifecycle()
    val activeEvent by mainVm.activeEvent.collectAsStateWithLifecycle()
    val weatherAlert by mainVm.weatherAlert.collectAsStateWithLifecycle()
    val session by mainVm.fishingSession.collectAsStateWithLifecycle()
    val toastMsg by mainVm.toastMessage.collectAsStateWithLifecycle()
    val syncStatus by mainVm.syncStatus.collectAsStateWithLifecycle()
    val pingMs by mainVm.pingMs.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(toastMsg) {
        val msg = toastMsg
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            mainVm.consumeToast()
        }
    }

    val mpNotice = mpVm?.mpNotice?.collectAsStateWithLifecycle()?.value
    LaunchedEffect(mpNotice) {
        if (mpNotice != null) {
            snackbarHostState.showSnackbar(mpNotice)
            mpVm?.consumeNotice()
        }
    }

    val claimableCount = remember(missions) {
        GameCatalog.missions.count { def ->
            val st = missions[def.id] ?: (0 to false)
            !st.second && st.first >= def.target
        }
    }

    FishingMasterTheme(
        weather = weather,
        isNight = timeOfDay.isNight,
        textScale = settings.textScale
    ) {
        val navController = rememberNavController()
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = Routes.LOBBY) {
                composable(Routes.LOBBY) {
                    LobbyScreen(
                        player = player,
                        weather = weather,
                        activeEvent = activeEvent,
                        forecast = forecast,
                        isOnlineAccount = isOnlineAccount,
                        pingMs = pingMs,
                        claimableMissionsCount = claimableCount,
                        onPlayClick = { navController.navigate(Routes.GAMEPLAY) },
                        onLocationsClick = { navController.navigate(Routes.LOCATIONS) },
                        onMultiplayerClick = {
                            if (isOnlineAccount) navController.navigate(Routes.MULTIPLAYER)
                            else onRequestGoogleSignIn()
                        },
                        onShopClick = { navController.navigate(Routes.SHOP) },
                        onInventoryClick = { navController.navigate(Routes.INVENTORY) },
                        onFishdexClick = { navController.navigate(Routes.FISHDEX) },
                        onMissionsClick = { navController.navigate(Routes.MISSIONS) },
                        onDailyLoginClick = { navController.navigate(Routes.DAILY_LOGIN) },
                        onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                        onRequestGoogleSignIn = onRequestGoogleSignIn,
                        onSimulateWeather = { mainVm.cycleWeatherManually() }
                    )
                }

                composable(Routes.LOCATIONS) {
                    ModeAndLocationScreen(
                        player = player,
                        playMode = playMode,
                        isOnlineAccount = isOnlineAccount,
                        pingMs = pingMs,
                        onSelectMode = { mainVm.selectPlayMode(it) },
                        onSelectLocationAndPlay = { locId ->
                            mainVm.selectLocation(locId)
                            navController.navigate(Routes.GAMEPLAY)
                        },
                        onRequestSignIn = onRequestGoogleSignIn,
                        onBack = { navController.popBackStack() },
                        weather = weather,
                        onShopClick = { navController.navigate(Routes.SHOP) }
                    )
                }

                composable(Routes.GAMEPLAY) {
                    val roomMsgs = mpVm?.roomMessages?.collectAsStateWithLifecycle()?.value ?: emptyList()
                    val activeRoom = mpVm?.activeRoom?.collectAsStateWithLifecycle()?.value
                    FishingGameplayScreen(
                        player = player,
                        ownedGear = gear,
                        session = session,
                        weather = weather,
                        timeOfDay = timeOfDay,
                        clockText = clockText,
                        activeEvent = activeEvent,
                        forecast = forecast,
                        weatherAlert = weatherAlert,
                        settings = settings,
                        isOnlineAccount = isOnlineAccount,
                        pingMs = pingMs,
                        roomMessages = roomMsgs,
                        inMultiplayerRoom = activeRoom != null,
                        onMoveAngler = { mainVm.moveAngler(it) },
                        onStartCast = { mainVm.startCasting() },
                        onReleaseCast = { mainVm.releaseCast() },
                        onHook = { mainVm.triggerHook() },
                        onReelPressChange = { mainVm.setReelingPressed(it) },
                        onCancelFishing = { mainVm.cancelFishing() },
                        onResolveCatch = { action ->
                            val caught = session.lastCatchResult
                            mainVm.resolveCatchResult(action)
                            if (caught != null && activeRoom != null) {
                                mpVm?.reportRoomCatch(player.username, caught.fishName, caught.sellPrice)
                            }
                        },
                        onEquipGear = { mainVm.equipGear(it) },
                        onSendQuickChat = { text -> mpVm?.sendChat(player.username, text, "quick_chat") },
                        onSimulateWeather = { mainVm.cycleWeatherManually() },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        onExitToLobby = {
                            mainVm.cancelFishing()
                            navController.popBackStack(Routes.LOBBY, inclusive = false)
                        },
                        onDismissAlert = { mainVm.dismissWeatherAlert() }
                    )
                }

                composable(Routes.INVENTORY) {
                    InventoryScreen(
                        player = player,
                        catches = catches,
                        ownedGear = gear,
                        onEquipGear = { mainVm.equipGear(it) },
                        onUpgradeGear = { mainVm.upgradeGear(it) },
                        onCraftRecipe = { mainVm.craftRecipe(it) },
                        onSellFish = { mainVm.sellKeptFish(it) },
                        onBack = { navController.popBackStack() },
                        onOpenShop = { navController.navigate(Routes.SHOP) }
                    )
                }

                composable(Routes.SHOP) {
                    ShopScreen(
                        player = player,
                        ownedGear = gear,
                        onBuyProduct = { mainVm.buyProduct(it) },
                        onBack = { navController.popBackStack() },
                        activeEvent = activeEvent,
                        onOpenEvents = { navController.navigate(Routes.MISSIONS) }
                    )
                }

                composable(Routes.FISHDEX) {
                    FishdexScreen(
                        catches = catches,
                        onBack = { navController.popBackStack() },
                        player = player,
                        onOpenShop = { navController.navigate(Routes.SHOP) },
                        onOpenLocation = { locId ->
                            mainVm.selectLocation(locId)
                            navController.navigate(Routes.LOCATIONS)
                        }
                    )
                }

                composable(Routes.MISSIONS) {
                    MissionsAndPassScreen(
                        player = player,
                        missionProgress = missions,
                        initialTab = 0,
                        onClaimMission = { mainVm.claimMission(it) },
                        onClaimDailyReward = { mainVm.claimDailyReward(it) },
                        onUnlockPremiumPass = { mainVm.unlockPremiumBattlePass() },
                        onClaimPassTier = { tier, prem -> mainVm.claimBattlePassTier(tier, prem) },
                        onBack = { navController.popBackStack() },
                        weather = weather,
                        pingMs = pingMs,
                        onOpenShop = { navController.navigate(Routes.SHOP) },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        onSimulateWeather = { mainVm.cycleWeatherManually() },
                        onPlayFishing = { navController.navigate(Routes.GAMEPLAY) }
                    )
                }

                composable(Routes.DAILY_LOGIN) {
                    MissionsAndPassScreen(
                        player = player,
                        missionProgress = missions,
                        initialTab = 3,
                        onClaimMission = { mainVm.claimMission(it) },
                        onClaimDailyReward = { mainVm.claimDailyReward(it) },
                        onUnlockPremiumPass = { mainVm.unlockPremiumBattlePass() },
                        onClaimPassTier = { tier, prem -> mainVm.claimBattlePassTier(tier, prem) },
                        onBack = { navController.popBackStack() },
                        weather = weather,
                        pingMs = pingMs,
                        onOpenShop = { navController.navigate(Routes.SHOP) },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        onSimulateWeather = { mainVm.cycleWeatherManually() },
                        onPlayFishing = { navController.navigate(Routes.GAMEPLAY) }
                    )
                }

                composable(Routes.MULTIPLAYER) {
                    if (mpVm != null) {
                        val rooms by mpVm.rooms.collectAsStateWithLifecycle()
                        val activeRoom by mpVm.activeRoom.collectAsStateWithLifecycle()
                        val roomPlayers by mpVm.roomPlayers.collectAsStateWithLifecycle()
                        val roomMessages by mpVm.roomMessages.collectAsStateWithLifecycle()
                        val leaderboard by mpVm.leaderboard.collectAsStateWithLifecycle()
                        val friends by mpVm.friends.collectAsStateWithLifecycle()
                        val gifts by mpVm.incomingGifts.collectAsStateWithLifecycle()
                        val connState by mpVm.connectionState.collectAsStateWithLifecycle()

                        MultiplayerScreen(
                            player = player,
                            currentUserId = uid,
                            rooms = rooms,
                            activeRoom = activeRoom,
                            roomPlayers = roomPlayers,
                            roomMessages = roomMessages,
                            leaderboard = leaderboard,
                            friends = friends,
                            incomingGifts = gifts,
                            connectionState = connState,
                            onCreateRoom = { name, reg, locId -> mpVm.createRoom(name, reg, locId, player) },
                            onJoinRoom = { mpVm.joinRoom(it, player) },
                            onJoinByCode = { mpVm.joinRoomByCode(it, player) },
                            onLeaveRoom = { mpVm.leaveRoom() },
                            onReconnect = { mpVm.reconnectRoom() },
                            onToggleReady = { ready, score, last -> mpVm.toggleReady(ready, score, last) },
                            onStartTournament = { mpVm.startTournamentInRoom() },
                            onSendChat = { text, type -> mpVm.sendChat(player.username, text, type) },
                            onStartFishingInRoom = {
                                activeRoom?.locationId?.let { mainVm.selectLocation(it) }
                                navController.navigate(Routes.GAMEPLAY)
                            },
                            onAddFriend = { fUid, fName, fLvl -> mpVm.addFriend(fUid, fName, fLvl) },
                            onSendGift = { recUid, itemId, qty, coins ->
                                if (player.coins >= coins) {
                                    mainVm.deductCoinsForGift(coins)
                                    mpVm.sendGift(recUid, player.username, itemId, "Cacing Tanah", qty, coins)
                                }
                            },
                            onClaimGift = { gift ->
                                mpVm.claimGift(gift) { grantedCoins ->
                                    mainVm.applyClaimedGift(gift.itemId, gift.itemName, gift.quantity, grantedCoins)
                                }
                            },
                            onBack = { navController.popBackStack() },
                            pingMs = pingMs,
                            onOpenShop = { navController.navigate(Routes.SHOP) },
                            onOpenSettings = { navController.navigate(Routes.SETTINGS) }
                        )
                    }
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        player = player,
                        settings = settings,
                        isOnlineAccount = isOnlineAccount,
                        syncStatus = syncStatus,
                        onUpdateProfile = { name, av -> mainVm.updateProfile(name, av) },
                        onUpdateSettings = { mainVm.updateSettings(it) },
                        onResetControls = { mainVm.resetControlSettings() },
                        onSyncNow = { mainVm.syncNow() },
                        onResetOfflineData = { mainVm.resetOfflineLocalData() },
                        onSignInWithGoogle = onRequestGoogleSignIn,
                        onSignOut = {
                            signOutGoogle(
                                credentialManager = credentialManager,
                                scope = scope,
                                onSignOutComplete = onSignOutComplete
                            )
                        },
                        onBack = { navController.popBackStack() },
                        pingMs = pingMs,
                        onOpenShop = { navController.navigate(Routes.SHOP) }
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }
}
