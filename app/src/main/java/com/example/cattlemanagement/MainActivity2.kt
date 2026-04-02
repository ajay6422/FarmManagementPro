package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.home

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> true
                R.id.market -> {
                    //startActivity(Intent(this, ShopActivity::class.java))
                    true
                }

                R.id.animals -> {
                    startActivity(Intent(this, CowListActivity::class.java))
                    true
                }

                R.id.detection -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.farm -> {
                    startActivity(Intent(this, AddCowActivity::class.java))
                    true
                }

                else -> false
            }
        }





    }
}