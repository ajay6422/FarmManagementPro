package com.example.cattlemanagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File

class CowDetailsActivity : AppCompatActivity() {

    private lateinit var imgCowDetails: ImageView

    private lateinit var tvDobDetails: TextView
    private lateinit var tvBornTypeDetails: TextView
    private lateinit var tvLactationDetails: TextView
    private lateinit var tvSireDetails: TextView
    private lateinit var tvDamDetails: TextView
    private lateinit var tvCalvingDetails: TextView
    private lateinit var tvYieldDetails: TextView
    private lateinit var tvDewormingDetails: TextView
    private lateinit var tvAiDateDetails: TextView
    private lateinit var tvPregDateDetails: TextView
    private lateinit var tvProgDetails: TextView
    private lateinit var tvMedicalDetails: TextView
    private lateinit var tvRemarksDetails: TextView
    private lateinit var tvVaccinesDetails: TextView

    private lateinit var btnEditCow: Button
    private lateinit var btnDeleteCow: Button
    private lateinit var tvHeaderTitle: TextView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    private var currentCowKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cow_details)

        auth = FirebaseAuth.getInstance()
        currentCowKey = intent.getStringExtra("COW_KEY")

        bindViews()

        if (currentCowKey == null) {
            Toast.makeText(this, "No cow data found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadCowData(currentCowKey!!)

        btnEditCow.setOnClickListener {
            val intent = Intent(this, AddCowActivity::class.java)
            intent.putExtra("IS_EDIT", true)
            intent.putExtra("COW_KEY", currentCowKey)
            startActivity(intent)
        }

        btnDeleteCow.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        btnPrev.setOnClickListener {
            Toast.makeText(this, "Previous Cow feature pending", Toast.LENGTH_SHORT).show()
        }

        btnNext.setOnClickListener {
            Toast.makeText(this, "Next Cow feature pending", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindViews() {
        imgCowDetails = findViewById(R.id.imgCowDetails)

        tvDobDetails = findViewById(R.id.tvDobDetails)
        tvBornTypeDetails = findViewById(R.id.tvBornTypeDetails)
        tvLactationDetails = findViewById(R.id.tvLactationDetails)
        tvSireDetails = findViewById(R.id.tvSireDetails)
        tvDamDetails = findViewById(R.id.tvDamDetails)
        tvCalvingDetails = findViewById(R.id.tvCalvingDetails)
        tvYieldDetails = findViewById(R.id.tvYieldDetails)
        tvDewormingDetails = findViewById(R.id.tvDewormingDetails)
        tvAiDateDetails = findViewById(R.id.tvAiDateDetails)
        tvPregDateDetails = findViewById(R.id.tvPregDateDetails)
        tvProgDetails = findViewById(R.id.tvProgDetails)
        tvMedicalDetails = findViewById(R.id.tvMedicalDetails)
        tvRemarksDetails = findViewById(R.id.tvRemarksDetails)
        tvVaccinesDetails = findViewById(R.id.tvVaccinesDetails)

        btnEditCow = findViewById(R.id.btnEditCow)
        btnDeleteCow = findViewById(R.id.btnDeleteCow)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun loadCowData(cowKey: String) {
        val uid = auth.currentUser?.uid ?: return

        database.child("users_data")
            .child(uid)
            .child("cows")
            .child(cowKey)
            .get()
            .addOnSuccessListener { snapshot ->
                val cow = snapshot.getValue(CowModel::class.java)

                cow?.let {
                    tvHeaderTitle.text = "Cow #${it.cowNo}"
                    tvDobDetails.text = "Date of Birth: ${it.dob}"
                    tvBornTypeDetails.text = "Type: ${it.bornType}"
                    tvLactationDetails.text = "Lactation: ${it.lactation}"
                    tvSireDetails.text = "Sire: ${it.sire}"
                    tvDamDetails.text = "Dam: ${it.dam}"
                    tvCalvingDetails.text = "Calving Date: ${it.calving}"
                    tvYieldDetails.text = "Milk Yield: ${it.yield}"
                    tvDewormingDetails.text = "Deworming: ${it.deworming}"
                    tvAiDateDetails.text = "AI Date: ${it.aiDate}"
                    tvPregDateDetails.text = "Pregnancy Test Date: ${it.pregnancyDate}"
                    tvProgDetails.text = "Progesterone Test: ${it.progesterone}"
                    tvMedicalDetails.text = "Medical Events: ${it.medical}"
                    tvRemarksDetails.text = "Remarks: ${it.remarks}"

                    val vaccinesText = if (it.vaccines.isNotEmpty()) {
                        it.vaccines.entries.joinToString("\n") { entry ->
                            "${entry.key}: ${entry.value}"
                        }
                    } else {
                        "Vaccines: No vaccine record"
                    }

                    tvVaccinesDetails.text = "Vaccinations:\n$vaccinesText"

                    if (it.imagePath.isNotEmpty()) {
                        val file = File(it.imagePath)
                        if (file.exists()) {
                            imgCowDetails.setImageURI(Uri.fromFile(file))
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load cow details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Cow")
            .setMessage("Are you sure you want to delete this cow?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCow()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCow() {
        val uid = auth.currentUser?.uid ?: return
        val cowKey = currentCowKey ?: return

        database.child("users_data")
            .child(uid)
            .child("cows")
            .child(cowKey)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Cow deleted successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CowListActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete cow", Toast.LENGTH_SHORT).show()
            }
    }
}