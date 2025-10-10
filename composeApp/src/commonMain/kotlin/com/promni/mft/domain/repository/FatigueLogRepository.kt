package com.promni.mft.domain.repository

import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.domain.model.FatigueLog
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface FatigueLogRepository {
    fun getFatigueLogsForMuscle(muscleId: MuscleId): Flow<List<FatigueLog>>
    suspend fun addFatigueLog(muscleId: MuscleId, value: Float)
    suspend fun deleteLog(muscleId: MuscleId, date: LocalDate)
}
