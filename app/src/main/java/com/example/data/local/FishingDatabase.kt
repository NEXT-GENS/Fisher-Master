package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocalPlayerEntity::class,
        LocalCatchEntity::class,
        LocalGearEntity::class,
        LocalMissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FishingDatabase : RoomDatabase() {
    abstract fun gameDao(): LocalGameDao

    companion object {
        @Volatile
        private var INSTANCE: FishingDatabase? = null

        fun getInstance(context: Context): FishingDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FishingDatabase::class.java,
                    "fishing_master_local.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
