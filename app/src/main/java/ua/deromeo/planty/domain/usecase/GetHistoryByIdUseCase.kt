package ua.deromeo.planty.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class GetHistoryByIdUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(historyId: Long): Flow<HistoryModel?> {
        return plantRepository.getHistoryById(historyId)
    }
}