package ua.deromeo.planty.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.domain.model.PlantModel
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class GetAllPlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(): Flow<List<PlantModel>> {
        return plantRepository.getAllPlants()
    }
}