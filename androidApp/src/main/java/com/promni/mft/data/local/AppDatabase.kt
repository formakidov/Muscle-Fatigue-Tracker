package com.promni.mft.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.promni.mft.data.local.dao.ExpectedRecoveryDao
import com.promni.mft.data.local.dao.FatigueLogDao
import com.promni.mft.data.local.dao.MuscleDao
import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.FatigueLogEntity
import com.promni.mft.data.local.entities.MuscleEntity

@Database(
    entities = [
        MuscleEntity::class,
        FatigueLogEntity::class,
        ExpectedRecoveryEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun muscleDao(): MuscleDao
    abstract fun fatigueLogDao(): FatigueLogDao
    abstract fun expectedRecoveryDao(): ExpectedRecoveryDao

    companion object {
        fun build(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "muscle_fatigue_db"
            )
                .createFromAsset("databases/default_muscles.db")
                .build()
        }
    }
}
