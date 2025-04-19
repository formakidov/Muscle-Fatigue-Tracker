package com.promni.mft.data.repository

import com.promni.mft.MuscleNotFoundException
import com.promni.mft.data.local.dao.ExpectedRecoveryDao
import com.promni.mft.data.local.dao.MuscleDao
import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.MuscleEntity
import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.data.local.entities.asExternalModel
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.util.RecoveryCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MuscleRepositoryImpl(
    private val muscleDao: MuscleDao,
    private val expectedRecoveryDao: ExpectedRecoveryDao
) : MuscleRepository {

    override fun observeMuscles(): Flow<List<MuscleInfo>> {
        val muscles: Flow<List<MuscleEntity>> = muscleDao.all()
        val expectedRecoveries = expectedRecoveryDao.all()
            .map { entities -> entities.associateBy(ExpectedRecoveryEntity::muscleId) }

        return combine(muscles, expectedRecoveries) { muscles, expectedRecoveries ->
            muscles.map { muscle ->
                buildMuscleInfo(muscle, expectedRecoveries[muscle.id])
            }
        }
    }

    override suspend fun setTotalRecoveryTime(muscleId: MuscleId, newTotalRecovery: Long) {
        val muscle = muscleDao.item(muscleId) ?: throw MuscleNotFoundException(muscleId)
        muscleDao.upsert(muscle.copy(totalRecoveryMillis = newTotalRecovery))
    }

    override suspend fun currentTotalRecoveryTime(id: MuscleId) =
        muscleDao.item(id)?.totalRecovery ?: throw MuscleNotFoundException(id)

    private fun buildMuscleInfo(
        muscle: MuscleEntity, expectedRecovery: ExpectedRecoveryEntity?
    ) = MuscleInfo(
        muscle = muscle.asExternalModel(),
        fatigue = RecoveryCalculator.calculateCurrentFatigue(expectedRecovery?.timestamp, muscle.totalRecovery),
        expectedRecovery = expectedRecovery?.timestamp ?: 0L,
        totalRecoveryTime = muscle.totalRecovery
    )
}
