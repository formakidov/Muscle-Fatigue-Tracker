package com.promni.mft.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.promni.mft.domain.model.Muscle


@Entity(tableName = "muscles")
data class MuscleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stringId: String,
    val name: String,
    val order: Int,
    val totalRecoveryMillis: Long,
)

fun MuscleEntity.asExternalModel() = Muscle(id, name, order)
