package com.promni.mft.domain.repository

import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.Recovery

interface ExpectedRecoveryRepository {
    suspend fun storeExpectedRecovery(muscleId: MuscleId, totalRecoveryTime: Recovery, fatigue: Float)
    suspend fun adjustRecovery(muscleId: MuscleId, currentTotalRecoveryTime: Recovery, newTotalRecoveryTime: Recovery)
    suspend fun fetchExpectedRecovery(id: MuscleId): ExpectedRecoveryEntity?
}
