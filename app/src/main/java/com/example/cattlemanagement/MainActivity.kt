package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var email: EditText
    private lateinit var pwd: EditText
    private lateinit var loginBtn: Button

    private lateinit var forgetpwd: TextView
    private lateinit var noaccount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)



        forgetpwd  =findViewById(R.id.fpwd)
        forgetpwd.setOnClickListener {
            val intent = Intent(this, ForgotPwd::class.java)
            startActivity(intent)
            finish()
        }
        noaccount = findViewById(R.id.textView3)
        noaccount.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
            finish()
        }

        auth  = FirebaseAuth.getInstance()
        email = findViewById(R.id.etEmail)
        pwd = findViewById(R.id.etPwd)
        loginBtn = findViewById(R.id.btnlogin)
        loginBtn.setOnClickListener {
            val emailtxt = email.text.toString()
            val pwdtxt = pwd.text.toString()
            if(emailtxt.isNotEmpty() && pwdtxt.isNotEmpty()){
                loginUser(emailtxt,pwdtxt)
            }else{
                Toast.makeText(this, "Enter Details ", Toast.LENGTH_LONG).show()
            }
        }
    }
    private  fun loginUser(email:String, password: String){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener (this){task ->
            if(task.isSuccessful){
                Toast.makeText(this,"LoginSucessful", Toast.LENGTH_LONG).show()
                val intent = Intent(this, PinActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"Login Fail", Toast.LENGTH_LONG).show()
            }

        }
    }
}