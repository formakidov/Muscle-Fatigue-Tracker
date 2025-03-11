package com.promni.mft.domain

import com.promni.mft.data.local.entities.MuscleId

class MuscleNotFoundException(id: MuscleId) : RuntimeException("Muscle '$id' not found.")
