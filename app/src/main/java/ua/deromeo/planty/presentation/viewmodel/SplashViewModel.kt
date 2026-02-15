package ua.deromeo.planty.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    init {
        initApp()
    }

    private fun initApp() {
        viewModelScope.launch {
            try {
                delay(1500)
                _isReady.value = true
            } catch (e: Exception) {
                Log.e("SplashViewModel", "Помилка ініціалізації: ${e.message}")
                _isReady.value = true
            }
        }
    }
}
