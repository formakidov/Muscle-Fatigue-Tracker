package com.promni.mft.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

private lateinit var dataStore: DataStore<Preferences>

fun initDataStore(context: Context) {
    dataStore = createDataStore(
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
    )
}

actual fun provideDataStore(): DataStore<Preferences> = dataStore
