package com.promni.mft.domain.usecase

import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.util.RecoveryCalculator
import kotlinx.datetime.LocalDate

class DeleteFatigueLogUseCase(
    private val fatigueLogRepository: FatigueLogRepository,
    private val expectedRecoveryRepository: ExpectedRecoveryRepository,
    private val muscleRepository: MuscleRepository,
) {
    suspend operator fun invoke(muscleId: Long, date: LocalDate) {
        fatigueLogRepository.deleteLog(muscleId, date)

        val latestLog = fatigueLogRepository.getLatestLogForMuscle(muscleId)
        if (latestLog != null) {
            val totalRecoveryTime = muscleRepository.currentTotalRecoveryTime(muscleId)
            val newExpectedRecoveryTime = RecoveryCalculator.calculateExpectedRecoveryFromLog(
                log = latestLog,
                totalRecoveryTime = totalRecoveryTime
            )
            expectedRecoveryRepository.setExpectedRecovery(muscleId, newExpectedRecoveryTime)
        } else {
            expectedRecoveryRepository.clearExpectedRecovery(muscleId)
        }
    }
}
