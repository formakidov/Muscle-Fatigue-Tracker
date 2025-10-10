package com.promni.mft.domain.usecase

import com.promni.mft.domain.repository.MuscleFilter
import com.promni.mft.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow

class GetMuscleFilterUseCase(
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(): Flow<MuscleFilter> = userDataRepository.muscleFilter
}
