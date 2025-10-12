package com.promni.mft.domain.usecase

import com.promni.mft.domain.repository.MuscleFilter
import com.promni.mft.domain.repository.UserDataRepository

class SetMuscleFilterUseCase(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(filter: MuscleFilter) = userDataRepository.setMuscleFilter(filter)
}
