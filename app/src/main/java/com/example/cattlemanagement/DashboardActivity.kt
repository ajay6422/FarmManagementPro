package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var tvPreg: TextView
    private lateinit var tvTodayMilk: TextView
    private lateinit var tvMonthMilk: TextView

    private lateinit var button : Button

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuBtn: ImageView
    private lateinit var navigationView: NavigationView



    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val cardView : CardView = findViewById(R.id.cardAnimals)
        cardView.setOnClickListener{
            val intent = Intent(this, CowListActivity::class.java)
            startActivity(intent)
        }

        tvTotal = findViewById(R.id.tvTotalAnimals)
        tvPreg = findViewById(R.id.tvPregCount)
        tvTodayMilk = findViewById(R.id.tvTodayMilk)
        tvMonthMilk = findViewById(R.id.tvMonthMilk)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuBtn = findViewById(R.id.menuBtn)
        navigationView = findViewById(R.id.navigationView)

        // 🔥 Drawer Click
        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        // 🔥 Drawer Menu Clicks
        setupDrawerMenu()

        loadDashboardData()
        setupQuickActions()
        setupBottomNavigation()




        button = findViewById(R.id.btnChat)
        button.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }



    }
    private fun setupDrawerMenu() {

        navigationView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_dashboard -> {
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    true
                }

                R.id.nav_cattle -> {
                    startActivity(Intent(this, CowListActivity::class.java))
                    true
                }

                R.id.nav_milk -> {
                    startActivity(Intent(this, RecordMilkActivity::class.java))
                    true
                }

                R.id.nav_health -> {
                    startActivity(Intent(this, HealthActivity::class.java))
                    true
                }

                R.id.nav_pregnancy -> {
                    startActivity(Intent(this, PregnancyActivity::class.java))
                    true
                }

                R.id.nav_market -> {
                    // startActivity(Intent(this, ShopActivity::class.java))
                    true
                }

                R.id.nav_ai -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.nav_logout -> {
                    finish()
                    true
                }

                else -> false
            }
        }
    }


    // ✅ BOTTOM NAVIGATION (UNCHANGED)
    private fun setupBottomNavigation() {

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.market -> {
                     startActivity(Intent(this, Shopping::class.java))
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

    // ✅ QUICK ACTIONS (UNCHANGED)
    private fun setupQuickActions() {

        findViewById<MaterialCardView>(R.id.cardMilk).setOnClickListener {
            startActivity(Intent(this, RecordMilkActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardHealth).setOnClickListener {
            startActivity(Intent(this, HealthActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardPregnancy).setOnClickListener {
            startActivity(Intent(this, PregnancyActivity::class.java))
        }
    }

    // ✅ DASHBOARD DATA (FIXED MILK LOGIC)
    private fun loadDashboardData() {

        // 🐄 TOTAL ANIMALS
        db.child("cows").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvTotal.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // 🤰 ACTIVE PREGNANCIES
        db.child("cows").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var count = 0

                for (snap in snapshot.children) {
                    if (snap.child("pregnancy").exists()) {
                        count++
                    }
                }

                tvPreg.text = count.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // 🥛 MILK DATA (✅ FIXED)
        db.child("milk_records").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var todayTotal = 0.0
                var monthTotal = 0.0

                val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val month = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())

                for (snap in snapshot.children) {

                    val date = snap.child("date").value?.toString() ?: ""
                    val milk = snap.child("milk").value?.toString()?.toDoubleOrNull() ?: 0.0

                    // ✅ HANDLE BOTH FORMATS (05/03/2026 + 5/3/2026)
                    if (date == today || date == removeLeadingZero(today)) {
                        todayTotal += milk
                    }

                    if (date.contains(month) || date.contains(removeLeadingZero(month))) {
                        monthTotal += milk
                    }
                }

                tvTodayMilk.text = "%.2f".format(todayTotal)
                tvMonthMilk.text = "%.2f".format(monthTotal)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ✅ HELPER FUNCTION (IMPORTANT)
    private fun removeLeadingZero(date: String): String {
        return date.replace("/0", "/")
    }
}