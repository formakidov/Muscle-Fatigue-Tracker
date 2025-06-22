package com.promni.mft.presentation.ui.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

fun formatMillisToLocalDate(millis: Long, timezone: TimeZone = TimeZone.currentSystemDefault()): String {
    val customFormat = LocalDateTime.Format {
        dayOfMonth(); char('.'); monthNumber(); char('.'); year(); char(' '); hour(); char(':'); minute()
    }
    val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timezone)
    return dateTime.format(customFormat)
}
