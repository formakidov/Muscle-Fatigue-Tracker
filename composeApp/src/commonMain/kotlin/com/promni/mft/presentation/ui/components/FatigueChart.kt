package com.promni.mft.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import kotlinx.datetime.daysUntil
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

    val fatigueByDay = logs.groupBy {
        Instant.fromEpochMilliseconds(it.timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
    }.mapValues { (_, logs) ->
        logs.maxByOrNull { it.value }?.value ?: 0f
    }

    val minDate = fatigueByDay.keys.minOrNull() ?: return
    val lastLogDate = fatigueByDay.keys.maxOrNull() ?: return
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val maxDate = if (lastLogDate > today) lastLogDate else today
    val days = minDate.daysUntil(maxDate)

    val allDates = (0..days).map { minDate.plus(it, DateTimeUnit.DAY) }

    val categories = allDates.map { "${it.month.name.substring(0, 3)} ${it.dayOfMonth}" }.reversed()
    val data = allDates.map { fatigueByDay[it] ?: 0f }.reversed()

    val scrollState = rememberScrollState()

    XYGraph(
        modifier = modifier.horizontalScroll(scrollState)
            .width((categories.size * 56).dp),
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
                        border = BorderStroke(1.dp, darkerColor)
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

@Preview
@Composable
private fun FatigueChartEmptyPreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        FatigueChart(
            logs = emptyList()
        )
    }
}

@Preview
@Composable
private fun FatigueChartPreview() {
    val logs = listOf(
        FatigueLog(timestamp = 1704067200000L, value = 20f, muscleId = 1, id = 0), // 2024-01-01
        FatigueLog(timestamp = 1704153600000L, value = 50f, muscleId = 1, id = 1), // 2024-01-02
        FatigueLog(timestamp = 1704153700000L, value = 55f, muscleId = 1, id = 2), // 2024-01-02 (max is 60)
        FatigueLog(timestamp = 1704240000000L, value = 80f, muscleId = 1, id = 3)  // 2024-01-03
    )
    AppTheme(darkTheme = true, dynamicColor = false) {
        FatigueChart(
            logs = logs
        )
    }
}

@Preview
@Composable
private fun FatigueChartWithGapsPreview() {
    val logs = listOf(
        FatigueLog(timestamp = 1704067200000L, value = 20f, muscleId = 1, id = 0), // 2024-01-01
        FatigueLog(timestamp = 1704240000000L, value = 80f, muscleId = 1, id = 3)  // 2024-01-03
    )
    AppTheme(darkTheme = true, dynamicColor = false) {
        FatigueChart(
            logs = logs
        )
    }
}

@Preview
@Composable
private fun FatigueChartLongPreview() {
    val logs = buildList {
        for (i in 1..20) {
            add(
                FatigueLog(
                    timestamp = 1704067200000L + (i * 86400000L),
                    value = (i * 5).toFloat(),
                    muscleId = 1,
                    id = i.toLong()
                )
            )
        }
    }
    AppTheme(darkTheme = true, dynamicColor = false) {
        FatigueChart(
            logs = logs
        )
    }
}
