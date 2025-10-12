package com.promni.mft.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.promni.mft.domain.repository.MuscleFilter
import com.promni.mft.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserDataRepository {

    private object Keys {
        val MUSCLE_FILTER = stringPreferencesKey("muscle_filter")
    }

    override val muscleFilter: Flow<MuscleFilter> = dataStore.data
        .map { preferences ->
            val filterName = preferences[Keys.MUSCLE_FILTER] ?: MuscleFilter.ALL.name
            MuscleFilter.valueOf(filterName)
        }

    override suspend fun setMuscleFilter(filter: MuscleFilter) {
        dataStore.edit { preferences ->
            preferences[Keys.MUSCLE_FILTER] = filter.name
        }
    }
}
