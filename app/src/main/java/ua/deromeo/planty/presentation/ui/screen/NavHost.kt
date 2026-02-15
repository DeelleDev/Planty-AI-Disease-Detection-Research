package ua.deromeo.planty.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ua.deromeo.planty.R
import ua.deromeo.planty.presentation.viewmodel.DiagnoseViewModel
import ua.deromeo.planty.presentation.viewmodel.DiseaseDetailViewModel
import ua.deromeo.planty.presentation.viewmodel.FavoritesListViewModel
import ua.deromeo.planty.presentation.viewmodel.HistoryViewModel
import ua.deromeo.planty.presentation.viewmodel.HomeViewModel
import ua.deromeo.planty.presentation.viewmodel.MapViewModel
import ua.deromeo.planty.presentation.viewmodel.PlantDetailViewModel
import ua.deromeo.planty.presentation.viewmodel.PlantListViewModel
import ua.deromeo.planty.presentation.viewmodel.ResultViewModel
import ua.deromeo.planty.presentation.viewmodel.SettingsViewModel
import ua.deromeo.planty.presentation.viewmodel.WeatherViewModel
import ua.deromeo.planty.presentation.ui.theme.montserrat

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home, Screen.Favorites, Screen.Camera, Screen.History, Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = items.map { it.route }
    val weatherViewModel: WeatherViewModel = hiltViewModel()

    BackHandler(enabled = currentRoute in mainRoutes && currentRoute != Screen.Home.route) {
        navController.navigate(Screen.Home.route) {
            popUpTo(0)
            launchSingleTop = true
        }
    }

    Scaffold(

        bottomBar = {
            BottomNavigationBar(navController = navController, items = items)
        }) { padding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(padding),
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300))
            }) {

            composable("splash") {
                SplashScreen(navController)
            }

            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(navController, viewModel, weatherViewModel)
            }

            composable(Screen.Favorites.route) {
                val viewModel: PlantListViewModel = hiltViewModel()
                PlantListScreen(navController, viewModel)
            }

            composable(Screen.Camera.route) {

                val diagnoseViewModel: DiagnoseViewModel = hiltViewModel()
                DiagnoseScreen(navController, diagnoseViewModel)
            }

            composable(Screen.History.route) {
                val viewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(navController, viewModel)
            }

            composable(Screen.Profile.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(navController, viewModel)
            }

            composable("plant_detail/{plantId}") { backStackEntry ->
                val plantId =
                    backStackEntry.arguments?.getString("plantId")?.toLong() ?: return@composable
                val viewModel: PlantDetailViewModel = hiltViewModel()

                LaunchedEffect(plantId) {
                    viewModel.loadPlantDetails(plantId)
                }

                val plant by viewModel.plant.collectAsState()
                if (plant.id != 0.toLong()) {
                    PlantDetailScreen(
                        navController = navController, viewModel = viewModel
                    )
                }
            }
            composable("results/{resultId}") { backStackEntry ->
                val resultId =
                    backStackEntry.arguments?.getString("resultId")?.toLong() ?: return@composable
                val viewModel: ResultViewModel = hiltViewModel()

                LaunchedEffect(resultId) {
                    viewModel.loadHistoryDetails(resultId)
                }
                ResultScreen(navController, viewModel)
            }
            composable("favourites") { backStackEntry ->
                val viewModel: FavoritesListViewModel = hiltViewModel()
                FavouritesScreen(navController, viewModel, backStackEntry)
            }
            composable("maps") { backStackEntry ->
                val viewModel: MapViewModel = hiltViewModel()
                LaunchedEffect("all") {
                    viewModel.loadHistory()
                }
                MapScreen(navController, viewModel)
            }
            composable("maps/{historyId}") { backStackEntry ->
                val historyId =
                    backStackEntry.arguments?.getString("historyId")?.toLong() ?: return@composable
                val viewModel: MapViewModel = hiltViewModel()
                LaunchedEffect(historyId) {
                    viewModel.loadHistory(historyId)
                }
                MapScreen(navController, viewModel)
            }
            composable("plant_detail/{plantId}/{diseaseId}") { backStackEntry ->
                val diseaseId =
                    backStackEntry.arguments?.getString("diseaseId")?.toLong() ?: return@composable
                val viewModel: DiseaseDetailViewModel = hiltViewModel()

                LaunchedEffect(diseaseId) {
                    viewModel.loadDiseaseDetails(diseaseId)
                }

                val disease by viewModel.disease.collectAsState()
                if (disease.id != 0.toLong()) {
                    DiseaseDetailScreen(
                        navController = navController, viewModel = viewModel
                    )
                }
            }
        }
    }
}


// ---- Секція екранів ----

sealed class Screen(val route: String, val icon: Int, val label: String) {
    data object Home : Screen("home", R.drawable.home, "Головна")
    data object Favorites : Screen("favorites", R.drawable.notebook, "Довідник")
    data object Camera : Screen("camera", R.drawable.camera, "Сканувати")
    data object History : Screen("history", R.drawable.history, "Історія")
    data object Profile : Screen("profile", R.drawable.settings, "Інше")
}

// ---- Нижня навігація ----

@Composable
fun BottomNavigationBar(
    navController: NavHostController, items: List<Screen>
) {
    val visibility: Boolean
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val hideBottomBarRoutes: List<String> = listOf("splash", Screen.Camera.route)
    visibility = if (currentDestination != null) currentDestination.route !in hideBottomBarRoutes
    else false
    AnimatedVisibility(
        visible = visibility, enter = slideInVertically(
            initialOffsetY = { it }, animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)), exit = slideOutVertically(
            targetOffsetY = { it }, animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        NavigationBar(containerColor = Color(0x8AE8F5E9)) {
            items.forEach { screen ->
                val selected = currentDestination?.route == screen.route

                NavigationBarItem(
                    selected = selected,
                    colors = NavigationBarItemColors(
                        selectedIndicatorColor = Color(0x402DA15D),
                        selectedIconColor = Color(0xFF000000),
                        selectedTextColor = Color(0xFF000000),
                        unselectedIconColor = Color(0xFF000000),
                        unselectedTextColor = Color(0xFF000000),
                        disabledIconColor = Color(0xFFFFFFFF),
                        disabledTextColor = Color(0xFFFFFFFF)
                    ),
                    onClick = {
                        if (currentDestination?.route != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(0)
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        if (screen == Screen.Camera) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary, shape = CircleShape
                                    )
                                    .clip(CircleShape), contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(screen.icon),
                                    contentDescription = screen.label,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        } else {
                            Icon(
                                painter = painterResource(screen.icon),
                                contentDescription = screen.label,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp),
                                tint = LocalContentColor.current,

                                )
                        }
                    },
                    label = {
                        Text(
                            screen.label,
                            fontSize = 11.sp,
                            fontFamily = montserrat,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(lineHeight = 24.sp),
                        )
                    },

                    )
            }
        }
    }
}
