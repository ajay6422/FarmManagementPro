package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var name: EditText
    private lateinit var farmName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnSignup: Button
    private lateinit var backBtn: ImageView

    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)

        auth = FirebaseAuth.getInstance()

        backBtn = findViewById(R.id.imgback)
        name = findViewById(R.id.etName)
        farmName = findViewById(R.id.etFarmName)
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPwd)
        btnSignup = findViewById(R.id.btnSignup)

        backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnSignup.setOnClickListener {
            val nameTxt = name.text.toString().trim()
            val farmNameTxt = farmName.text.toString().trim()
            val emailTxt = email.text.toString().trim()
            val pwdTxt = password.text.toString().trim()

            if (nameTxt.isEmpty() || farmNameTxt.isEmpty() || emailTxt.isEmpty() || pwdTxt.isEmpty()) {
                Toast.makeText(this, "Please fill all details first", Toast.LENGTH_LONG).show()
            } else if (pwdTxt.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
            } else {
                registerUser(nameTxt, farmNameTxt, emailTxt, pwdTxt)
            }
        }
    }

    private fun registerUser(name: String, farmName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid

                    if (uid == null) {
                        Toast.makeText(this, "User ID not found", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }

                    val userMap = HashMap<String, Any>()
                    userMap["name"] = name
                    userMap["farmName"] = farmName
                    userMap["email"] = email

                    database.child("users").child(uid).setValue(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration done successfully", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finishAffinity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, e.message ?: "Failed to save profile", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(
                        this,
                        task.exception?.message ?: "Error in registration",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}