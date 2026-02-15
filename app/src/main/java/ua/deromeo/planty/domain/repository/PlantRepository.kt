package ua.deromeo.planty.domain.repository

import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.domain.model.DiseaseModel
import ua.deromeo.planty.domain.model.FavoriteModel
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.model.LocationModel
import ua.deromeo.planty.domain.model.PlantModel
import ua.deromeo.planty.domain.model.PredictionModel
import ua.deromeo.planty.util.Resource

interface PlantRepository {

    fun getAllPlants(): Flow<List<PlantModel>>
    fun getPlantById(plantId: Long): Flow<PlantModel>

    fun getDiseasesByPlantId(plantId: Long): Flow<List<DiseaseModel>>
    fun getDiseaseById(diseaseId: Long): Flow<DiseaseModel>

    suspend fun insertDiagnosis(
        imageData: ByteArray,
        imageExtension: String,
        predictions: List<PredictionModel>,
        location: LocationModel?
    ): Long

    fun getLastHistory(): Flow<List<HistoryModel>>
    fun getAllHistory(): Flow<List<HistoryModel>>
    suspend fun clearHistory()
    fun getHistoryById(historyId: Long): Flow<HistoryModel?>

    suspend fun isFavorite(id: Long, type: String): Boolean
    suspend fun toggleFavorite(id: Long, type: String): Boolean
    suspend fun getAllFavorite(): List<FavoriteModel>

    suspend fun uploadDiagnosisToRemote(
        imageData: ByteArray,
        resultJson: String,
        location: LocationModel?
    ): Resource<Unit>

    suspend fun submitFeedbackToRemote(
        timestamp: Long,
        resultJson: String,
        correctPlant: String,
        correctDisease: String,
        additionalInfo: String?,
        imageData: ByteArray,
        location: LocationModel?
    ): Resource<Unit>
}
