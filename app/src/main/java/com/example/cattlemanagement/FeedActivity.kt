package com.example.cattlemanagement

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedActivity : AppCompatActivity() {

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_feed)

                val recycler = findViewById<RecyclerView>(R.id.feedRecycler)
                recycler.layoutManager = GridLayoutManager(this, 2)

                val feedList = listOf(
                    Product(
                        name = "Ultimate Dry Care",
                        price = "₹1804",
                        oldPrice = "₹2391",
                        discount = "25% OFF",
                        weight = "50Kg",
                        image = R.drawable.feed_1,
                        imageUrl = "https://m.media-amazon.com/images/I/71k6XlMlMTL._SX679_.jpg",
                        link = "https://www.amazon.in/s?k=cattle+dry+care+feed+50kg"
                    ),
                    Product(
                        name = "Transition Wellness (Pre-20)",
                        price = "₹2438",
                        oldPrice = "₹2981",
                        discount = "18% OFF",
                        weight = "50Kg",
                        image = R.drawable.feed_2,
                        imageUrl = "https://m.media-amazon.com/images/I/71k6XlMlMTL._SX679_.jpg",
                        link = "https://www.amazon.in/s?k=transition+wellness+cattle+feed"
                    ),
                    Product(
                        name = "Xtra Milk Prime",
                        price = "₹2145",
                        oldPrice = "₹2500",
                        discount = "14% OFF",
                        weight = "50Kg",
                        image = R.drawable.feed_3,
                        imageUrl = "https://m.media-amazon.com/images/I/71k6XlMlMTL._SX679_.jpg",
                        link = "https://www.amazon.in/s?k=xtra+milk+prime+cattle+feed"
                    ),
                    Product(
                        name = "VetMantra Salt Lick 2 Kg",
                        price = "₹430",
                        oldPrice = "₹720",
                        discount = "40% OFF",
                        weight = "2Kg",
                        image = R.drawable.feed_4,
                        imageUrl = "https://m.media-amazon.com/images/I/61CQ5YNYGEL._SX679_.jpg",
                        link = "https://www.amazon.in/s?k=vetmantra+salt+lick+cattle"
                    ),
                    Product(
                        name = "Dairy Silver Calves Feed",
                        price = "₹1455",
                        oldPrice = "₹1670",
                        discount = "13% OFF",
                        weight = "50Kg",
                        image = R.drawable.feed_5,
                        imageUrl = "https://m.media-amazon.com/images/I/71k6XlMlMTL._SX679_.jpg",
                        link = "https://www.amazon.in/s?k=dairy+silver+calves+feed"
                    )
                )

                recycler.adapter = ProductAdaptor(feedList)
            }
        }
