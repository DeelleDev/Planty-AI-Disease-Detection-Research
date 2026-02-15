package ua.deromeo.planty.presentation.ui.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import ua.deromeo.planty.domain.model.DiseaseModel
import ua.deromeo.planty.presentation.viewmodel.FavoritesListViewModel
import ua.deromeo.planty.presentation.ui.theme.montserrat

@Composable
fun FavouritesScreen(
    navController: NavHostController,
    viewModel: FavoritesListViewModel,
    backStackEntry: NavBackStackEntry
) {

    val backStackEntryForFavourites = remember(backStackEntry) {
        navController.getBackStackEntry("favourites")
    }
    DisposableEffect(backStackEntryForFavourites) {
        val lifecycle = backStackEntryForFavourites.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.reloadFavorites()
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    val expanded = rememberSaveable { mutableStateOf(false) }
    val results by viewModel.favorites.collectAsState()
    val searchText = rememberSaveable { mutableStateOf("") }
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


    val groupedFavourites = remember(searchText.value, results) {
        results.filter { pred ->
                searchText.value.isBlank() || (pred.plant.name.contains(
                    searchText.value, ignoreCase = true
                ) && pred.disease == null) || pred.disease?.name?.contains(
                    searchText.value, ignoreCase = true
                ) == true
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
                text = "Обране",
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

        // Відображення історії, згрупованої по даті
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(groupedFavourites) { favorite ->

                if (favorite.disease == null) {
                    PlantCard(plant = favorite.plant) {

                        val trimmed = searchText.value.trim()
                        if (trimmed.isNotEmpty() && !recentSearches.contains(trimmed)) {
                            recentSearches.add(trimmed)
                            viewModel.saveSearch(trimmed)
                        }
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        expanded.value = false
                        isSearchFocused = false

                        navController.navigate("plant_detail/${favorite.plant.id}")
                    }
                } else {

                    DiseaseCardClick(disease = favorite.disease) {

                        val trimmed = searchText.value.trim()
                        if (trimmed.isNotEmpty() && !recentSearches.contains(trimmed)) {
                            recentSearches.add(trimmed)
                            viewModel.saveSearch(trimmed)
                        }
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        expanded.value = false
                        isSearchFocused = false

                        navController.navigate("plant_detail/${favorite.plant.id}/${favorite.disease.id}")
                    }
                }
            }
        }
    }
}


@Composable
fun DiseaseCardClick(disease: DiseaseModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AssetImage(
                assetPath = disease.imageUrl,
                contentDescription = disease.fullName,
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
                text = disease.fullName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontFamily = montserrat,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }
    }
}