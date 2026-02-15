package ua.deromeo.planty.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import ua.deromeo.planty.presentation.viewmodel.WeatherViewModel
import ua.deromeo.planty.presentation.ui.theme.montserrat
import ua.deromeo.planty.util.WeatherUtils.getCurrentDate

@Composable
fun WeatherWidget(viewModel: WeatherViewModel) {
    val weatherData by viewModel.weatherData.observeAsState()

    weatherData?.let { data ->
        val forecastDay = data.forecast.forecastday.firstOrNull()
        val day = forecastDay?.day
        val condition = day?.condition
        val astro = forecastDay?.astro

        val moonPhase = astro?.moon_phase.orEmpty()

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Сьогодні, ${getCurrentDate()}",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = montserrat,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${condition?.text}",
                        fontSize = 16.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                            text = "${day?.maxtemp_c}°C / ${day?.mintemp_c}°C",
                    fontSize = 16.sp,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                    )
                    Text(
                        text = "Фаза місяця: ${translateMoonPhase(moonPhase)}",
                        fontSize = 14.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }

                condition?.icon?.let { iconPath ->
                    val fullIconUrl = "https:$iconPath"
                    Image(
                        painter = rememberAsyncImagePainter(fullIconUrl),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(78.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }


            }
        }
    }
}

fun translateMoonPhase(englishPhase: String): String {
    return when (englishPhase) {
        "New Moon" -> "Новий місяць"
        "Waxing Crescent" -> "Молодий місяць"
        "First Quarter" -> "Перша чверть"
        "Waxing Gibbous" -> "Зростаючий місяць"
        "Full Moon" -> "Повний місяць"
        "Waning Gibbous" -> "Спадаючий місяць"
        "Last Quarter" -> "Остання чверть"
        "Waning Crescent" -> "Старий місяць "
        else -> englishPhase
    }
}