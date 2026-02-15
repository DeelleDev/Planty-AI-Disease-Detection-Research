package ua.deromeo.planty.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import ua.deromeo.planty.presentation.viewmodel.MapViewModel
import ua.deromeo.planty.presentation.ui.theme.montserrat

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel,
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    val diagnosisList by viewModel.locations.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.padding(16.dp)) {
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
                    text = "Карта сканувань",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF002828),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState
            ) {
                diagnosisList.forEach { item ->
                    item.second.latitude?.let {
                        item.second.longitude?.let { it1 ->
                            LatLng(
                                it, it1
                            )
                        }
                    }?.let { MarkerState(position = it) }?.let {
                            Marker(
                                state = it,
                                title = "${item.first.plant.name}  ${(if (item.first.disease != null) item.first.disease!!.name else "Здорова")}",
                                snippet = "Ймовірність: ${(item.first.confidence * 100).toInt()}%"
                            )
                        }

                }
            }

            if (diagnosisList.isNotEmpty()) {
                val first =
                    diagnosisList.firstOrNull { it.second.latitude != null && it.second.longitude != null }
                first?.let { loc ->
                    LaunchedEffect(Unit) {
                        loc.second.latitude?.let {
                            loc.second.longitude?.let { it1 ->
                                LatLng(
                                    it, it1
                                )
                            }
                        }?.let {
                                CameraUpdateFactory.newLatLngZoom(
                                    it, 10f
                                )
                            }?.let {
                                cameraPositionState.animate(
                                    update = it
                                )
                            }
                    }
                }
            }
        }
    }
}