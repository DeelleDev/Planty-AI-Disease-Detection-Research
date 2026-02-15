package ua.deromeo.planty.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class GetLastHistoryUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(): Flow<List<HistoryModel>> {
        return plantRepository.getLastHistory()
    }
}