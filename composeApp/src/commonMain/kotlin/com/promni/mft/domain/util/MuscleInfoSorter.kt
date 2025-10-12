package com.promni.mft.domain.util

import com.promni.mft.annotations.AllOpen
import com.promni.mft.domain.model.MuscleInfo

@AllOpen
class MuscleInfoSorter {
    fun sort(muscles: List<MuscleInfo>) = muscles.sortedWith(compareBy<MuscleInfo> {
        // Fresh muscles (recovered) should come first.
        if (it.expectedRecovery <= SystemTime.nowMillis()) Long.MIN_VALUE else it.expectedRecovery
    }.thenBy { it.muscle.name })
}
