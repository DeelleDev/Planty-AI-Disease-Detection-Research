package ua.deromeo.planty.domain.usecase

import ua.deromeo.planty.domain.model.WeatherResponse
import ua.deromeo.planty.domain.repository.WeatherRepository
import ua.deromeo.planty.util.Resource
import javax.inject.Inject
import javax.inject.Named

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    @Named("WeatherApiKey") private val apiKey: String
) {
    suspend operator fun invoke(lang: String): Resource<WeatherResponse> {
        return weatherRepository.getForecast(apiKey, lang)
    }
}