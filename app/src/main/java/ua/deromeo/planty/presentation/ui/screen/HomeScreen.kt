package ua.deromeo.planty.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ua.deromeo.planty.R
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.model.ResultModel
import ua.deromeo.planty.presentation.ui.component.WeatherWidget
import ua.deromeo.planty.presentation.viewmodel.HomeViewModel
import ua.deromeo.planty.presentation.viewmodel.WeatherViewModel
import ua.deromeo.planty.presentation.ui.theme.montserrat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavHostController, viewModel: HomeViewModel, weatherViewModel: WeatherViewModel
) {
    val results by viewModel.history.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { GreetingHeader(navController) }
        item { WeatherWidget(viewModel = weatherViewModel) }
        item { ExplanationBlock(navController) }
        if (results.isNotEmpty()) item { HistoryBlock(results, navController) }
    }

}

@Composable
fun GreetingHeader(navController: NavHostController) {
    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {

        Card(
            shape = androidx.compose.foundation.shape.CircleShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.padding(start = 8.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Start)
            )
        }
        Text(
            "PlantyAI",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = montserrat,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Center)
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Card(
                shape = androidx.compose.foundation.shape.CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {

                Image(
                    painter = painterResource(R.drawable.notification),
                    contentDescription = "Notifications Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))

            Card(
                shape = androidx.compose.foundation.shape.CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color(0xFCFFFFFF)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {

                Image(
                    painter = painterResource(R.drawable.bookmark_icon),
                    contentDescription = "Saved Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("favourites")
                        })
            }
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
fun ExplanationBlock(navController: NavHostController) {
    Text(
        "Допоможи своїм рослинам",
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = montserrat,
        color = MaterialTheme.colorScheme.primary
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PlantStepIcon(text = "Знімок", R.drawable.macro)
            Spacer(modifier = Modifier.width(8.dp))
            ArrowIcon()
            Spacer(modifier = Modifier.width(8.dp))
            PlantStepIcon(text = "Аналіз", R.drawable.scan)
            Spacer(modifier = Modifier.width(8.dp))
            ArrowIcon()
            Spacer(modifier = Modifier.width(8.dp))
            PlantStepIcon(text = "Діагноз", R.drawable.report)
            Spacer(modifier = Modifier.height(16.dp))

        }
        Button(
            onClick = { navController.navigate(Screen.Camera.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Перейти до діагностування",
                fontSize = 16.sp,
                fontFamily = montserrat,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun PlantStepIcon(text: String, icon: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 16.sp,
            fontFamily = montserrat,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ArrowIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(32.dp)
    )
}

@Composable
fun HistoryBlock(
    history: List<Pair<ResultModel, HistoryModel>>, navController: NavHostController
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Історія діагностики",
            fontSize = 20.sp,
            fontFamily = montserrat,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        history.forEach { item ->
            HistoryCard(
                item.first, item.second.imagePath, SimpleDateFormat(
                    "dd/MM/yy", Locale.getDefault()
                ).format(Date(item.second.timestamp)).toString()
            ) {
                navController.navigate("results/${item.second.id}")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Переглянути всю історію",
            color = MaterialTheme.colorScheme.primary,
            fontFamily = montserrat,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { navController.navigate(Screen.History.route) })
        Spacer(modifier = Modifier.height(16.dp))

    }
}
