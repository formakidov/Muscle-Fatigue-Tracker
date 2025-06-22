package com.promni.mft.domain.util

import com.promni.mft.domain.model.MuscleInfo

class MuscleInfoSorter {
    fun sort(muscles: List<MuscleInfo>) = muscles.sortedWith(compareBy<MuscleInfo> {
        if (it.expectedRecovery <= SystemTime.nowMillis()) Long.MAX_VALUE else it.expectedRecovery
    }.thenBy { it.muscle.name })
}
