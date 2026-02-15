package ua.deromeo.planty.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ua.deromeo.planty.data.local.AppDatabase
import ua.deromeo.planty.data.remote.RemoteDataSource
import ua.deromeo.planty.data.local.entity.DiseaseEntity
import ua.deromeo.planty.data.local.entity.FavoriteEntity
import ua.deromeo.planty.data.local.entity.HistoryEntity
import ua.deromeo.planty.data.local.entity.PlantEntity
import ua.deromeo.planty.domain.model.DiseaseModel
import ua.deromeo.planty.domain.model.FavoriteModel
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.model.LocationModel
import ua.deromeo.planty.domain.model.PlantModel
import ua.deromeo.planty.domain.model.PredictionModel
import ua.deromeo.planty.domain.repository.PlantRepository
import ua.deromeo.planty.util.Resource
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class PlantRepositoryImpl(
    private val context: Context,
    private val db: AppDatabase,
    private val remoteDataSource: RemoteDataSource
) : PlantRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
                loadInitialDataFromAssets()
        }
    }

    override fun getAllPlants(): Flow<List<PlantModel>> {
        return db.plantDao().getAll().map { plantEntities ->
            plantEntities.map { plant ->
                PlantModel(
                    id = plant.id,
                    name = plant.name,
                    botanicalName = plant.botanicalName,
                    scientificName = plant.scientificName,
                    alsoKnownAs = plant.alsoKnownAs,
                    description = plant.description,
                    genus = plant.genus,
                    family = plant.family,
                    order = plant.order,
                    plantClass = plant.plantClass,
                    division = plant.division,
                    temperature = plant.temperature,
                    light = plant.light,
                    hardinessZone = plant.hardinessZone,
                    growthRate = plant.growthRate,
                    soilType = plant.soilType,
                    soilDrainage = plant.soilDrainage,
                    soilPH = plant.soilPH,
                    watering = plant.watering,
                    fertilizer = plant.fertilizer,
                    pruning = plant.pruning,
                    propagation = plant.propagation,
                    humidity = plant.humidity,
                    transplanting = plant.transplanting,
                    commonPestsAndDiseases = plant.commonPestsAndDiseases,
                    features = plant.features,
                    uses = plant.uses,
                    interestingFacts = plant.interestingFacts,
                    imageUrl = plant.imageUrl,
                    images = listOfNotNull(plant.imageUrl1, plant.imageUrl2, plant.imageUrl3)
                )
            }
        }
    }

    override fun getPlantById(plantId: Long): Flow<PlantModel> {
        return db.plantDao().getById(plantId).let { plantEntity ->
            plantEntity.map { plant ->

                PlantModel(
                    id = plant.id,
                    name = plant.name,
                    botanicalName = plant.botanicalName,
                    scientificName = plant.scientificName,
                    alsoKnownAs = plant.alsoKnownAs,
                    description = plant.description,
                    genus = plant.genus,
                    family = plant.family,
                    order = plant.order,
                    plantClass = plant.plantClass,
                    division = plant.division,
                    temperature = plant.temperature,
                    light = plant.light,
                    hardinessZone = plant.hardinessZone,
                    growthRate = plant.growthRate,
                    soilType = plant.soilType,
                    soilDrainage = plant.soilDrainage,
                    soilPH = plant.soilPH,
                    watering = plant.watering,
                    fertilizer = plant.fertilizer,
                    pruning = plant.pruning,
                    propagation = plant.propagation,
                    humidity = plant.humidity,
                    transplanting = plant.transplanting,
                    commonPestsAndDiseases = plant.commonPestsAndDiseases,
                    features = plant.features,
                    uses = plant.uses,
                    interestingFacts = plant.interestingFacts,
                    imageUrl = plant.imageUrl,
                    images = listOfNotNull(plant.imageUrl1, plant.imageUrl2, plant.imageUrl3)
                )
            }
        }
    }

    override fun getDiseasesByPlantId(plantId: Long): Flow<List<DiseaseModel>> {
        return db.diseaseDao().getByPlantId(plantId).map { diseaseEntities ->
            diseaseEntities.map { disease ->
                DiseaseModel(
                    id = disease.id,
                    plantId = disease.plantId,
                    fullName = disease.fullName,
                    scientificName = disease.scientificName,
                    alsoKnownAs = disease.alsoKnownAs,
                    name = disease.name,
                    description = disease.description,
                    symptoms = disease.symptoms,
                    treatment = disease.treatment,
                    prevention = disease.prevention,
                    imageUrl = disease.imageUrl,
                    images = listOfNotNull(disease.imageUrl1, disease.imageUrl2, disease.imageUrl3)
                )
            }
        }
    }

    override fun getDiseaseById(diseaseId: Long): Flow<DiseaseModel> {
        return db.diseaseDao().getById(diseaseId).let { diseaseEntity ->
            diseaseEntity.map { disease ->
                DiseaseModel(
                    id = disease.id,
                    plantId = disease.plantId,
                    fullName = disease.fullName,
                    name = disease.name,
                    scientificName = disease.scientificName,
                    alsoKnownAs = disease.alsoKnownAs,
                    description = disease.description,
                    symptoms = disease.symptoms,
                    treatment = disease.treatment,
                    prevention = disease.prevention,
                    imageUrl = disease.imageUrl,
                    images = listOfNotNull(disease.imageUrl1, disease.imageUrl2, disease.imageUrl3)
                )
            }
        }
    }

    override suspend fun insertDiagnosis(
        imageData: ByteArray,
        imageExtension: String,
        predictions: List<PredictionModel>,
        location: LocationModel?
    ): Long {
        val imageFileName = "${System.currentTimeMillis()}.$imageExtension"
        val file = File(context.filesDir, imageFileName)
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { out ->
                out.write(imageData)
            }
        }
        val jsonPredictions = Gson().toJson(predictions)
        return db.historyDao().insert(
            HistoryEntity(
                timestamp = System.currentTimeMillis(),
                imagePath = file.absolutePath,
                topPredictions = jsonPredictions,
                latitude = location?.latitude,
                longitude = location?.longitude
            )
        )

    }

    override fun getLastHistory(): Flow<List<HistoryModel>> {
        return db.historyDao().getLastThree().map { diagnosisEntities ->
            val type = object : TypeToken<List<PredictionModel>>() {}.type
            diagnosisEntities.map { diagnosis ->
                HistoryModel(
                    id = diagnosis.id,
                    timestamp = diagnosis.timestamp,
                    imagePath = diagnosis.imagePath,
                    topPredictions = Gson().fromJson(diagnosis.topPredictions, type),
                    latitude = diagnosis.latitude,
                    longitude = diagnosis.longitude
                )
            }
        }
    }

    override fun getAllHistory(): Flow<List<HistoryModel>> {
        return db.historyDao().getAll().map { diagnosisEntities ->
            val type = object : TypeToken<List<PredictionModel>>() {}.type
            diagnosisEntities.map { diagnosis ->
                HistoryModel(
                    id = diagnosis.id,
                    timestamp = diagnosis.timestamp,
                    imagePath = diagnosis.imagePath,
                    topPredictions = Gson().fromJson(diagnosis.topPredictions, type),
                    latitude = diagnosis.latitude,
                    longitude = diagnosis.longitude
                )
            }
        }
    }

    override suspend fun clearHistory() {
        db.historyDao().clearAll()
    }

    override fun getHistoryById(historyId: Long): Flow<HistoryModel?> {
        return db.historyDao().getHistoryById(historyId).let { historyEntity ->
            historyEntity.map { history ->
                val type = object : TypeToken<List<PredictionModel>>() {}.type
                history?.let {
                    HistoryModel(
                        id = it.id,
                        timestamp = history.timestamp,
                        imagePath = history.imagePath,
                        topPredictions = Gson().fromJson(history.topPredictions, type),
                        latitude = history.latitude,
                        longitude = history.longitude
                    )
                }
            }
        }
    }

    override suspend fun isFavorite(id: Long, type: String): Boolean =
        db.favoriteDao().getFavoriteById(id, type) != null

    override suspend fun toggleFavorite(id: Long, type: String): Boolean {
        val exists = db.favoriteDao().getFavoriteById(id, type)
        return if (exists == null) {
            db.favoriteDao().addToFavorites(FavoriteEntity(id = id, type = type))
            true
        } else {
            db.favoriteDao().removeFromFavorites(exists)
            false
        }
    }

    override suspend fun getAllFavorite(): List<FavoriteModel> {
        return db.favoriteDao().getAllOnce().map { favourite ->
            if (favourite.type == "plant") {
                val plant = getPlantById(favourite.id).first()
                FavoriteModel(plant = plant, disease = null)
            } else {
                val disease = getDiseaseById(favourite.id).first()
                val plant = getPlantById(disease.plantId).first()
                FavoriteModel(plant = plant, disease = disease)
            }
        }
    }


    private suspend fun loadInitialDataFromAssets() {
        try {
            val inputStream = context.assets.open("plants_and_diseases.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(jsonString)
            val plantsJson = jsonObject.getJSONArray("plants")
            val diseasesJson = jsonObject.getJSONArray("diseases")

            val gson = Gson()
            val plantType = object : TypeToken<List<PlantEntity>>() {}.type
            val diseaseType = object : TypeToken<List<DiseaseEntity>>() {}.type

            val plants: List<PlantEntity> = gson.fromJson(plantsJson.toString(), plantType)
            val diseases: List<DiseaseEntity> = gson.fromJson(diseasesJson.toString(), diseaseType)

            db.plantDao().insertAll(plants)
            db.diseaseDao().insertAll(diseases)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun uploadDiagnosisToRemote(
        imageData: ByteArray, resultJson: String, location: LocationModel?
    ): Resource<Unit> {
        val fileName = "${UUID.randomUUID()}.jpg"
        when (val uploadResult = remoteDataSource.uploadDiagnosisImage(imageData, fileName)) {
            is Resource.Success -> {
                val imageUrl = uploadResult.data!!
                return remoteDataSource.saveDiagnosisResult(resultJson, imageUrl, location)
            }

            is Resource.Error -> {
                return Resource.Error(
                    uploadResult.message ?: "Failed to upload diagnosis to remote"
                )
            }

            else -> return Resource.Error("Unexpected state in image upload")
        }
    }

    override suspend fun submitFeedbackToRemote(
        timestamp: Long,
        resultJson: String,
        correctPlant: String,
        correctDisease: String,
        additionalInfo: String?,
        imageData: ByteArray,
        location: LocationModel?
    ): Resource<Unit> {
        val fileName = "${UUID.randomUUID()}.jpg"
        when (val uploadResult = remoteDataSource.uploadFeedbackImage(imageData, fileName)) {
            is Resource.Success -> {
                val imageUrl = uploadResult.data!!
                return remoteDataSource.saveFeedback(
                    timestamp,
                    resultJson,
                    correctPlant,
                    correctDisease,
                    additionalInfo,
                    imageUrl,
                    location
                )
            }

            is Resource.Error -> {
                return Resource.Error(
                    uploadResult.message ?: "Failed to upload feedback to remote"
                )
            }

            else -> return Resource.Error("Unexpected state in feedback image upload")
        }
    }
}
