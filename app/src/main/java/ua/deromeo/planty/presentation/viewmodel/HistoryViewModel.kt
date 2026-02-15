package ua.deromeo.planty.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.model.PredictionModel
import ua.deromeo.planty.domain.model.ResultModel
import ua.deromeo.planty.domain.usecase.GetAllHistoryUseCase
import ua.deromeo.planty.domain.usecase.GetDiseaseByIdUseCase
import ua.deromeo.planty.domain.usecase.GetHistorySearchesUseCase
import ua.deromeo.planty.domain.usecase.GetPlantByIdUseCase
import ua.deromeo.planty.domain.usecase.SaveHistorySearchUseCase
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val getDiseaseByIdUseCase: GetDiseaseByIdUseCase,
    private val getPlantByIdUseCase: GetPlantByIdUseCase,
    private val saveHistorySearchUseCase: SaveHistorySearchUseCase,
    private val getHistorySearchesUseCase: GetHistorySearchesUseCase
) : ViewModel() {

    private val _history = MutableStateFlow<List<Pair<ResultModel, HistoryModel>>>(emptyList())
    val history: StateFlow<List<Pair<ResultModel, HistoryModel>>> = _history
    val recentSearches = mutableStateListOf<String>()

    init {
        viewModelScope.launch {
            getAllHistoryUseCase().collect { list ->
                _history.value = list.map { history ->
                    Pair(getPlantDiseaseInfo(history.topPredictions[0]), history)
                }
            }
        }
        loadRecentSearches()

    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            recentSearches.addAll(getHistorySearchesUseCase())
        }
    }

    fun saveSearch(trimmed: String) {
        viewModelScope.launch {
            saveHistorySearchUseCase(trimmed)
            if (!recentSearches.contains(trimmed)) {
                recentSearches.add(0, trimmed)
                if (recentSearches.size > 3) recentSearches.removeAt(recentSearches.lastIndex)
            }
        }
    }

    private suspend fun getPlantDiseaseInfo(prediction: PredictionModel): ResultModel {
        val plant = getPlantByIdUseCase(labelToPlantId(prediction.label)).first()
        val diseaseId = labelToDiseaseId(prediction.label)
        val disease =
            if (diseaseId != (-1).toLong()) getDiseaseByIdUseCase(diseaseId).first() else null

        return ResultModel(
            plant = plant, disease = disease, confidence = prediction.confidence
        )

    }

    private fun labelToDiseaseId(label: String): Long {
        return when (label) {
            "Apple__black_rot" -> 1
            "Apple__rust" -> 2
            "Apple__scab" -> 3
            "Cassava__bacterial_blight" -> 4
            "Cassava__brown_streak_disease" -> 5
            "Cassava__green_mottle" -> 6
            "Cassava__mosaic_disease" -> 7
            "Cherry__powdery_mildew" -> 8
            "Chili__leaf_curl" -> 9
            "Chili__leaf_spot" -> 10
            "Chili__whitefly" -> 11
            "Chili__yellowish" -> 12
            "Coffee__cercospora_leaf_spot" -> 13
            "Coffee__red_spider_mite" -> 14
            "Coffee__rust" -> 15
            "Corn__common_rust" -> 16
            "Corn__gray_leaf_spot" -> 17
            "Corn__northern_leaf_blight" -> 18
            "Cucumber__diseased" -> 19
            "Gauva__diseased" -> 20
            "Grape__black_measles" -> 21
            "Grape__black_rot" -> 22
            "Grape__leaf_blight_(isariopsis_leaf_spot)" -> 23
            "Jamun__diseased" -> 24
            "Lemon__diseased" -> 25
            "Mango__diseased" -> 26
            "Peach__bacterial_spot" -> 27
            "Pepper_bell__bacterial_spot" -> 28
            "Pomegranate__diseased" -> 29
            "Potato__early_blight" -> 30
            "Potato__late_blight" -> 31
            "Rice__brown_spot" -> 32
            "Rice__hispa" -> 33
            "Rice__leaf_blast" -> 34
            "Rice__neck_blast" -> 35
            "Soybean__bacterial_blight" -> 36
            "Soybean__caterpillar" -> 37
            "Soybean__diabrotica_speciosa" -> 38
            "Soybean__downy_mildew" -> 39
            "Soybean__mosaic_virus" -> 40
            "Soybean__powdery_mildew" -> 41
            "Soybean__rust" -> 42
            "Soybean__southern_blight" -> 43
            "Strawberry__leaf_scorch" -> 44
            "Sugarcane__bacterial_blight" -> 45
            "Sugarcane__red_rot" -> 46
            "Sugarcane__red_stripe" -> 47
            "Sugarcane__rust" -> 48
            "Tea__algal_leaf" -> 49
            "Tea__anthracnose" -> 50
            "Tea__bird_eye_spot" -> 51
            "Tea__brown_blight" -> 52
            "Tea__red_leaf_spot" -> 53
            "Tomato__bacterial_spot" -> 54
            "Tomato__early_blight" -> 55
            "Tomato__late_blight" -> 56
            "Tomato__leaf_mold" -> 57
            "Tomato__mosaic_virus" -> 58
            "Tomato__septoria_leaf_spot" -> 59
            "Tomato__spider_mites_(two_spotted_spider_mite)" -> 60
            "Tomato__target_spot" -> 61
            "Tomato__yellow_leaf_curl_virus" -> 62
            "Wheat__brown_rust" -> 63
            "Wheat__septoria" -> 64
            "Wheat__yellow_rust" -> 65
            else -> -1
        }
    }

    private fun labelToPlantId(label: String): Long {
        return when (label) {
            "Apple__black_rot" -> 1
            "Apple__healthy" -> 1
            "Apple__rust" -> 1
            "Apple__scab" -> 1
            "Cassava__bacterial_blight" -> 2
            "Cassava__brown_streak_disease" -> 2
            "Cassava__green_mottle" -> 2
            "Cassava__healthy" -> 2
            "Cassava__mosaic_disease" -> 2
            "Cherry__healthy" -> 3
            "Cherry__powdery_mildew" -> 3
            "Chili__healthy" -> 4
            "Chili__leaf_curl" -> 4
            "Chili__leaf_spot" -> 4
            "Chili__whitefly" -> 4
            "Chili__yellowish" -> 4
            "Coffee__cercospora_leaf_spot" -> 5
            "Coffee__healthy" -> 5
            "Coffee__red_spider_mite" -> 5
            "Coffee__rust" -> 5
            "Corn__common_rust" -> 6
            "Corn__gray_leaf_spot" -> 6
            "Corn__healthy" -> 6
            "Corn__northern_leaf_blight" -> 6
            "Cucumber__diseased" -> 7
            "Cucumber__healthy" -> 7
            "Gauva__diseased" -> 8
            "Gauva__healthy" -> 8
            "Grape__black_measles" -> 9
            "Grape__black_rot" -> 9
            "Grape__healthy" -> 9
            "Grape__leaf_blight_(isariopsis_leaf_spot)" -> 9
            "Jamun__diseased" -> 10
            "Jamun__healthy" -> 10
            "Lemon__diseased" -> 11
            "Lemon__healthy" -> 11
            "Mango__diseased" -> 12
            "Mango__healthy" -> 12
            "Peach__bacterial_spot" -> 13
            "Peach__healthy" -> 13
            "Pepper_bell__bacterial_spot" -> 14
            "Pepper_bell__healthy" -> 14
            "Pomegranate__diseased" -> 15
            "Pomegranate__healthy" -> 15
            "Potato__early_blight" -> 16
            "Potato__healthy" -> 16
            "Potato__late_blight" -> 16
            "Rice__brown_spot" -> 17
            "Rice__healthy" -> 17
            "Rice__hispa" -> 17
            "Rice__leaf_blast" -> 17
            "Rice__neck_blast" -> 17
            "Soybean__bacterial_blight" -> 18
            "Soybean__caterpillar" -> 18
            "Soybean__diabrotica_speciosa" -> 18
            "Soybean__downy_mildew" -> 18
            "Soybean__healthy" -> 18
            "Soybean__mosaic_virus" -> 18
            "Soybean__powdery_mildew" -> 18
            "Soybean__rust" -> 18
            "Soybean__southern_blight" -> 18
            "Strawberry__healthy" -> 19
            "Strawberry__leaf_scorch" -> 19
            "Sugarcane__bacterial_blight" -> 20
            "Sugarcane__healthy" -> 20
            "Sugarcane__red_rot" -> 20
            "Sugarcane__red_stripe" -> 20
            "Sugarcane__rust" -> 20
            "Tea__algal_leaf" -> 21
            "Tea__anthracnose" -> 21
            "Tea__bird_eye_spot" -> 21
            "Tea__brown_blight" -> 21
            "Tea__healthy" -> 21
            "Tea__red_leaf_spot" -> 21
            "Tomato__bacterial_spot" -> 22
            "Tomato__early_blight" -> 22
            "Tomato__healthy" -> 22
            "Tomato__late_blight" -> 22
            "Tomato__leaf_mold" -> 22
            "Tomato__mosaic_virus" -> 22
            "Tomato__septoria_leaf_spot" -> 22
            "Tomato__spider_mites_(two_spotted_spider_mite)" -> 22
            "Tomato__target_spot" -> 22
            "Tomato__yellow_leaf_curl_virus" -> 22
            "Wheat__brown_rust" -> 23
            "Wheat__healthy" -> 23
            "Wheat__septoria" -> 23
            "Wheat__yellow_rust" -> 23
            else -> 0
        }
    }


}
