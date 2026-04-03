package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan)


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.detection

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.detection -> true
                R.id.market -> {
                    startActivity(Intent(this, Shopping::class.java))
                    true
                }
                R.id.home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.animals -> {
                    startActivity(Intent(this, CowListActivity::class.java))
                    finish()
                    true
                }
                R.id.farm -> {
                    startActivity(Intent(this, AddCowActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // ✅ CHANGED: Launch CameraActivity instead of DetectActivity directly
        val btnScan = findViewById<RelativeLayout>(R.id.btnScan)
        btnScan.setOnClickListener {
            startActivity(Intent(this, DetectActivity::class.java))
        }

        // Handle other buttons
        val btnGallery = findViewById<RelativeLayout>(R.id.btnGallery)
        btnGallery.setOnClickListener {
            // TODO: Gallery picker -> CameraActivity or DetectActivity
            Toast.makeText(this, "Gallery picker coming soon", Toast.LENGTH_SHORT).show()
        }

        val btnAnimals = findViewById<RelativeLayout>(R.id.btnAnimals)
        btnAnimals.setOnClickListener {
            startActivity(Intent(this, CowListActivity::class.java))
        }

        val btnHistory = findViewById<RelativeLayout>(R.id.btnHistory)
        btnHistory.setOnClickListener {
            Toast.makeText(this, "History coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}
