package ua.deromeo.planty.presentation.ui.screen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idapgroup.autosizetext.AutoSizeText
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.deromeo.planty.R
import ua.deromeo.planty.domain.model.DiseaseModel
import ua.deromeo.planty.presentation.ui.component.FavoriteIcon
import ua.deromeo.planty.presentation.ui.theme.MainGreen
import ua.deromeo.planty.presentation.ui.theme.montserrat
import ua.deromeo.planty.presentation.ui.theme.nunito
import ua.deromeo.planty.presentation.viewmodel.FavoritesViewModel
import ua.deromeo.planty.presentation.viewmodel.PlantDetailViewModel
import java.io.File
import kotlin.math.floor

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun PlantDetailScreen(
    navController: NavController, viewModel: PlantDetailViewModel = hiltViewModel()
) {
    val plant by viewModel.plant.collectAsState()
    val diseases: List<DiseaseModel> by viewModel.diseases.collectAsState()
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    if (plant.id == 0.toLong()) return
    val favoritesViewModel: FavoritesViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavoriteStatus(plant.id, "plant")

    }
    val captureController = rememberCaptureController()
    val context: Context = LocalContext.current
    var isCapturing by remember { mutableStateOf(false) }


    if (isCapturing) {
        LaunchedEffect(Unit) {
            delay(500)
            val bitmapAsync = captureController.captureAsync()
            try {
                val bitmap = bitmapAsync.await().asAndroidBitmap()

                val file = File(context.cacheDir, "diagnosis_result.png")
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
                isCapturing = false

            } catch (_: Throwable) {
                isCapturing = false

            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { navController.popBackStack() })
                }

                AutoSizeText(
                    text = plant.name,
                    maxLines = 1,
                    fontSize = 24.sp,
                    minFontSize = 14.sp,
                    color = Color(0xFF002828),
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    keepLineHeight = true,
                    textAlign = TextAlign.Center
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),

                        ) {

                        Image(
                            painter = painterResource(R.drawable.share),
                            contentDescription = "Notifications Icon",
                            modifier = Modifier
                                .size(42.dp)
                                .padding(8.dp)
                                .clickable {
                                    isCapturing = true

                                }

                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Card(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFCFFFFFF)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    ) {

                        FavoriteIcon(viewModel = favoritesViewModel, plant.id, "plant")

                    }
                }
            }

            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            val sectionTitles = listOf("Опис", "Умови", "Догляд", "Хвороби")
            val sectionIndices = mapOf(
                "Опис" to 1, "Умови" to 3, "Догляд" to 4, "Хвороби" to 5
            )
            if (isCapturing) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(unbounded = true)
                        .capturable(captureController)
                        .background(Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    ) {
                        Text(
                            text = plant.name,
                            fontFamily = montserrat,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF002828),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                    ) {
                        AssetImage(
                            assetPath = plant.imageUrl,
                            contentDescription = plant.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                        )
                    }

                    InfoLabelBox("Ботанічна назва: ", plant.botanicalName, Color(0xFFF7FAF9))
                    InfoLabelBox("Наукова назва: ", plant.scientificName)
                    InfoLabelBox("Також відома як: ", plant.alsoKnownAs, Color(0xFFF7FAF9))

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Зображення здорових листків",
                        fontFamily = montserrat,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    ImageCarousel(imagePaths = plant.images) { path ->
                        selectedImagePath = path
                    }


                    Spacer(Modifier.height(16.dp))



                    InfoSection(title = "Опис") {

                        ExpandableText(text = plant.description)
                    }

                    InfoSection(title = "Наукова класифікація", colorCard = Color.Transparent) {
                        InfoLabelClassificationBox("Рід: ", plant.genus, Color(0xFFF7FAF9))
                        InfoLabelClassificationBox("Родина: ", plant.family)
                        InfoLabelClassificationBox("Порядок: ", plant.order, Color(0xFFF7FAF9))
                        InfoLabelClassificationBox("Клас: ", plant.plantClass)
                        InfoLabelClassificationBox(
                            "Відділ: ", plant.division, Color(0xFFF7FAF9)
                        )
                    }

                    InfoSection(title = "Умови вирощування", colorCard = Color.Transparent) {
                        AttributeGrid(
                            attributes = listOf(
                                Triple(
                                    "Температура",
                                    plant.temperature,
                                    painterResource(id = R.drawable.thermometer)
                                ), Triple(
                                    "Світло", plant.light, painterResource(id = R.drawable.sun)
                                ), Triple(
                                    "Зона морозостійкості",
                                    plant.hardinessZone,
                                    painterResource(id = R.drawable.location)
                                ), Triple(
                                    "Темп росту",
                                    plant.growthRate,
                                    painterResource(id = R.drawable.trend)
                                )
                            )
                        )
                    }

                    SoilCard(
                        type = plant.soilType, drainage = plant.soilDrainage, pH = plant.soilPH
                    )

                    InfoSection(title = "Догляд", colorCard = Color.Transparent) {
                        AttributeGrid(
                            attributes = listOf(
                                Triple(
                                    "Полив", plant.watering, painterResource(
                                        id = R.drawable.watering_can
                                    )
                                ),
                                Triple(
                                    "Добриво",
                                    plant.fertilizer,
                                    painterResource(id = R.drawable.compost)
                                ),
                                Triple(
                                    "Обрізка",
                                    plant.pruning,
                                    painterResource(id = R.drawable.scissors)
                                ),
                                Triple(
                                    "Розмноження",
                                    plant.propagation,
                                    painterResource(id = R.drawable.crops)
                                ),
                                Triple(
                                    "Вологість",
                                    plant.humidity,
                                    painterResource(id = R.drawable.humidity)
                                ),
                                Triple(
                                    "Пересадка", plant.transplanting, painterResource(
                                        id = R.drawable.pot
                                    )
                                ),
                            )
                        )
                    }

                    InfoSection(
                        title = "Поширені шкідники та хвороби", painterResource(id = R.drawable.bug)
                    ) {
                        Text(
                            text = plant.commonPestsAndDiseases,
                            fontSize = 16.sp,
                            fontFamily = nunito,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF002828)
                        )
                    }
                    DiseaseCarousel(diseases = diseases, navController)


                    InfoSection(
                        title = "Особливості", painterResource(id = R.drawable.star)
                    ) {
                        Text(
                            text = plant.features,
                            fontSize = 16.sp,
                            fontFamily = nunito,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    InfoSection(
                        title = "Застосування", painterResource(id = R.drawable.usefull)
                    ) {
                        Text(
                            text = plant.uses,
                            fontSize = 16.sp,
                            fontFamily = nunito,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    InfoSection(
                        title = "Цікаві факти", painterResource(id = R.drawable.lightbulb)
                    ) {
                        Text(
                            text = plant.interestingFacts,
                            fontSize = 16.sp,
                            fontFamily = nunito,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }
            } else {
                LazyColumn(
                    state = listState,
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                        ) {
                            AssetImage(
                                assetPath = plant.imageUrl,
                                contentDescription = plant.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                            )
                        }

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(horizontal = 1.dp)
                        ) {
                            items(sectionTitles) { title ->
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(
                                                sectionIndices[title] ?: 0
                                            )
                                        }
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF5F8F7)
                                    ),
                                    border = BorderStroke(1.dp, MainGreen)
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 14.sp,
                                        fontFamily = montserrat,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF000000)
                                    )
                                }
                            }
                        }


                        InfoLabelBox(
                            "Ботанічна назва: ", plant.botanicalName, Color(0xFFF7FAF9)
                        )
                        InfoLabelBox("Наукова назва: ", plant.scientificName)
                        InfoLabelBox("Також відома як: ", plant.alsoKnownAs, Color(0xFFF7FAF9))

                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Зображення здорових листків",
                            fontFamily = montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        ImageCarousel(imagePaths = plant.images) { path ->
                            selectedImagePath = path
                        }


                        Spacer(Modifier.height(16.dp))
                    }
                    item(key = "Опис") {

                        InfoSection(title = "Опис") {

                            ExpandableText(text = plant.description)
                        }
                    }
                    item {
                        InfoSection(
                            title = "Наукова класифікація", colorCard = Color.Transparent
                        ) {
                            InfoLabelClassificationBox("Рід: ", plant.genus, Color(0xFFF7FAF9))
                            InfoLabelClassificationBox("Родина: ", plant.family)
                            InfoLabelClassificationBox(
                                "Порядок: ", plant.order, Color(0xFFF7FAF9)
                            )
                            InfoLabelClassificationBox("Клас: ", plant.plantClass)
                            InfoLabelClassificationBox(
                                "Відділ: ", plant.division, Color(0xFFF7FAF9)
                            )
                        }
                    }
                    item(key = "Умови") {
                        InfoSection(
                            title = "Умови вирощування", colorCard = Color.Transparent
                        ) {
                            AttributeGrid(
                                attributes = listOf(
                                    Triple(
                                        "Температура",
                                        plant.temperature,
                                        painterResource(id = R.drawable.thermometer)
                                    ), Triple(
                                        "Світло", plant.light, painterResource(id = R.drawable.sun)
                                    ), Triple(
                                        "Зона морозостійкості",
                                        plant.hardinessZone,
                                        painterResource(id = R.drawable.location)
                                    ), Triple(
                                        "Темп росту",
                                        plant.growthRate,
                                        painterResource(id = R.drawable.trend)
                                    )
                                )
                            )
                        }

                        SoilCard(
                            type = plant.soilType, drainage = plant.soilDrainage, pH = plant.soilPH
                        )
                    }
                    item(key = "Догляд") {
                        InfoSection(title = "Догляд", colorCard = Color.Transparent) {
                            AttributeGrid(
                                attributes = listOf(
                                    Triple(
                                        "Полив", plant.watering, painterResource(
                                            id = R.drawable.watering_can
                                        )
                                    ),
                                    Triple(
                                        "Добриво",
                                        plant.fertilizer,
                                        painterResource(id = R.drawable.compost)
                                    ),
                                    Triple(
                                        "Обрізка",
                                        plant.pruning,
                                        painterResource(id = R.drawable.scissors)
                                    ),
                                    Triple(
                                        "Розмноження",
                                        plant.propagation,
                                        painterResource(id = R.drawable.crops)
                                    ),
                                    Triple(
                                        "Вологість",
                                        plant.humidity,
                                        painterResource(id = R.drawable.humidity)
                                    ),
                                    Triple(
                                        "Пересадка", plant.transplanting, painterResource(
                                            id = R.drawable.pot
                                        )
                                    ),
                                )
                            )
                        }
                    }
                    item(key = "Хвороби") {
                        InfoSection(
                            title = "Поширені шкідники та хвороби",
                            painterResource(id = R.drawable.bug)
                        ) {
                            Text(
                                text = plant.commonPestsAndDiseases,
                                fontSize = 16.sp,
                                fontFamily = nunito,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF002828)
                            )
                        }
                        DiseaseCarousel(diseases = diseases, navController)
                    }
                    item {
                        InfoSection(
                            title = "Особливості", painterResource(id = R.drawable.star)
                        ) {
                            Text(
                                text = plant.features,
                                fontSize = 16.sp,
                                fontFamily = nunito,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        InfoSection(
                            title = "Застосування", painterResource(id = R.drawable.usefull)
                        ) {
                            Text(
                                text = plant.uses,
                                fontSize = 16.sp,
                                fontFamily = nunito,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        InfoSection(
                            title = "Цікаві факти", painterResource(id = R.drawable.lightbulb)
                        ) {
                            Text(
                                text = plant.interestingFacts,
                                fontSize = 16.sp,
                                fontFamily = nunito,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                    }
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
                AssetImage(
                    assetPath = selectedImagePath!!,
                    contentDescription = "Full Screen Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
fun InfoLabelBox(
    title: String,
    value: String,
    colorCard: Color = Color.Transparent,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = montserrat,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF004040)
                        )
                    ) {
                        append(title)
                        append(" ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF004040)
                        )
                    ) {
                        append(value)
                    }
                })
        }
    }
}

@Composable
fun InfoLabelClassificationBox(
    title: String,
    value: String,
    colorCard: Color = Color.Transparent,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Text(
                text = title,
                fontFamily = nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Start,

                )
            Text(
                text = value,
                fontFamily = nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    icon: Painter? = null,
    colorCard: Color = Color(0xFFF7FAF9),
    content: @Composable () -> Unit
) {
    Spacer(Modifier.height(16.dp))
    Row {
        if (icon != null) {
            Image(
                painter = icon, contentDescription = title, Modifier
                    .height(25.dp)
                    .width(25.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontFamily = montserrat,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF020617)
        )
    }
    Spacer(Modifier.height(8.dp))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorCard)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {


            content()
        }
    }
}

@Composable
fun AttributeGrid(attributes: List<Triple<String, String, Painter>>) {
    Column {
        for (i in attributes.indices step 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AttributeCard(attributes[i])
                }
                if (i + 1 < attributes.size) {
                    Box(modifier = Modifier.weight(1f)) {
                        AttributeCard(attributes[i + 1])
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
fun AttributeCard(attribute: Triple<String, String, Painter>) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAF9)),
    ) {

        Image(
            painter = attribute.third,
            contentDescription = attribute.second,
            Modifier
                .height(25.dp)
                .width(25.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = attribute.first,
            fontSize = 14.sp,
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF666666),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),

                text = attribute.second,
                fontSize = 16.sp,
                fontFamily = nunito,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color(0xFF020617)
            )
        }
    }
}

@Composable
fun SoilCard(type: String, drainage: String, pH: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAF9)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Image(
                    painter = painterResource(R.drawable.soil),
                    contentDescription = "Ґрунт",
                    Modifier
                        .height(25.dp)
                        .width(25.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Ґрунт",
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = montserrat,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.height(12.dp))
            Row {
                Text(
                    "Тип: ",
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = montserrat,
                    fontSize = 16.sp,
                    color = Color(0xFF004040)
                )
                Text(
                    type,
                    fontWeight = FontWeight.Medium,
                    fontFamily = montserrat,
                    fontSize = 16.sp,
                    color = Color(0xFF004040)
                )
            }
            Row {
                Text(
                    "Дренаж: ",
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = montserrat,
                    fontSize = 16.sp,
                    color = Color(0xFF004040)
                )
                Text(
                    drainage,
                    fontWeight = FontWeight.Medium,
                    fontFamily = montserrat,
                    fontSize = 16.sp,
                    color = Color(0xFF004040)
                )
            }
            Spacer(Modifier.height(24.dp))
            PHScale(pH)
        }
    }
}

@Composable
fun PHScale(soilPH: String) {
    val phColors = listOf(
        Color(0xFFFF8A8A),
        Color(0xFFFFB347),
        Color(0xFFFFD966),
        Color(0xFFFFF266),
        Color(0xFFE5F944),
        Color(0xFFB4F85E),
        Color(0xFF7EF4A5),
        Color(0xFF66EFC5),
        Color(0xFF66E0C4),
        Color(0xFF88E5DB),
        Color(0xFF75CFFF),
        Color(0xFF8FA7FF),
        Color(0xFFA77EFF),
        Color(0xFFB27AFF)
    )
    val phColors50 = listOf(
        Color(0x54F8B5B5),
        Color(0x54FACD9E),
        Color(0x54FDE8B3),
        Color(0x54FFF3A0),
        Color(0x54E9ED95),
        Color(0x54D6ECA4),
        Color(0x54CCEBD3),
        Color(0x54B8E7D0),
        Color(0x54A8E2C4),
        Color(0x54D3F2E3),
        Color(0x54C7E5F4),
        Color(0x54B6BFE4),
        Color(0x54CBB3D7),
        Color(0x54D0C3DF)
    )
    val phRange = remember(soilPH) {
        val parts = soilPH.replace(',', '.').split("–", "-", "–")
        if (parts.size == 2) {
            val start = parts[0].toFloatOrNull() ?: 0f
            val end = parts[1].toFloatOrNull() ?: 0f
            start..end
        } else 0f..0f
    }

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        for (ph in 1..14) {
            val fill = getFillPercentage(ph.toFloat(), phRange)
            Column {
                Text(
                    "$ph",
                    fontWeight = FontWeight.Medium,
                    fontFamily = montserrat,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFF004040)
                )
                Box(
                    modifier = Modifier
                        .size(22.dp, 32.dp)
                        .background(phColors50[ph - 1], RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = fill)
                            .requiredHeight(32.dp)
                            .align(
                                when {
                                    ph == floor(phRange.start).toInt() && ph == floor(phRange.endInclusive).toInt() -> Alignment.Center
                                    ph == floor(phRange.start).toInt() -> Alignment.CenterEnd
                                    ph == floor(phRange.endInclusive).toInt() -> Alignment.CenterStart
                                    else -> Alignment.Center
                                }
                            )
                            .background(
                                phColors[ph - 1], RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.align(Alignment.Center),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = MainGreen)
        ) {
            Row {
                Text(
                    text = "  ${phRange.start} pH - ${phRange.endInclusive}  pH  ",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,

                    )
            }
        }
    }
}

fun getFillPercentage(ph: Float, range: ClosedFloatingPointRange<Float>): Float {
    return when {
        ph == floor(range.start) && ph == floor(range.endInclusive) -> range.endInclusive - range.start
        ph == floor(range.start) -> (floor(range.start) + 1 - range.start)
        ph == floor(range.endInclusive) -> (floor(range.endInclusive) + 1 - range.endInclusive)
        (ph > range.start && ph < range.endInclusive) -> 1f
        else -> 0f
    }

}

@Composable
fun ImageCarousel(
    imagePaths: List<String>, modifier: Modifier = Modifier, onImageClick: (String) -> Unit
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(imagePaths) { path ->
            Card(
                modifier = Modifier
                    .width(180.dp)
                    .height(180.dp)
                    .clickable { onImageClick(path) },
                shape = RoundedCornerShape(12.dp),
            ) {
                AssetImage(
                    assetPath = path,
                    contentDescription = "Plant Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun DiseaseCarousel(diseases: List<DiseaseModel>, navController: NavController) {
    LazyRow(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        items(diseases) { disease ->
            DiseaseCard(disease = disease) {
                navController.navigate("plant_detail/${disease.plantId}/${disease.id}")
            }
        }
    }
}

@Composable
fun DiseaseCard(disease: DiseaseModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AssetImage(
                assetPath = disease.imageUrl,
                contentDescription = disease.name,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                        )
                    )
            )

            Text(
                text = disease.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                fontFamily = montserrat,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ExpandableText(
    text: String,
) {
    var expanded by remember { mutableStateOf(false) }
    val paragraphs = text.split("\n").filter { it.isNotBlank() }
    val firstParagraph = paragraphs.firstOrNull() ?: ""

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = firstParagraph,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp,
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF002828)
        )

        AnimatedVisibility(
            visible = expanded && paragraphs.size > 1,
            enter = fadeIn(tween(300)) + expandVertically(),
            exit = fadeOut(tween(300)) + shrinkVertically()
        ) {
            Column {
                for (i in 1 until paragraphs.size) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = paragraphs[i],
                        fontSize = 14.sp,
                        fontFamily = nunito,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF002828)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp)) {
            Text(
                text = if (expanded) "Приховати" else "Читати далі",
                fontSize = 14.sp,
                fontFamily = nunito,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 4.dp),
                color = MainGreen
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle description",
                tint = MainGreen
            )
        }
    }
}
