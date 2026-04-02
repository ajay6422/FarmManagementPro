package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ForgotPwd : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var request: Button
    private lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_pwd)
        back = findViewById(R.id.imgback)
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.etEmail)
        request = findViewById(R.id.btnReq)
        request.setOnClickListener {
            val emailtxt = email.text.toString().trim()
            if(emailtxt.isEmpty()){
                email.error = "Please Enter Your Registered Email...."
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(emailtxt).addOnCompleteListener(this) { task->
                if(task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Reset Link Is Sent to your Regirtered Email",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,"Error Sending mail", Toast.LENGTH_LONG).show()
                }


        }

        }


    }
}