package com.promni.mft.domain.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface Time {
    fun now(): Instant
    fun nowMillis(): Long
}

object SystemTime : Time {
    override fun now(): Instant = Clock.System.now()
    override fun nowMillis(): Long = now().toEpochMilliseconds()
}
