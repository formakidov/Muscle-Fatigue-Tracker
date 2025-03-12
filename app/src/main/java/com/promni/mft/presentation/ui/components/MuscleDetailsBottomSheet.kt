package com.promni.mft.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.model.MuscleInfo
import java.util.Date
import java.util.Locale


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

            FatigueLogs(logs)
        }
    }
}

@Composable
private fun OverviewInfo(muscleInfo: MuscleInfo) {
    Text(text = muscleInfo.muscle.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(text = "Current Fatigue: ${muscleInfo.fatigue.toInt()}%", fontSize = 18.sp)
    Text(
        text = "Full Recovery in: ${muscleInfo.totalRecoveryTime / (24 * 60 * 60 * 1000)} days",
        fontSize = 16.sp
    )
}

@Composable
private fun FatigueLogs(logs: List<FatigueLog>) {
    Text(text = "Fatigue Log", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    if (logs.isEmpty()) {
        Text("No logs yet")
    } else {
        LazyColumn {
            items(logs.count()) { index ->
                val log = logs[index]
                Text(
                    text = "${String.format(Locale.getDefault(), "%+d", log.changeAmount.toInt())}% on ${
                        Date(
                            log.timestamp
                        )
                    }",
                    fontSize = 14.sp
                )
            }
        }
    }
}
