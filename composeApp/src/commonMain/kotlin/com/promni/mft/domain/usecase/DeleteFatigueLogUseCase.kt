package com.promni.mft.domain.usecase

import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.domain.repository.FatigueLogRepository
import kotlinx.datetime.LocalDate

class DeleteFatigueLogUseCase(
    private val fatigueLogRepository: FatigueLogRepository
) {
    suspend operator fun invoke(muscleId: MuscleId, date: LocalDate) {
//        fatigueLogRepository.deleteLog(muscleId, date)
        // todo delete log, then update current fatigue for muscle
    }
}
