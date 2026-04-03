package com.example.cattlemanagement

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class PregnancyActivity : AppCompatActivity() {

    private lateinit var layoutList: LinearLayout
    private lateinit var tvCount: TextView
    private lateinit var btnAdd: Button

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregnancy)

        auth = FirebaseAuth.getInstance()

        layoutList = findViewById(R.id.layoutPregList)
        tvCount = findViewById(R.id.tvPregCount)
        btnAdd = findViewById(R.id.btnAddPregnancy)

        loadPregnancyData()

        btnAdd.setOnClickListener {
            openAddDialog()
        }
    }

    private fun loadPregnancyData() {
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

                        view.findViewById<TextView>(R.id.tvCow).text = "Cow: $cowNo"
                        view.findViewById<TextView>(R.id.tvBreed).text = "Breeding: $breedDate"
                        view.findViewById<TextView>(R.id.tvDelivery).text = "Delivery: $delivery"

                        layoutList.addView(view)
                    }

                    tvCount.text = "$count active pregnancies"

                    if (count == 0) {
                        val tv = TextView(this@PregnancyActivity)
                        tv.text = "No pregnancy records yet"
                        layoutList.addView(tv)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PregnancyActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun openAddDialog() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_pregnancy, null)

        val spinner = view.findViewById<Spinner>(R.id.spinnerCow)
        val etBreed = view.findViewById<EditText>(R.id.etBreedingDate)
        val etDelivery = view.findViewById<EditText>(R.id.etDeliveryDate)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val cowNames = ArrayList<String>()
        val cowKeyMap = HashMap<String, String>()
        cowNames.add("Select Cow")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cowNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        db.child("users_data")
            .child(uid)
            .child("cows")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    cowNames.clear()
                    cowKeyMap.clear()
                    cowNames.add("Select Cow")

                    for (snap in snapshot.children) {
                        val cowNo = snap.child("cowNo").value?.toString()
                        val cowKey = snap.key

                        if (!cowNo.isNullOrEmpty() && !cowKey.isNullOrEmpty()) {
                            cowNames.add(cowNo)
                            cowKeyMap[cowNo] = cowKey
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PregnancyActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })

        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        etBreed.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    val cal = Calendar.getInstance()
                    cal.set(y, m, d)

                    etBreed.setText(format.format(cal.time))

                    cal.add(Calendar.DAY_OF_YEAR, 280)
                    etDelivery.setText(format.format(cal.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etDelivery.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    val cal = Calendar.getInstance()
                    cal.set(y, m, d)
                    etDelivery.setText(format.format(cal.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnSave.setOnClickListener {
            val selectedCow = spinner.selectedItem.toString()
            val cowKey = cowKeyMap[selectedCow]
            val breed = etBreed.text.toString().trim()
            val delivery = etDelivery.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            if (selectedCow == "Select Cow") {
                Toast.makeText(this, "Please select cow", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (breed.isEmpty() || delivery.isEmpty()) {
                Toast.makeText(this, "Select dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cowKey.isNullOrEmpty()) {
                Toast.makeText(this, "Invalid cow selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val map = HashMap<String, Any>()
            map["breedingDate"] = breed
            map["deliveryDate"] = delivery
            map["notes"] = notes

            db.child("users_data")
                .child(uid)
                .child("cows")
                .child(cowKey)
                .child("pregnancy")
                .setValue(map)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pregnancy Added", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save pregnancy", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }
}