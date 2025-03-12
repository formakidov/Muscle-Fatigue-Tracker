package com.promni.mft.domain.usecase

import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.Recovery
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.util.RecoveryCalculator

class SetTotalRecoveryTimeUseCase(
    private val muscleRepository: MuscleRepository,
    private val expectedRecoveryRepository: ExpectedRecoveryRepository
) {
    suspend operator fun invoke(muscleId: MuscleId, newTotalRecoveryTime: Recovery) {
        val currentTotalRecoveryTime = muscleRepository.currentTotalRecoveryTime(muscleId)
        muscleRepository.setTotalRecoveryTime(muscleId, newTotalRecoveryTime)

        val currentExpectedRecovery = expectedRecoveryRepository.getExpectedRecovery(muscleId)
        if (currentExpectedRecovery != null) { // null means fatigue for this muscle has never been set, skip calculation
            val newExpectedRecoveryTime = RecoveryCalculator.calculateNewExpectedRecovery(
                currentTotalRecoveryTime, currentExpectedRecovery.timestamp, newTotalRecoveryTime
            )
            expectedRecoveryRepository.setExpectedRecovery(muscleId, newExpectedRecoveryTime)
        }
    }
}
