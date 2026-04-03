package com.example.cattlemanagement

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class HealthActivity : AppCompatActivity() {

    private lateinit var layoutList: LinearLayout
    private lateinit var tvCount: TextView
    private lateinit var auth: FirebaseAuth

    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health)

        auth = FirebaseAuth.getInstance()

        layoutList = findViewById(R.id.layoutHealthList)
        tvCount = findViewById(R.id.tvHealthCount)

        findViewById<Button>(R.id.btnAddHealth).setOnClickListener {
            openDialog()
        }

        loadHealthRecords()
    }

    private fun loadHealthRecords() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.child("users_data")
            .child(uid)
            .child("health_records")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    layoutList.removeAllViews()
                    tvCount.text = "${snapshot.childrenCount} records"

                    for (snap in snapshot.children) {
                        val cow = snap.child("cowNo").value?.toString() ?: "-"
                        val record = snap.child("record").value?.toString() ?: "-"
                        val date = snap.child("date").value?.toString() ?: "-"
                        val key = snap.key ?: ""

                        val card = MaterialCardView(this@HealthActivity)
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 0, 0, 20)
                        card.layoutParams = params

                        card.radius = 20f
                        card.cardElevation = 8f
                        card.setCardBackgroundColor(
                            ContextCompat.getColor(this@HealthActivity, android.R.color.white)
                        )

                        val inner = LinearLayout(this@HealthActivity)
                        inner.orientation = LinearLayout.VERTICAL
                        inner.setPadding(24, 24, 24, 24)

                        val tvCow = TextView(this@HealthActivity)
                        tvCow.text = "🐄 Cow: $cow"
                        tvCow.setTextColor(ContextCompat.getColor(this@HealthActivity, R.color.textColor))

                        val tvRecord = TextView(this@HealthActivity)
                        tvRecord.text = "💊 $record"
                        tvRecord.setTextColor(ContextCompat.getColor(this@HealthActivity, R.color.textColor))

                        val tvDate = TextView(this@HealthActivity)
                        tvDate.text = "📅 Date: $date"
                        tvDate.setTextColor(ContextCompat.getColor(this@HealthActivity, R.color.textColor))

                        inner.addView(tvCow)
                        inner.addView(tvRecord)
                        inner.addView(tvDate)

                        card.addView(inner)

                        card.setOnLongClickListener {
                            AlertDialog.Builder(this@HealthActivity)
                                .setTitle("Delete Record?")
                                .setPositiveButton("Yes") { _, _ ->
                                    db.child("users_data")
                                        .child(uid)
                                        .child("health_records")
                                        .child(key)
                                        .removeValue()
                                }
                                .setNegativeButton("No", null)
                                .show()
                            true
                        }

                        layoutList.addView(card)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HealthActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun openDialog() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_health, null)

        val spinner = view.findViewById<Spinner>(R.id.spinnerCow)
        val etHealth = view.findViewById<EditText>(R.id.etHealth)
        val etDate = view.findViewById<EditText>(R.id.etDate)
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

        db.child("users_data")
            .child(uid)
            .child("cows")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cowList.clear()
                    cowList.add("Select Cow")

                    for (snap in snapshot.children) {
                        val cow = snap.child("cowNo").value?.toString()
                        if (!cow.isNullOrEmpty()) cowList.add(cow)
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HealthActivity, error.message, Toast.LENGTH_SHORT).show()
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

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        btnSave.setOnClickListener {
            val cow = spinner.selectedItem.toString()
            val record = etHealth.text.toString().trim()
            val date = etDate.text.toString().trim()

            if (cow == "Select Cow" || record.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val map = HashMap<String, Any>()
            map["cowNo"] = cow
            map["record"] = record
            map["date"] = date

            db.child("users_data")
                .child(uid)
                .child("health_records")
                .push()
                .setValue(map)
                .addOnSuccessListener {
                    Toast.makeText(this, "Health Saved ❤️", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save record", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }
}