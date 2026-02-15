package ua.deromeo.planty.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.domain.model.PlantModel
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class GetPlantByIdUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(plantId: Long): Flow<PlantModel> {
        return plantRepository.getPlantById(plantId)
    }
}