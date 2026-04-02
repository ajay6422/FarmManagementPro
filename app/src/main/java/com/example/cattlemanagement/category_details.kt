package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class category_details : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_category_details)

            val title = findViewById<TextView>(R.id.categoryTitle)
            val description = findViewById<TextView>(R.id.categoryDescription)
            val image = findViewById<ImageView>(R.id.categoryImage)
            val viewBtn = findViewById<Button>(R.id.viewProductsBtn)

            val categoryName = intent.getStringExtra("category_name")

            title.text = categoryName

            when (categoryName) {

                "Feed and Nutrition" -> {
                    description.text =
                        "High quality cattle feed improves milk production, strength and overall health of cows and buffaloes."

                }

                "Health and Medicines" -> {
                    description.text =
                        "Medicines and vaccines help prevent diseases and keep cattle healthy."

                }

                "Machines" -> {
                    description.text =
                        "Milking machines and equipment make farm work faster and efficient."

                }

                "Products" -> {
                    description.text = "Explore all available cattle related products in one place."
                }

                "Become Vendor" -> {
                    description.text =
                        "Register as a vendor and sell your cattle products through our platform."

                }
            }

            viewBtn.setOnClickListener {
                when (categoryName) {

                    "Feed and Nutrition" -> {
                        startActivity(Intent(this, FeedActivity::class.java))
                    }

                    "Health and Medicines" -> {
                        startActivity(Intent(this, Health::class.java))

                        // Later create MedicineActivity
                    }

                    "Machines" -> {
                        startActivity(Intent(this, Machines::class.java))

                        // Later create MachineActivity
                    }
                    "Products" -> {
                        startActivity(Intent(this, Products::class.java))
                    }
                    "Become Vendor" -> {
                        startActivity(Intent(this, Become_vendor::class.java))
                    }

                }
            }
        }
    }
