package ua.deromeo.planty.data.repository

import android.util.Log
import ua.deromeo.planty.data.remote.WeatherApiService
import ua.deromeo.planty.domain.model.WeatherResponse
import ua.deromeo.planty.domain.repository.WeatherRepository
import ua.deromeo.planty.util.Resource
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {
    override suspend fun getForecast(apiKey: String, lang: String): Resource<WeatherResponse> {
        return try {
            val response = weatherApiService.getForecast(key = apiKey, lang = lang)
            Resource.Success(response)
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Error getting forecast", e)
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}