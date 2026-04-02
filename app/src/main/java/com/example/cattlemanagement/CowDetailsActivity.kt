package com.example.cattlemanagement

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanagement.CowModel
import com.google.firebase.database.*
import java.io.File

class CowDetailsActivity : AppCompatActivity() {

    private lateinit var imgCow: ImageView
    private lateinit var tvHeaderTitle: TextView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    private lateinit var tvDob: TextView
    private lateinit var tvBornType: TextView
    private lateinit var tvLactation: TextView
    private lateinit var tvSire: TextView
    private lateinit var tvDam: TextView
    private lateinit var tvCalving: TextView
    private lateinit var tvYield: TextView
    private lateinit var tvDeworming: TextView
    private lateinit var tvAiDate: TextView
    private lateinit var tvPregDate: TextView
    private lateinit var tvMedical: TextView
    private lateinit var tvRemarks: TextView
    private lateinit var tvProg: TextView
    private lateinit var tvVaccines: TextView

    // ✅ NEW
    private lateinit var tvMilk: TextView

    private val cowKeys = ArrayList<String>()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cow_details)

        bindViews()

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val cowNo = intent.getStringExtra("cowNo") ?: return
        loadAllCowKeys(cowNo)
    }

    private fun bindViews() {
        imgCow = findViewById(R.id.imgCowDetails)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)

        tvDob = findViewById(R.id.tvDobDetails)
        tvBornType = findViewById(R.id.tvBornTypeDetails)
        tvLactation = findViewById(R.id.tvLactationDetails)
        tvSire = findViewById(R.id.tvSireDetails)
        tvDam = findViewById(R.id.tvDamDetails)
        tvCalving = findViewById(R.id.tvCalvingDetails)
        tvYield = findViewById(R.id.tvYieldDetails)
        tvDeworming = findViewById(R.id.tvDewormingDetails)
        tvAiDate = findViewById(R.id.tvAiDateDetails)
        tvPregDate = findViewById(R.id.tvPregDateDetails)
        tvMedical = findViewById(R.id.tvMedicalDetails)
        tvRemarks = findViewById(R.id.tvRemarksDetails)
        tvProg = findViewById(R.id.tvProgDetails)
        tvVaccines = findViewById(R.id.tvVaccinesDetails)

        // ✅ LINK THIS ID IN XML
        tvMilk = findViewById(R.id.tvMilkDetails)
    }

    private fun loadAllCowKeys(selectedCowNo: String) {

        FirebaseDatabase.getInstance()
            .reference.child("cows")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    cowKeys.clear()

                    for (snap in snapshot.children) {
                        snap.key?.let { cowKeys.add(it) }
                    }

                    currentIndex = cowKeys.indexOf(selectedCowNo)
                    if (currentIndex == -1) currentIndex = 0

                    fetchCowDetails(cowKeys[currentIndex])
                    setupButtons()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setupButtons() {

        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                fetchCowDetails(cowKeys[currentIndex])
            }
        }

        btnNext.setOnClickListener {
            if (currentIndex < cowKeys.size - 1) {
                currentIndex++
                fetchCowDetails(cowKeys[currentIndex])
            }
        }
    }

    private fun fetchCowDetails(cowNo: String) {

        FirebaseDatabase.getInstance()
            .reference.child("cows").child(cowNo)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val cow = snapshot.getValue(CowModel::class.java) ?: return

                    tvHeaderTitle.text = "Cow #${cow.cowNo}"

                    tvDob.text = "Date of Birth: ${cow.dob}"
                    tvBornType.text = cow.bornType
                    tvLactation.text = "Lactation: ${cow.lactation}"
                    tvSire.text = "Sire: ${cow.sire}"
                    tvDam.text = "Dam: ${cow.dam}"

                    tvCalving.text = "Calving Date: ${cow.calving}"
                    tvYield.text = "Milk Yield: ${cow.yield} L/day"
                    tvDeworming.text = "Deworming: ${cow.deworming}"
                    tvAiDate.text = "AI Date: ${cow.aiDate}"
                    tvPregDate.text = "Pregnancy Test: ${cow.pregnancyDate}"
                    tvMedical.text = "Medical: ${cow.medical}"
                    tvRemarks.text = "Remarks: ${cow.remarks}"

                    // ✅ PREGNANCY
                    val pregSnap = snapshot.child("pregnancy")

                    if (pregSnap.exists()) {
                        val breed = pregSnap.child("breedingDate").value?.toString() ?: "-"
                        val delivery = pregSnap.child("deliveryDate").value?.toString() ?: "-"

                        tvProg.text = "Pregnancy:\nBreeding: $breed\nDelivery: $delivery"
                    } else {
                        tvProg.text = "Pregnancy: Not Available"
                    }

                    // ✅ MILK DATA (FIXED 🔥)
                    FirebaseDatabase.getInstance()
                        .reference.child("milk_records")
                        .addListenerForSingleValueEvent(object : ValueEventListener {

                            override fun onDataChange(milkSnapshot: DataSnapshot) {

                                var total = 0.0

                                for (snap in milkSnapshot.children) {

                                    val cowId = snap.child("cowNo").value?.toString()

                                    if (cowId == cowNo) {
                                        val milk = snap.child("milk").value
                                            ?.toString()
                                            ?.toDoubleOrNull() ?: 0.0
                                        total += milk
                                    }
                                }

                                tvMilk.text = "Total Milk: %.2f L".format(total)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })

                    // VACCINES
                    val vaccineText = StringBuilder()

                    if (cow.vaccines.isEmpty()) {
                        vaccineText.append("Vaccines: No Data")
                    } else {
                        vaccineText.append("Vaccinations:\n")
                        cow.vaccines.forEach { (name, date) ->
                            vaccineText.append("$name: $date\n")
                        }
                    }

                    tvVaccines.text = vaccineText.toString()

                    // IMAGE
                    if (cow.imagePath.isNotEmpty()) {
                        val file = File(cow.imagePath)
                        if (file.exists()) {
                            imgCow.setImageBitmap(
                                BitmapFactory.decodeFile(file.absolutePath)
                            )
                        } else {
                            imgCow.setImageResource(R.drawable.home)
                        }
                    } else {
                        imgCow.setImageResource(R.drawable.home)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}