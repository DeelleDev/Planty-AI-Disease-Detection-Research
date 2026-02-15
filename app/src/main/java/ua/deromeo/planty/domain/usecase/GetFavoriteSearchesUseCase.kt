package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class GetFavoriteSearchesUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    suspend operator fun invoke(): List<String> {
        return searchHistoryRepository.getFavoriteSearches()
    }
}