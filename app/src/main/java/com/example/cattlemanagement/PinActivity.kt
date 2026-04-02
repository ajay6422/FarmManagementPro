package com.example.cattlemanagement

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class xxPinActivity : AppCompatActivity() {

    private lateinit var dots: Array<ImageView>
    private lateinit var tvTitle: TextView
    private lateinit var forgetPin: TextView

    private lateinit var sharedPreferences: SharedPreferences

    private var enteredPin = ""
    private var firstPin = ""
    private var isCreatingPin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)
        forgetPin = findViewById(R.id.tvForgotPin)
        forgetPin.setOnClickListener {
            val intent = Intent(this,ForgetPin::class.java)
            startActivity(intent)
            finish()
        }


        tvTitle = findViewById(R.id.tvTitle)

        dots = arrayOf(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3),
            findViewById(R.id.dot4),
            findViewById(R.id.dot5),
            findViewById(R.id.dot6)
        )

        sharedPreferences = getSharedPreferences("PIN_PREF", MODE_PRIVATE)

        // Check if PIN already exists
        if (!sharedPreferences.contains("USER_PIN")) {
            isCreatingPin = true
            tvTitle.text = "Create New PIN"
        } else {
            tvTitle.text = "Enter PIN"
        }

        setupKeypad()
        setupDelete()
    }

    // ===================== KEYPAD =====================
    private fun setupKeypad() {
        val keys = listOf(
            R.id.key0, R.id.key1, R.id.key2, R.id.key3,
            R.id.key4, R.id.key5, R.id.key6,
            R.id.key7, R.id.key8, R.id.key9
        )

        for (id in keys) {
            findViewById<TextView>(id).setOnClickListener {
                if (enteredPin.length < 6) {
                    enteredPin += (it as TextView).text.toString()
                    updateDots()

                    if (enteredPin.length == 6) {
                        handlePin()
                    }
                }
            }
        }
    }

    // ===================== DELETE =====================
    private fun setupDelete() {
        findViewById<ImageView>(R.id.keyDelete).setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.dropLast(1)
                updateDots()
            }
        }
    }

    // ===================== DOTS =====================
    private fun updateDots() {
        for (i in dots.indices) {
            if (i < enteredPin.length) {
                dots[i].setImageResource(R.drawable.dot_filled)
            } else {
                dots[i].setImageResource(R.drawable.dot_empty)
            }
        }
    }

    // ===================== PIN LOGIC =====================
    private fun handlePin() {
        if (isCreatingPin) {

            if (firstPin.isEmpty()) {
                // First entry
                firstPin = enteredPin
                enteredPin = ""
                updateDots()
                tvTitle.text = "Confirm PIN"

            } else {
                // Confirm PIN
                if (firstPin == enteredPin) {
                    sharedPreferences.edit()
                        .putString("USER_PIN", firstPin)
                        .apply()

                    Toast.makeText(this, "PIN Created Successfully", Toast.LENGTH_SHORT).show()
                    openHome()
                } else {
                    Toast.makeText(this, "PIN does not match", Toast.LENGTH_SHORT).show()
                    resetAll()
                }
            }

        } else {
            // Verify PIN
            val savedPin = sharedPreferences.getString("USER_PIN", "")

            if (enteredPin == savedPin) {
                openHome()
            } else {
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
                enteredPin = ""
                updateDots()
            }
        }
    }

    // ===================== RESET =====================
    private fun resetAll() {
        enteredPin = ""
        firstPin = ""
        updateDots()
        tvTitle.text = "Create New PIN"
    }

    // ===================== GO TO HOME =====================
    private fun openHome() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
