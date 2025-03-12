package com.promni.mft.domain.usecase

import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.util.MuscleInfoSorter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMuscleInfoUseCase(
    private val muscleRepository: MuscleRepository,
    private val muscleInfoSorter: MuscleInfoSorter
) {
    operator fun invoke(): Flow<List<MuscleInfo>> = muscleRepository.observeMuscles().map { muscleInfoSorter.sort(it) }
}
