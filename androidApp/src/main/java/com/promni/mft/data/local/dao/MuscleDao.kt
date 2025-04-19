package com.promni.mft.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.promni.mft.data.local.entities.MuscleEntity
import com.promni.mft.data.local.entities.MuscleId
import kotlinx.coroutines.flow.Flow

@Dao
interface MuscleDao {

    @Query("SELECT * FROM muscles ORDER BY name ASC")
    fun all(): Flow<List<MuscleEntity>>

    @Query("SELECT * FROM muscles WHERE id = :muscleId LIMIT 1")
    suspend fun item(muscleId: MuscleId): MuscleEntity?

    @Upsert
    suspend fun upsert(muscle: MuscleEntity)

//    @Delete
//    suspend fun delete(muscle: MuscleEntity)
}
