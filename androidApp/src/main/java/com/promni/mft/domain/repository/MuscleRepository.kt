package com.promni.mft.domain.repository

import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.Recovery
import com.promni.mft.domain.model.MuscleInfo
import kotlinx.coroutines.flow.Flow

interface MuscleRepository {
    fun observeMuscles(): Flow<List<MuscleInfo>>
    suspend fun setTotalRecoveryTime(muscleId: MuscleId, newTotalRecovery: Recovery)
    suspend fun currentTotalRecoveryTime(id: MuscleId): Recovery
}
