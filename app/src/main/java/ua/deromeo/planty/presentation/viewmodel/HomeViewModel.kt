package ua.deromeo.planty.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.model.ResultModel
import ua.deromeo.planty.domain.usecase.GetLastHistoryUseCase
import ua.deromeo.planty.domain.usecase.TransformPredictionsUseCase
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLastHistoryUseCase: GetLastHistoryUseCase,
    private val transformPredictionsUseCase: TransformPredictionsUseCase
) : ViewModel() {

    private val _history = MutableStateFlow<List<
            Pair<ResultModel, HistoryModel>>>(emptyList())
    val history: StateFlow<List<
            Pair<ResultModel, HistoryModel>>> = _history

    init {
        viewModelScope.launch {
            getLastHistoryUseCase().collect { historyList ->
                val resultModelPairs = historyList.mapNotNull { history ->
                    if (history.topPredictions.isNotEmpty()) {
                        val resultModelList =
                            transformPredictionsUseCase(listOf(history.topPredictions.first()))
                        if (resultModelList.isNotEmpty()) {
                            Pair(resultModelList.first(), history)
                        } else null
                    } else null
                }
                _history.value = resultModelPairs
            }
        }
    }

}
