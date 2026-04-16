package com.ajaykumar.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class Shopping : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val cartBtn = findViewById<ImageView>(R.id.cartBtn)
        val searchBar = findViewById<EditText>(R.id.searchBar)

        val feedCard = findViewById<CardView>(R.id.feedCard)
        val healthCard = findViewById<CardView>(R.id.healthCard)
        val machineCard = findViewById<CardView>(R.id.machineCard)
        val productCard = findViewById<CardView>(R.id.productCard)
        val vendorCard = findViewById<CardView>(R.id.vendorCard)

        drawerLayout = findViewById(R.id.drawerLayout)
        bottomNavigation = findViewById(R.id.bottomNavigation1)



        backBtn.setOnClickListener {
            finish()
        }

        cartBtn.setOnClickListener {
            try {
                startActivity(Intent(this, CartActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Cart screen not available yet", Toast.LENGTH_SHORT).show()
            }
        }

        searchBar.setOnClickListener {
            Toast.makeText(this, "Search feature can be added next", Toast.LENGTH_SHORT).show()
        }

        feedCard.setOnClickListener { openCategory("Feed and Nutrition") }
        healthCard.setOnClickListener { openCategory("Health and Medicines") }
        machineCard.setOnClickListener { openCategory("Machines") }
        productCard.setOnClickListener { openCategory("Products") }

        vendorCard.setOnClickListener {
            try {
                startActivity(Intent(this, Become_vendor::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Vendor screen not available", Toast.LENGTH_SHORT).show()
            }
        }

        setupBottomNavigation()
        handleBackPressed()
    }

    private fun openCategory(categoryName: String) {
        try {
            val intent = Intent(this, category_details::class.java)
            intent.putExtra("category_name", categoryName)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "$categoryName screen not available", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation1)
        bottomNav.selectedItemId = R.id.market

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.market -> true

                R.id.home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }

                R.id.detection -> {
                    startActivity(Intent(this, ScanActivity::class.java))
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
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }
}                R.id.farm -> {
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
