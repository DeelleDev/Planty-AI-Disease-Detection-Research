package ua.deromeo.planty.domain.model

data class DiseaseModel(
    val id: Long,
    val plantId: Long,
    val name: String,
    val fullName: String,
    val scientificName: String,
    val alsoKnownAs: String,
    val description: String,
    val symptoms: String,
    val treatment: String,
    val prevention: String,
    val imageUrl: String,
    val images: List<String>
)
