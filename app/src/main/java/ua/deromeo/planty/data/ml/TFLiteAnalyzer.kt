package ua.deromeo.planty.data.ml

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import ua.deromeo.planty.domain.model.PredictionModel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class TFLiteAnalyzer @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private val imageSize: Int = 256
    private val numClasses: Int = 88

    private val interpreter: Interpreter by lazy {
        val model = FileUtil.loadMappedFile(appContext, "model.tflite")
        Interpreter(model)
    }

    private val classes: List<String> by lazy {
        listOf(
            "Apple__black_rot",
            "Apple__healthy",
            "Apple__rust",
            "Apple__scab",
            "Cassava__bacterial_blight",
            "Cassava__brown_streak_disease",
            "Cassava__green_mottle",
            "Cassava__healthy",
            "Cassava__mosaic_disease",
            "Cherry__healthy",
            "Cherry__powdery_mildew",
            "Chili__healthy",
            "Chili__leaf_curl",
            "Chili__leaf_spot",
            "Chili__whitefly",
            "Chili__yellowish",
            "Coffee__cercospora_leaf_spot",
            "Coffee__healthy",
            "Coffee__red_spider_mite",
            "Coffee__rust",
            "Corn__common_rust",
            "Corn__gray_leaf_spot",
            "Corn__healthy",
            "Corn__northern_leaf_blight",
            "Cucumber__diseased",
            "Cucumber__healthy",
            "Gauva__diseased",
            "Gauva__healthy",
            "Grape__black_measles",
            "Grape__black_rot",
            "Grape__healthy",
            "Grape__leaf_blight_(isariopsis_leaf_spot)",
            "Jamun__diseased",
            "Jamun__healthy",
            "Lemon__diseased",
            "Lemon__healthy",
            "Mango__diseased",
            "Mango__healthy",
            "Peach__bacterial_spot",
            "Peach__healthy",
            "Pepper_bell__bacterial_spot",
            "Pepper_bell__healthy",
            "Pomegranate__diseased",
            "Pomegranate__healthy",
            "Potato__early_blight",
            "Potato__healthy",
            "Potato__late_blight",
            "Rice__brown_spot",
            "Rice__healthy",
            "Rice__hispa",
            "Rice__leaf_blast",
            "Rice__neck_blast",
            "Soybean__bacterial_blight",
            "Soybean__caterpillar",
            "Soybean__diabrotica_speciosa",
            "Soybean__downy_mildew",
            "Soybean__healthy",
            "Soybean__mosaic_virus",
            "Soybean__powdery_mildew",
            "Soybean__rust",
            "Soybean__southern_blight",
            "Strawberry__healthy",
            "Strawberry__leaf_scorch",
            "Sugarcane__bacterial_blight",
            "Sugarcane__healthy",
            "Sugarcane__red_rot",
            "Sugarcane__red_stripe",
            "Sugarcane__rust",
            "Tea__algal_leaf",
            "Tea__anthracnose",
            "Tea__bird_eye_spot",
            "Tea__brown_blight",
            "Tea__healthy",
            "Tea__red_leaf_spot",
            "Tomato__bacterial_spot",
            "Tomato__early_blight",
            "Tomato__healthy",
            "Tomato__late_blight",
            "Tomato__leaf_mold",
            "Tomato__mosaic_virus",
            "Tomato__septoria_leaf_spot",
            "Tomato__spider_mites_(two_spotted_spider_mite)",
            "Tomato__target_spot",
            "Tomato__yellow_leaf_curl_virus",
            "Wheat__brown_rust",
            "Wheat__healthy",
            "Wheat__septoria",
            "Wheat__yellow_rust"
        )
    }

    suspend fun analyzeImage(
        bitmap: Bitmap, onProgressUpdate: suspend (Float) -> Unit
    ): List<PredictionModel> {
        return withContext(Dispatchers.Default) { // Виконуємо важку роботу в фоновому потоці
            onProgressUpdate(0.05f)
            delay(50)

            val resizedBitmap = bitmap.scale(imageSize, imageSize)
            onProgressUpdate(0.10f)
            delay(50)

            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            onProgressUpdate(0.15f)
            delay(50)

            val intValues = IntArray(imageSize * imageSize)
            resizedBitmap.getPixels(intValues, 0, imageSize, 0, 0, imageSize, imageSize)
            onProgressUpdate(0.20f)
            delay(50)

            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val value = intValues[pixel++]
                    byteBuffer.putFloat(((value shr 16) and 0xFF) / 255f)
                    byteBuffer.putFloat(((value shr 8) and 0xFF) / 255f)
                    byteBuffer.putFloat((value and 0xFF) / 255f)
                }
            }
            onProgressUpdate(0.40f)
            delay(50)

            val inputBuffer = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32
            )
            inputBuffer.loadBuffer(byteBuffer)
            onProgressUpdate(0.50f)
            delay(50)


            val outputBufferArray = Array(1) { FloatArray(numClasses) }
            interpreter.run(inputBuffer.buffer, outputBufferArray)
            onProgressUpdate(0.75f)
            delay(50)

            val confidences = outputBufferArray[0]
            val top5 = confidences.mapIndexed { index, confidence -> index to confidence }
                .sortedByDescending { it.second }.take(5)
                .map { (i, conf) -> PredictionModel(getLabel(i), conf) }
            onProgressUpdate(0.90f)
            delay(50)
            top5
        }
    }

    private fun getLabel(index: Int): String {
        return classes.getOrElse(index) { "Unknown" }
    }

    fun closeInterpreter() {
        interpreter.close()
    }
}