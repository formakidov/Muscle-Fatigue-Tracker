package com.promni.mft.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.presentation.ui.theme.AppTheme
import com.promni.mft.presentation.ui.utils.fatigueLogs
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun FatigueChart(
    modifier: Modifier = Modifier,
    logs: List<FatigueLog>,
    onBarClick: (LocalDate, Offset) -> Unit = { _, _ -> }
) {
    if (logs.isEmpty()) {
        Text(
            text = "Add a fatigue record to see your progress on the chart.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        logs.maxByOrNull { it.timestamp }?.value ?: 0f
    }

    val weekDates = (0..6).map { startOfWeek.plus(it, DateTimeUnit.DAY) }
    val categories = weekDates.map {
        val dayOfWeek = it.dayOfWeek.name.substring(0, 3)
        "$dayOfWeek\n${it.dayOfMonth}"
    }
    val data = weekDates.map { fatigueByDay[it] ?: 0f }

    Column(modifier = modifier) {
        WeekNavigator(
            startOfWeek = startOfWeek,
            endOfWeek = endOfWeek,
            onPreviousWeek = { selectedDate = selectedDate.minus(7, DateTimeUnit.DAY) },
            onNextWeek = { selectedDate = selectedDate.plus(7, DateTimeUnit.DAY) },
            isNextWeekEnabled = endOfWeek < today
        )

        WeeklyFatigueGraph(
            categories = categories,
            data = data,
            weekDates = weekDates,
            onBarClick = onBarClick
        )
    }
}

@Composable
private fun WeekNavigator(
    modifier: Modifier = Modifier,
    startOfWeek: LocalDate,
    endOfWeek: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    isNextWeekEnabled: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Week")
        }
        Text(
            text = "${startOfWeek.dayOfMonth}.${startOfWeek.monthNumber} - ${endOfWeek.dayOfMonth}.${endOfWeek.monthNumber}",
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(
            onClick = onNextWeek,
            enabled = isNextWeekEnabled
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Week")
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun WeeklyFatigueGraph(
    modifier: Modifier = Modifier,
    categories: List<String>,
    data: List<Float>,
    weekDates: List<LocalDate>,
    onBarClick: (LocalDate, Offset) -> Unit
) {
    XYGraph(
        modifier = modifier
            .fillMaxWidth(),
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
                    val date = weekDates[index]
                    val (darkerColor, lighterColor) = getFatigueColors(fatigueValue)
                    DefaultVerticalBar(
                        modifier = Modifier.pointerInput(date) {
                            detectTapGestures { offset ->
                                onBarClick(date, offset)
                            }
                        },
                        brush = Brush.verticalGradient(listOf(lighterColor, darkerColor)),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        border = BorderStroke(1.dp, darkerColor),
                    )
                }
            }
        )
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
                logs = logs,
                onBarClick = { _, _ -> }
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
    ThemedFatigueChartPreview(darkTheme = false, logs = fatigueLogs)
}

@Preview(name = "Dark")
@Composable
private fun FatigueChartPreviewDark() {
    ThemedFatigueChartPreview(darkTheme = true, logs = fatigueLogs)
}

@Preview(name = "With Gaps Light")
@Composable
private fun FatigueChartWithGapsPreviewLight() {
    ThemedFatigueChartPreview(darkTheme = false, logs = fatigueLogs)
}

@Preview(name = "With Gaps Dark")
@Composable
private fun FatigueChartWithGapsPreviewDark() {
    ThemedFatigueChartPreview(darkTheme = true, logs = fatigueLogs)
}

@Preview(name = "Long Preview Light")
@Composable
private fun FatigueChartLongPreviewLight() {
    ThemedFatigueChartPreview(darkTheme = false, logs = fatigueLogs)
}

@Preview(name = "Long Preview Dark")
@Composable
private fun FatigueChartLongPreviewDark() {
    ThemedFatigueChartPreview(darkTheme = true, logs = fatigueLogs)
}
