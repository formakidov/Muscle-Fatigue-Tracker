package com.promni.mft.data.repository

import com.promni.mft.data.local.dao.ExpectedRecoveryDao
import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.Recovery
import com.promni.mft.domain.repository.ExpectedRecoveryRepository

class ExpectedRecoveryRepositoryImpl(
    private val expectedRecoveryDao: ExpectedRecoveryDao
) : ExpectedRecoveryRepository {

    override suspend fun setExpectedRecovery(muscleId: MuscleId, time: Recovery) {
        val now = System.currentTimeMillis()
        expectedRecoveryDao.upsert(ExpectedRecoveryEntity(muscleId, time, now))
    }

    override suspend fun getExpectedRecovery(id: MuscleId) = expectedRecoveryDao.item(id)
}
