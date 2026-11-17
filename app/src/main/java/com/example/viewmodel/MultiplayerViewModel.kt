package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.FirestoreRepository
import com.example.domain.FriendInfo
import com.example.domain.GiftInfo
import com.example.domain.LeaderboardPlayer
import com.example.domain.PlayerState
import com.example.domain.RoomChatMessage
import com.example.domain.RoomPlayerInfo
import com.example.domain.RoomSummary
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MultiplayerViewModel(
    private val remoteRepo: FirestoreRepository,
    private val currentUserId: String
) : ViewModel() {

    val rooms: StateFlow<List<RoomSummary>> = remoteRepo.observeOpenRooms()
        .catch { e ->
            handleStreamError("rooms", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val leaderboard: StateFlow<List<LeaderboardPlayer>> = remoteRepo.observeLeaderboard()
        .catch { e ->
            handleStreamError("leaderboard", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val friends: StateFlow<List<FriendInfo>> = remoteRepo.observeFriends()
        .catch { e ->
            handleStreamError("friends", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val incomingGifts: StateFlow<List<GiftInfo>> = remoteRepo.observeIncomingGifts()
        .catch { e ->
            handleStreamError("gifts", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _activeRoom = MutableStateFlow<RoomSummary?>(null)
    val activeRoom: StateFlow<RoomSummary?> = _activeRoom.asStateFlow()

    private val _roomPlayers = MutableStateFlow<List<RoomPlayerInfo>>(emptyList())
    val roomPlayers: StateFlow<List<RoomPlayerInfo>> = _roomPlayers.asStateFlow()

    private val _roomMessages = MutableStateFlow<List<RoomChatMessage>>(emptyList())
    val roomMessages: StateFlow<List<RoomChatMessage>> = _roomMessages.asStateFlow()

    private val _connectionState = MutableStateFlow("Terhubung")
    val connectionState: StateFlow<String> = _connectionState.asStateFlow()

    private val _mpNotice = MutableStateFlow<String?>(null)
    val mpNotice: StateFlow<String?> = _mpNotice.asStateFlow()

    private var activeRoomObserveJob: kotlinx.coroutines.Job? = null

    fun createRoom(name: String, region: String, locationId: String, player: PlayerState) {
        viewModelScope.launch {
            _connectionState.value = "Membuat room..."
            val res = remoteRepo.createRoom(name, region, locationId, player)
            if (res.isSuccess) {
                val room = res.getOrThrow()
                enterRoomInternal(room)
                _connectionState.value = "Terhubung"
                _mpNotice.value = "Room ${room.roomCode} dibuat!"
            } else {
                _connectionState.value = "Koneksi bermasalah"
                _mpNotice.value = res.exceptionOrNull()?.localizedMessage ?: "Gagal membuat room."
            }
        }
    }

    fun joinRoom(room: RoomSummary, player: PlayerState) {
        viewModelScope.launch {
            _connectionState.value = "Menghubungkan ke room..."
            val res = remoteRepo.joinRoom(room, player)
            if (res.isSuccess) {
                enterRoomInternal(room)
                _connectionState.value = "Terhubung"
            } else {
                _connectionState.value = "Gagal terhubung"
                _mpNotice.value = res.exceptionOrNull()?.localizedMessage ?: "Gagal masuk room."
            }
        }
    }

    fun joinRoomByCode(code: String, player: PlayerState) {
        viewModelScope.launch {
            _connectionState.value = "Mencari kode room..."
            val res = remoteRepo.joinRoomByCode(code, player)
            val found = res.getOrNull()
            if (found != null) {
                enterRoomInternal(found)
                _connectionState.value = "Terhubung"
            } else {
                _connectionState.value = "Terhubung"
                _mpNotice.value = "Kode room '$code' tidak ditemukan."
            }
        }
    }

    private fun enterRoomInternal(room: RoomSummary) {
        _activeRoom.value = room
        activeRoomObserveJob?.cancel()
        activeRoomObserveJob = viewModelScope.launch {
            launch {
                remoteRepo.observeRoomPlayers(room.id)
                    .catch { e -> handleStreamError("room_players", e) }
                    .collect { _roomPlayers.value = it }
            }
            launch {
                remoteRepo.observeRoomMessages(room.id)
                    .catch { e -> handleStreamError("room_messages", e) }
                    .collect { _roomMessages.value = it }
            }
        }
    }

    fun reconnectRoom() {
        val room = _activeRoom.value ?: return
        _connectionState.value = "Menghubungkan ulang..."
        enterRoomInternal(room)
        _connectionState.value = "Berhasil terhubung ulang"
    }

    fun leaveRoom() {
        activeRoomObserveJob?.cancel()
        _activeRoom.value = null
        _roomPlayers.value = emptyList()
        _roomMessages.value = emptyList()
    }

    fun toggleReady(currentReady: Boolean, score: Int, lastCatch: String) {
        val room = _activeRoom.value ?: return
        viewModelScope.launch {
            remoteRepo.updateRoomPlayerState(room.id, !currentReady, score, lastCatch)
        }
    }

    fun startTournamentInRoom() {
        val room = _activeRoom.value ?: return
        if (room.hostId != currentUserId) return
        viewModelScope.launch {
            remoteRepo.updateRoomStatus(room.id, "playing")
            _activeRoom.value = room.copy(status = "playing")
            remoteRepo.sendRoomMessage(room.id, "Sistem", "Turnamen memancing dimulai! Tangkap ikan terbesar!", "system")
        }
    }

    fun reportRoomCatch(playerName: String, fishName: String, addedScore: Int) {
        val room = _activeRoom.value ?: return
        val me = _roomPlayers.value.find { it.userId == currentUserId }
        val nextScore = (me?.score ?: 0) + addedScore
        viewModelScope.launch {
            remoteRepo.updateRoomPlayerState(room.id, true, nextScore, fishName)
            remoteRepo.sendRoomMessage(room.id, playerName, "Berhasil menangkap $fishName (+$addedScore poin)!", "system")
        }
    }

    fun sendChat(senderName: String, text: String, type: String = "chat") {
        val room = _activeRoom.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            remoteRepo.sendRoomMessage(room.id, senderName, text.trim(), type)
        }
    }

    fun addFriend(uid: String, name: String, level: Int) {
        viewModelScope.launch {
            val res = remoteRepo.addFriend(uid, name, level)
            _mpNotice.value = if (res.isSuccess) "$name ditambahkan ke Daftar Teman!" else "Gagal menambah teman."
        }
    }

    fun sendGift(recipientUid: String, senderName: String, itemId: String, itemName: String, qty: Int, coins: Int) {
        viewModelScope.launch {
            val res = remoteRepo.sendGift(recipientUid, senderName, itemId, itemName, qty, coins)
            _mpNotice.value = if (res.isSuccess) "Hadiah terkirim!" else "Gagal mengirim hadiah."
        }
    }

    fun claimGift(gift: GiftInfo, onGrantCoins: (Int) -> Unit) {
        viewModelScope.launch {
            val res = remoteRepo.claimGift(gift.id)
            if (res.isSuccess) {
                onGrantCoins(gift.coinsGift)
                _mpNotice.value = "Hadiah dari ${gift.senderName} diklaim (+${gift.coinsGift} Koin)!"
            }
        }
    }

    fun consumeNotice() {
        _mpNotice.value = null
    }

    private fun handleStreamError(tag: String, error: Throwable) {
        val fsErr = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (fsErr == null && error is CancellationException) throw error
        Log.w("MultiplayerVM", "Stream error ($tag): ${(fsErr ?: error).message}")
        _connectionState.value = "Koneksi terputus - ketuk Reconnect"
    }

    override fun onCleared() {
        super.onCleared()
        activeRoomObserveJob?.cancel()
    }
}
