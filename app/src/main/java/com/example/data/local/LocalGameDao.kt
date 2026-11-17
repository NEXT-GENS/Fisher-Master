package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalGameDao {
    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun observePlayer(): Flow<LocalPlayerEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    suspend fun getPlayer(): LocalPlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlayer(player: LocalPlayerEntity)

    @Query("SELECT * FROM caught_fish ORDER BY caughtAtMillis DESC")
    fun observeCatches(): Flow<List<LocalCatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatch(catch: LocalCatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatches(catches: List<LocalCatchEntity>)

    @Query("UPDATE caught_fish SET isKept = :isKept WHERE id = :id")
    suspend fun updateCatchKept(id: String, isKept: Boolean)

    @Query("SELECT * FROM owned_gear")
    fun observeGear(): Flow<List<LocalGearEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGear(gear: LocalGearEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGearList(items: List<LocalGearEntity>)

    @Query("SELECT * FROM mission_progress")
    fun observeMissions(): Flow<List<LocalMissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMission(mission: LocalMissionEntity)

    @Query("DELETE FROM caught_fish")
    suspend fun clearCatches()

    @Query("DELETE FROM owned_gear")
    suspend fun clearGear()

    @Query("DELETE FROM mission_progress")
    suspend fun clearMissions()
}
