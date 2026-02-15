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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
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
import ua.deromeo.planty.presentation.ui.component.FavoriteIcon
import ua.deromeo.planty.presentation.ui.theme.MainGreen
import ua.deromeo.planty.presentation.ui.theme.montserrat
import ua.deromeo.planty.presentation.ui.theme.nunito
import ua.deromeo.planty.presentation.viewmodel.DiseaseDetailViewModel
import ua.deromeo.planty.presentation.viewmodel.FavoritesViewModel
import java.io.File

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun DiseaseDetailScreen(
    navController: NavController, viewModel: DiseaseDetailViewModel = hiltViewModel()
) {
    val disease by viewModel.disease.collectAsState()
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    val captureController = rememberCaptureController()
    val context: Context = LocalContext.current

    if (disease.id == 0.toLong()) return
    val favoritesViewModel: FavoritesViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavoriteStatus(disease.id, "disease")

    }
    var isCapturing by remember { mutableStateOf(false) }


    if (isCapturing) {
        LaunchedEffect(Unit) {
            delay(100)
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

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                    text = disease.name,
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

                        FavoriteIcon(viewModel = favoritesViewModel, disease.id, "disease")

                    }
                }
            }

            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            val sectionTitles = listOf("Опис", "Симптоми", "Лікування", "Профілактика")
            val sectionIndices = mapOf(
                "Опис" to 1, "Симптоми" to 2, "Лікування" to 3, "Профілактика" to 4
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
                            text = disease.name,
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
                            assetPath = disease.imageUrl,
                            contentDescription = disease.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                        )
                    }



                    InfoLabelBox("Назва хвороби: ", disease.fullName, Color(0xFFF7FAF9))
                    InfoLabelBox("Наукова назва: ", disease.scientificName)
                    InfoLabelBox("Також відома як: ", disease.alsoKnownAs, Color(0xFFF7FAF9))

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Зображення захворювання",
                        fontFamily = montserrat,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    ImageCarousel(imagePaths = disease.images) { path ->
                        selectedImagePath = path
                    }

                    Spacer(Modifier.height(16.dp))
                    InfoSection(title = "Опис") {
                        ExpandableText(text = disease.description)
                    }

                    InfoSection(title = "Симптоми", colorCard = Color.Transparent) {
                        ParagraphTextWithBoldLabels(disease.symptoms)
                    }

                    InfoSection(title = "Лікування (Рішення)", colorCard = Color.Transparent) {
                        ParagraphTextWithBoldLabels(disease.treatment)

                    }

                    InfoSection(title = "Профілактика", colorCard = Color.Transparent) {
                        ParagraphTextWithBoldLabels(disease.prevention)

                    }


                    Spacer(Modifier.height(16.dp))

                }

            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .capturable(captureController)
                ) {
                    item {

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                        ) {
                            AssetImage(
                                assetPath = disease.imageUrl,
                                contentDescription = disease.name,
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

                        InfoLabelBox("Назва хвороби: ", disease.fullName, Color(0xFFF7FAF9))
                        InfoLabelBox("Наукова назва: ", disease.scientificName)
                        InfoLabelBox("Також відома як: ", disease.alsoKnownAs, Color(0xFFF7FAF9))

                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Зображення захворювання",
                            fontFamily = montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        ImageCarousel(imagePaths = disease.images) { path ->
                            selectedImagePath = path
                        }
                    }
                    item(key = "Опис") {
                        Spacer(Modifier.height(16.dp))
                        InfoSection(title = "Опис") {
                            ExpandableText(text = disease.description)
                        }
                    }
                    item(key = "Симптоми") {
                        InfoSection(title = "Симптоми", colorCard = Color.Transparent) {
                            ParagraphTextWithBoldLabels(disease.symptoms)
                        }
                    }
                    item(key = "Лікування") {
                        InfoSection(title = "Лікування (Рішення)", colorCard = Color.Transparent) {
                            ParagraphTextWithBoldLabels(disease.treatment)

                        }
                    }
                    item(key = "Профілактика") {
                        InfoSection(title = "Профілактика", colorCard = Color.Transparent) {
                            ParagraphTextWithBoldLabels(disease.prevention)

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
fun ParagraphTextWithBoldLabels(text: String) {
    val paragraphs = text.trim().split("\n").filter { it.isNotBlank() }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        paragraphs.forEach { line ->
            var textHeight by remember { mutableIntStateOf(0) }
            Row {
                if (textHeight > 0) {
                    Image(
                        painter = painterResource(R.drawable.loza),
                        contentDescription = "Пункт",
                        contentScale = ContentScale.FillBounds,

                        modifier = Modifier
                            .height(with(LocalDensity.current) { textHeight.toDp() })
                            .width(24.dp)
                    )
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        textHeight = it.size.height
                    }
                    .drawDashedBorderWithBackground(
                        borderColor = Color(0xFFC4DBFF),
                        backgroundColor = Color(0xFFF7FAF9),
                        strokeWidth = 1.dp,
                        dashLength = 5.dp,
                        cornerRadius = 8.dp
                    )
                    .padding(8.dp)) {
                    if (":" in line) {
                        val parts = line.split(":", limit = 2)
                        val label = parts[0].trim() + ":"
                        val value = parts.getOrNull(1)?.trim() ?: ""

                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                ) {
                                    append("$label ")
                                }
                                append(value)
                            },
                            fontFamily = nunito,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF004040),
                        )
                    } else {
                        Text(
                            text = line.trim(),
                            fontFamily = nunito,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF004040)
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.drawDashedBorderWithBackground(
    borderColor: Color,
    backgroundColor: Color,
    strokeWidth: Dp,
    dashLength: Dp,
    cornerRadius: Dp = 12.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val stroke = strokeWidth.toPx()
        val dash = dashLength.toPx()
        val radius = cornerRadius.toPx()

        val canvas = drawContext.canvas
        val width = size.width
        val height = size.height

        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radius, radius)
        )

        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            this.color = borderColor.toArgb()
            this.strokeWidth = stroke
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(dash, dash), 0f)
        }

        canvas.nativeCanvas.drawRoundRect(
            0f, 0f, width, height, radius, radius, paint
        )
    })

