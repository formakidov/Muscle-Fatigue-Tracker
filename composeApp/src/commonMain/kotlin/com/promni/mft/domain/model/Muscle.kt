package com.promni.mft.domain.model

import com.promni.mft.annotations.OpenForMokkery
import com.promni.mft.domain.util.MuscleId

@OpenForMokkery
class Muscle(
    val id: MuscleId,
    val name: String
)
