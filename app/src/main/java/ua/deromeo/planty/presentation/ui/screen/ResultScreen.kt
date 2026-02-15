package ua.deromeo.planty.presentation.ui.screen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import ua.deromeo.planty.R
import ua.deromeo.planty.domain.model.ResultModel
import ua.deromeo.planty.presentation.viewmodel.ResultViewModel
import ua.deromeo.planty.presentation.ui.theme.montserrat
import ua.deromeo.planty.presentation.ui.theme.nunito
import java.io.File

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun ResultScreen(
    navController: NavController, viewModel: ResultViewModel = hiltViewModel()
) {
    val history by viewModel.result.collectAsState()
    val image = history.imagePath
    val results: List<ResultModel> by viewModel.info.collectAsState()
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    val context: Context = LocalContext.current
    val captureController = rememberCaptureController()

    if (history.id == (-1).toLong() || results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var showFeedbackForm by remember { mutableStateOf(false) }
    var showThankYouDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Card(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { navController.popBackStack() })
                }
                Text(
                    text = "Результати",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF002828),
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
                        val scope = rememberCoroutineScope()
                        Image(
                            painter = painterResource(R.drawable.share),
                            contentDescription = "Notifications Icon",
                            modifier = Modifier
                                .size(42.dp)
                                .padding(8.dp)
                                .clickable {

                                    scope.launch {
                                        val bitmapAsync = captureController.captureAsync()
                                        try {
                                            val bitmap = bitmapAsync.await().asAndroidBitmap()

                                            val file =
                                                File(context.cacheDir, "diagnosis_result.png")
                                            file.outputStream().use {
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                            }

                                            val uri = FileProvider.getUriForFile(
                                                context, "${context.packageName}.provider", file
                                            )

                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                type = "image/png"
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }

                                            context.startActivity(
                                                Intent.createChooser(
                                                    intent, "Поділитися результатами"
                                                )
                                            )
                                        } catch (_: Throwable) {
                                        }
                                    }
                                })
                    }

                }
            }




            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .wrapContentHeight(unbounded = true)
                    .capturable(captureController)
                    .background(Color.White), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LocalImage(
                    path = image,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedImagePath = image
                        })
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (history.longitude != null && history.latitude != null) {
                        Button(onClick = {
                            navController.navigate("maps/${history.id}")
                        }) {
                            Text(
                                "  На карті  ",
                                fontSize = 16.sp,
                                fontFamily = montserrat,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Button(
                        onClick = { showFeedbackForm = true },
                    ) {
                        Text(
                            "  Це не те!?  ",
                            fontSize = 16.sp,
                            fontFamily = montserrat,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Найбільш імовірні діагнози:",
                    fontSize = 20.sp,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                results.forEach { result ->
                    ResultCard(result) {
                        if (result.disease != null) navController.navigate("plant_detail/${result.disease.plantId}/${result.disease.id}")
                        else navController.navigate("plant_detail/${result.plant.id}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (selectedImagePath != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { selectedImagePath = null }
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center) {
                LocalImage(
                    path = selectedImagePath!!,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
        if (showFeedbackForm) {
            var correctPlant by remember { mutableStateOf("") }
            var correctDisease by remember { mutableStateOf("") }
            var additionalInfo by remember { mutableStateOf("") }

            var plantError by remember { mutableStateOf(false) }
            var diseaseError by remember { mutableStateOf(false) }

            AlertDialog(
                containerColor = Color(0xFFF7FAF9),
                onDismissRequest = { showFeedbackForm = false },
                title = { Text("Форма зворотного зв'язку") },
                text = {
                    Column {
                        Text("Будь ласка, вкажіть правильну інформацію:")
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = correctPlant,
                            onValueChange = {
                                correctPlant = it
                                if (it.isNotBlank()) plantError = false
                            },
                            isError = plantError,
                            label = { Text("Правильна рослина") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (plantError) {
                            Text(
                                text = "Це поле є обов'язковим",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        OutlinedTextField(
                            value = correctDisease,
                            onValueChange = {
                                correctDisease = it
                                if (it.isNotBlank()) diseaseError = false
                            },
                            isError = diseaseError,
                            label = { Text("Правильне захворювання") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (diseaseError) {
                            Text(
                                text = "Це поле є обов'язковим",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        OutlinedTextField(
                            value = additionalInfo,
                            onValueChange = { additionalInfo = it },
                            label = { Text("Додатково (необов'язково)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Натискаючи \"Відправити\", ви даєте згоду анонімно зберегти зображення та дані зйомки на сервері для подальшої обробки.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val isPlantValid = correctPlant.trim().isNotEmpty()
                        val isDiseaseValid = correctDisease.trim().isNotEmpty()

                        plantError = !isPlantValid
                        diseaseError = !isDiseaseValid

                        if (isPlantValid && isDiseaseValid) {
                            viewModel.submitFeedback(
                                plantName = correctPlant.trim(),
                                diseaseName = correctDisease.trim(),
                                additionalInfo = additionalInfo.trim(),
                            )
                            showFeedbackForm = false
                            showThankYouDialog = true
                        }
                    }) {
                        Text(
                            "Відправити",
                            fontSize = 18.sp,
                            fontFamily = montserrat,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFeedbackForm = false }) {
                        Text(
                            "Скасувати",
                            fontSize = 18.sp,
                            fontFamily = montserrat,
                            fontWeight = FontWeight.Bold
                        )
                    }
                })
        }


        if (showThankYouDialog) {
            AlertDialog(
                onDismissRequest = { showThankYouDialog = false },
                title = { Text("Дякуємо!") },
                text = { Text("Ваш відгук допоможе нам стати кращими.") },
                confirmButton = {
                    TextButton(onClick = { showThankYouDialog = false }) {
                        Text("Закрити")
                    }
                })
        }


    }

}


@Composable
fun ResultCard(result: ResultModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(Color(0xFFF7FAF9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            AssetImage(
                assetPath = (if (result.disease != null) result.disease.imageUrl else result.plant.imageUrl),
                contentDescription = "",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                            )
                        ) {
                            append("${result.plant.name} — ")
                        }
                        append((if (result.disease != null) result.disease.name else "Здорова"))
                    },
                    fontSize = 14.sp,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF002828),
                )

                Text(
                    "Впевненість: ${(result.confidence * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color(0xFF002828),
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = (if (result.disease != null) result.disease.description else result.plant.description).take(
                        70
                    ) + "...",
                    fontSize = 14.sp,
                    fontFamily = nunito,
                    color = Color(0xFF002828),
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(lineHeight = 18.sp)
                )
            }

            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Деталі")
        }
    }

}

@Composable
fun LocalImage(
    modifier: Modifier = Modifier, path: String, contentScale: ContentScale = ContentScale.Fit
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(File(path)).crossfade(true).build(),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        error = painterResource(R.drawable.image_error)
    )
}
