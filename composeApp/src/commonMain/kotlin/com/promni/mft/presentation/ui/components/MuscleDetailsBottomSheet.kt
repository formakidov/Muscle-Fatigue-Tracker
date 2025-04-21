package com.promni.mft.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.presentation.ui.utils.formatMillisToLocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleDetailsBottomSheet(
    muscleInfo: MuscleInfo,
    logs: List<FatigueLog>,
    onDismiss: () -> Unit,
    onFatigueChanged: (MuscleInfo, newValue: Float) -> Unit,
) {

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            OverviewInfo(muscleInfo)

            Spacer(modifier = Modifier.height(16.dp))

            var fatigueSliderValue by remember { mutableFloatStateOf(muscleInfo.fatigue / 100f) }
            Slider(
                value = fatigueSliderValue,
                onValueChange = { fatigueSliderValue = it },
                valueRange = 0f..1f,
                onValueChangeFinished = { onFatigueChanged(muscleInfo, fatigueSliderValue * 100) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            var isExpanded by rememberSaveable { mutableStateOf(false) }
            ExpandableContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                isExpanded = isExpanded,
                onExpandedChange = { isExpanded = !isExpanded },
                expandedContent = {
                    FatigueLogs(modifier = Modifier.padding(top = 16.dp), logs = logs)
                }
            ) { isExpanded ->
                FatigueLogsHeader(isExpanded = isExpanded)
            }

        }
    }
}

@Composable
private fun OverviewInfo(muscleInfo: MuscleInfo) {
    Text(text = muscleInfo.muscle.name, style = MaterialTheme.typography.headlineLarge)
    Text(text = "Fatigue: ${muscleInfo.fatigue.toInt()}%", fontSize = 18.sp)
    Text(
        text = "Total Recovery Period: ${muscleInfo.totalRecoveryTime / (24 * 60 * 60 * 1000)} days",
        fontSize = 16.sp
    )
}

@Composable
private fun FatigueLogsHeader(isExpanded: Boolean) {
    Row {
        Text(text = "Fatigue Logs", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier.size(36.dp),
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null
        )
    }

}

@Composable
private fun FatigueLogs(modifier: Modifier, logs: List<FatigueLog>) {
    if (logs.isEmpty()) {
        Text(modifier = modifier, text = "No logs yet")
    } else {
        LazyColumn(modifier.animateContentSize()) {
            items(logs.count()) { index ->
                val log = logs[index]
                Text(
                    text = "${log.value.toInt()}% on ${formatMillisToLocalDate(log.timestamp)}",
                    fontSize = 14.sp
                )
            }
        }
    }
}
