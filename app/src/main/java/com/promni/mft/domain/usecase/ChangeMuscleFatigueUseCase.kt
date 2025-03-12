package com.promni.mft.domain.usecase

import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.util.RecoveryCalculator

class ChangeMuscleFatigueUseCase(
    private val muscleRepository: MuscleRepository,
    private val fatigueLogRepository: FatigueLogRepository,
    private val expectedRecoveryRepository: ExpectedRecoveryRepository
) {
    suspend operator fun invoke(
        muscleId: MuscleId,
        newValue: Float,
    ) {
        if (newValue < 0f || newValue > 100f) throw IllegalArgumentException("Fatigue value must be between 0 and 100")

        val totalRecoveryTime = muscleRepository.currentTotalRecoveryTime(muscleId)
        val expectedRecoveryTime = RecoveryCalculator.calculateExpectedRecovery(newValue, totalRecoveryTime)
        expectedRecoveryRepository.setExpectedRecovery(muscleId, expectedRecoveryTime)

        fatigueLogRepository.addFatigueLog(muscleId, newValue)
    }
}
