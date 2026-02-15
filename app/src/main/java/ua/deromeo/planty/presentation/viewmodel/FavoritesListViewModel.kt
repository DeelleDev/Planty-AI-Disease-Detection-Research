package ua.deromeo.planty.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.model.FavoriteModel
import ua.deromeo.planty.domain.usecase.GetAllFavoriteUseCase
import ua.deromeo.planty.domain.usecase.GetFavoriteSearchesUseCase
import ua.deromeo.planty.domain.usecase.SaveFavoriteSearchUseCase
import javax.inject.Inject

@HiltViewModel
class FavoritesListViewModel @Inject constructor(
    private val getAllFavoriteUseCase: GetAllFavoriteUseCase,
    private val getFavoriteSearchesUseCase: GetFavoriteSearchesUseCase,
    private val saveFavoriteSearchUseCase: SaveFavoriteSearchUseCase
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteModel>>(emptyList())
    val favorites: StateFlow<List<FavoriteModel>> = _favorites
    val recentSearches = mutableStateListOf<String>()

    init {
        reloadFavorites()
        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            recentSearches.addAll(getFavoriteSearchesUseCase())
        }
    }

    fun reloadFavorites() {
        viewModelScope.launch {
            _favorites.value = getAllFavoriteUseCase()
        }
    }

    fun saveSearch(trimmed: String) {
        viewModelScope.launch {
            saveFavoriteSearchUseCase(trimmed)
            if (!recentSearches.contains(trimmed)) {
                recentSearches.add(0, trimmed)
                if (recentSearches.size > 3) recentSearches.removeAt(recentSearches.lastIndex)
            }
        }
    }
}
