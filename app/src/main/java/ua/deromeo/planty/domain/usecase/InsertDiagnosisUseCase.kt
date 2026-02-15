package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.model.LocationModel
import ua.deromeo.planty.domain.model.PredictionModel
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class InsertDiagnosisUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(
        imageData: ByteArray,
        imageExtension: String,
        predictions: List<PredictionModel>,
        location: LocationModel?
    ): Long {
        return plantRepository.insertDiagnosis(imageData, imageExtension, predictions, location)
    }
}