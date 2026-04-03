package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CowListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchCow: EditText
    private lateinit var adapter: CowAdapter
    private val cowList = ArrayList<CowModel>()

    private lateinit var auth: FirebaseAuth
    private lateinit var cowRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cow_list)

        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerCows)
        etSearchCow = findViewById(R.id.etSearchCow)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CowAdapter(cowList)
        recyclerView.adapter = adapter

        setupSearch()
        fetchCows()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.animals

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.animals -> true

                R.id.market -> {
                    startActivity(Intent(this, Shopping::class.java))
                    true
                }

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

                R.id.farm -> {
                    startActivity(Intent(this, AddCowActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    private fun setupSearch() {
        etSearchCow.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchCows() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        cowRef = FirebaseDatabase.getInstance()
            .reference
            .child("users_data")
            .child(uid)
            .child("cows")

        cowRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cowList.clear()

                for (snap in snapshot.children) {
                    val cow = snap.getValue(CowModel::class.java)
                    if (cow != null) {
                        cow.firebaseKey = snap.key ?: ""
                        cowList.add(cow)
                    }
                }

                adapter.refreshData()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CowListActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}