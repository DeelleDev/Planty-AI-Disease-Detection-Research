package ua.deromeo.planty.presentation.ui.screen

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ua.deromeo.planty.R
import ua.deromeo.planty.presentation.viewmodel.DiagnoseViewModel
import ua.deromeo.planty.presentation.ui.theme.MainGreen
import ua.deromeo.planty.presentation.ui.theme.montserrat
import ua.deromeo.planty.presentation.ui.theme.nunito
import java.io.File
import java.io.InputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiagnoseScreen(
    navController: NavController, diagnoseViewModel: DiagnoseViewModel,
) {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val progress by diagnoseViewModel.progress.collectAsState()
    var takePhoto: (() -> Unit)? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }

    val allowUpload by diagnoseViewModel.allowUpload.collectAsState()
    var isCapturing by remember { mutableStateOf(true) }
    var navigateBack by remember { mutableStateOf(false) }

    if (navigateBack) {
        // Прибрати камеру до навігації
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0)
                launchSingleTop = true
            }

        }
    }

    BackHandler(enabled = true) {
        isCapturing = false
        navigateBack = true
    }
    // Галерея
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage = bitmap
            inputStream?.close()
            diagnoseViewModel.startAnalysis(bitmap) { result ->
                navController.navigate("results/$result")
            }
        }
    }

// Файли
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage = bitmap
            inputStream?.close()
            diagnoseViewModel.startAnalysis(bitmap) { result ->
                navController.navigate("results/$result")
            }
        }
    }

    if (allowUpload == null && permissionState.status.isGranted) {
        AlertDialog(
            onDismissRequest = {
            diagnoseViewModel.setAllowUpload(false)
        },
            confirmButton = {
                TextButton(onClick = {
                    diagnoseViewModel.setAllowUpload(true)
                }) {
                    Text(
                        "Так",
                        fontSize = 18.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    diagnoseViewModel.setAllowUpload(false)
                }) {
                    Text(
                        "Ні",
                        fontSize = 18.sp,
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = Color(0xFFF7FAF9),
            title = { Text("Надсилати зображення?") },
            text = { Text("Дозвольте анонімно надсилати результати діагностики для покращення системи.") })
    }


    if (permissionState.status.isGranted) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), verticalArrangement = Arrangement.Top
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 16.dp),
            ) {
                Card(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                isCapturing = false
                                navigateBack = true
                            })
                }
                Text(
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.Center),
                    text = "Діагностика",
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Наведіть камеру та зробить знімок або оберіть зображення — і ми допоможемо визначити хворобу рослини за кілька секунд!",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = nunito,
                textAlign = TextAlign.Center,
                style = TextStyle(lineHeight = 18.sp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.fillMaxWidth()) {
                if (selectedImage == null && isCapturing) CameraPreviewWithCapture(
                    modifier = Modifier
                        .fillMaxHeight(
                            0.7f
                        )
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp)),
                    onImageCaptured = { imageProxy ->
                        val bitmap = imageProxyToBitmap(imageProxy)
                        imageProxy.close()
                        val matrix = Matrix().apply { postRotate(90f) }
                        val rotated = Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )

                        selectedImage = rotated

                        diagnoseViewModel.startAnalysis(rotated) { result ->
                            navController.navigate("results/$result")
                            selectedImage = null

                        }
                    },
                    captureLauncher = { launcher -> takePhoto = launcher })

                selectedImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Вибране зображення",
                        modifier = Modifier
                            .fillMaxHeight(0.7f)
                            .aspectRatio(1f)
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, MainGreen, RoundedCornerShape(8.dp))
                    )
                    if (progress != 0f && progress != 1f) ScanningLaserLine(modifier = Modifier.fillMaxSize())
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (progress != 0f && progress != 1f) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .width(200.dp)
                            .height(8.dp),
                        color = MainGreen,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = MainGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Діагностування рослини...", color = MainGreen, fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = "Фото має бути чітким, бажано крупним планом ураженої ділянки листя або стебла. Уникайте тіней, розмиття та зайвих об’єктів у кадрі.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = nunito,
                    textAlign = TextAlign.Center,
                    style = TextStyle(lineHeight = 18.sp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.background(MainGreen, RoundedCornerShape(32.dp))) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ImageActionButton(
                        "Галерея", R.drawable.photo_library
                    ) {
                        galleryLauncher.launch("image/*")
                    }
                    ImageActionButton("", R.drawable.baseline_camera) {
                        takePhoto?.invoke()
                    }
                    ImageActionButton("Файли", R.drawable.baseline_folder) {
                        filePickerLauncher.launch(arrayOf("image/*"))
                    }
                }


            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Для продовження потрібен дозвіл на доступ до камери",
                modifier = Modifier.fillMaxWidth(0.8f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Надати дозвіл")
            }
        }
    }
}

@Composable
fun ImageActionButton(label: String, iconRes: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(128.dp)
            )
        }
    }
}

@Composable
fun CameraPreviewWithCapture(
    modifier: Modifier = Modifier,
    onImageCaptured: (ImageProxy) -> Unit,
    captureLauncher: (() -> Unit) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3) // 4:3, але потім обріжемо в 1:1
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
    }
    val previewView = remember {
        PreviewView(context).apply {

            scaleType = PreviewView.ScaleType.FILL_CENTER
            layoutParams = FrameLayout.LayoutParams(1080, 1080)
        }
    }

    AndroidView(
        factory = { previewView }, modifier = modifier.aspectRatio(1f)
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(previewView.display.rotation).build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }


    captureLauncher {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            File(context.cacheDir, "temp.jpg")
        ).build()

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    onImageCaptured(image)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Capture", "Capture failed", exception)
                }
            })
    }


}


fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val bitmap = image.toBitmap()
    return bitmap
}


@Composable
fun ScanningLaserLine(modifier: Modifier = Modifier) {
    var boxHeight by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "LaserLine")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "LaserY"
    )

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            boxHeight = coordinates.size.height
        }) {
        if (boxHeight > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .offset { IntOffset(0, (offsetY * boxHeight).toInt()) }
                    .background(MainGreen))
        }
    }
}
