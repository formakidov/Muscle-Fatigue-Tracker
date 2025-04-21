package com.promni.mft.domain

import com.promni.mft.domain.util.MuscleId

class MuscleNotFoundException(id: MuscleId) : RuntimeException("Muscle '$id' not found.")
