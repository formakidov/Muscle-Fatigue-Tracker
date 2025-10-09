package com.promni.mft.presentation.ui.utils

import com.promni.mft.domain.model.Muscle
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.util.SystemTime

private const val dayMillis: Long = 24 * 60 * 60 * 1000
private const val fourDays: Long = 4 * dayMillis

val muscleAbsNotTrained = MuscleInfo(
    muscle = Muscle(1, "Abdomen"),
    fatigue = 0f,
    expectedRecovery = 0,
    totalRecoveryTime = fourDays
)
val muscleBicepsEasyTrained = MuscleInfo(
    muscle = Muscle(2, "Biceps"),
    fatigue = 25f,
    expectedRecovery = SystemTime.nowMillis() + dayMillis,
    totalRecoveryTime = fourDays
)
val muscleTricepsMiddleTrained = MuscleInfo(
    muscle = Muscle(3, "Triceps"),
    fatigue = 50f,
    expectedRecovery = SystemTime.nowMillis() + dayMillis * 2,
    totalRecoveryTime = fourDays
)
val muscleQuadricepsHardTrained = MuscleInfo(
    muscle = Muscle(4, "Quadriceps"),
    fatigue = 75f,
    expectedRecovery = SystemTime.nowMillis() + fourDays,
    totalRecoveryTime = fourDays
)

val allMuscles = listOf(muscleAbsNotTrained, muscleBicepsEasyTrained, muscleTricepsMiddleTrained, muscleQuadricepsHardTrained)
