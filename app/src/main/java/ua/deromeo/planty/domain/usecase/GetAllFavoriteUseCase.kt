package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.model.FavoriteModel
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

class GetAllFavoriteUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(): List<FavoriteModel> {
        return plantRepository.getAllFavorite()
    }
}