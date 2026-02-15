package ua.deromeo.planty.presentation.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.model.WeatherResponse
import ua.deromeo.planty.domain.usecase.GetWeatherUseCase
import ua.deromeo.planty.util.Resource

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: MutableLiveData<WeatherResponse?> = _weatherData

    private val _weatherState = MutableLiveData<Resource<WeatherResponse>>()

    init {
        loadWeather()
    }

    private fun loadWeather() {
        viewModelScope.launch {
            _weatherState.value = Resource.Loading()
            when (val resource = getWeatherUseCase("uk")) {
                is Resource.Success -> {
                    _weatherData.value = resource.data
                    _weatherState.value = resource
                }

                is Resource.Error -> {
                    Log.e("WeatherViewModel", "Помилка при отриманні погоди: ${resource.message}")
                    Toast.makeText(
                        context,
                        resource.message ?: "Помилка при завантаженні погоди",
                        Toast.LENGTH_SHORT
                    ).show()
                    _weatherState.value = resource
                }

                is Resource.Loading -> { /* Вже оброблено або не використовується */
                }
            }
        }
    }
}

