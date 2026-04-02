package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class Shopping: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping)


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.market

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.market -> true
                R.id.detection -> {
                     startActivity(Intent(this, ScanActivity::class.java))
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

        // Toolbar Buttons
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val cartBtn = findViewById<ImageView>(R.id.cartBtn)

        backBtn.setOnClickListener {
            finish()
        }

        cartBtn.setOnClickListener {
            // Open Cart Activity (create later)
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Category Cards (Access by position)
        val feedCard = findViewById<CardView>(R.id.feedCard)
        val healthCard = findViewById<CardView>(R.id.healthCard)
        val machineCard = findViewById<CardView>(R.id.machineCard)
        val productCard = findViewById<CardView>(R.id.productCard)
        val vendorCard = findViewById<CardView>(R.id.vendorCard)

        // Feed Click
        feedCard.setOnClickListener {
            openCategory("Feed and Nutrition")
        }

        // Health Click
        healthCard.setOnClickListener {
            openCategory("Health and Medicines")
        }

        // Machines Click
        machineCard.setOnClickListener {
            openCategory("Machines")
        }

        // Products Click
        productCard.setOnClickListener {
            openCategory("Products")
        }

        // Vendor Click
        vendorCard.setOnClickListener {
            startActivity(Intent(this, Become_vendor::class.java))
        }
    }

    private fun openCategory(categoryName: String) {
        val intent = Intent(this, category_details::class.java)
        intent.putExtra("category_name", categoryName)
        startActivity(intent)
    }
}