package com.promni.mft.domain.usecase

import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleRepository
import kotlinx.coroutines.flow.Flow

class GetMuscleByIdUseCase(
    private val muscleRepository: MuscleRepository,
) {
    operator fun invoke(muscleId: Long): Flow<MuscleInfo> =
        muscleRepository.observeMuscle(id = muscleId)
}
