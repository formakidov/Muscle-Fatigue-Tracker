@file:OptIn(ExperimentalMaterial3Api::class)

package com.promni.mft.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.promni.mft.domain.model.FatigueLog
import com.promni.mft.domain.model.MuscleInfo
import com.promni.mft.presentation.ui.theme.AppTheme
import com.promni.mft.presentation.ui.utils.DevicePreviews
import com.promni.mft.presentation.ui.utils.fatigueLogs
import com.promni.mft.presentation.ui.utils.muscleTricepsMiddleTrained
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleDetailsBottomSheet(
    muscleInfo: MuscleInfo,
    sheetState: SheetState,
    logs: List<FatigueLog>,
    onDismiss: () -> Unit,
    onFatigueChanged: (MuscleInfo, newValue: Float) -> Unit,
    onRecoveryPeriodChanged: (MuscleInfo, Int) -> Unit,
    onDeleteLog: (LocalDate) -> Unit,
    onUpdateLog: (LocalDate) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        MuscleDetailsContent(
            muscleInfo = muscleInfo,
            logs = logs,
            onFatigueChanged = { fatigue -> onFatigueChanged(muscleInfo, fatigue) },
            onRecoveryPeriodChanged = { days -> onRecoveryPeriodChanged(muscleInfo, days) },
            onDeleteLog = onDeleteLog,
            onUpdateLog = onUpdateLog
        )
    }
}

@Composable
private fun MuscleDetailsContent(
    muscleInfo: MuscleInfo,
    logs: List<FatigueLog>,
    onFatigueChanged: (Float) -> Unit,
    onRecoveryPeriodChanged: (Int) -> Unit,
    onDeleteLog: (LocalDate) -> Unit,
    onUpdateLog: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp)) {

        Text(text = muscleInfo.muscle.name, style = MaterialTheme.typography.headlineLarge)

        EditableRecoveryPeriod(
            modifier = Modifier.padding(vertical = 8.dp),
            muscleInfo = muscleInfo
        ) { days -> onRecoveryPeriodChanged(days) }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Slide to set how tired your muscle is right now:", style = MaterialTheme.typography.bodyMedium)

        // todo if record for today exists: ask if to update todays record or add new
        var fatigueSliderValue by remember { mutableFloatStateOf(muscleInfo.fatigue / 100f) }
        Slider(
            value = fatigueSliderValue,
            onValueChange = { fatigueSliderValue = it },
            valueRange = 0f..1f,
            onValueChangeFinished = { onFatigueChanged(fatigueSliderValue * 100) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            var expanded by remember { mutableStateOf(false) }
            var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
            var barClickOffset by remember { mutableStateOf(Offset.Zero) }

            FatigueChart(
                modifier = Modifier
                    .height(300.dp),
                logs = logs,
                onBarClick = { date, offset ->
                    barClickOffset = offset
                    selectedDate = date
                    expanded = true
                }
            )

            FatigueLogDropDownMenu(
                expanded = expanded,
                selectedDate = selectedDate,
                onDismiss = { expanded = false },
                onUpdateLog = onUpdateLog,
                onDeleteLog = onDeleteLog,
                offset = DpOffset(barClickOffset.x.dp, barClickOffset.y.dp)
            )
        }
    }
}

@Composable
private fun FatigueLogDropDownMenu(
    expanded: Boolean,
    selectedDate: LocalDate?,
    onDismiss: () -> Unit,
    offset: DpOffset,
    onUpdateLog: (LocalDate) -> Unit,
    onDeleteLog: (LocalDate) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        offset = offset,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Update") },
            onClick = {
                selectedDate?.let(onUpdateLog)
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text("Delete") },
            onClick = {
                selectedDate?.let(onDeleteLog)
                onDismiss()
            }
        )
    }
}

@Composable
private fun ThemedMuscleDetailsBottomSheetPreview(
    darkTheme: Boolean,
    skipPartiallyExpanded: Boolean,
) {
    AppTheme(darkTheme = darkTheme, dynamicColor = false) {
        MuscleDetailsBottomSheet(
            muscleInfo = muscleTricepsMiddleTrained,
            logs = fatigueLogs,
            onDismiss = {},
            onFatigueChanged = { _, _ -> },
            onRecoveryPeriodChanged = { _, _ -> },
            onDeleteLog = {},
            onUpdateLog = {},
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = skipPartiallyExpanded
            ),
        )
    }
}

@DevicePreviews
@Composable
private fun MuscleDetailsBottomSheetExpandedDarkPreview() =
    ThemedMuscleDetailsBottomSheetPreview(darkTheme = true, skipPartiallyExpanded = false)

@DevicePreviews
@Composable
private fun MuscleDetailsBottomSheetExpandedLightPreview() =
    ThemedMuscleDetailsBottomSheetPreview(darkTheme = false, skipPartiallyExpanded = false)


@DevicePreviews
@Composable
private fun MuscleDetailsBottomSheetCollapsedDarkPreview() =
    ThemedMuscleDetailsBottomSheetPreview(darkTheme = true, skipPartiallyExpanded = true)

@DevicePreviews
@Composable
private fun MuscleDetailsBottomSheetCollapsedLightPreview() =
    ThemedMuscleDetailsBottomSheetPreview(darkTheme = false, skipPartiallyExpanded = true)
