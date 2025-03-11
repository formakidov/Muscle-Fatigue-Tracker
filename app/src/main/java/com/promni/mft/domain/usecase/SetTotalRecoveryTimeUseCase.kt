package com.promni.mft.domain.usecase

import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.Recovery
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.MuscleRepository

class SetTotalRecoveryTimeUseCase(
    private val muscleRepository: MuscleRepository, private val expectedRecoveryRepository: ExpectedRecoveryRepository
) {
    suspend operator fun invoke(muscleId: MuscleId, newTotalRecoveryTime: Recovery) {
        val currentTotalRecoveryTime = muscleRepository.currentTotalRecoveryTime(muscleId)
        muscleRepository.setTotalRecoveryTime(muscleId, newTotalRecoveryTime)
        expectedRecoveryRepository.adjustRecovery(muscleId, currentTotalRecoveryTime, newTotalRecoveryTime)
    }
}
