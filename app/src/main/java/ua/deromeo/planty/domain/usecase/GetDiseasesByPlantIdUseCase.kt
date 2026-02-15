package ua.deromeo.planty.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.domain.model.DiseaseModel
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class GetDiseasesByPlantIdUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(plantId: Long): Flow<List<DiseaseModel>> {
        return plantRepository.getDiseasesByPlantId(plantId)
    }
}