package com.example.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cattlemanagement.CowModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class CowListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchCow: EditText
    private lateinit var adapter: CowAdapter
    private val cowList = ArrayList<CowModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cow_list)

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

    // 🔍 SEARCH BAR LOGIC
    private fun setupSearch() {
        etSearchCow.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }



    // 🔥 FIREBASE FETCH (ALL COWS)
    private fun fetchCows() {
        FirebaseDatabase.getInstance()
            .reference.child("cows")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    cowList.clear()
                    for (snap in snapshot.children) {
                        val cow = snap.getValue(CowModel::class.java)
                        cow?.let { cowList.add(it) }
                    }
                    adapter.refreshData() // IMPORTANT
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}