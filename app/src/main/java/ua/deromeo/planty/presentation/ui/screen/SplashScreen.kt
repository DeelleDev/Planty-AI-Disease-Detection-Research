package ua.deromeo.planty.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ua.deromeo.planty.R
import ua.deromeo.planty.presentation.viewmodel.SplashViewModel
import ua.deromeo.planty.presentation.ui.theme.MainGreen
import ua.deromeo.planty.presentation.ui.theme.montserrat

@Composable
fun SplashScreen(navController: NavController, viewModel: SplashViewModel = hiltViewModel()) {
    val isReady by viewModel.isReady.collectAsState()

    LaunchedEffect(isReady) {
        if (isReady) {
            navController.navigate(Screen.Home.route) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.icon2),
                contentDescription = "Лого",
                modifier = Modifier.size(128.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "PlantyAI",
                fontSize = 64.sp,
                fontFamily = montserrat,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004040)
            )
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = MainGreen)
        }
    }
}
