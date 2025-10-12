package com.promni.mft.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.promni.mft.domain.model.MuscleInfo

@Composable
fun EditableRecoveryPeriod(
    modifier: Modifier = Modifier,
    muscleInfo: MuscleInfo,
    onRecoveryPeriodChanged: (Int) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var recoveryPeriodInput by remember { mutableStateOf((muscleInfo.totalRecoveryTime / (24 * 60 * 60 * 1000)).toString()) }
    val initialRecoveryPeriodDays = (muscleInfo.totalRecoveryTime / (24 * 60 * 60 * 1000)).toString()

    val isInputValid = recoveryPeriodInput.toIntOrNull() in 1..60

    if (isEditing) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = recoveryPeriodInput,
                onValueChange = { recoveryPeriodInput = it },
                label = { Text("Recovery (days)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Button(
                modifier = Modifier.padding(start = 8.dp),
                onClick = {
                    if (isInputValid) {
                        val newRecoveryPeriod = recoveryPeriodInput.toInt()
                        if (recoveryPeriodInput != initialRecoveryPeriodDays) {
                            onRecoveryPeriodChanged(newRecoveryPeriod)
                        }
                        isEditing = false
                    }
                },
                enabled = isInputValid
            ) {
                Text(text = "Save")
            }
            Button(
                modifier = Modifier.padding(start = 4.dp),
                onClick = {
                    isEditing = false
                    recoveryPeriodInput = initialRecoveryPeriodDays
                }) {
                Text(text = "Cancel")
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f, fill = true),
                text = "Recovery Period: ${muscleInfo.totalRecoveryTime / (24 * 60 * 60 * 1000)} days",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.padding(start = 8.dp))
            FilledTonalButton(
                modifier = Modifier.widthIn(min = 90.dp),
                onClick = { isEditing = true }
            ) {
                Text(text = "Change")
            }
        }
    }
}
