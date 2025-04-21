@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.promni.mft.presentation.ui.utils

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@Composable
actual fun getWindowSizeClass(): WindowSizeClass = calculateWindowSizeClass(LocalActivity.current!!)
