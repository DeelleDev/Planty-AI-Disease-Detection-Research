package ua.deromeo.planty.data.remote

import ua.deromeo.planty.domain.model.LocationModel // Створимо цю модель
import ua.deromeo.planty.util.Resource

interface RemoteDataSource {
    suspend fun uploadDiagnosisImage(imageData: ByteArray, fileName: String): Resource<String>
    suspend fun saveDiagnosisResult(
        resultJson: String, imageUrl: String, location: LocationModel?
    ): Resource<Unit>

    suspend fun uploadFeedbackImage(imageData: ByteArray, fileName: String): Resource<String>
    suspend fun saveFeedback(
        timestamp: Long,
        resultJson: String,
        correctPlant: String,
        correctDisease: String,
        additionalInfo: String?,
        imageUrl: String,
        location: LocationModel?
    ): Resource<Unit>
}