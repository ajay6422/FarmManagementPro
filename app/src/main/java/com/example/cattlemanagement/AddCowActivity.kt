package com.ajaykumar.cattlemanagement

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

class AddCowActivity : AppCompatActivity() {

    private lateinit var imgCow: ImageView
    private var imageUri: Uri? = null
    private var isEditMode = false
    private var currentCowKey: String? = null
    private var existingImagePath: String = ""

    private lateinit var etCowNo: EditText
    private lateinit var etDob: EditText
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

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cow)

        auth = FirebaseAuth.getInstance()

        bindViews()
        setupCalendars()

        isEditMode = intent.getBooleanExtra("IS_EDIT", false)
        currentCowKey = intent.getStringExtra("COW_KEY")

        if (isEditMode && currentCowKey != null) {
            btnSave.text = "Update Cow"
            loadCowData(currentCowKey!!)
        } else {
            btnSave.text = "Save Cow"
        }

        imgCow.setOnClickListener {
            openGallery()
        }

        btnSave.setOnClickListener {
            if (isEditMode && currentCowKey != null) {
                updateCow(currentCowKey!!)
            } else {
                saveCow()
            }
        }

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

    private fun bindViews() {
        imgCow = findViewById(R.id.imgCow)

        etCowNo = findViewById(R.id.etCowNo)
        etDob = findViewById(R.id.etDob)
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

    private fun setupCalendars() {
        val allDates = listOf(
            etDob, etCalving, etAIDate, etPregDate,
            etFMD, etRabies, etTT, etLumpy, etThileria, etBrucella
        )

        for (editText in allDates) {
            editText.setOnClickListener { openDate(editText) }
        }
    }

    private fun openDate(editText: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(y, m, d)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editText.setText(format.format(selectedCal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun openGallery() {
        galleryPicker.launch("image/*")
    }

    private val galleryPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                imgCow.setImageURI(uri)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun loadCowData(cowKey: String) {
        val uid = auth.currentUser?.uid ?: return

        database.child("users_data")
            .child(uid)
            .child("cows")
            .child(cowKey)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    Toast.makeText(this, "Cow not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val cow = snapshot.getValue(CowModel::class.java)
                if (cow != null) {
                    etCowNo.setText(cow.cowNo)
                    etDob.setText(cow.dob)
                    etLactation.setText(cow.lactation)
                    etSire.setText(cow.sire)
                    etDam.setText(cow.dam)
                    etCalving.setText(cow.calving)
                    etYield.setText(cow.yield)
                    etDeWorming.setText(cow.deworming)
                    etAIDate.setText(cow.aiDate)

                    etPregDate.setText("")
                    etMedical.setText(cow.medical)
                    etRemarks.setText(cow.remarks)

                    existingImagePath = cow.imagePath

                    when (cow.bornType) {
                        "Purchased" -> rgLocation.check(R.id.purchased)
                        "Born On Farm" -> rgLocation.check(R.id.born)
                    }

                    when (cow.progesterone) {
                        "1 Month" -> rgProg.check(R.id.rbProg1)
                        "3 Months" -> rgProg.check(R.id.rbProg3)
                        "7 Months" -> rgProg.check(R.id.rbProg7)
                    }

                    cbFMD.isChecked = false
                    cbRabies.isChecked = false
                    cbTT.isChecked = false
                    cbLumpy.isChecked = false
                    cbThileria.isChecked = false
                    cbBrucella.isChecked = false

                    etFMD.setText("")
                    etRabies.setText("")
                    etTT.setText("")
                    etLumpy.setText("")
                    etThileria.setText("")
                    etBrucella.setText("")

                    cow.vaccines.forEach { (vaccine, date) ->
                        when (vaccine) {
                            "FMD" -> {
                                cbFMD.isChecked = true
                                etFMD.setText(date)
                            }
                            "Rabies" -> {
                                cbRabies.isChecked = true
                                etRabies.setText(date)
                            }
                            "TT" -> {
                                cbTT.isChecked = true
                                etTT.setText(date)
                            }
                            "Lumpy" -> {
                                cbLumpy.isChecked = true
                                etLumpy.setText(date)
                            }
                            "Thileria" -> {
                                cbThileria.isChecked = true
                                etThileria.setText(date)
                            }
                            "Brucella" -> {
                                cbBrucella.isChecked = true
                                etBrucella.setText(date)
                            }
                        }
                    }

                    if (cow.imagePath.isNotEmpty()) {
                        val file = File(cow.imagePath)
                        if (file.exists()) {
                            imgCow.setImageURI(Uri.fromFile(file))
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load cow data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveCow() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val cowNo = etCowNo.text.toString().trim()
        val bornType = getRadioText(rgLocation)

        if (cowNo.isEmpty()) {
            Toast.makeText(this, "Cow Number is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select cow image", Toast.LENGTH_SHORT).show()
            return
        }

        if (bornType.isEmpty()) {
            Toast.makeText(this, "Please select Purchased or Born On Farm", Toast.LENGTH_SHORT).show()
            return
        }

        val localPath = saveImageLocally(imageUri!!, cowNo)

        val cowRef = database.child("users_data")
            .child(uid)
            .child("cows")
            .push()

        val cow = CowModel(
            firebaseKey = cowRef.key ?: "",
            cowNo = cowNo,
            dob = etDob.text.toString().trim(),
            bornType = bornType,
            lactation = etLactation.text.toString().trim(),
            sire = etSire.text.toString().trim(),
            dam = etDam.text.toString().trim(),
            calving = etCalving.text.toString().trim(),
            yield = etYield.text.toString().trim(),
            deworming = etDeWorming.text.toString().trim(),
            aiDate = etAIDate.text.toString().trim(),
            pregnancyDate = "",
            medical = etMedical.text.toString().trim(),
            remarks = etRemarks.text.toString().trim(),
            progesterone = getRadioText(rgProg),
            vaccines = getVaccines(),
            imagePath = localPath,
            time = System.currentTimeMillis()
        )

        cowRef.setValue(cow)
            .addOnSuccessListener {
                val manualPregDate = etPregDate.text.toString().trim()

                if (manualPregDate.isNotEmpty()) {
                    val pregnancyMap = hashMapOf<String, Any>(
                        "breedingDate" to manualPregDate,
                        "deliveryDate" to "",
                        "notes" to ""
                    )

                    cowRef.child("pregnancy").setValue(pregnancyMap)
                }

                Toast.makeText(this, "Cow Saved 🐄", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, CowListActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message ?: "Failed to save cow", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateCow(cowKey: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val cowNo = etCowNo.text.toString().trim()
        val bornType = getRadioText(rgLocation)

        if (cowNo.isEmpty()) {
            Toast.makeText(this, "Cow Number is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (bornType.isEmpty()) {
            Toast.makeText(this, "Please select Purchased or Born On Farm", Toast.LENGTH_SHORT).show()
            return
        }

        val finalImagePath = when {
            imageUri != null -> saveImageLocally(imageUri!!, cowNo)
            existingImagePath.isNotEmpty() -> existingImagePath
            else -> ""
        }

        val updatedCow = CowModel(
            firebaseKey = cowKey,
            cowNo = cowNo,
            dob = etDob.text.toString().trim(),
            bornType = bornType,
            lactation = etLactation.text.toString().trim(),
            sire = etSire.text.toString().trim(),
            dam = etDam.text.toString().trim(),
            calving = etCalving.text.toString().trim(),
            yield = etYield.text.toString().trim(),
            deworming = etDeWorming.text.toString().trim(),
            aiDate = etAIDate.text.toString().trim(),
            pregnancyDate = "",
            medical = etMedical.text.toString().trim(),
            remarks = etRemarks.text.toString().trim(),
            progesterone = getRadioText(rgProg),
            vaccines = getVaccines(),
            imagePath = finalImagePath,
            time = System.currentTimeMillis()
        )

        database.child("users_data")
            .child(uid)
            .child("cows")
            .child(cowKey)
            .get()
            .addOnSuccessListener { snapshot ->
                val pregnancySnapshot = snapshot.child("pregnancy").value

                val updateMap = HashMap<String, Any?>()
                updateMap["firebaseKey"] = updatedCow.firebaseKey
                updateMap["cowNo"] = updatedCow.cowNo
                updateMap["dob"] = updatedCow.dob
                updateMap["bornType"] = updatedCow.bornType
                updateMap["lactation"] = updatedCow.lactation
                updateMap["sire"] = updatedCow.sire
                updateMap["dam"] = updatedCow.dam
                updateMap["calving"] = updatedCow.calving
                updateMap["yield"] = updatedCow.yield
                updateMap["deworming"] = updatedCow.deworming
                updateMap["aiDate"] = updatedCow.aiDate
                updateMap["pregnancyDate"] = ""
                updateMap["medical"] = updatedCow.medical
                updateMap["remarks"] = updatedCow.remarks
                updateMap["progesterone"] = updatedCow.progesterone
                updateMap["vaccines"] = updatedCow.vaccines
                updateMap["imagePath"] = updatedCow.imagePath
                updateMap["time"] = updatedCow.time
                updateMap["pregnancy"] = pregnancySnapshot

                val manualPregDate = etPregDate.text.toString().trim()
                if (pregnancySnapshot == null && manualPregDate.isNotEmpty()) {
                    updateMap["pregnancy"] = hashMapOf(
                        "breedingDate" to manualPregDate,
                        "deliveryDate" to "",
                        "notes" to ""
                    )
                }

                database.child("users_data")
                    .child(uid)
                    .child("cows")
                    .child(cowKey)
                    .updateChildren(updateMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cow Updated 🐄", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, CowDetailsActivity::class.java).apply {
                            putExtra("COW_KEY", cowKey)
                        })
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message ?: "Failed to update cow", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message ?: "Failed to update cow", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveImageLocally(uri: Uri, cowNo: String): String {
        val input = contentResolver.openInputStream(uri)
        val folder = File(getExternalFilesDir("cow_images"), "")
        if (!folder.exists()) folder.mkdirs()

        val safeCowNo = cowNo.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val file = File(folder, "$safeCowNo.jpg")
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

        if (cbFMD.isChecked) map["FMD"] = etFMD.text.toString().trim()
        if (cbRabies.isChecked) map["Rabies"] = etRabies.text.toString().trim()
        if (cbTT.isChecked) map["TT"] = etTT.text.toString().trim()
        if (cbLumpy.isChecked) map["Lumpy"] = etLumpy.text.toString().trim()
        if (cbThileria.isChecked) map["Thileria"] = etThileria.text.toString().trim()
        if (cbBrucella.isChecked) map["Brucella"] = etBrucella.text.toString().trim()

        return map
    }
}    private lateinit var etPregDate: EditText
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

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cow)

        auth = FirebaseAuth.getInstance()

        bindViews()
        setupCalendars()

        isEditMode = intent.getBooleanExtra("IS_EDIT", false)
        currentCowKey = intent.getStringExtra("COW_KEY")

        if (isEditMode && currentCowKey != null) {
            btnSave.text = "Update Cow"
            loadCowData(currentCowKey!!)
        } else {
            btnSave.text = "Save Cow"
        }

        imgCow.setOnClickListener {
            openGallery()
        }

        btnSave.setOnClickListener {
            if (isEditMode && currentCowKey != null) {
                updateCow(currentCowKey!!)
            } else {
                saveCow()
            }
        }

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

    private fun bindViews() {
        imgCow = findViewById(R.id.imgCow)

        etCowNo = findViewById(R.id.etCowNo)
        etDob = findViewById(R.id.etDob)
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

    private fun setupCalendars() {
        val allDates = listOf(
            etDob, etCalving, etAIDate, etPregDate,
            etFMD, etRabies, etTT, etLumpy, etThileria, etBrucella
        )

        for (editText in allDates) {
            editText.setOnClickListener { openDate(editText) }
        }
    }

    private fun openDate(editText: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(y, m, d)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editText.setText(format.format(selectedCal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun openGallery() {
        galleryPicker.launch("image/*")
    }

    private val galleryPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                imgCow.setImageURI(uri)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun loadCowData(cowKey: String) {
        val uid = auth.currentUser?.uid ?: return

        database.child("users_data")
            .child(uid)
            .child("cows")
            .child(cowKey)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    Toast.makeText(this, "Cow not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val cow = snapshot.getValue(CowModel::class.java)
                if (cow != null) {
                    etCowNo.setText(cow.cowNo)
                    etDob.setText(cow.dob)
                    etLactation.setText(cow.lactation)
                    etSire.setText(cow.sire)
                    etDam.setText(cow.dam)
                    etCalving.setText(cow.calving)
                    etYield.setText(cow.yield)
                    etDeWorming.setText(cow.deworming)
                    etAIDate.setText(cow.aiDate)

                    etPregDate.setText("")
                    etMedical.setText(cow.medical)
                    etRemarks.setText(cow.remarks)

                    existingImagePath = cow.imagePath

                    when (cow.bornType) {
                        "Purchased" -> rgLocation.check(R.id.purchased)
                        "Born On Farm" -> rgLocation.check(R.id.born)
                    }

                    when (cow.progesterone) {
                        "1 Month" -> rgProg.check(R.id.rbProg1)
                        "3 Months" -> rgProg.check(R.id.rbProg3)
                        "7 Months" -> rgProg.check(R.id.rbProg7)
                    }

                    cbFMD.isChecked = false
                    cbRabies.isChecked = false
                    cbTT.isChecked = false
                    cbLumpy.isChecked = false
                    cbThileria.isChecked = false
                    cbBrucella.isChecked = false

                    etFMD.setText("")
                    etRabies.setText("")
                    etTT.setText("")
                    etLumpy.setText("")
                    etThileria.setText("")
                    etBrucella.setText("")

                    cow.vaccines.forEach { (vaccine, date) ->
                        when (vaccine) {
                            "FMD" -> {
                                cbFMD.isChecked = true
                                etFMD.setText(date)
                            }
                            "Rabies" -> {
                                cbRabies.isChecked = true
                                etRabies.setText(date)
                            }
                            "TT" -> {
                                cbTT.isChecked = true
                                etTT.setText(date)
                            }
                            "Lumpy" -> {
                                cbLumpy.isChecked = true
                                etLumpy.setText(date)
                            }
                            "Thileria" -> {
                                cbThileria.isChecked = true
                                etThileria.setText(date)
                            }
                            "Brucella" -> {
                                cbBrucella.isChecked = true
                                etBrucella.setText(date)
                            }
                        }
                    }

                    if (cow.imagePath.isNotEmpty()) {
                        val file = File(cow.imagePath)
                        if (file.exists()) {
                            imgCow.setImageURI(Uri.fromFile(file))
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load cow data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveCow() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val cowNo = etCowNo.text.toString().trim()
        val bornType = getRadioText(rgLocation)

        if (cowNo.isEmpty()) {
            Toast.makeText(this, "Cow Number is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select cow image", Toast.LENGTH_SHORT).show()
            return
        }

        if (bornType.isEmpty()) {
            Toast.makeText(this, "Please select Purchased or Born On Farm", Toast.LENGTH_SHORT).show()
            return
        }

        val localPath = saveImageLocally(imageUri!!, cowNo)

        val cowRef = database.child("users_data")
            .child(uid)
            .child("cows")
            .push()

        val cow = CowModel(
            firebaseKey = cowRef.key ?: "",
            cowNo = cowNo,
            dob = etDob.text.toString().trim(),
            bornType = bornType,
            lactation = etLactation.text.toString().trim(),
            sire = etSire.text.toString().trim(),
            dam = etDam.text.toString().trim(),
            calving = etCalving.text.toString().trim(),
            yield = etYield.text.toString().trim(),
            deworming = etDeWorming.text.toString().trim(),
            aiDate = etAIDate.text.toString().trim(),
            pregnancyDate = "",
            medical = etMedical.text.toString().trim(),
            remarks = etRemarks.text.toString().trim(),
            progesterone = getRadioText(rgProg),
            vaccines = getVaccines(),
            imagePath = localPath,
            time = System.currentTimeMillis()
        )

        cowRef.setValue(cow)
            .addOnSuccessListener {
                val manualPregDate = etPregDate.text.toString().trim()

                if (manualPregDate.isNotEmpty()) {
                    val pregnancyMap = hashMapOf<String, Any>(
                        "breedingDate" to manualPregDate,
                        "deliveryDate" to "",
                        "notes" to ""
                    )

                    cowRef.child("pregnancy").setValue(pregnancyMap)
                }

                Toast.makeText(this, "Cow Saved 🐄", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, CowListActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message ?: "Failed to save cow", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateCow(cowKey: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val cowNo = etCowNo.text.toString().trim()
        val bornType = getRadioText(rgLocation)

        if (cowNo.isEmpty()) {
            Toast.makeText(this, "Cow Number is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (bornType.isEmpty()) {
            Toast.makeText(this, "Please select Purchased or Born On Farm", Toast.LENGTH_SHORT).show()
            return
        }

        val finalImagePath = when {
            imageUri != null -> saveImageLocally(imageUri!!, cowNo)
            existingImagePath.isNotEmpty() -> existingImagePath
            else -> ""
        }

        val updatedCow = CowModel(
            firebaseKey = cowKey,
            cowNo = cowNo,
            dob = etDob.text.toString().trim(),
            bornType = bornType,
            lactation = etLactation.text.toString().trim(),
            sire = etSire.text.toString().trim(),
            dam = etDam.text.toString().trim(),
            calving = etCalving.text.toString().trim(),
            yield = etYield.text.toString().trim(),
            deworming = etDeWorming.text.toString().trim(),
            aiDate = etAIDate.text.toString().trim(),
            pregnancyDate = "",
            medical = etMedical.text.toString().trim(),
            remarks = etRemarks.text.toString().trim(),
            progesterone = getRadioText(rgProg),
            vaccines = getVaccines(),
            imagePath = finalImagePath,
            time = System.currentTimeMillis()
        )

        database.child("users_data")
            .child(uid)
            .child("cows")
            .child(cowKey)
            .get()
            .addOnSuccessListener { snapshot ->
                val pregnancySnapshot = snapshot.child("pregnancy").value

                val updateMap = HashMap<String, Any?>()
                updateMap["firebaseKey"] = updatedCow.firebaseKey
                updateMap["cowNo"] = updatedCow.cowNo
                updateMap["dob"] = updatedCow.dob
                updateMap["bornType"] = updatedCow.bornType
                updateMap["lactation"] = updatedCow.lactation
                updateMap["sire"] = updatedCow.sire
                updateMap["dam"] = updatedCow.dam
                updateMap["calving"] = updatedCow.calving
                updateMap["yield"] = updatedCow.yield
                updateMap["deworming"] = updatedCow.deworming
                updateMap["aiDate"] = updatedCow.aiDate
                updateMap["pregnancyDate"] = ""
                updateMap["medical"] = updatedCow.medical
                updateMap["remarks"] = updatedCow.remarks
                updateMap["progesterone"] = updatedCow.progesterone
                updateMap["vaccines"] = updatedCow.vaccines
                updateMap["imagePath"] = updatedCow.imagePath
                updateMap["time"] = updatedCow.time
                updateMap["pregnancy"] = pregnancySnapshot

                val manualPregDate = etPregDate.text.toString().trim()
                if (pregnancySnapshot == null && manualPregDate.isNotEmpty()) {
                    updateMap["pregnancy"] = hashMapOf(
                        "breedingDate" to manualPregDate,
                        "deliveryDate" to "",
                        "notes" to ""
                    )
                }

                database.child("users_data")
                    .child(uid)
                    .child("cows")
                    .child(cowKey)
                    .updateChildren(updateMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cow Updated 🐄", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, CowDetailsActivity::class.java).apply {
                            putExtra("COW_KEY", cowKey)
                        })
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message ?: "Failed to update cow", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message ?: "Failed to update cow", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveImageLocally(uri: Uri, cowNo: String): String {
        val input = contentResolver.openInputStream(uri)
        val folder = File(getExternalFilesDir("cow_images"), "")
        if (!folder.exists()) folder.mkdirs()

        val safeCowNo = cowNo.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val file = File(folder, "$safeCowNo.jpg")
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

        if (cbFMD.isChecked) map["FMD"] = etFMD.text.toString().trim()
        if (cbRabies.isChecked) map["Rabies"] = etRabies.text.toString().trim()
        if (cbTT.isChecked) map["TT"] = etTT.text.toString().trim()
        if (cbLumpy.isChecked) map["Lumpy"] = etLumpy.text.toString().trim()
        if (cbThileria.isChecked) map["Thileria"] = etThileria.text.toString().trim()
        if (cbBrucella.isChecked) map["Brucella"] = etBrucella.text.toString().trim()

        return map
    }
}
