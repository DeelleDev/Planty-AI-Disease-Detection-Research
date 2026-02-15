package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.model.LocationModel
import ua.deromeo.planty.domain.repository.PlantRepository
import ua.deromeo.planty.util.Resource
import javax.inject.Inject

class SubmitFeedbackToRemoteUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(
        timestamp: Long,
        resultJson: String,
        correctPlant: String,
        correctDisease: String,
        additionalInfo: String?,
        imageData: ByteArray,
        location: LocationModel?
    ): Resource<Unit> {
        return plantRepository.submitFeedbackToRemote(
            timestamp, resultJson, correctPlant, correctDisease, additionalInfo, imageData, location
        )
    }
}