package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class SaveHistorySearchUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    suspend operator fun invoke(search: String) {
        searchHistoryRepository.saveHistorySearch(search)
    }
}