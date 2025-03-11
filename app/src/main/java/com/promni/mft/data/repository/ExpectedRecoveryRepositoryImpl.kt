package com.promni.mft.data.repository

import com.promni.mft.data.local.dao.ExpectedRecoveryDao
import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.Recovery
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.util.RecoveryCalculator

class ExpectedRecoveryRepositoryImpl(
    private val expectedRecoveryDao: ExpectedRecoveryDao
) : ExpectedRecoveryRepository {

    override suspend fun storeExpectedRecovery(muscleId: MuscleId, totalRecoveryTime: Recovery, fatigue: Float) {
        val currentTimeMillis = System.currentTimeMillis()
        val expectedFullRecoveryTimestamp =
            RecoveryCalculator.calculateExpectedRecoveryTimestamp(currentTimeMillis, fatigue, totalRecoveryTime)

        expectedRecoveryDao.upsert(
            ExpectedRecoveryEntity(
                muscleId = muscleId, timestamp = expectedFullRecoveryTimestamp, lastUpdated = currentTimeMillis
            )
        )
    }

    override suspend fun adjustRecovery(
        muscleId: MuscleId, currentTotalRecoveryTime: Recovery, newTotalRecoveryTime: Long
    ) {
        val expectedRecovery = expectedRecoveryDao.item(muscleId)
        if (expectedRecovery == null) {
            storeExpectedRecovery(muscleId, newTotalRecoveryTime, 0f)
            return
        }
        val currentFatigue =
            RecoveryCalculator.calculateCurrentFatigue(expectedRecovery.timestamp, currentTotalRecoveryTime)
        val originalTimestamp = RecoveryCalculator.calculateOriginalTimestamp(
            currentFatigue, expectedRecovery.timestamp, currentTotalRecoveryTime
        )
        val newExpectedRecoveryTimestamp = RecoveryCalculator.calculateExpectedRecoveryTimestamp(
            originalTimestamp, currentFatigue, newTotalRecoveryTime
        )

        expectedRecoveryDao.upsert(
            expectedRecovery.copy(
                timestamp = newExpectedRecoveryTimestamp, lastUpdated = System.currentTimeMillis()
            )
        )
    }

    override suspend fun fetchExpectedRecovery(id: MuscleId) = expectedRecoveryDao.item(id)
}
