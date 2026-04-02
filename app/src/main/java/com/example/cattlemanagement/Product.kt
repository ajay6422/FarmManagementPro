package com.example.cattlemanagement
data class Product(
    val name: String,
    val price: String,
    val oldPrice: String,
    val discount: String,
    val weight: String,
    val image: Int,
    val imageUrl: String = "",
    val link: String = ""

)
