package com.promni.mft.domain.usecase

import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMuscleInfoUseCase(
    private val muscleRepository: MuscleRepository,
) {
    operator fun invoke(): Flow<List<MuscleInfo>> = muscleRepository.observeMuscles()
        .map {
            it.sortedWith(compareBy<MuscleInfo> {
                if (it.expectedRecovery == 0L) Long.MAX_VALUE else it.expectedRecovery
            }.thenBy { it.muscle.name })
        }
}
