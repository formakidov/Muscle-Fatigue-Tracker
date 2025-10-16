package com.promni.mft.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.domain.util.SystemTime
import com.promni.mft.muscleImageMap
import com.promni.mft.presentation.ui.theme.AppTheme
import com.promni.mft.presentation.ui.utils.adjustBrightness
import com.promni.mft.presentation.ui.utils.muscleAbsNotTrained
import com.promni.mft.presentation.ui.utils.muscleBicepsEasyTrained
import com.promni.mft.presentation.ui.utils.muscleQuadricepsHardTrained
import com.promni.mft.presentation.ui.utils.muscleTricepsMiddleTrained
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MuscleItem(
    modifier: Modifier = Modifier,
    muscleInfo: MuscleInfo,
    onClick: () -> Unit
) {
    val fatigue = muscleInfo.fatigue
    val backgroundColor = getBgColor(baseColor = MaterialTheme.colorScheme.primaryContainer, fatigue)
    val textColor = getTextColor(baseColor = MaterialTheme.colorScheme.onPrimaryContainer, fatigue)
    val imageAlpha = getImageAlpha(fatigue)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(color = backgroundColor)
            .clickable { onClick() }
            .padding(12.dp)
            .animateContentSize(),
    ) {
        Row(verticalAlignment = CenterVertically) {
            muscleImageMap[muscleInfo.muscle.name]?.let {
                Image(
                    painter = painterResource(it),
                    modifier = Modifier.size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surface)
                        .alpha(imageAlpha)
                        .padding(4.dp),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column {
                Text(
                    text = muscleInfo.muscle.name,
                    color = textColor,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )

                if (muscleInfo.fatigue > 0 && muscleInfo.expectedRecovery > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recovery in: ${formatRemainingTime(muscleInfo.expectedRecovery)}",
                        color = textColor,
                        fontSize = 16.sp
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

@Composable
private fun getBgColor(baseColor: Color, fatigue: Float, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    val factor = when {
        fatigue in 0f..10f -> if (isDarkTheme) 1.0f else 0.8f  // Not tired or very lightly tired
        fatigue in 11f..33f -> if (isDarkTheme) 0.9f else 0.9f // A bit tired
        fatigue in 34f..70f -> if (isDarkTheme) 0.5f else 1.0f // Moderately tired
        fatigue > 70f -> if (isDarkTheme) 0.3f else 1.05f       // Very tired
        else -> 1.0f
    }

    return baseColor.adjustBrightness(factor)
}

@Composable
private fun getTextColor(baseColor: Color, fatigue: Float, isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
    val alpha = when {
        fatigue in 0f..10f -> if (isDarkTheme) 1.0f else 1.0f  // Not tired or very lightly tired
        fatigue in 11f..33f -> if (isDarkTheme) 1.0f else 1.0f // A bit tired
        fatigue in 34f..70f -> if (isDarkTheme) 0.7f else 0.8f // Moderately tired
        fatigue > 70f -> if (isDarkTheme) 0.6f else 0.7f       // Very tired
        else -> 1.0f
    }

    return baseColor.copy(alpha = alpha)
}

@Composable
private fun getImageAlpha(fatigue: Float, isDarkTheme: Boolean = isSystemInDarkTheme()): Float {
    val alpha = when {
        fatigue in 0f..10f -> if (isDarkTheme) 1.0f else 1.0f  // Not tired or very lightly tired
        fatigue in 11f..33f -> if (isDarkTheme) 1.0f else 1.0f // A bit tired
        fatigue in 34f..70f -> if (isDarkTheme) 0.7f else 0.8f // Moderately tired
        fatigue > 70f -> if (isDarkTheme) 0.6f else 0.7f       // Very tired
        else -> 1.0f
    }

    return alpha
}

@Composable
private fun ThemedMuscleItemPreview(darkTheme: Boolean, muscleInfo: MuscleInfo) {
    AppTheme(darkTheme = darkTheme, dynamicColor = false) {
        Surface {
            Column(modifier = Modifier.padding(8.dp)) {
                MuscleItem(muscleInfo = muscleInfo) {}
            }
        }
    }
}

@Preview(name = "Abs Light (Not trained)")
@Composable
private fun MuscleItemPreviewAbsLight() = ThemedMuscleItemPreview(darkTheme = false, muscleInfo = muscleAbsNotTrained)

@Preview(name = "Abs Dark (Not trained)")
@Composable
private fun MuscleItemPreviewAbsDark() = ThemedMuscleItemPreview(darkTheme = true, muscleInfo = muscleAbsNotTrained)

@Preview(name = "Biceps Light (Easy trained)")
@Composable
private fun MuscleItemPreviewBicepsLight() = ThemedMuscleItemPreview(darkTheme = false, muscleInfo = muscleBicepsEasyTrained)

@Preview(name = "Biceps Dark (Easy trained)")
@Composable
private fun MuscleItemPreviewBicepsDark() = ThemedMuscleItemPreview(darkTheme = true, muscleInfo = muscleBicepsEasyTrained)

@Preview(name = "Triceps Light (Middle trained)")
@Composable
private fun MuscleItemPreviewTricepsLight() = ThemedMuscleItemPreview(darkTheme = false, muscleInfo = muscleTricepsMiddleTrained)

@Preview(name = "Triceps Dark (Middle trained)")
@Composable
private fun MuscleItemPreviewTricepsDark() = ThemedMuscleItemPreview(darkTheme = true, muscleInfo = muscleTricepsMiddleTrained)

@Preview(name = "Quadriceps Light (Hard trained)")
@Composable
private fun MuscleItemPreviewQuadricepsLight() = ThemedMuscleItemPreview(darkTheme = false, muscleInfo = muscleQuadricepsHardTrained)

@Preview(name = "Quadriceps Dark (Hard trained)")
@Composable
private fun MuscleItemPreviewQuadricepsDark() = ThemedMuscleItemPreview(darkTheme = true, muscleInfo = muscleQuadricepsHardTrained)
