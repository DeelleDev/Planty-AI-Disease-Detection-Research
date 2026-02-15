package ua.deromeo.planty.presentation.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.data.ml.TFLiteAnalyzer
import ua.deromeo.planty.domain.model.LocationModel
import ua.deromeo.planty.domain.usecase.GetAllowGeoKeyUseCase
import ua.deromeo.planty.domain.usecase.GetAllowUploadUseCase
import ua.deromeo.planty.domain.usecase.InsertDiagnosisUseCase
import ua.deromeo.planty.domain.usecase.SetAllowUploadUseCase
import ua.deromeo.planty.domain.usecase.UploadDiagnosisToRemoteUseCase
import ua.deromeo.planty.util.Resource
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

@HiltViewModel
class DiagnoseViewModel @Inject constructor(
    private val insertDiagnosisUseCase: InsertDiagnosisUseCase,
    private val uploadDiagnosisToRemoteUseCase: UploadDiagnosisToRemoteUseCase,
    private val getAllowUploadUseCase: GetAllowUploadUseCase,
    private val getAllowGeoKeyUseCase: GetAllowGeoKeyUseCase,
    private val setAllowUploadUseCase: SetAllowUploadUseCase,
    private val tfLiteAnalyzer: TFLiteAnalyzer,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()
    private val _allowUpload = MutableStateFlow<Boolean?>(null)
    val allowUpload: StateFlow<Boolean?> = _allowUpload
    private val _allowGeoKey = MutableStateFlow<Boolean?>(null)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

    init {
        viewModelScope.launch {
            getAllowUploadUseCase().collect { _allowUpload.value = it }
        }
        viewModelScope.launch {
            getAllowGeoKeyUseCase().collect { _allowGeoKey.value = it }
        }
    }

    fun startAnalysis(bitmap: Bitmap, onFinish: (Long) -> Unit) {

        viewModelScope.launch {
            _progress.value = 0.01f
            val preparedBitmap = prepareBitmapForAnalysis(bitmap)
            val predictions = tfLiteAnalyzer.analyzeImage(preparedBitmap) { newProgress ->
                _progress.value = newProgress
            }
            _progress.value = 0.95f
            delay(50)


            val baos = ByteArrayOutputStream()
            preparedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val imageData = baos.toByteArray()
            val imageExtension = "jpg"

            val currentLocModel: LocationModel? =
                if (_allowGeoKey.value == true && hasLocationPermission()) {
                    getCurrentLocationSuspend()?.let { LocationModel(it.latitude, it.longitude) }
                } else null

            val historyId = insertDiagnosisUseCase(
                imageData, imageExtension, predictions, currentLocModel
            )
            _progress.value = 0.99f // Прогрес після локального збереження
            delay(50)
            if (_allowUpload.value == true) {
                val jsonPredictions = Gson().toJson(predictions)
                when (val uploadResource =
                    uploadDiagnosisToRemoteUseCase(imageData, jsonPredictions, currentLocModel)) {
                    is Resource.Success -> {
                        Log.i("DiagnoseVM", "Diagnosis uploaded to remote successfully")
                    }

                    is Resource.Error -> {
                        Log.e(
                            "DiagnoseVM",
                            "Failed to upload diagnosis to remote: ${uploadResource.message}"
                        )
                        Toast.makeText(
                            appContext,
                            uploadResource.message ?: "Помилка завантаження результатів",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> { /* Loading or unexpected */
                    }
                }
            }
            _progress.value = 1f
            delay(50)
            onFinish(historyId)
            _progress.value = 0f
        }
    }

    private fun prepareBitmapForAnalysis(original: Bitmap): Bitmap {
        val width = original.width
        val height = original.height

        val side = min(width, height)
        val x = (width - side) / 2
        val y = (height - side) / 2

        return Bitmap.createBitmap(original, x, y, side, side)
    }


    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun setAllowUpload(enabled: Boolean) {
        viewModelScope.launch {
            setAllowUploadUseCase(enabled)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocationSuspend(): Location? = suspendCoroutine { cont ->
        if (hasLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location -> cont.resume(location) }
                .addOnFailureListener { cont.resume(null) }
        } else {
            cont.resume(null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tfLiteAnalyzer.closeInterpreter()
    }
}

