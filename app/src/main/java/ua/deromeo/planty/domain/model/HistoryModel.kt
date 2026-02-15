package ua.deromeo.planty.domain.model

data class HistoryModel(
    val id: Long = 0,
    val timestamp: Long,
    val imagePath: String,
    val topPredictions: List<PredictionModel>,
    val latitude: Double?,
    val longitude: Double?
)