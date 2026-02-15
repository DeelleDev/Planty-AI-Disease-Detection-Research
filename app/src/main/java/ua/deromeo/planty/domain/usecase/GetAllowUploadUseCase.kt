package ua.deromeo.planty.domain.usecase

import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetAllowUploadUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<Boolean?> {
        return preferencesRepository.getAllowUpload()
    }
}