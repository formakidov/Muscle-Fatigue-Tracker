package com.promni.mft.domain.repository

import com.promni.mft.domain.model.MuscleInfo
import kotlinx.coroutines.flow.Flow

interface MuscleRepository {
    fun observeMuscles(): Flow<List<MuscleInfo>>
    fun observeMuscle(id: Long): Flow<MuscleInfo>
    suspend fun setTotalRecoveryTime(muscleId: Long, newTotalRecovery: Long)
    suspend fun currentTotalRecoveryTime(id: Long): Long
}
