package com.example.cattlemanagement

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class PregnancyActivity : AppCompatActivity() {

    private lateinit var layoutList: LinearLayout
    private lateinit var tvCount: TextView
    private lateinit var btnAdd: Button

    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregnancy)

        layoutList = findViewById(R.id.layoutPregList)
        tvCount = findViewById(R.id.tvPregCount)
        btnAdd = findViewById(R.id.btnAddPregnancy)

        loadPregnancyData()

        btnAdd.setOnClickListener {
            openAddDialog()   // ✅ This will now use updated function
        }
    }

    // 🔥 LOAD PREGNANT COWS
    private fun loadPregnancyData() {

        db.child("cows")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    layoutList.removeAllViews()

                    var count = 0

                    for (snap in snapshot.children) {

                        val cowNo = snap.child("cowNo").value?.toString() ?: continue
                        val pregSnap = snap.child("pregnancy")

                        if (!pregSnap.exists()) continue

                        count++

                        val breedDate = pregSnap.child("breedingDate").value?.toString() ?: "-"
                        val delivery = pregSnap.child("deliveryDate").value?.toString() ?: "-"

                        val view = LayoutInflater.from(this@PregnancyActivity)
                            .inflate(R.layout.item_pregnancy, layoutList, false)

                        view.findViewById<TextView>(R.id.tvCow).text = "🐄 Cow: $cowNo"
                        view.findViewById<TextView>(R.id.tvBreed).text = "📅 Breeding: $breedDate"
                        view.findViewById<TextView>(R.id.tvDelivery).text = "🤰 Delivery: $delivery"

                        layoutList.addView(view)
                    }

                    tvCount.text = "$count active pregnancies"

                    if (count == 0) {
                        val tv = TextView(this@PregnancyActivity)
                        tv.text = "No pregnancy records yet"
                        layoutList.addView(tv)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ✅ UPDATED DIALOG FUNCTION (YOUR FIX ADDED HERE)
    private fun openAddDialog() {

        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_pregnancy, null)

        val spinner = view.findViewById<Spinner>(R.id.spinnerCow)
        val etBreed = view.findViewById<EditText>(R.id.etBreedingDate)
        val etDelivery = view.findViewById<EditText>(R.id.etDeliveryDate)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val cowList = ArrayList<String>()
        cowList.add("Select Cow")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cowList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        // 🔥 LOAD COWS FROM FIREBASE
        db.child("cows")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    cowList.clear()
                    cowList.add("Select Cow")

                    for (snap in snapshot.children) {
                        val cow = snap.child("cowNo").value?.toString()
                        if (!cow.isNullOrEmpty()) {
                            cowList.add(cow)
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        val calendar = Calendar.getInstance()

        // 📅 BREED DATE
        etBreed.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->

                val cal = Calendar.getInstance()
                cal.set(y, m, d)

                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                etBreed.setText(format.format(cal.time))

                cal.add(Calendar.DAY_OF_YEAR, 280)
                etDelivery.setText(format.format(cal.time))

            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // 📅 DELIVERY DATE
        etDelivery.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                etDelivery.setText("$d/${m + 1}/$y")
            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ✅ SAVE
        btnSave.setOnClickListener {

            val cow = spinner.selectedItem.toString()
            val breed = etBreed.text.toString()
            val delivery = etDelivery.text.toString()

            if (cow == "Select Cow") {
                Toast.makeText(this, "Please select cow", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (breed.isEmpty() || delivery.isEmpty()) {
                Toast.makeText(this, "Select dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val map = HashMap<String, Any>()
            map["breedingDate"] = breed
            map["deliveryDate"] = delivery
            map["notes"] = etNotes.text.toString()

            db.child("cows")
                .child(cow)
                .child("pregnancy")
                .setValue(map)

            Toast.makeText(this, "Pregnancy Added ✅", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}