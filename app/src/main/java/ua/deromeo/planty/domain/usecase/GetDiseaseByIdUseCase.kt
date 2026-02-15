package ua.deromeo.planty.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.domain.model.DiseaseModel
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class GetDiseaseByIdUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(diseaseId: Long): Flow<DiseaseModel> {
        return plantRepository.getDiseaseById(diseaseId)
    }
}