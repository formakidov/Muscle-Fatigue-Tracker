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
        changeAmount: Float,
    ) {
        if (changeAmount == 0f) return

        val totalRecoveryTime = muscleRepository.currentTotalRecoveryTime(muscleId)
        val currentExpectedRecovery = expectedRecoveryRepository.fetchExpectedRecovery(muscleId)
        val currentFatigue = RecoveryCalculator.calculateCurrentFatigue(currentExpectedRecovery?.timestamp, totalRecoveryTime)
        val newFatigue = (currentFatigue + changeAmount).coerceIn(0f, 100f)
        if (newFatigue == currentFatigue) return
        expectedRecoveryRepository.storeExpectedRecovery(muscleId, totalRecoveryTime, newFatigue)

        fatigueLogRepository.addFatigueLog(muscleId, changeAmount)
    }
}
