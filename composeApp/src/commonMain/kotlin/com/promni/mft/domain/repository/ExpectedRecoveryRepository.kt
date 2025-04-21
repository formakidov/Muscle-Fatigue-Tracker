package com.promni.mft.domain.repository

import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.Recovery

interface ExpectedRecoveryRepository {
    suspend fun setExpectedRecovery(muscleId: MuscleId, time: Recovery)
    suspend fun getExpectedRecovery(id: MuscleId): ExpectedRecoveryEntity?
}
