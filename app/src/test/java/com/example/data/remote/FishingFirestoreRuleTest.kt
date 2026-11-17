package com.example.data.remote

import com.example.base.FirestoreEmulatorTestBase
import com.example.domain.CaughtFish
import com.example.domain.FishRarity
import com.example.domain.PlayerState
import com.example.domain.WeatherType
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class FishingFirestoreRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndLoadPlayerProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = FirestoreRepository(firestore, auth)
        val profile = PlayerState(userId = uid, username = "AliceAngler", level = 4, coins = 2400)

        val saveRes = withTimeout(TIMEOUT_MS) { repo.savePlayerProfile(profile) }
        assertTrue(saveRes.isSuccess)

        val loaded = withTimeout(TIMEOUT_MS) { repo.loadPlayerProfile().getOrThrow() }
        assertNotNull(loaded)
        assertEquals("AliceAngler", loaded?.username)
        assertEquals(4, loaded?.level)
    }

    @Test
    fun saveCatchAndObserveLeaderboard_authenticatedUser_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = FirestoreRepository(firestore, auth)
        val catch = CaughtFish(
            id = "catch_${UUID.randomUUID().toString().take(8)}",
            fishId = "fish_marlin",
            fishName = "Marlin Biru Agung",
            rarity = FishRarity.LEGENDARY,
            weightKg = 112.5,
            lengthCm = 290.0,
            locationId = "laut_biru",
            weather = WeatherType.BADAI,
            sellPrice = 1850,
            isKept = true,
            isRecord = true,
            caughtAtMillis = System.currentTimeMillis()
        )

        val saveCatch = withTimeout(TIMEOUT_MS) { repo.saveCatchRecord(catch) }
        assertTrue(saveCatch.isSuccess)

        val profile = PlayerState(
            userId = uid,
            username = "AliceAngler",
            level = 4,
            totalCatches = 1,
            biggestCatchWeightKg = 112.5,
            biggestCatchFishName = "Marlin Biru Agung"
        )
        val pubRes = withTimeout(TIMEOUT_MS) { repo.publishLeaderboardScore(profile, 950) }
        assertTrue(pubRes.isSuccess)

        val board = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeLeaderboard().first { list -> list.any { it.userId == uid } }
        }
        assertTrue(board.any { it.biggestFishName == "Marlin Biru Agung" })
    }

    @Test
    fun crossUserRoomCreationAndChat_succeedsAndPreventsCrossUserProfileRead() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = FirestoreRepository(firestore, auth)
        val aliceState = PlayerState(userId = aliceUid, username = "AliceAngler", level = 5)
        aliceRepo.savePlayerProfile(aliceState).getOrThrow()

        val room = withTimeout(TIMEOUT_MS) {
            aliceRepo.createRoom("Turnamen Danau", "Indonesia", "danau_tenang", aliceState).getOrThrow()
        }

        val bobUid = signInTestUser(BOB_EMAIL)
        val bobRepo = FirestoreRepository(firestore, auth)
        val bobState = PlayerState(userId = bobUid, username = "BobAngler", level = 3)
        withTimeout(TIMEOUT_MS) { bobRepo.joinRoom(room, bobState).getOrThrow() }
        withTimeout(TIMEOUT_MS) { bobRepo.sendRoomMessage(room.id, "BobAngler", "Mantap!").getOrThrow() }

        val messages = withTimeout(FLOW_TIMEOUT_MS) {
            bobRepo.observeRoomMessages(room.id).first { it.isNotEmpty() }
        }
        assertTrue(messages.any { it.text == "Mantap!" })

        // Verify Bob cannot read Alice's private profile doc
        val ex = try {
            withTimeout(TIMEOUT_MS) {
                firestore.collection("users").document(aliceUid).get().let {
                    com.google.android.gms.tasks.Tasks.await(it)
                }
            }
            null
        } catch (e: Exception) {
            e.unwrapFirestoreException()
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, ex?.code)
    }

    @Test
    fun observeLeaderboard_unauthenticated_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repo = FirestoreRepository(firestore, auth)
        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) { repo.observeLeaderboard().first() }
        } catch (e: Throwable) {
            caught = e
        }
        assertNotNull(caught)
        // Repository throws IllegalStateException fail-fast when unauthenticated OR Firestore returns PERMISSION_DENIED
        assertTrue(
            caught is IllegalStateException ||
                caught?.unwrapFirestoreException()?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
        )
    }

    private companion object {
        const val ALICE_EMAIL = "alice_fm@test.com"
        const val BOB_EMAIL = "bob_fm@test.com"
        const val TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
