package com.example.cattlemanagement

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cattlemanagement.CowModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AddCowActivity : AppCompatActivity() {

    private lateinit var imgCow: ImageView
    private var imageUri: Uri? = null

    private lateinit var etCowNo: EditText
    private lateinit var etDob: EditText
    private lateinit var etBornType: EditText
    private lateinit var etLactation: EditText
    private lateinit var etSire: EditText
    private lateinit var etDam: EditText
    private lateinit var etCalving: EditText
    private lateinit var etYield: EditText
    private lateinit var etDeWorming: EditText
    private lateinit var etAIDate: EditText
    private lateinit var etPregDate: EditText
    private lateinit var etMedical: EditText
    private lateinit var etRemarks: EditText

    private lateinit var rgLocation: RadioGroup
    private lateinit var rgProg: RadioGroup

    private lateinit var cbFMD: CheckBox
    private lateinit var etFMD: EditText
    private lateinit var cbRabies: CheckBox
    private lateinit var etRabies: EditText
    private lateinit var cbTT: CheckBox
    private lateinit var etTT: EditText
    private lateinit var cbLumpy: CheckBox
    private lateinit var etLumpy: EditText
    private lateinit var cbThileria: CheckBox
    private lateinit var etThileria: EditText
    private lateinit var cbBrucella: CheckBox
    private lateinit var etBrucella: EditText

    private lateinit var btnSave: Button

    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cow)

        bindViews()
        setupCalendars()

        imgCow.setOnClickListener { pickImage() }
        btnSave.setOnClickListener { saveCow() }


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation1)
        bottomNav.selectedItemId = R.id.farm

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.farm -> true
                R.id.market -> {
                    startActivity(Intent(this, Shopping::class.java))
                    true
                }
                R.id.home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.animals -> {
                    startActivity(Intent(this, CowListActivity::class.java))
                    finish()
                    true
                }
                R.id.detection -> {
                    startActivity(Intent(this, ScanActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }


    // ---------------- BIND ----------------
    private fun bindViews() {
        imgCow = findViewById(R.id.imgCow)
        etCowNo = findViewById(R.id.etCowNo)
        etDob = findViewById(R.id.etDob)
        etBornType = findViewById(R.id.etBornType)
        etLactation = findViewById(R.id.etLactation)
        etSire = findViewById(R.id.etSire)
        etDam = findViewById(R.id.etDam1)
        etCalving = findViewById(R.id.etCalving)
        etYield = findViewById(R.id.etYeilding)
        etDeWorming = findViewById(R.id.devorming)
        etAIDate = findViewById(R.id.AIDate)
        etPregDate = findViewById(R.id.PragnencyDate)
        etMedical = findViewById(R.id.etMedicalEvents)
        etRemarks = findViewById(R.id.remarks)

        rgLocation = findViewById(R.id.Location)
        rgProg = findViewById(R.id.rgProgesterone)

        cbFMD = findViewById(R.id.cbFMD)
        etFMD = findViewById(R.id.etFmdDate)
        cbRabies = findViewById(R.id.cbRabies)
        etRabies = findViewById(R.id.etRabiesDate)
        cbTT = findViewById(R.id.cbTT)
        etTT = findViewById(R.id.etTTDate)
        cbLumpy = findViewById(R.id.cbLumpy)
        etLumpy = findViewById(R.id.etLumpyDate)
        cbThileria = findViewById(R.id.cbThileria)
        etThileria = findViewById(R.id.etThileriaDate)
        cbBrucella = findViewById(R.id.cbBrucella)
        etBrucella = findViewById(R.id.etBrucellaDate)

        btnSave = findViewById(R.id.btnSave)
    }

    // ---------------- DATE PICKERS ----------------
    private fun setupCalendars() {
        val allDates = listOf(etDob, etCalving, etAIDate, etPregDate, etFMD, etRabies, etTT, etLumpy, etThileria, etBrucella)
        for (e in allDates) e.setOnClickListener { openDate(e) }
    }

    private fun openDate(editText: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            editText.setText("$d/${m + 1}/$y")
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    // ---------------- IMAGE PICKER ----------------
    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            imageUri = it
            imgCow.setImageURI(it)
        }
    }

    private fun pickImage() {
        imagePicker.launch("image/*")
    }

    // ---------------- SAVE ----------------
    private fun saveCow() {
        val cowNo = etCowNo.text.toString()

        if (cowNo.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Cow No & Image required", Toast.LENGTH_SHORT).show()
            return
        }

        val localPath = saveImageLocally(imageUri!!, cowNo)

        val cow = CowModel(
            cowNo = cowNo,
            dob = etDob.text.toString(),
            bornType = etBornType.text.toString(),
            lactation = etLactation.text.toString(),
            sire = etSire.text.toString(),
            dam = etDam.text.toString(),
            calving = etCalving.text.toString(),
            yield = etYield.text.toString(),
            deworming = etDeWorming.text.toString(),
            aiDate = etAIDate.text.toString(),
            pregnancyDate = etPregDate.text.toString(),
            medical = etMedical.text.toString(),
            remarks = etRemarks.text.toString(),
            location = getRadioText(rgLocation),
            progesterone = getRadioText(rgProg),
            vaccines = getVaccines(),
            imagePath = localPath,
            time = System.currentTimeMillis()
        )

        database.child("cows").child(cowNo).setValue(cow)
            .addOnSuccessListener {
                Toast.makeText(this, "Cow Saved 🐄", Toast.LENGTH_LONG).show()

                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }

    // ---------------- SAVE IMAGE LOCALLY ----------------
    private fun saveImageLocally(uri: Uri, cowNo: String): String {
        val input = contentResolver.openInputStream(uri)
        val file = File(getExternalFilesDir("cow_images"), "$cowNo.jpg")
        val output = FileOutputStream(file)

        input?.copyTo(output)

        input?.close()
        output.close()

        return file.absolutePath
    }

    private fun getRadioText(rg: RadioGroup): String {
        val id = rg.checkedRadioButtonId
        return if (id == -1) "" else findViewById<RadioButton>(id).text.toString()
    }

    private fun getVaccines(): Map<String, String> {
        val map = HashMap<String, String>()
        if (cbFMD.isChecked) map["FMD"] = etFMD.text.toString()
        if (cbRabies.isChecked) map["Rabies"] = etRabies.text.toString()
        if (cbTT.isChecked) map["TT"] = etTT.text.toString()
        if (cbLumpy.isChecked) map["Lumpy"] = etLumpy.text.toString()
        if (cbThileria.isChecked) map["Thileria"] = etThileria.text.toString()
        if (cbBrucella.isChecked) map["Brucella"] = etBrucella.text.toString()
        return map
    }
}