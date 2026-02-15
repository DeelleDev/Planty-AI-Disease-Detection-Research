package ua.deromeo.planty.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import ua.deromeo.planty.BuildConfig
import ua.deromeo.planty.domain.model.WeatherResponse

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") q: String = "auto:ip",
        @Query("key") key: String = BuildConfig.WEATHER_API_KEY,
        @Query("days") days: Int = 1,
        @Query("lang") lang: String = "uk"
    ): WeatherResponse
}
