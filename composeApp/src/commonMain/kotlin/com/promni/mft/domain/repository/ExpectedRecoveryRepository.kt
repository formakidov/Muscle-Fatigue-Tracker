package com.promni.mft.domain.repository

import com.promni.mft.data.local.entities.ExpectedRecoveryEntity

interface ExpectedRecoveryRepository {
    suspend fun setExpectedRecovery(muscleId: Long, time: Long)
    suspend fun getExpectedRecovery(id: Long): ExpectedRecoveryEntity?
    suspend fun clearExpectedRecovery(muscleId: Long)
}
