package com.example.cattlemanagement

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Products : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        val recycler = findViewById<RecyclerView>(R.id.productsRecycler)
        recycler.layoutManager = GridLayoutManager(this, 2)

        val productList = listOf(
            Product(
                name = "A2 Gir Bilona Ghee (500ml)",
                price = "₹1499",
                oldPrice = "₹1799",
                discount = "17% OFF",
                weight = "500 ml",
                image = R.drawable.pro_1,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=A2+gir+bilona+ghee+500ml"
            ),
            Product(
                name = "Krishiv Organic A2 Gir Bilona",
                price = "₹2499",
                oldPrice = "₹3399",
                discount = "26% OFF",
                weight = "1 Liter",
                image = R.drawable.pro_2,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=krishiv+organic+a2+gir+bilona+ghee+1+liter"
            ),
            Product(
                name = "DEV DHUNI - 100% Pure",
                price = "₹349",
                oldPrice = "₹499",
                discount = "30% OFF",
                weight = "100 gm",
                image = R.drawable.pro_3,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=dev+dhuni+pure+cow+dung+dhoop"
            ),
            Product(
                name = "Cow Dung Panchgavya Cups",
                price = "₹299",
                oldPrice = "₹999",
                discount = "70% OFF",
                weight = "105 Pieces",
                image = R.drawable.pro_4,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=cow+dung+panchgavya+cups+hawan"
            ),
            Product(
                name = "Anveshan A2 Cow Ghee",
                price = "₹1607",
                oldPrice = "₹2160",
                discount = "26% OFF",
                weight = "1 Kg",
                image = R.drawable.pro_5,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=anveshan+a2+cow+ghee+1kg"
            ),
            Product(
                name = "Vermicompost",
                price = "₹227",
                oldPrice = "₹499",
                discount = "55% OFF",
                weight = "5 Kg",
                image = R.drawable.pro_6,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=vermicompost+organic+fertilizer+5kg"
            ),
            Product(
                name = "Cow Dung Manure Fertilizers",
                price = "₹195",
                oldPrice = "₹299",
                discount = "35% OFF",
                weight = "Organic Pack",
                image = R.drawable.pro_7,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=cow+dung+manure+organic+fertilizer"
            ),
            Product(
                name = "Sandalwood Chandan Dhoop Batti",
                price = "₹299",
                oldPrice = "₹420",
                discount = "29% OFF",
                weight = "Pack of 1",
                image = R.drawable.pro_8,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=sandalwood+chandan+dhoop+batti"
            ),
            Product(
                name = "Two Brothers Organic Ghee",
                price = "₹3202",
                oldPrice = "₹3599",
                discount = "11% OFF",
                weight = "1 Liter",
                image = R.drawable.pro_9,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=two+brothers+organic+ghee+1+liter"
            ),
            Product(
                name = "Vedic Premium A2 Gir Cow Ghee",
                price = "₹2029",
                oldPrice = "₹2299",
                discount = "12% OFF",
                weight = "1 Liter",
                image = R.drawable.pro_10,
                imageUrl = "https://m.media-amazon.com/images/I/71dNVMjpVdL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=vedic+premium+a2+gir+cow+ghee+1+liter"
            )
        )

        recycler.adapter = ProductAdaptor(productList)
    }
}
