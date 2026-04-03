package com.example.cattlemanagement

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class RecordMilkActivity : AppCompatActivity() {

    private lateinit var layoutList: LinearLayout
    private lateinit var tvCount: TextView
    private lateinit var etSearch: EditText
    private lateinit var spinnerMonth: Spinner
    private lateinit var tvTotal: TextView

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseDatabase.getInstance().reference

    private val milkList = ArrayList<HashMap<String, String>>()
    private val filteredList = ArrayList<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_milk)

        auth = FirebaseAuth.getInstance()

        layoutList = findViewById(R.id.layoutMilkList)
        tvCount = findViewById(R.id.tvMilkCount)
        etSearch = findViewById(R.id.etSearchCow)
        spinnerMonth = findViewById(R.id.spinnerMonth)
        tvTotal = findViewById(R.id.tvTotalMilk)

        findViewById<Button>(R.id.btnAddMilk).setOnClickListener {
            openAddDialog()
        }

        setupSearch()
        setupMonthFilter()
        loadMilkRecords()
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                applyFilters()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupMonthFilter() {
        val months = ArrayList<String>()
        months.add("All")

        val year = Calendar.getInstance().get(Calendar.YEAR)

        for (i in 1..12) {
            val m = if (i < 10) "0$i" else "$i"
            months.add("$m/$year")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = adapter

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun applyFilters() {
        val query = etSearch.text.toString().trim().lowercase()
        val selectedMonth = spinnerMonth.selectedItem.toString()

        filteredList.clear()

        for (item in milkList) {
            val cow = item["cow"]?.lowercase() ?: ""
            val date = item["date"] ?: ""

            val matchCow = cow.contains(query)
            val matchMonth = selectedMonth == "All" || date.contains(selectedMonth)

            if (matchCow && matchMonth) {
                filteredList.add(item)
            }
        }

        displayMilkRecords(filteredList)
    }

    private fun displayMilkRecords(list: List<HashMap<String, String>>) {
        layoutList.removeAllViews()

        var totalMilk = 0.0

        for (item in list) {
            val cow = item["cow"] ?: "-"
            val milk = item["milk"] ?: "0"
            val date = item["date"] ?: "-"
            val key = item["key"] ?: ""

            totalMilk += milk.toDoubleOrNull() ?: 0.0

            val card = MaterialCardView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 20)
            card.layoutParams = params
            card.radius = 20f
            card.cardElevation = 8f
            card.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.white))

            val innerLayout = LinearLayout(this)
            innerLayout.orientation = LinearLayout.VERTICAL
            innerLayout.setPadding(24, 24, 24, 24)

            val tvCow = TextView(this)
            tvCow.text = "Cow: $cow"
            tvCow.textSize = 16f
            tvCow.setTextColor(ContextCompat.getColor(this, R.color.textColor))

            val tvMilk = TextView(this)
            tvMilk.text = "Milk: $milk L"
            tvMilk.setTextColor(ContextCompat.getColor(this, R.color.textColor))

            val tvDate = TextView(this)
            tvDate.text = "Date: $date"
            tvDate.setTextColor(ContextCompat.getColor(this, R.color.textColor))

            innerLayout.addView(tvCow)
            innerLayout.addView(tvMilk)
            innerLayout.addView(tvDate)
            card.addView(innerLayout)

            card.setOnLongClickListener {
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@setOnLongClickListener true
                }

                AlertDialog.Builder(this)
                    .setTitle("Delete Record?")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes") { _, _ ->
                        db.child("users_data")
                            .child(uid)
                            .child("milk_records")
                            .child(key)
                            .removeValue()

                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()

                true
            }

            layoutList.addView(card)
        }

        tvTotal.text = "Total: %.2f L".format(totalMilk)
    }

    private fun loadMilkRecords() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.child("users_data")
            .child(uid)
            .child("milk_records")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    milkList.clear()
                    tvCount.text = "${snapshot.childrenCount} records"

                    for (snap in snapshot.children) {
                        val map = HashMap<String, String>()
                        map["cow"] = snap.child("cowNo").value?.toString() ?: "-"
                        map["milk"] = snap.child("milk").value?.toString() ?: "0"
                        map["date"] = snap.child("date").value?.toString() ?: "-"
                        map["key"] = snap.key ?: ""
                        milkList.add(map)
                    }

                    applyFilters()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@RecordMilkActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun openAddDialog() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_milk, null)

        val spinner = view.findViewById<Spinner>(R.id.spinnerCow)
        val etDate = view.findViewById<EditText>(R.id.etDate)
        val etMorning = view.findViewById<EditText>(R.id.etMorning)
        val etEvening = view.findViewById<EditText>(R.id.etEvening)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val cowNames = ArrayList<String>()
        cowNames.add("Select Cow")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cowNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        db.child("users_data")
            .child(uid)
            .child("cows")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cowNames.clear()
                    cowNames.add("Select Cow")

                    for (snap in snapshot.children) {
                        val cow = snap.child("cowNo").value?.toString()
                        if (!cow.isNullOrEmpty()) {
                            cowNames.add(cow)
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@RecordMilkActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })

        val calendar = Calendar.getInstance()

        etDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    val cal = Calendar.getInstance()
                    cal.set(y, m, d)
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    etDate.setText(format.format(cal.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = AlertDialog.Builder(this).setView(view).create()

        btnSave.setOnClickListener {
            val cow = spinner.selectedItem.toString()
            val morning = etMorning.text.toString().trim().toDoubleOrNull() ?: 0.0
            val evening = etEvening.text.toString().trim().toDoubleOrNull() ?: 0.0
            val date = etDate.text.toString().trim()

            if (cow == "Select Cow" || date.isEmpty()) {
                Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = morning + evening

            val map = HashMap<String, Any>()
            map["cowNo"] = cow
            map["morning"] = morning
            map["evening"] = evening
            map["milk"] = total
            map["date"] = date
            map["notes"] = etNotes.text.toString().trim()

            db.child("users_data")
                .child(uid)
                .child("milk_records")
                .push()
                .setValue(map)
                .addOnSuccessListener {
                    Toast.makeText(this, "Milk Recorded", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save record", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }
}