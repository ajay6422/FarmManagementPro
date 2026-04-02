package com.example.cattlemanagement

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

class Machines : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machines)

        val recycler = findViewById<RecyclerView>(R.id.machineRecycler)
        recycler.layoutManager = GridLayoutManager(this, 2)

        val machineList = listOf(
            Product(
                name = "Hand Operated Milking Machine",
                price = "₹12000",
                oldPrice = "₹12500",
                discount = "4% OFF",
                weight = "12 Kg",
                image = R.drawable.machine_1,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=hand+operated+milking+machine+cattle"
            ),
            Product(
                name = "Neno Chaff Cutter + Single Phase",
                price = "₹16000",
                oldPrice = "₹17500",
                discount = "8% OFF",
                weight = "60 Kg",
                image = R.drawable.machine_2,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=neno+chaff+cutter+single+phase"
            ),
            Product(
                name = "Milking Machine 200 OIL",
                price = "₹23000",
                oldPrice = "₹25000",
                discount = "8% OFF",
                weight = "50 Kg",
                image = R.drawable.machine_2,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=milking+machine+200+oil+cattle"
            ),
            Product(
                name = "MODEL NO 120 OIL",
                price = "₹19000",
                oldPrice = "₹20000",
                discount = "5% OFF",
                weight = "35 Kg",
                image = R.drawable.machine_3,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=milking+machine+120+oil"
            ),
            Product(
                name = "L8 Chaff Cutter - 2 Blade",
                price = "₹12500",
                oldPrice = "₹14000",
                discount = "10% OFF",
                weight = "65 Kg",
                image = R.drawable.machine_4,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=L8+chaff+cutter+2+blade"
            ),
            Product(
                name = "Neno Plus Chaff Cutter",
                price = "₹8500",
                oldPrice = "₹10500",
                discount = "19% OFF",
                weight = "40 Kg",
                image = R.drawable.machine_5,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=neno+plus+chaff+cutter"
            ),
            Product(
                name = "Ayushman CowFit (10 Cattle)",
                price = "₹65000",
                oldPrice = "₹80000",
                discount = "19% OFF",
                weight = "Per Unit",
                image = R.drawable.ic_machine,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=ayushman+cowfit+cattle+health+monitor"
            ),
            Product(
                name = "Cattle Identification Belt",
                price = "₹249",
                oldPrice = "₹299",
                discount = "17% OFF",
                weight = "Standard Size",
                image = R.drawable.machine_6,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=cattle+identification+belt+tag"
            ),
            Product(
                name = "Drinking Water Bowl",
                price = "₹1399",
                oldPrice = "₹2499",
                discount = "44% OFF",
                weight = "Heavy Plastic",
                image = R.drawable.machine_7,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=cattle+drinking+water+bowl+heavy+plastic"
            ),
            Product(
                name = "Cattle Pregnancy Test Kit",
                price = "₹295",
                oldPrice = "₹350",
                discount = "15% OFF",
                weight = "Single Kit",
                image = R.drawable.machine_8,
                imageUrl = "https://m.media-amazon.com/images/I/71e5pPSXSQL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=cattle+pregnancy+test+kit"
            )
        )

        recycler.adapter = ProductAdaptor(machineList)
    }
}
