package ua.deromeo.planty.presentation.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import ua.deromeo.planty.R


val montserrat = FontFamily(
    Font(
        resId = R.font.montserrat_medium,
        weight = FontWeight.W500,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.montserrat_semibold,
        weight = FontWeight.W600,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.montserrat_bold,
        weight = FontWeight.W700,
        style = FontStyle.Normal
    )

)

val nunito = FontFamily(
    Font(
        resId = R.font.nunito_regular,
        weight = FontWeight.W400,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.nunito_medium,
        weight = FontWeight.W500,
        style = FontStyle.Normal
    ),
    Font(
        resId = R.font.nunito_bold,
        weight = FontWeight.W700,
        style = FontStyle.Normal
    )
)
