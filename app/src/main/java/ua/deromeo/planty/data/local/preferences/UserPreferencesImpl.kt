package ua.deromeo.planty.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.deromeo.planty.domain.repository.PreferencesRepository
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PreferencesRepository {

    private object PreferencesKeys {
        val ALLOW_UPLOAD = booleanPreferencesKey("allow_upload")
        val ALLOW_GEO_KEY = booleanPreferencesKey("allow_geo_key")
    }

    override fun getAllowUpload(): Flow<Boolean?> {
        return context.dataStore.data.map { preferences ->
                preferences[PreferencesKeys.ALLOW_UPLOAD]
            }
    }

    override suspend fun setAllowUpload(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ALLOW_UPLOAD] = enabled
        }
    }

    override fun getAllowGeoKey(): Flow<Boolean?> {
        return context.dataStore.data.map { preferences ->
                preferences[PreferencesKeys.ALLOW_GEO_KEY] ?: false // Дефолтне значення, якщо null
            }
    }

    override suspend fun setAllowGeoKey(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ALLOW_GEO_KEY] = enabled
        }
    }
}