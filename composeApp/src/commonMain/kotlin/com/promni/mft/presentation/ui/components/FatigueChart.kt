package com.promni.mft.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.presentation.ui.theme.AppTheme
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun FatigueChart(modifier: Modifier = Modifier, logs: List<FatigueLog>) {
    if (logs.isEmpty()) {
        return
    }

    var selectedDate by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    val startOfWeek = selectedDate.minus(selectedDate.dayOfWeek.ordinal, DateTimeUnit.DAY)
    val endOfWeek = startOfWeek.plus(6, DateTimeUnit.DAY)

    val fatigueByDay = logs.groupBy {
        Instant.fromEpochMilliseconds(it.timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
    }.mapValues { (_, logs) ->
        logs.maxByOrNull { it.value }?.value ?: 0f
    }

    val weekDates = (0..6).map { startOfWeek.plus(it, DateTimeUnit.DAY) }
    val categories = weekDates.map {
        val dayOfWeek = it.dayOfWeek.name.substring(0, 3)
        val date = "${it.dayOfMonth.toString().padStart(2, '0')}.${it.monthNumber.toString().padStart(2, '0')}"
        "$dayOfWeek\n$date"
    }
    val data = weekDates.map { fatigueByDay[it] ?: 0f }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { selectedDate = selectedDate.minus(7, DateTimeUnit.DAY) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Week")
            }
            Text(
                text = "${startOfWeek.dayOfMonth}.${startOfWeek.monthNumber} - ${endOfWeek.dayOfMonth}.${endOfWeek.monthNumber}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = { selectedDate = selectedDate.plus(7, DateTimeUnit.DAY) },
                enabled = endOfWeek < today
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Week")
            }
        }

        XYGraph(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            xAxisModel = CategoryAxisModel(categories),
            yAxisModel = rememberFloatLinearAxisModel(
                range = 0f..100f,
                minorTickCount = 0
            ),
        ) {
            VerticalBarPlot(
                xData = categories,
                yData = data,
                bar = { index ->
                    val fatigueValue = data[index]
                    if (fatigueValue > 0f) {
                        val (darkerColor, lighterColor) = getFatigueColors(fatigueValue)
                        DefaultVerticalBar(
                            color = lighterColor,
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                            border = BorderStroke(1.dp, darkerColor),
                        )
                    }
                }
            )
        }
    }
}


private fun getFatigueColors(fatigue: Float): Pair<Color, Color> {
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

    return colorStart to colorEnd
}

@Composable
private fun ThemedFatigueChartPreview(darkTheme: Boolean, logs: List<FatigueLog>) {
    AppTheme(darkTheme = darkTheme, dynamicColor = false) {
        Surface {
            FatigueChart(
                logs = logs
            )
        }
    }
}

@Preview(name = "Empty Light")
@Composable
private fun FatigueChartEmptyPreviewLight() {
    ThemedFatigueChartPreview(darkTheme = false, logs = emptyList())
}

@Preview(name = "Empty Dark")
@Composable
private fun FatigueChartEmptyPreviewDark() {
    ThemedFatigueChartPreview(darkTheme = true, logs = emptyList())
}

@Preview(name = "Light")
@Composable
private fun FatigueChartPreviewLight() {
    val logs = listOf(
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds(), value = 20f, muscleId = 1, id = 0),
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - 86400000, value = 50f, muscleId = 1, id = 1),
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - 86400000, value = 55f, muscleId = 1, id = 2),
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - (2 * 86400000), value = 80f, muscleId = 1, id = 3)
    )
    ThemedFatigueChartPreview(darkTheme = false, logs = logs)
}

@Preview(name = "Dark")
@Composable
private fun FatigueChartPreviewDark() {
    val logs = listOf(
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds(), value = 20f, muscleId = 1, id = 0),
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - 86400000, value = 50f, muscleId = 1, id = 1),
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - 86400000, value = 55f, muscleId = 1, id = 2),
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - (2 * 86400000), value = 80f, muscleId = 1, id = 3)
    )
    ThemedFatigueChartPreview(darkTheme = true, logs = logs)
}

@Preview(name = "With Gaps Light")
@Composable
private fun FatigueChartWithGapsPreviewLight() {
    val logs = listOf(
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds(), value = 20f, muscleId = 1, id = 0),
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - (2 * 86400000), value = 80f, muscleId = 1, id = 3)
    )
    ThemedFatigueChartPreview(darkTheme = false, logs = logs)
}

@Preview(name = "With Gaps Dark")
@Composable
private fun FatigueChartWithGapsPreviewDark() {
    val logs = listOf(
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds(), value = 20f, muscleId = 1, id = 0),
        FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - (2 * 86400000), value = 80f, muscleId = 1, id = 3)
    )
    ThemedFatigueChartPreview(darkTheme = true, logs = logs)
}

@Preview(name = "Long Preview Light")
@Composable
private fun FatigueChartLongPreviewLight() {
    val logs = buildList { for (i in 1..20) add(FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - (i * 86400000L), value = (i * 5).toFloat(), muscleId = 1, id = i.toLong())) }
    ThemedFatigueChartPreview(darkTheme = false, logs = logs)
}

@Preview(name = "Long Preview Dark")
@Composable
private fun FatigueChartLongPreviewDark() {
    val logs = buildList { for (i in 1..20) add(FatigueLog(timestamp = Clock.System.now().toEpochMilliseconds() - (i * 86400000L), value = (i * 5).toFloat(), muscleId = 1, id = i.toLong())) }
    ThemedFatigueChartPreview(darkTheme = true, logs = logs)
}
