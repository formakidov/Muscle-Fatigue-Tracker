package com.promni.mft.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun provideDataStore(): DataStore<Preferences>
