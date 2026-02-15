package ua.deromeo.planty.domain.repository

import ua.deromeo.planty.domain.model.WeatherResponse
import ua.deromeo.planty.util.Resource

interface WeatherRepository {
    suspend fun getForecast(
        apiKey: String, lang: String
    ): Resource<WeatherResponse>
}