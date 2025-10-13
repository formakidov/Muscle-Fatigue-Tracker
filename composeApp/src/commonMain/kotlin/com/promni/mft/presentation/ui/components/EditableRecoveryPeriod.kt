package com.promni.mft.presentation.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.promni.mft.presentation.ui.theme.AppTheme
import com.promni.mft.presentation.ui.utils.DevicePreviews
import com.promni.mft.presentation.ui.utils.muscleTricepsMiddleTrained

@Composable
fun EditableRecoveryPeriod(
    modifier: Modifier = Modifier,
    muscleInfo: MuscleInfo,
    onRecoveryPeriodChanged: (Int) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var recoveryPeriodInput by remember {
        mutableStateOf(
            (muscleInfo.totalRecoveryTime / (24 * 60 * 60 * 1000)).toString()
        )
    }
    val initialRecoveryPeriodDays =
        (muscleInfo.totalRecoveryTime / (24 * 60 * 60 * 1000)).toString()

    val isInputValid = recoveryPeriodInput.toIntOrNull() in 1..60

    Row(
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = isEditing,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { editing ->
            if (editing) {
                TextField(
                    value = recoveryPeriodInput,
                    onValueChange = { recoveryPeriodInput = it },
                    label = { Text("Recovery (days)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                )
            } else {
                Text(
                    text = "Recovery Period: ${muscleInfo.totalRecoveryTime / (24 * 60 * 60 * 1000)} days",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.padding(start = 16.dp))

        AnimatedContent(
            targetState = isEditing,
            transitionSpec = {
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                    .using(SizeTransform(clip = false))
            },
            contentAlignment = Alignment.Center,
        ) { editing ->
            if (editing) {
                Row {
                    Button(
                        modifier = Modifier.widthIn(min = 90.dp).heightIn(min = 56.dp),
                        onClick = {
                            if (isInputValid) {
                                val newRecoveryPeriod = recoveryPeriodInput.toInt()
                                if (recoveryPeriodInput != initialRecoveryPeriodDays) {
                                    onRecoveryPeriodChanged(newRecoveryPeriod)
                                }
                                isEditing = false
                            }
                        },
                        enabled = isInputValid,
                        shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                    ) {
                        Text(text = "Save")
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Button(
                        modifier = Modifier.widthIn(min = 90.dp).heightIn(min = 56.dp),
                        onClick = {
                            isEditing = false
                            recoveryPeriodInput = initialRecoveryPeriodDays
                        },
                        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                }
            } else {
                Button(
                    modifier = Modifier.widthIn(min = 90.dp).heightIn(min = 56.dp),
                    onClick = { isEditing = true },
                ) {
                    Text(text = "Change")
                }
            }
        }
    }
}

@Composable
private fun ThemedEditableRecoveryPeriodPreview(
    darkTheme: Boolean,
) {
    AppTheme(darkTheme = darkTheme, dynamicColor = false) {
        Surface {
            EditableRecoveryPeriod(
                muscleInfo = muscleTricepsMiddleTrained,
                onRecoveryPeriodChanged = {},
            )
        }
    }
}

@DevicePreviews
@Composable
private fun EditableRecoveryPeriodDarkPreview() =
    ThemedEditableRecoveryPeriodPreview(darkTheme = true)

@DevicePreviews
@Composable
private fun EditableRecoveryPeriodLightPreview() =
    ThemedEditableRecoveryPeriodPreview(darkTheme = false)
