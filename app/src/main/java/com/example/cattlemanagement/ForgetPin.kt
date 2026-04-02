package com.example.cattlemanagement

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ForgetPin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var verify: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_pin)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.etEmail)
        verify = findViewById(R.id.btnVerify)
        verify.setOnClickListener {
            val emailtxt = email.text.toString()
            if (emailtxt.isEmpty()) {
                email.error = "Enter Email"
                return@setOnClickListener

            }
            auth.sendPasswordResetEmail(emailtxt)
                .addOnSuccessListener {
                    Toast.makeText(this, "Reset link sent", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

        }


    }

}
