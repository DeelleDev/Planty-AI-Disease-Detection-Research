package ua.deromeo.planty.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ua.deromeo.planty.domain.model.ResultModel
import ua.deromeo.planty.presentation.viewmodel.HistoryViewModel
import ua.deromeo.planty.presentation.ui.theme.montserrat
import ua.deromeo.planty.presentation.ui.theme.nunito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(navController: NavHostController, viewModel: HistoryViewModel) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    val results by viewModel.history.collectAsState()
    val searchText = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val recentSearches = viewModel.recentSearches

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }
    var isSearchFocused by remember { mutableStateOf(false) }

    BackHandler(enabled = isSearchFocused) {
        focusManager.clearFocus()
        keyboardController?.hide()
        isSearchFocused = false
        expanded.value = false
    }

    val groupedHistory = remember(searchText.value, results) {
        results.filter { pred ->
                searchText.value.isBlank() || pred.first.plant.name.contains(
                    searchText.value, ignoreCase = true
                ) || pred.first.disease?.name?.contains(
                    searchText.value, ignoreCase = true
                ) == true || "Здорова".contains(
                    searchText.value, ignoreCase = true
                )
            }.groupBy { diagnosis ->
                SimpleDateFormat(
                    "dd MMM yyyy", Locale.getDefault()
                ).format(Date(diagnosis.second.timestamp))
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Історія сканувань",
                fontFamily = montserrat,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF002828),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box {
            OutlinedTextField(
                value = searchText.value,
                onValueChange = {
                    val maxChars = 26
                    val maxWords = 5

                    if (it.length <= maxChars && it.trim()
                            .split("\\s+".toRegex()).size <= maxWords
                    ) {
                        searchText.value = it
                    }
                },
                label = {
                    Text(
                        "Пошук",
                        fontSize = 16.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(searchFocusRequester)
                    .onFocusChanged {
                        isSearchFocused = it.isFocused
                        expanded.value = it.isFocused
                    },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        val trimmed = searchText.value.trim()
                        if (trimmed.isNotEmpty() && !recentSearches.contains(trimmed)) {
                            recentSearches.add(trimmed)
                            viewModel.saveSearch(trimmed)
                        }
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        expanded.value = false
                        isSearchFocused = false
                    }),
                trailingIcon = {
                    if (searchText.value.isNotEmpty()) {
                        IconButton(onClick = {
                            searchText.value = ""
                            expanded.value = false
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистити")
                        }
                    }
                },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = montserrat,
                ),
            )
        }
        if (expanded.value) {

            recentSearches.takeLast(3).reversed().forEach { search ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .clickable {
                            searchText.value = search
                            expanded.value = false
                            keyboardController?.hide()
                            isSearchFocused = false
                            focusManager.clearFocus()
                        }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                    )
                    Text(
                        text = search,
                        fontSize = 16.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            groupedHistory.forEach { (date, items) ->
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = date,
                            fontSize = 16.sp,
                            fontFamily = montserrat,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp),

                            )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color.LightGray)
                                .size(2.dp)
                        )
                    }
                }
                items.forEach {
                    item {
                        HistoryCard(
                            it.first, it.second.imagePath, SimpleDateFormat(
                                "dd MMM yyyy", Locale.getDefault()
                            ).format(Date(it.second.timestamp)).toString()
                        ) {
                            val trimmed = searchText.value.trim()
                            if (trimmed.isNotEmpty() && !recentSearches.contains(trimmed)) {
                                recentSearches.add(trimmed)
                                // Збереження в DataStore
                                viewModel.saveSearch(trimmed)
                            }
                            keyboardController?.hide()
                            expanded.value = false
                            navController.navigate("results/${it.second.id}")
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(result: ResultModel, image: String, date: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(Color(0xFFF7FAF9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box {
            Row(
                modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                LocalImage(
                    path = image,
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            result.plant.name,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            fontFamily = montserrat,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF002828),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(lineHeight = 24.sp),
                        )

                        Text(
                            date,
                            fontSize = 13.sp,
                            fontFamily = montserrat,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            maxLines = 1,
                            style = TextStyle(lineHeight = 24.sp),
                        )
                    }
                    Text(
                        (if (result.disease != null) result.disease.name else "Здорова"),
                        fontSize = 14.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF002828),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(lineHeight = 24.sp),
                    )
                    Text(
                        "Впевненість: ${(result.confidence * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF002828),
                        style = TextStyle(lineHeight = 24.sp),
                    )
                    Text(
                        text = (if (result.disease != null) result.disease.description else result.plant.description),
                        fontSize = 14.sp,
                        fontFamily = nunito,
                        fontWeight = FontWeight.Medium,
                        style = TextStyle(lineHeight = 18.sp),
                        color = Color(0xFF002828),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Деталі")
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {}
        }
    }
}