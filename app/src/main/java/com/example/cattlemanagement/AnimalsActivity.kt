package com.ajaykumar.cattlemanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AnimalsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_animals)

        val bottonNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation2)
        bottonNavigation.selectedItemId = R.id.animals

        bottonNavigation.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.animals -> {
                    startActivity(Intent(this, CowListActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    true
                }

                R.id.home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                R.id.farm -> {
                    startActivity(Intent(this, AddCowActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                else -> true
            }
        }

    }
}
