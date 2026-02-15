package ua.deromeo.planty.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.model.DiseaseModel
import ua.deromeo.planty.domain.model.PlantModel
import ua.deromeo.planty.domain.usecase.GetDiseasesByPlantIdUseCase
import ua.deromeo.planty.domain.usecase.GetPlantByIdUseCase
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    private val getPlantByIdUseCase: GetPlantByIdUseCase,
    private val getDiseasesByPlantIdUseCase: GetDiseasesByPlantIdUseCase
) : ViewModel() {

    private val _plant = MutableStateFlow(
        PlantModel(
            0,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            emptyList()
        )
    )
    val plant: StateFlow<PlantModel> = _plant.asStateFlow()

    private val _diseases = MutableStateFlow<List<DiseaseModel>>(emptyList())
    val diseases: StateFlow<List<DiseaseModel>> = _diseases.asStateFlow()


    fun loadPlantDetails(plantId: Long) {
        viewModelScope.launch {
            getPlantByIdUseCase(plantId).collect { plantModel ->
                _plant.value = plantModel
            }
        }
        viewModelScope.launch {
            getDiseasesByPlantIdUseCase(plantId).collect { diseaseList ->
                _diseases.value = diseaseList
            }
        }


    }
}

