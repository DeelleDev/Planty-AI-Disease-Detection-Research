package ua.deromeo.planty.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.model.DiseaseModel
import ua.deromeo.planty.domain.usecase.GetDiseaseByIdUseCase
import javax.inject.Inject

@HiltViewModel
class DiseaseDetailViewModel @Inject constructor(
    private val getDiseaseByIdUseCase: GetDiseaseByIdUseCase
) : ViewModel() {

    private val _disease =
        MutableStateFlow(DiseaseModel(0, 0, "", "", "", "", "", "", "", "", "", emptyList()))
    val disease: StateFlow<DiseaseModel> = _disease.asStateFlow()


    fun loadDiseaseDetails(diseaseId: Long) {
        viewModelScope.launch {
            getDiseaseByIdUseCase(diseaseId).collect { diseaseModel ->
                _disease.value = diseaseModel
            }
        }


    }
}

