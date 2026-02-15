package ua.deromeo.planty.presentation.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.deromeo.planty.domain.model.HistoryModel
import ua.deromeo.planty.domain.model.LocationModel
import ua.deromeo.planty.domain.model.ResultModel
import ua.deromeo.planty.domain.usecase.GetAllowGeoKeyUseCase
import ua.deromeo.planty.domain.usecase.GetHistoryByIdUseCase
import ua.deromeo.planty.domain.usecase.SubmitFeedbackToRemoteUseCase
import ua.deromeo.planty.domain.usecase.TransformPredictionsUseCase
import ua.deromeo.planty.util.Resource
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getHistoryByIdUseCase: GetHistoryByIdUseCase,
    private val getAllowGeoKeyUseCase: GetAllowGeoKeyUseCase,
    private val submitFeedbackToRemoteUseCase: SubmitFeedbackToRemoteUseCase,
    private val transformPredictionsUseCase: TransformPredictionsUseCase,
    @ApplicationContext private val appContext: Context,

    ) : ViewModel() {

    private val _result = MutableStateFlow(HistoryModel(-1, 0, "", emptyList(), 0.0, 0.0))
    val result: StateFlow<HistoryModel> = _result.asStateFlow()

    private val _info = MutableStateFlow<List<ResultModel>>(emptyList())
    val info: StateFlow<List<ResultModel>> = _info.asStateFlow()

    private val _allowGeoKey = MutableStateFlow<Boolean?>(null)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

    init {
        viewModelScope.launch {
            getAllowGeoKeyUseCase().collect { _allowGeoKey.value = it }
        }
    }


    fun loadHistoryDetails(historyId: Long) {
        viewModelScope.launch {
            getHistoryByIdUseCase(historyId).collect { historyModel ->
                if (historyModel != null) {
                    _result.value = historyModel
                    _info.value = transformPredictionsUseCase(historyModel.topPredictions)
                } else {
                    _result.value = createDefaultHistoryModel()
                    _info.value = emptyList()
                }
            }
        }
    }

    private fun createDefaultHistoryModel(): HistoryModel {
        return HistoryModel(
            id = -1,
            timestamp = 0,
            imagePath = "",
            topPredictions = emptyList(),
            latitude = null,
            longitude = null
        )
    }

    fun submitFeedback(
        plantName: String, diseaseName: String, additionalInfo: String?
    ) {
        viewModelScope.launch {
            val currentResult = _result.value
            val imagePath = currentResult.imagePath

            val file = File(imagePath)
            val imageData: ByteArray? = if (file.exists()) {
                withContext(Dispatchers.IO) {
                    try {
                        FileInputStream(file).use { it.readBytes() }
                    } catch (e: IOException) {
                        Log.e("ResultVM", "Error reading image file for feedback", e)
                        null
                    }
                }
            } else {
                Log.w("ResultVM", "Image file for feedback does not exist: $imagePath")
                null
            }

            if (imageData == null) {
                Toast.makeText(
                    appContext, "Помилка: файл зображення не знайдено", Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val currentLocModel: LocationModel? =
                if (_allowGeoKey.value == true && hasLocationPermission()) {
                    getCurrentLocationSuspend()?.let { LocationModel(it.latitude, it.longitude) }
                } else null

            try {
                when (val feedbackResource = submitFeedbackToRemoteUseCase(
                    timestamp = currentResult.timestamp,
                    resultJson = Gson().toJson(currentResult.topPredictions),
                    correctPlant = plantName,
                    correctDisease = diseaseName,
                    additionalInfo = additionalInfo,
                    imageData = imageData,
                    location = currentLocModel
                )) {
                    is Resource.Success -> {
                        Toast.makeText(appContext, "Відгук надіслано, дякуємо!", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Resource.Error -> {
                        Log.e("ResultVM", "Failed to submit feedback: ${feedbackResource.message}")
                        Toast.makeText(
                            appContext,
                            feedbackResource.message ?: "Помилка надсилання відгуку",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> { /* Loading or unexpected */
                    }
                }
            } catch (e: Exception) {
                Log.e("ResultVM", "Exception during feedback submission", e)
                Toast.makeText(appContext, "Сталася помилка", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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

}