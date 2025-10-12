package com.promni.mft.domain.repository

import kotlinx.coroutines.flow.Flow

enum class MuscleFilter {
    ALL,
    IN_RECOVERY,
    READY_TO_TRAIN
}

interface UserDataRepository {
    val muscleFilter: Flow<MuscleFilter>

    suspend fun setMuscleFilter(filter: MuscleFilter)
}
