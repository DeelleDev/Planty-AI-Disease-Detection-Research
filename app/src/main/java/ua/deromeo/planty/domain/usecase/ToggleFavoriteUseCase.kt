package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(id: Long, type: String): Boolean {
        return plantRepository.toggleFavorite(id, type)
    }
}