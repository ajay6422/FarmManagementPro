package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var pwd: EditText
    private lateinit var loginBtn: Button
    private lateinit var forgetpwd: TextView
    private lateinit var noaccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        forgetpwd = findViewById(R.id.fpwd)
        noaccount = findViewById(R.id.textView3)
        email = findViewById(R.id.etEmail)
        pwd = findViewById(R.id.etPwd)
        loginBtn = findViewById(R.id.btnlogin)

        forgetpwd.setOnClickListener {
            startActivity(Intent(this, ForgotPwd::class.java))
            finish()
        }

        noaccount.setOnClickListener {
            startActivity(Intent(this, CreateAccount::class.java))
            finish()
        }

        loginBtn.setOnClickListener {
            val emailtxt = email.text.toString().trim()
            val pwdtxt = pwd.text.toString().trim()

            if (emailtxt.isNotEmpty() && pwdtxt.isNotEmpty()) {
                loginUser(emailtxt, pwdtxt)
            } else {
                Toast.makeText(this, "Enter details", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        task.exception?.message ?: "Login failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}