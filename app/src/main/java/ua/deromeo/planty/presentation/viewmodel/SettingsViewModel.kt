package ua.deromeo.planty.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.usecase.ClearHistoryUseCase
import ua.deromeo.planty.domain.usecase.GetAllowGeoKeyUseCase
import ua.deromeo.planty.domain.usecase.GetAllowUploadUseCase
import ua.deromeo.planty.domain.usecase.SetAllowGeoKeyUseCase
import ua.deromeo.planty.domain.usecase.SetAllowUploadUseCase
import javax.inject.Inject

@HiltViewModel

class SettingsViewModel @Inject constructor(
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val getAllowUploadUseCase: GetAllowUploadUseCase,
    private val setAllowUploadUseCase: SetAllowUploadUseCase,
    private val getAllowGeoKeyUseCase: GetAllowGeoKeyUseCase,
    private val setAllowGeoKeyUseCase: SetAllowGeoKeyUseCase,
) : ViewModel() {

    private val _allowUpload: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val allowUpload: StateFlow<Boolean?> = _allowUpload

    private val _allowLocation: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val allowLocation: StateFlow<Boolean?> = _allowLocation

    init {
        viewModelScope.launch {
            getAllowUploadUseCase().collect {
                _allowUpload.value = it
            }
        }

        viewModelScope.launch {
            getAllowGeoKeyUseCase().collect {
                _allowLocation.value = it
            }
        }
    }

    fun setAllowUpload(enabled: Boolean) {
        viewModelScope.launch {
            setAllowUploadUseCase(enabled)
        }
    }

    fun setAllowLocation(enabled: Boolean) {
        viewModelScope.launch {
            setAllowGeoKeyUseCase(enabled)
        }
    }

    fun clearDiagnosisHistory() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }
}