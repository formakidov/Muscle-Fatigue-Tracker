package com.promni.mft.domain.repository

import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.MuscleId


interface ExpectedRecoveryRepository {
    suspend fun setExpectedRecovery(muscleId: MuscleId, time: Long)
    suspend fun getExpectedRecovery(id: MuscleId): ExpectedRecoveryEntity?
}
