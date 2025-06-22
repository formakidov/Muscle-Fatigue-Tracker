package com.promni.mft.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.MuscleId
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpectedRecoveryDao {

    @Query("SELECT * FROM expected_recovery WHERE muscleId = :muscleId LIMIT 1")
    suspend fun item(muscleId: MuscleId): ExpectedRecoveryEntity?

    @Query("SELECT * FROM expected_recovery")
    fun all(): Flow<List<ExpectedRecoveryEntity>>

    @Upsert
    suspend fun upsert(tracker: ExpectedRecoveryEntity)

//    @Query("DELETE FROM expected_recovery WHERE muscleId = :muscleId")
//    suspend fun delete(muscleId: MuscleId)
}
