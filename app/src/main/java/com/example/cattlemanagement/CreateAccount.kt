package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class CreateAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnSignup: Button

    private lateinit var backBtn: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)

        backBtn  =findViewById(R.id.imgback)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()
        name = findViewById(R.id.etName)
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPwd)
        btnSignup = findViewById(R.id.btnSignup)

        btnSignup.setOnClickListener {
            val nametxt = name.text.toString()
            val emailtxt = email.text.toString()
            val pwdtxt = password.text.toString()
            if (nametxt.isNotEmpty() && emailtxt.isNotEmpty() && pwdtxt.isNotEmpty()) {
                registerUser(nametxt, emailtxt, pwdtxt)
            } else {
                Toast.makeText(this, "Please Fill Details First", Toast.LENGTH_LONG).show()
            }

        }



    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registration done successfully", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "error in registration", Toast.LENGTH_LONG).show()
            }
        }
    }

}
