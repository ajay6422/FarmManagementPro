package com.example.cattlemanagement

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class PinActivity : AppCompatActivity() {

    private lateinit var dots: Array<ImageView>
    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvForgotPin: TextView
    private lateinit var btnBiometric: ImageView
    private lateinit var keyDelete: ImageView
    private lateinit var securePrefs: SharedPreferences

    private var enteredPin = ""
    private var firstPin = ""
    private var isCreatingPin = false
    private var wrongAttempts = 0
    private var screenMode = MODE_UNLOCK

    companion object {
        private const val PIN_LENGTH = 6
        private const val MAX_ATTEMPTS = 5

        const val KEY_USER_PIN = "USER_PIN"
        const val KEY_PIN_ENABLED = "PIN_ENABLED"
        const val KEY_BIOMETRIC_ENABLED = "BIOMETRIC_ENABLED"

        const val MODE_CREATE = "create"
        const val MODE_UNLOCK = "unlock"
        const val MODE_DISABLE_LOCK = "disable_lock"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        securePrefs = SecurePrefs.create(this)
        screenMode = intent.getStringExtra("mode") ?: MODE_UNLOCK

        initViews()
        setupScreenState()
        setupKeypad()
        setupDelete()
        setupForgotPin()
        setupBiometricButton()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvTitle)
        tvSubtitle = findViewById(R.id.tvSubtitle)
        tvForgotPin = findViewById(R.id.tvForgotPin)
        btnBiometric = findViewById(R.id.btnBiometric)
        keyDelete = findViewById(R.id.keyDelete)

        dots = arrayOf(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3),
            findViewById(R.id.dot4),
            findViewById(R.id.dot5),
            findViewById(R.id.dot6)
        )
    }

    private fun setupScreenState() {
        val savedPin = securePrefs.getString(KEY_USER_PIN, null)

        when {
            screenMode == MODE_CREATE -> {
                isCreatingPin = true
                tvTitle.text = "Create PIN"
                tvSubtitle.text = "Set a 6-digit PIN to protect your data"
                tvForgotPin.visibility = View.GONE
            }

            screenMode == MODE_DISABLE_LOCK -> {
                isCreatingPin = false
                tvTitle.text = "Verify PIN"
                tvSubtitle.text = "Enter PIN to disable app lock"
                tvForgotPin.visibility = View.GONE
            }

            savedPin.isNullOrEmpty() -> {
                isCreatingPin = true
                tvTitle.text = "Create PIN"
                tvSubtitle.text = "Set a 6-digit PIN to protect your data"
                tvForgotPin.visibility = View.GONE
            }

            else -> {
                isCreatingPin = false
                tvTitle.text = "Enter PIN"
                tvSubtitle.text = "Unlock your app securely"
                tvForgotPin.visibility = View.VISIBLE
            }
        }
    }

    private fun setupKeypad() {
        val keyIds = listOf(
            R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4,
            R.id.key5, R.id.key6, R.id.key7, R.id.key8, R.id.key9
        )

        keyIds.forEach { id ->
            findViewById<TextView>(id).setOnClickListener { view ->
                if (enteredPin.length < PIN_LENGTH) {
                    enteredPin += (view as TextView).text.toString()
                    updateDots()

                    if (enteredPin.length == PIN_LENGTH) {
                        handlePin()
                    }
                }
            }
        }
    }

    private fun setupDelete() {
        keyDelete.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.dropLast(1)
                updateDots()
            }
        }

        keyDelete.setOnLongClickListener {
            enteredPin = ""
            updateDots()
            true
        }
    }

    private fun setupForgotPin() {
        tvForgotPin.setOnClickListener {
            startActivity(Intent(this, ForgetPin::class.java))
        }
    }

    private fun setupBiometricButton() {
        val biometricEnabled = securePrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

        if (isCreatingPin || screenMode == MODE_DISABLE_LOCK || !biometricEnabled || !isBiometricAvailable()) {
            btnBiometric.visibility = View.GONE
            return
        }

        btnBiometric.visibility = View.VISIBLE
        btnBiometric.setOnClickListener {
            showBiometricPromptForUnlock()
        }
    }

    private fun updateDots() {
        for (i in dots.indices) {
            if (i < enteredPin.length) {
                dots[i].setImageResource(R.drawable.dot_filled)
            } else {
                dots[i].setImageResource(R.drawable.dot_empty)
            }
        }
    }

    private fun handlePin() {
        if (isCreatingPin) {
            createPinFlow()
        } else {
            verifyPinFlow()
        }
    }

    private fun createPinFlow() {
        if (firstPin.isEmpty()) {
            firstPin = enteredPin
            enteredPin = ""
            updateDots()
            tvTitle.text = "Confirm PIN"
            tvSubtitle.text = "Re-enter the same 6-digit PIN"
        } else {
            if (firstPin == enteredPin) {
                securePrefs.edit()
                    .putString(KEY_USER_PIN, firstPin)
                    .putBoolean(KEY_PIN_ENABLED, true)
                    .apply()

                Toast.makeText(this, "PIN created successfully", Toast.LENGTH_SHORT).show()
                openDashboard()
            } else {
                Toast.makeText(this, "PIN does not match", Toast.LENGTH_SHORT).show()
                resetPinCreation()
            }
        }
    }

    private fun verifyPinFlow() {
        val savedPin = securePrefs.getString(KEY_USER_PIN, null)

        if (enteredPin == savedPin) {
            wrongAttempts = 0

            when (screenMode) {
                MODE_DISABLE_LOCK -> {
                    securePrefs.edit()
                        .remove(KEY_USER_PIN)
                        .putBoolean(KEY_PIN_ENABLED, false)
                        .putBoolean(KEY_BIOMETRIC_ENABLED, false)
                        .apply()

                    Toast.makeText(this, "App lock disabled successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }

                else -> {
                    Toast.makeText(this, "PIN verified", Toast.LENGTH_SHORT).show()
                    openDashboard()
                }
            }
        } else {
            wrongAttempts++
            enteredPin = ""
            updateDots()

            if (wrongAttempts >= MAX_ATTEMPTS) {
                Toast.makeText(this, "Too many wrong attempts", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Wrong PIN. Attempts left: ${MAX_ATTEMPTS - wrongAttempts}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun resetPinCreation() {
        enteredPin = ""
        firstPin = ""
        updateDots()
        tvTitle.text = "Create PIN"
        tvSubtitle.text = "Set a 6-digit PIN to protect your data"
    }

    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            ) == BiometricManager.BIOMETRIC_SUCCESS
        } else {
            biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
        }
    }

    private fun showBiometricPromptForUnlock() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@PinActivity, "Biometric verified", Toast.LENGTH_SHORT).show()
                    openDashboard()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@PinActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@PinActivity, errString, Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock App")
            .setSubtitle("Use biometric to unlock your app")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptBuilder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            promptBuilder.setDeviceCredentialAllowed(true)
        }

        biometricPrompt.authenticate(promptBuilder.build())
    }

    private fun openDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}