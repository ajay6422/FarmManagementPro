package com.example.cattlemanagement

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Health : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_vendor)

        val recycler = findViewById<RecyclerView>(R.id.healthRecycler)
        recycler.layoutManager = GridLayoutManager(this, 2)

        val healthList = listOf(
            Product(
                name = "FOOMASULE No.1 - for CATTLE",
                price = "₹188",
                oldPrice = "₹194",
                discount = "3% OFF",
                weight = "82 grams",
                image = R.drawable.hea_1,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=foomasule+cattle+foot+mouth+disease"
            ),
            Product(
                name = "PROLAPSGO",
                price = "₹234",
                oldPrice = "₹239",
                discount = "2% OFF",
                weight = "125 grams",
                image = R.drawable.hea_2,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=prolapsgo+cattle+medicine"
            ),
            Product(
                name = "HIT-O-GEN",
                price = "₹140",
                oldPrice = "₹141",
                discount = "1% OFF",
                weight = "47 grams",
                image = R.drawable.hea_3,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=hit-o-gen+cattle+heat+inducer"
            ),
            Product(
                name = "THUJA WARTNIL KIT",
                price = "₹271",
                oldPrice = "₹271",
                discount = "Full kit",
                weight = "138 grams",
                image = R.drawable.hea_4,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=thuja+wartnil+cattle+warts"
            ),
            Product(
                name = "FERTISULE",
                price = "₹281",
                oldPrice = "₹284",
                discount = "1% OFF",
                weight = "88 grams",
                image = R.drawable.hea_5,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=fertisule+cattle+repeat+breeding"
            ),
            Product(
                name = "SEPTIGO",
                price = "₹199",
                oldPrice = "₹234",
                discount = "15% OFF",
                weight = "100 ml",
                image = R.drawable.hea_6,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=septigo+antiseptic+cattle"
            ),
            Product(
                name = "UTEROGEN",
                price = "₹350",
                oldPrice = "₹427",
                discount = "18% OFF",
                weight = "200 ml",
                image = R.drawable.hea_7,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=uterogen+cattle+uterine+infection"
            ),
            Product(
                name = "MILKOGEN KIT",
                price = "₹499",
                oldPrice = "₹601",
                discount = "17% OFF",
                weight = "Full kit",
                image = R.drawable.hea_8,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=milkogen+kit+cattle+milk+booster"
            ),
            Product(
                name = "TEATASULE FIBRO GOLD KIT",
                price = "₹699",
                oldPrice = "₹822",
                discount = "15% OFF",
                weight = "Complete pack",
                image = R.drawable.hea_9,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=teatasule+fibro+gold+mastitis"
            ),
            Product(
                name = "TEATASULE MASTITIS KIT",
                price = "₹650",
                oldPrice = "₹765",
                discount = "15% OFF",
                weight = "Complete pack",
                image = R.drawable.hea_10,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=teatasule+mastitis+kit+cattle"
            )
        )

        recycler.adapter = ProductAdaptor(healthList)
    }
}
