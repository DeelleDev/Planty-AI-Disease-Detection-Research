package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke() {
        plantRepository.clearHistory()
    }
}