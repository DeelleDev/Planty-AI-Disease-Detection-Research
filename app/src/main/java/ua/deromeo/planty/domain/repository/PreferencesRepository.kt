package ua.deromeo.planty.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getAllowUpload(): Flow<Boolean?>
    suspend fun setAllowUpload(enabled: Boolean)

    fun getAllowGeoKey(): Flow<Boolean?>
    suspend fun setAllowGeoKey(enabled: Boolean)
}