package com.promni.mft.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun provideDataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        val appName = "MuscleFatigueTracker"
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")

        val path = when {
            os.contains("win") -> "$userHome\\AppData\\Roaming\\$appName"
            os.contains("mac") -> "$userHome/Library/Application Support/$appName"
            else -> "$userHome/.$appName" // Linux and others
        }
        
        File(path).mkdirs()
        File(path, dataStoreFileName).absolutePath
    }
)
