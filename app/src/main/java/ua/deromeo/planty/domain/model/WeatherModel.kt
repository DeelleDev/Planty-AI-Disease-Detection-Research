package ua.deromeo.planty.domain.model

data class WeatherResponse(
    val location: Location, val forecast: Forecast
)

data class Location(
    val name: String, val region: String, val country: String, val localtime: String
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String, val day: Day, val astro: Astro
)

data class Day(
    val maxtemp_c: Double, val mintemp_c: Double, val condition: Condition
)

data class Condition(
    val text: String, val icon: String
)

data class Astro(
    val moon_phase: String
)

