package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.repository.PreferencesRepository
import javax.inject.Inject

class SetAllowGeoKeyUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        preferencesRepository.setAllowGeoKey(enabled)
    }
}