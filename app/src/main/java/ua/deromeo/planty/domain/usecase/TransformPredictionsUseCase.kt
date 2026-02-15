package ua.deromeo.planty.domain.usecase

import kotlinx.coroutines.flow.first
import ua.deromeo.planty.domain.model.PlantModel
import ua.deromeo.planty.domain.model.PredictionModel
import ua.deromeo.planty.domain.model.ResultModel
import ua.deromeo.planty.domain.util.LabelMapper
import javax.inject.Inject

class TransformPredictionsUseCase @Inject constructor(
    private val getPlantByIdUseCase: GetPlantByIdUseCase,
    private val getDiseaseByIdUseCase: GetDiseaseByIdUseCase
) {
    suspend operator fun invoke(predictions: List<PredictionModel>): List<ResultModel> {
        return predictions.map { prediction ->
            val plantId = LabelMapper.getPlantIdFromLabel(prediction.label)
            val plant = if (plantId != 0L) {
                try {
                    getPlantByIdUseCase(plantId).first()
                } catch (e: NoSuchElementException) {
                    null
                }
            } else null

            val diseaseId = LabelMapper.getDiseaseIdFromLabel(prediction.label)
            val disease = if (diseaseId != -1L && plant != null) {
                try {
                    getDiseaseByIdUseCase(diseaseId).first()
                } catch (e: NoSuchElementException) {
                    null
                }
            } else null

            ResultModel(
                plant = plant ?: createDefaultPlantModel(prediction.label),
                disease = disease,
                confidence = prediction.confidence
            )
        }
    }


    private fun createDefaultPlantModel(labelAttempt: String): PlantModel {
        val plantNameGuess = labelAttempt.split("__").firstOrNull() ?: "Unknown Plant"
        return PlantModel(
            id = 0,
            name = plantNameGuess,
            botanicalName = "",
            scientificName = "",
            alsoKnownAs = "",
            description = "Information not available.",
            genus = "",
            family = "",
            order = "",
            plantClass = "",
            division = "",
            temperature = "",
            light = "",
            hardinessZone = "",
            growthRate = "",
            soilType = "",
            soilDrainage = "",
            soilPH = "",
            watering = "",
            fertilizer = "",
            pruning = "",
            propagation = "",
            humidity = "",
            transplanting = "",
            commonPestsAndDiseases = "",
            features = "",
            uses = "",
            interestingFacts = "",
            imageUrl = "",
            images = emptyList()
        )
    }
}