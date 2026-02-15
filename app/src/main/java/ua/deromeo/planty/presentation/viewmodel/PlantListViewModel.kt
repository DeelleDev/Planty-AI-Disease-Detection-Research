package ua.deromeo.planty.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.model.PlantModel
import ua.deromeo.planty.domain.usecase.GetAllPlantsUseCase
import ua.deromeo.planty.domain.usecase.GetPlantSearchesUseCase
import ua.deromeo.planty.domain.usecase.SavePlantSearchUseCase
import javax.inject.Inject

@HiltViewModel
class PlantListViewModel @Inject constructor(
    private val getAllPlantsUseCase: GetAllPlantsUseCase,
    private val savePlantSearchUseCase: SavePlantSearchUseCase,
    private val getPlantSearchesUseCase: GetPlantSearchesUseCase,
) : ViewModel() {

    private val _plants = MutableStateFlow<List<PlantModel>>(emptyList())
    val plants: StateFlow<List<PlantModel>> = _plants
    val recentSearches = mutableStateListOf<String>()

    init {
        observePlants()
        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            recentSearches.addAll(getPlantSearchesUseCase())
        }
    }

    fun saveSearch(trimmed: String) {
        viewModelScope.launch {
            savePlantSearchUseCase(trimmed)
            if (!recentSearches.contains(trimmed)) {
                recentSearches.add(0, trimmed)
                if (recentSearches.size > 3) recentSearches.removeAt(recentSearches.lastIndex)
            }
        }
    }

    private fun observePlants() {
        viewModelScope.launch {
            getAllPlantsUseCase().collect { plantModels ->
                _plants.value = plantModels
            }
        }
    }
}

