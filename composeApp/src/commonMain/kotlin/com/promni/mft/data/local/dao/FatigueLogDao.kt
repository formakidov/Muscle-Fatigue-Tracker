package com.promni.mft.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.promni.mft.data.local.entities.FatigueLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FatigueLogDao {

    @Query("SELECT * FROM fatigue_logs WHERE muscleId = :muscleId ORDER BY timestamp DESC")
    fun getLogsForMuscle(muscleId: Long): Flow<List<FatigueLogEntity>>

    @Query("SELECT * FROM fatigue_logs WHERE muscleId = :muscleId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLogForMuscle(muscleId: Long): FatigueLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFatigueLog(log: FatigueLogEntity)

    @Query("DELETE FROM fatigue_logs WHERE muscleId = :muscleId AND timestamp >= :startOfDay AND timestamp < :endOfDay")
    suspend fun deleteLogsForMuscleInRange(muscleId: Long, startOfDay: Long, endOfDay: Long)
}
