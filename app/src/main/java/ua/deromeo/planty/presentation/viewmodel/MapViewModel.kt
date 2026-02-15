package ua.deromeo.planty.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.model.ResultModel
import ua.deromeo.planty.domain.usecase.GetAllHistoryUseCase
import ua.deromeo.planty.domain.usecase.GetHistoryByIdUseCase
import ua.deromeo.planty.domain.usecase.TransformPredictionsUseCase

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val transformPredictionsUseCase: TransformPredictionsUseCase,
    private val getHistoryByIdUseCase: GetHistoryByIdUseCase

) : ViewModel() {

    private val _locations = MutableStateFlow<List<Pair<ResultModel, HistoryModel>>>(emptyList())
    val locations: StateFlow<List<Pair<ResultModel, HistoryModel>>> = _locations.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            getAllHistoryUseCase().collect { historyList ->
                val resultModelPairs = historyList.mapNotNull { history ->
                    if (history.topPredictions.isNotEmpty()) {
                        val resultModelList =
                            transformPredictionsUseCase(listOf(history.topPredictions.first()))
                        if (resultModelList.isNotEmpty()) {
                            Pair(resultModelList.first(), history)
                        } else null
                    } else null
                }
                _locations.value = resultModelPairs
            }
        }
    }

    fun loadHistory(historyId: Long) {
        viewModelScope.launch {
            getHistoryByIdUseCase(historyId).collect { historyModel ->
                if (historyModel != null && historyModel.topPredictions.isNotEmpty()) {
                    val resultModelList =
                        transformPredictionsUseCase(listOf(historyModel.topPredictions.first()))
                    if (resultModelList.isNotEmpty()) {
                        _locations.value = listOf(Pair(resultModelList.first(), historyModel))
                    } else {
                        _locations.value = emptyList()
                    }
                } else {
                    _locations.value = emptyList()
                }
            }
        }
    }

}
