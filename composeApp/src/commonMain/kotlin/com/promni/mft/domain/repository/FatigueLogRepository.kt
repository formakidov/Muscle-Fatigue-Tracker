package com.promni.mft.domain.repository

import com.promni.mft.domain.model.FatigueLog
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface FatigueLogRepository {
    fun getFatigueLogsForMuscle(muscleId: Long): Flow<List<FatigueLog>>
    suspend fun getLatestLogForMuscle(muscleId: Long): FatigueLog?
    suspend fun addFatigueLog(muscleId: Long, value: Float)
    suspend fun deleteLog(muscleId: Long, date: LocalDate)
}
