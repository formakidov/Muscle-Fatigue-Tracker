package com.promni.mft.domain


class MuscleNotFoundException(id: Long) : RuntimeException("Muscle '$id' not found.")
