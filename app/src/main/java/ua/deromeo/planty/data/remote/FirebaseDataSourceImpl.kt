package ua.deromeo.planty.data.remote

import android.util.Log
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import ua.deromeo.planty.domain.model.LocationModel
import ua.deromeo.planty.util.Resource
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor() : RemoteDataSource {

    private val storage = Firebase.storage
    private val database = Firebase.database.reference

    override suspend fun uploadDiagnosisImage(
        imageData: ByteArray, fileName: String
    ): Resource<String> {
        return try {
            val imageRef = storage.reference.child("diagnoses/$fileName")
            imageRef.putBytes(imageData).await()
            val url = imageRef.downloadUrl.await().toString()
            Resource.Success(url)
        } catch (e: Exception) {
            Log.e("FirebaseDS", "Error uploading diagnosis image", e)
            Resource.Error(e.localizedMessage ?: "Failed to upload image")
        }
    }


    override suspend fun saveDiagnosisResult(
        resultJson: String, imageUrl: String, location: LocationModel?
    ): Resource<Unit> {
        return try {
            val diagnosisEntry = mapOf(
                "result" to resultJson,
                "timestamp" to ServerValue.TIMESTAMP,
                "imageUrl" to imageUrl,
                "latitude" to location?.latitude?.toString(),
                "longitude" to location?.longitude?.toString()
            ).filterValues { it != null }

            database.child("diagnoses").push().setValue(diagnosisEntry).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseDS", "Error saving diagnosis result", e)
            Resource.Error(e.localizedMessage ?: "Failed to save diagnosis result")
        }
    }

    override suspend fun uploadFeedbackImage(
        imageData: ByteArray, fileName: String
    ): Resource<String> {
        return try {
            val imageRef = storage.reference.child("feedbacks/$fileName")
            imageRef.putBytes(imageData).await()
            val url = imageRef.downloadUrl.await().toString()
            Resource.Success(url)
        } catch (e: Exception) {
            Log.e("FirebaseDS", "Error uploading feedback image", e)
            Resource.Error(e.localizedMessage ?: "Failed to upload feedback image")
        }
    }

    override suspend fun saveFeedback(
        timestamp: Long,
        resultJson: String,
        correctPlant: String,
        correctDisease: String,
        additionalInfo: String?,
        imageUrl: String,
        location: LocationModel?
    ): Resource<Unit> {

        return try {
            val feedbackEntry = mapOf(
                "timestamp" to timestamp,
                "result" to resultJson,
                "correctPlant" to correctPlant,
                "correctDisease" to correctDisease,
                "additionalInfo" to additionalInfo,
                "imageUrl" to imageUrl,
                "latitude" to location?.latitude?.toString(),
                "longitude" to location?.longitude?.toString()
            ).filterValues { it != null }

            database.child("feedbacks").push().setValue(feedbackEntry).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseDS", "Error saving feedback", e)
            Resource.Error(e.localizedMessage ?: "Failed to save feedback")
        }
    }
}