package com.example.cattlemanagement

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var tvPreg: TextView
    private lateinit var tvTodayMilk: TextView
    private lateinit var tvMonthMilk: TextView
    private lateinit var tvHealthCount: TextView
    private lateinit var tvWelcomeName: TextView
    private lateinit var tvFarmOverview: TextView

    private lateinit var button: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuBtn: ImageView
    private lateinit var navigationView: NavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var securePrefs: SharedPreferences

    private val db = FirebaseDatabase.getInstance().reference

    companion object {
        private const val KEY_USER_PIN = "USER_PIN"
        private const val KEY_PIN_ENABLED = "PIN_ENABLED"
        private const val KEY_BIOMETRIC_ENABLED = "BIOMETRIC_ENABLED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        securePrefs = SecurePrefs.create(this)

        if (auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_dashboard)

        initViews()
        setupDrawer()
        setupDrawerMenu()
        setupBottomNavigation()
        setupQuickActions()
        setupTopCards()
        setupChatButton()
        loadUserProfile()
        loadDashboardData()
    }

    private fun initViews() {
        tvTotal = findViewById(R.id.tvTotalAnimals)
        tvPreg = findViewById(R.id.tvPregCount)
        tvTodayMilk = findViewById(R.id.tvTodayMilk)
        tvMonthMilk = findViewById(R.id.tvMonthMilk)
        tvHealthCount = findViewById(R.id.tvHealthCount)
        tvWelcomeName = findViewById(R.id.tvWelcomeName)
        tvFarmOverview = findViewById(R.id.tvFarmOverview)

        drawerLayout = findViewById(R.id.drawer_layout)
        menuBtn = findViewById(R.id.menuBtn)
        navigationView = findViewById(R.id.navigationView)
    }

    private fun setupDrawer() {
        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupChatButton() {
        button = findViewById(R.id.btnChat)
        button.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }

    private fun setupTopCards() {
        val cardAnimals = findViewById<CardView>(R.id.cardAnimals)
        val cardPregnancyStats = findViewById<MaterialCardView>(R.id.cardPregnancyStats)
        val cardTodayMilkStats = findViewById<MaterialCardView>(R.id.cardTodayMilkStats)
        val cardMonthMilkStats = findViewById<MaterialCardView>(R.id.cardMonthMilkStats)
        val cardHealthStats = findViewById<MaterialCardView>(R.id.cardHealthStats)
        val cardMarketPlace = findViewById<MaterialCardView>(R.id.cardMarketPlace)
        cardMarketPlace.setOnClickListener {
            startActivity(Intent(this, Shopping::class.java))
        }

        cardAnimals.setOnClickListener {
            startActivity(Intent(this, CowListActivity::class.java))
        }

        cardPregnancyStats.setOnClickListener {
            startActivity(Intent(this, PregnancyActivity::class.java))
        }

        cardTodayMilkStats.setOnClickListener {
            startActivity(Intent(this, RecordMilkActivity::class.java))
        }

        cardMonthMilkStats.setOnClickListener {
            startActivity(Intent(this, RecordMilkActivity::class.java))
        }

        cardHealthStats.setOnClickListener {
            startActivity(Intent(this, HealthActivity::class.java))
        }
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return

        db.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").value?.toString()?.trim().orEmpty()
                val farmName = snapshot.child("farmName").value?.toString()?.trim().orEmpty()

                val finalUserName = if (userName.isEmpty()) "User" else userName
                val finalFarmName = if (farmName.isEmpty()) "My Dairy Farm" else farmName

                tvWelcomeName.text = "Welcome back, $finalUserName!"
                tvFarmOverview.text = "$finalFarmName Overview"

                val headerView = navigationView.getHeaderView(0)
                val tvDrawerUserName = headerView.findViewById<TextView>(R.id.tvDrawerUserName)
                val tvDrawerFarmName = headerView.findViewById<TextView>(R.id.tvDrawerFarmName)

                tvDrawerUserName.text = finalUserName
                tvDrawerFarmName.text = finalFarmName
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, "Failed to load user profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupDrawerMenu() {
        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)

            when (item.itemId) {
                R.id.nav_dashboard -> true

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
                    startActivity(Intent(this, Shopping::class.java))
                    true
                }

                R.id.nav_ai -> {
                    startActivity(Intent(this, DetectActivity::class.java))
                    true
                }

                R.id.nav_pin -> {
                    openPinSetup()
                    true
                }

                R.id.nav_biometric -> {
                    enableBiometricLock()
                    true
                }

                R.id.nav_disable_lock -> {
                    requestAuthBeforeDisable()
                    true
                }

                R.id.nav_logout -> {
                    auth.signOut()
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                    true
                }

                else -> false
            }
        }
    }

    private fun openPinSetup() {
        val intent = Intent(this, PinActivity::class.java)
        intent.putExtra("mode", PinActivity.MODE_CREATE)
        startActivity(intent)
    }

    private fun enableBiometricLock() {
        val hasPin = securePrefs.getBoolean(KEY_PIN_ENABLED, false)
        if (!hasPin) {
            Toast.makeText(this, "Please create PIN first", Toast.LENGTH_SHORT).show()
            return
        }

        when (getBiometricAvailability()) {
            BiometricManager.BIOMETRIC_SUCCESS -> showBiometricEnablePrompt()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "Biometric hardware not available", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometric hardware unavailable", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No biometric enrolled on this device", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Biometric authentication not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestAuthBeforeDisable() {
        val isBiometricEnabled = securePrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        val isPinEnabled = securePrefs.getBoolean(KEY_PIN_ENABLED, false)
        val savedPin = securePrefs.getString(KEY_USER_PIN, null)

        when {
            isBiometricEnabled && isBiometricAvailable() -> {
                showBiometricDisablePrompt()
            }

            isPinEnabled && !savedPin.isNullOrEmpty() -> {
                val intent = Intent(this, PinActivity::class.java)
                intent.putExtra("mode", PinActivity.MODE_DISABLE_LOCK)
                startActivity(intent)
            }

            else -> {
                Toast.makeText(this, "No app lock is enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBiometricAvailability(): Int {
        val biometricManager = BiometricManager.from(this)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            biometricManager.canAuthenticate()
        }
    }

    private fun isBiometricAvailable(): Boolean {
        return getBiometricAvailability() == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricEnablePrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    securePrefs.edit()
                        .putBoolean(KEY_BIOMETRIC_ENABLED, true)
                        .apply()

                    Toast.makeText(
                        this@DashboardActivity,
                        "Biometric lock enabled successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@DashboardActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@DashboardActivity, errString, Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Enable Biometric Lock")
            .setSubtitle("Authenticate to enable biometric protection")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptBuilder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            promptBuilder.setDeviceCredentialAllowed(true)
        }

        biometricPrompt.authenticate(promptBuilder.build())
    }

    private fun showBiometricDisablePrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    securePrefs.edit()
                        .remove(KEY_USER_PIN)
                        .putBoolean(KEY_PIN_ENABLED, false)
                        .putBoolean(KEY_BIOMETRIC_ENABLED, false)
                        .apply()

                    Toast.makeText(
                        this@DashboardActivity,
                        "App lock disabled successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@DashboardActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@DashboardActivity, errString, Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Disable App Lock")
            .setSubtitle("Authenticate to disable app protection")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptBuilder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            promptBuilder.setDeviceCredentialAllowed(true)
        }

        biometricPrompt.authenticate(promptBuilder.build())
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true

                R.id.market -> {
                    startActivity(Intent(this, Shopping::class.java))
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

    private fun loadDashboardData() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.child("users_data")
            .child(uid)
            .child("cows")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tvTotal.text = snapshot.childrenCount.toString()

                    var pregnancyCount = 0

                    for (snap in snapshot.children) {
                        val pregnancySnap = snap.child("pregnancy")
                        val breedingDate = pregnancySnap.child("breedingDate").value?.toString()?.trim().orEmpty()
                        val deliveryDate = pregnancySnap.child("deliveryDate").value?.toString()?.trim().orEmpty()

                        if (pregnancySnap.exists() && breedingDate.isNotEmpty() && deliveryDate.isNotEmpty()) {
                            pregnancyCount++
                        }
                    }

                    tvPreg.text = pregnancyCount.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })

        db.child("users_data")
            .child(uid)
            .child("milk_records")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var todayTotal = 0.0
                    var monthTotal = 0.0

                    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val month = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())

                    for (snap in snapshot.children) {
                        val date = snap.child("date").value?.toString()?.trim() ?: ""
                        val milk = snap.child("milk").value?.toString()?.trim()?.toDoubleOrNull() ?: 0.0

                        if (date == today || date == removeLeadingZero(today)) {
                            todayTotal += milk
                        }

                        if (date.endsWith(month) || date.endsWith(removeLeadingZero(month))) {
                            monthTotal += milk
                        }
                    }

                    tvTodayMilk.text = String.format(Locale.getDefault(), "%.2f L", todayTotal)
                    tvMonthMilk.text = String.format(Locale.getDefault(), "%.2f L", monthTotal)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })

        db.child("users_data")
            .child(uid)
            .child("health_records")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tvHealthCount.text = snapshot.childrenCount.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun removeLeadingZero(date: String): String {
        return date.replace("/0", "/")
    }

    override fun onBackPressed() {
        if (::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}