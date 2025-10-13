package com.promni.mft.data.repository

import com.promni.mft.data.local.dao.FatigueLogDao
import com.promni.mft.data.local.entities.FatigueLogEntity
import com.promni.mft.data.local.entities.asExternalModel
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.util.SystemTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus

class FatigueLogRepositoryImpl(
    private val fatigueLogDao: FatigueLogDao
) : FatigueLogRepository {

    override fun getFatigueLogsForMuscle(muscleId: Long): Flow<List<FatigueLog>> =
        fatigueLogDao.getLogsForMuscle(muscleId).map { it.map(FatigueLogEntity::asExternalModel) }

    override suspend fun getLatestLogForMuscle(muscleId: Long): FatigueLog? =
        fatigueLogDao.getLatestLogForMuscle(muscleId)?.asExternalModel()

    override suspend fun addFatigueLog(muscleId: Long, value: Float) {
        fatigueLogDao.insertFatigueLog(
            FatigueLogEntity(muscleId = muscleId, value = value, timestamp = SystemTime.nowMillis())
        )
    }

    override suspend fun deleteLog(muscleId: Long, date: LocalDate) {
        val zone = TimeZone.currentSystemDefault()
        val startOfDay = date.atStartOfDayIn(zone).toEpochMilliseconds()
        val endOfDay = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(zone).toEpochMilliseconds()
        fatigueLogDao.deleteLogsForMuscleInRange(muscleId, startOfDay, endOfDay)
    }
}
