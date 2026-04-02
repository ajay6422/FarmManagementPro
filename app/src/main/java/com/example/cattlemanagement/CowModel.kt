package com.example.cattlemanagement

data class CowModel(
    var cowNo: String = "",
    var dob: String = "",
    var bornType: String = "",
    var lactation: String = "",
    var sire: String = "",
    var dam: String = "",
    var calving: String = "",
    var yield: String = "",
    var deworming: String = "",
    var aiDate: String = "",
    var pregnancyDate: String = "",
    var medical: String = "",
    var remarks: String = "",
    var location: String = "",
    var progesterone: String = "",
    var vaccines: Map<String, String> = HashMap(),
    var imagePath: String = "",   // Local phone path
    var time: Long = 0L
)