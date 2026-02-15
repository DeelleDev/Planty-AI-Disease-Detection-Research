package ua.deromeo.planty.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WeatherUtils {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("d MMMM", Locale("uk"))
        return dateFormat.format(Date())
    }

}