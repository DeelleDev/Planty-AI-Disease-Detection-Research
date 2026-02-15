package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.model.LocationModel
import ua.deromeo.planty.domain.repository.PlantRepository
import ua.deromeo.planty.util.Resource
import javax.inject.Inject

class UploadDiagnosisToRemoteUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(
        imageData: ByteArray, resultJson: String, location: LocationModel?
    ): Resource<Unit> {
        return plantRepository.uploadDiagnosisToRemote(imageData, resultJson, location)
    }
}