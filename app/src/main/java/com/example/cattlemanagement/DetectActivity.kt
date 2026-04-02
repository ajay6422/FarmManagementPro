package com.example.cattlemanagement

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.camera.core.Camera  // ✅ EXPLICIT Camera import
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cattlemanagement.databinding.ActivityDetectBinding
import com.example.cattlemanagement.ml.Classifier
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetectActivity : AppCompatActivity() {
    private var binding: ActivityDetectBinding? = null
    private lateinit var classifier: Classifier
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private var currentBreed = ""
    private val TAG = "DetectActivity"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startCamera() else showResult("Camera permission needed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        initModel()
        setupClickListeners()
        checkCameraPermission()
    }

    private fun initModel() {
        try {
            classifier = Classifier(assets)
            classifier.init()
            showResult("✅ Ready! Tap Capture")
            Log.d(TAG, "✅ Model ready")
        } catch (e: Exception) {
            Log.e(TAG, "Model failed", e)
            showResult("⚠️ Demo mode active")
        }
    }

    private fun setupClickListeners() {
        binding?.btnCapture?.setOnClickListener { takePhoto() }
        binding?.txtLink?.setOnClickListener {
            if (currentBreed.isNotEmpty()) openWikipedia(currentBreed)
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
            Handler(Looper.getMainLooper()).postDelayed({ startCamera() }, 500)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding?.previewView?.surfaceProvider)
                }
                imageCapture = ImageCapture.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                showResult("📷 Point at cow & tap Capture")
            } catch (e: Exception) {
                Log.e(TAG, "Camera error", e)
                showResult("Camera failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return showResult("Camera not ready")
        showResult("🔄 Processing...")

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    // ✅ UI Thread processing - NO background crash
                    try {
                        val bitmap = imageProxyToBitmap(image)
                        Log.d(TAG, "📸 Captured: ${bitmap.width}x${bitmap.height}")

                        val (breed, accuracy) = classifier.classify(bitmap)
                        currentBreed = breed

                        binding?.txtResult?.text = "Breed: $breed\nAccuracy: ${String.format("%.1f", accuracy * 100)}%"
                        binding?.txtLink?.text = "👆 Tap for $breed info"
                        binding?.txtLink?.isClickable = true

                        Log.d(TAG, "✅ SUCCESS: $breed (${accuracy * 100}%)")
                    } catch (e: Exception) {
                        Log.e(TAG, "Processing error", e)
                        showResult("Error: Try again")
                    } finally {
                        image.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Capture error", exception)
                    showResult("Capture failed")
                }
            }
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size).let { bitmap ->
            val matrix = Matrix().apply {
                postRotate(image.imageInfo.rotationDegrees.toFloat())
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }

    private fun openWikipedia(breed: String) {
        try {
            val query = breed.lowercase().replace(" ", "_")
            val uri = Uri.parse("https://en.wikipedia.org/wiki/$query")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            Log.e(TAG, "Wikipedia failed", e)
        }
    }

    private fun showResult(text: String) {
        Handler(Looper.getMainLooper()).post {
            binding?.txtResult?.text = text
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            cameraExecutor.shutdown()
        } catch (e: Exception) {}
    }
}
