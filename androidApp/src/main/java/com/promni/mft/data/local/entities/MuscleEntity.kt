package com.promni.mft.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.promni.mft.domain.model.Muscle

typealias MuscleId = Long

const val DefaultTotalRecoveryTime = 4 * 24 * 60 * 60 * 1000L // 4 days

@Entity(tableName = "muscles")
data class MuscleEntity(
    @PrimaryKey(autoGenerate = true) val id: MuscleId = 0,
    val name: String,
    val totalRecoveryMillis: Long?,
) {
    val totalRecovery: Long
        get() = if (totalRecoveryMillis == null || totalRecoveryMillis == 0L) DefaultTotalRecoveryTime else totalRecoveryMillis
}

fun MuscleEntity.asExternalModel() = Muscle(id, name)
