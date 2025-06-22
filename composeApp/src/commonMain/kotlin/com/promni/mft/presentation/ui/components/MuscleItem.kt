package com.promni.mft.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.util.SystemTime
import com.promni.mft.muscleImageMap
import com.promni.mft.presentation.ui.utils.muscleAbs
import com.promni.mft.presentation.ui.utils.muscleBiceps
import com.promni.mft.presentation.ui.utils.muscleQuadriceps
import com.promni.mft.presentation.ui.utils.muscleTriceps
import org.jetbrains.compose.resources.painterResource

@Composable
fun MuscleItem(
    muscleInfo: MuscleInfo,
    onClick: () -> Unit,
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
        Row(verticalAlignment = CenterVertically) {
            muscleImageMap[muscleInfo.muscle.name]?.let {
                Image(
                    painter = painterResource(it),
                    modifier = Modifier.size(80.dp)
                        .background(color = Color.Gray, shape = RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
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
}

private fun formatRemainingTime(expectedRecoveryTimestamp: Long): String {
    val currentTime = SystemTime.nowMillis()
    val remainingMillis = (expectedRecoveryTimestamp - currentTime).coerceAtLeast(0)
    val remainingSeconds = remainingMillis / 1000

    val days = remainingSeconds / (60 * 60 * 24)
    val hours = (remainingSeconds % (60 * 60 * 24)) / (60 * 60)

    return when {
        remainingMillis < 3600000 -> "less than an hour" // 1 hour in milliseconds
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

@Preview
@Composable
fun MuscleItemPreviewBiceps() {
    Column {
        MuscleItem(muscleInfo = muscleBiceps) {}
    }
}

@Preview
@Composable
fun MuscleItemPreviewAbs() {
    Column {
        MuscleItem(muscleInfo = muscleAbs) {}
    }
}

@Preview
@Composable
fun MuscleItemPreviewTriceps() {
    Column {
        MuscleItem(muscleInfo = muscleTriceps) {}
    }
}

@Preview
@Composable
fun MuscleItemPreviewQuadriceps() {
    Column {
        MuscleItem(muscleInfo = muscleQuadriceps) {}
    }
}
