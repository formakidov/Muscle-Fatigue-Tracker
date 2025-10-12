@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.promni.mft.presentation.ui.utils

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
actual fun getWindowSizeClass(): WindowSizeClass {
    if (LocalInspectionMode.current) {
        val configuration = LocalConfiguration.current
        return WindowSizeClass.calculateFromSize(DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp))
    }
    val context = LocalContext.current
    val activity = context as Activity
    return calculateWindowSizeClass(activity)
}
