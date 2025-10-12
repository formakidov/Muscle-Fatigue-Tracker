package com.promni.mft.domain.model

import com.promni.mft.annotations.AllOpen
import com.promni.mft.domain.util.MuscleId

@AllOpen
class Muscle(
    val id: MuscleId,
    val name: String
)
