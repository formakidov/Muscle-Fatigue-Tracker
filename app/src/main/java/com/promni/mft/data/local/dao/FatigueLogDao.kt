package com.promni.mft.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.promni.mft.data.local.entities.FatigueLogEntity
import com.promni.mft.data.local.entities.MuscleId
import kotlinx.coroutines.flow.Flow

@Dao
interface FatigueLogDao {

    @Query("SELECT * FROM fatigue_logs WHERE muscleId = :muscleId ORDER BY timestamp DESC")
    fun getLogsForMuscle(muscleId: MuscleId): Flow<List<FatigueLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFatigueLog(log: FatigueLogEntity)

//    @Query("DELETE FROM fatigue_logs WHERE muscleId = :muscleId")
//    suspend fun deleteLogsForMuscle(muscleId: MuscleId)
}
