package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgetPin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var verify: Button



    companion object {
        private const val KEY_USER_PIN = "USER_PIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_pin)

        val backToPin = findViewById<TextView>(R.id.tvBackToPin)
        backToPin.setOnClickListener {
            finish()
        }


        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.etEmail)
        verify = findViewById(R.id.btnVerify)

        verify.setOnClickListener {
            verifyAndResetPin()
        }
    }

    private fun verifyAndResetPin() {
        val emailText = email.text.toString().trim()

        if (emailText.isEmpty()) {
            email.error = "Enter email"
            email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.error = "Enter valid email"
            email.requestFocus()
            return
        }

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "No logged-in user found", Toast.LENGTH_LONG).show()
            return
        }

        val registeredEmail = currentUser.email

        if (registeredEmail.isNullOrEmpty()) {
            Toast.makeText(this, "No registered email found", Toast.LENGTH_LONG).show()
            return
        }

        if (emailText.equals(registeredEmail, ignoreCase = true)) {
            val prefs = SecurePrefs.create(this)
            prefs.edit()
                .remove(KEY_USER_PIN)
                .apply()

            Toast.makeText(this, "PIN reset successful. Create a new PIN.", Toast.LENGTH_LONG).show()

            val intent = Intent(this, PinActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Entered email does not match your account", Toast.LENGTH_LONG).show()
        }
    }
}