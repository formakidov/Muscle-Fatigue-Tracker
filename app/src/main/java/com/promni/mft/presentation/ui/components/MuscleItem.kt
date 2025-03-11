package com.promni.mft.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promni.mft.domain.model.MuscleInfo
import java.util.concurrent.TimeUnit

@Composable
fun MuscleItem(
    muscleInfo: MuscleInfo,
    onClick: () -> Unit,
    onIncreaseFatigue: (MuscleInfo, Float) -> Unit
) {
    val backgroundGradient = getFatigueGradient(muscleInfo.fatigue)

    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .heightIn(min = 70.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
            .background(brush = backgroundGradient, shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(24.dp)
            .animateContentSize(),
    ) {
        Column {
            Text(
                text = muscleInfo.muscle.name,
                color = Color.Companion.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Companion.Bold,
            )

            if (muscleInfo.fatigue > 0 && muscleInfo.expectedRecovery > 0) {
                Spacer(modifier = Modifier.Companion.height(12.dp))
                Text(
                    text = "Recovered in: ${formatRemainingTime(muscleInfo.expectedRecovery)}",
                    color = Color.Companion.White.copy(alpha = 0.9f),
                    fontSize = 18.sp
                )
            }
        }
    }
}

private fun formatRemainingTime(expectedRecoveryTimestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val remainingMillis = (expectedRecoveryTimestamp - currentTime).coerceAtLeast(0)

    val days = TimeUnit.MILLISECONDS.toDays(remainingMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis) % 24

    return when {
        remainingMillis < TimeUnit.HOURS.toMillis(1) -> "less than an hour"
        days > 0 -> "$days days $hours hours"
        else -> "$hours hours"
    }
}

private fun getFatigueGradient(fatigue: Float): Brush {
    val colorStart: Color
    val colorEnd: Color

    when {
        fatigue <= 33 -> {
            colorStart = Color(0xFF1B5E20)
            colorEnd = Color(0xFF66BB6A)
        }
        fatigue <= 66 -> {
            colorStart = Color(0xFFF9A825)
            colorEnd = Color(0xFFFFF176)
        }
        else -> { // Red range
            colorStart = Color(0xFFC62828)
            colorEnd = Color(0xFFFF5252)
        }
    }

    return Brush.linearGradient(
        colors = listOf(colorStart, colorEnd),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(800f, 400f), // Diagonal direction
        tileMode = TileMode.Mirror
    )
}
