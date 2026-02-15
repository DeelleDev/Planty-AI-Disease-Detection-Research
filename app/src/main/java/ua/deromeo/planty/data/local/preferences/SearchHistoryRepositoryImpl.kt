package ua.deromeo.planty.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import ua.deromeo.planty.domain.repository.SearchHistoryRepository
import javax.inject.Inject

private val Context.recentPlantsDataStore: DataStore<Preferences> by preferencesDataStore(name = "recent_searches_plants")
private val Context.recentFavoritesDataStore: DataStore<Preferences> by preferencesDataStore(name = "recent_searches_favorites")
private val Context.recentHistoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "recent_searches_history")


class SearchHistoryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SearchHistoryRepository {

    private object PreferencesKeys {
        val RECENT_PLANTS_SEARCHES = stringSetPreferencesKey("recent_searches_plants")
        val RECENT_FAVORITES_SEARCHES = stringSetPreferencesKey("recent_searches_favorites")
        val RECENT_HISTORY_SEARCHES = stringSetPreferencesKey("recent_searches_history")
    }

    private suspend fun saveSearchOrderedInternal(
        dataStore: DataStore<Preferences>,
        key: Preferences.Key<Set<String>>,
        search: String
    ) {
        dataStore.edit { prefs ->
            val currentList = prefs[key]?.toList()?.toMutableList() ?: mutableListOf()
            currentList.remove(search)
            currentList.add(0, search)
            prefs[key] = currentList.take(3).toSet()
        }
    }

    private suspend fun getSearchesInternal(
        dataStore: DataStore<Preferences>, key: Preferences.Key<Set<String>>
    ): List<String> {
        val prefs = dataStore.data.first()
        return prefs[key]?.toList() ?: emptyList()
    }

    override suspend fun savePlantSearch(search: String) {
        saveSearchOrderedInternal(
            context.recentPlantsDataStore, PreferencesKeys.RECENT_PLANTS_SEARCHES, search
        )
    }

    override suspend fun getPlantSearches(): List<String> {
        return getSearchesInternal(
            context.recentPlantsDataStore, PreferencesKeys.RECENT_PLANTS_SEARCHES
        )
    }

    override suspend fun saveFavoriteSearch(search: String) {
        saveSearchOrderedInternal(
            context.recentFavoritesDataStore, PreferencesKeys.RECENT_FAVORITES_SEARCHES, search
        )
    }

    override suspend fun getFavoriteSearches(): List<String> {
        return getSearchesInternal(
            context.recentFavoritesDataStore, PreferencesKeys.RECENT_FAVORITES_SEARCHES
        )
    }

    override suspend fun saveHistorySearch(search: String) {
        saveSearchOrderedInternal(
            context.recentHistoryDataStore, PreferencesKeys.RECENT_HISTORY_SEARCHES, search
        )
    }

    override suspend fun getHistorySearches(): List<String> {
        return getSearchesInternal(
            context.recentHistoryDataStore, PreferencesKeys.RECENT_HISTORY_SEARCHES
        )
    }
}