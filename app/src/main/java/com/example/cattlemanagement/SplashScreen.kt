package com.example.cattlemanagement

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var securePrefs: SharedPreferences

    companion object {
        private const val SPLASH_DELAY = 2000L
        private const val KEY_USER_PIN = "USER_PIN"
        private const val KEY_PIN_ENABLED = "PIN_ENABLED"
        private const val KEY_BIOMETRIC_ENABLED = "BIOMETRIC_ENABLED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()
        securePrefs = SecurePrefs.create(this)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateNext()
        }, SPLASH_DELAY)
    }

    private fun navigateNext() {
        val currentUser = auth.currentUser
        val savedPin = securePrefs.getString(KEY_USER_PIN, null)
        val isPinEnabled = securePrefs.getBoolean(KEY_PIN_ENABLED, false)
        val isBiometricEnabled = securePrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

        val intent = when {
            currentUser == null -> {
                Intent(this, MainActivity::class.java)
            }

            isPinEnabled && !savedPin.isNullOrEmpty() -> {
                Intent(this, PinActivity::class.java)
            }

            isBiometricEnabled -> {
                Intent(this, PinActivity::class.java)
            }

            else -> {
                Intent(this, DashboardActivity::class.java)
            }
        }

        startActivity(intent)
        finish()
    }
}