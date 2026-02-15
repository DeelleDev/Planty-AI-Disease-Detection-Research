package ua.deromeo.planty.domain.repository

interface SearchHistoryRepository {
    suspend fun savePlantSearch(search: String)
    suspend fun getPlantSearches(): List<String>

    suspend fun saveFavoriteSearch(search: String)
    suspend fun getFavoriteSearches(): List<String>

    suspend fun saveHistorySearch(search: String)
    suspend fun getHistorySearches(): List<String>
}