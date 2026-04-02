package com.example.cattlemanagement.ml

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import android.content.res.AssetFileDescriptor
import kotlin.math.max  // For manual max finding
import kotlin.math.min

class Classifier(private val assetManager: AssetManager) {
    private var interpreter: Interpreter? = null
    private lateinit var labels: List<String>
    private val TAG = "BovineClassifier"
    private var inputSize = 224
    private var isInitialized = false

    fun init() {
        try {
            // Load model buffer first
            val modelBuffer = loadModelFile("bovine_model.tflite")
            Log.d(TAG, "✅ Model buffer loaded, size: ${modelBuffer.capacity()} bytes")

            // Create interpreter
            interpreter = Interpreter(modelBuffer)
            Log.d(TAG, "✅ Interpreter created successfully")

            // Get input tensor info safely
            val inputTensor = interpreter?.getInputTensor(0)
            if (inputTensor == null) {
                throw RuntimeException("Input tensor is null")
            }
            val inputShape = inputTensor.shape()
            if (inputShape.size != 4 || inputShape[0] != 1) {
                throw RuntimeException("Unexpected input shape: ${inputShape.contentToString()}")
            }
            inputSize = inputShape[1]  // Assume square: [1,H,W,3]
            Log.d(TAG, "✅ Input shape: ${inputShape.contentToString()}, using size: ${inputSize}x${inputSize}")

            // Load labels
            labels = loadLabels()

            isInitialized = true
            Log.d(TAG, "✅ ✅ FULLY LOADED: ${inputSize}x${inputSize}, ${labels.size} labels: ${labels.take(10).joinToString()}${if (labels.size > 10) "..." else ""}")

        } catch (e: Exception) {
            Log.e(TAG, "❌ MODEL INIT FAILED: ${e.message}", e)
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            interpreter?.close()
            interpreter = null
            isInitialized = false
            labels = listOf("Holstein", "Jersey", "Angus", "Hereford", "Simmental", "Unknown")
        }
    }

    private fun loadModelFile(filename: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(filename)
        fileDescriptor.use {
            val inputStream = FileInputStream(it.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = it.startOffset
            val declaredLength = it.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }

    private fun loadLabels(): List<String> {
        return try {
            assetManager.open("labels.txt").bufferedReader().useLines { lines ->
                lines.filter { it.isNotBlank() }.toList()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Labels file not found, using defaults", e)
            listOf("Holstein", "Jersey", "Angus", "Hereford", "Simmental")
        }
    }

    fun classify(bitmap: Bitmap): Pair<String, Float> {
        if (!isInitialized || interpreter == null) {
            Log.w(TAG, "Model not initialized")
            return Pair("Model not loaded", 0f)
        }

        return try {
            // Preprocess: resize to input size (center crop if needed)
            val resizedBitmap = preprocessImage(bitmap, inputSize)

            // Create input: [1, H, W, 3] float32, normalized to [-1, 1]
            val input = Array(1) { Array(inputSize) { Array(inputSize) { FloatArray(3) } } }
            for (x in 0 until inputSize) {
                for (y in 0 until inputSize) {
                    val pixel = resizedBitmap.getPixel(x, y)
                    input[0][y][x][0] = (Color.red(pixel) / 255f * 2f) - 1f
                    input[0][y][x][1] = (Color.green(pixel) / 255f * 2f) - 1f
                    input[0][y][x][2] = (Color.blue(pixel) / 255f * 2f) - 1f
                }
            }
            resizedBitmap.recycle()

            // Output buffer: [1, num_labels]
            val output = Array(1) { FloatArray(labels.size) }

            // Run inference
            interpreter!!.run(input, output)

            // Get top prediction MANUALLY (no maxOf)
            val probs = output[0]
            var maxIdx = 0
            var maxConfidence = probs[0]
            for (i in 1 until probs.size) {
                if (probs[i] > maxConfidence) {
                    maxConfidence = probs[i]
                    maxIdx = i
                }
            }

            val breed = labels.getOrNull(maxIdx) ?: "Unknown"

            Log.d(TAG, "✅ PRED: $breed (${String.format("%.1f", maxConfidence * 100)}%)")
            Pair(breed, maxConfidence.coerceAtMost(1f))

        } catch (e: Exception) {
            Log.e(TAG, "❌ CLASSIFY FAILED", e)
            Pair("Classification error", 0f)
        }
    }


    private fun preprocessImage(original: Bitmap, targetSize: Int): Bitmap {
        val originalWidth = original.width
        val originalHeight = original.height

        // Scale to cover target size (center crop aspect)
        val scale = maxOf(targetSize.toFloat() / originalWidth, targetSize.toFloat() / originalHeight)
        val scaledWidth = (originalWidth * scale).toInt()
        val scaledHeight = (originalHeight * scale).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true)

        // Center crop to exact square
        val startX = (scaledWidth - targetSize) / 2
        val startY = (scaledHeight - targetSize) / 2
        return Bitmap.createBitmap(scaledBitmap, startX, startY, targetSize, targetSize)
    }

    fun close() {
        interpreter?.close()
        interpreter = null
        isInitialized = false
    }
}
