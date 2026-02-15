package ua.deromeo.planty.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val WEATHER_BASE_URL = "https://api.weatherapi.com/v1/"

    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder().baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(WeatherApiService::class.java)
    }
}
