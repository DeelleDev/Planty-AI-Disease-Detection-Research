package ua.deromeo.planty.domain.model

data class ResultModel(val plant: PlantModel, val disease: DiseaseModel?, val confidence: Float)
