package com.promni.mft.domain.usecase

import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleFilter
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.repository.UserDataRepository
import com.promni.mft.domain.util.MuscleInfoSorter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetMusclesInfoUseCase(
    private val muscleRepository: MuscleRepository,
    private val userDataRepository: UserDataRepository,
    private val muscleInfoSorter: MuscleInfoSorter
) {
    operator fun invoke(): Flow<List<MuscleInfo>> = combine(
        muscleRepository.observeMuscles(),
        userDataRepository.muscleFilter
    ) { muscles, filter ->
        val filteredMuscles = when (filter) {
            MuscleFilter.ALL -> muscles
            MuscleFilter.IN_RECOVERY -> muscles.filter { it.fatigue > 0f }
            MuscleFilter.READY_TO_TRAIN -> muscles.filter { it.fatigue == 0f }
        }
        muscleInfoSorter.sort(filteredMuscles)
    }
}
