package com.promni.mft.domain.usecase

import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.repository.FatigueLogRepository
import kotlinx.coroutines.flow.Flow

class GetFatigueLogsUseCase(
    private val fatigueLogRepository: FatigueLogRepository
) {
    operator fun invoke(muscleId: MuscleId): Flow<List<FatigueLog>> =
        fatigueLogRepository.getFatigueLogsForMuscle(muscleId)
}
