package ua.deromeo.planty.presentation.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import ua.deromeo.planty.presentation.viewmodel.SettingsViewModel
import ua.deromeo.planty.presentation.ui.theme.montserrat

@Composable
fun SettingsScreen(
    navController: NavController, viewModel: SettingsViewModel
) {
    val context: Context = LocalContext.current

    val allowUpload by viewModel.allowUpload.collectAsState()
    val allowLocation by viewModel.allowLocation.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Запуск дозволу на локацію
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted ->
            viewModel.setAllowLocation(isGranted)
        })


    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Підтвердження") },
            text = { Text("Ви впевнені, що хочете очистити всю історію діагностик? Цю дію не можна скасувати.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearDiagnosisHistory()
                    Toast.makeText(context, "Історію очищено", Toast.LENGTH_SHORT).show()
                    showConfirmationDialog = false
                }) {
                    Text(
                        "Очистити",
                        color = Color.Red,
                        fontSize = 18.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text(
                        "Скасувати",
                        fontSize = 18.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = Color(0xFFF7FAF9)
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Налаштування",
                fontFamily = montserrat,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF002828),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    "Надсилати результати діагностики",
                    fontSize = 16.sp,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = allowUpload ?: false, onCheckedChange = { isChecked ->
                        viewModel.setAllowUpload(isChecked)
                    }, modifier = Modifier.padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Додавати геолокацію до результатів",
                    fontSize = 16.sp,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = allowLocation ?: false, onCheckedChange = { isChecked ->
                        if (isChecked) {
                            val granted = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) {
                                viewModel.setAllowLocation(true)
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                viewModel.setAllowLocation(false)
                            }
                        } else {
                            viewModel.setAllowLocation(false)
                        }
                    }, modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    "Переглянути всі сканування на карті",
                    fontSize = 16.sp,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = {
                    navController.navigate("maps")
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Показати карту всіх сканувань",
                        fontSize = 14.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Очистити історію діагностик",
                    fontSize = 16.sp,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold
                )


                Button(
                    onClick = { showConfirmationDialog = true }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Очистити всю історію діагностик",
                        fontSize = 14.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    "Про застосунок",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Planty – додаток для розпізнавання хвороб рослин за допомогою штучного інтелекту. Він аналізує фото рослини, визначає ймовірну хворобу та надає рекомендації з лікування й профілактики.",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    style = TextStyle(lineHeight = 24.sp),

                    )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Автор: Чешенко Дмитро",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Text(
                    "dmitry.cheshenko@gmail.com",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

            }

        }}
    }
}
