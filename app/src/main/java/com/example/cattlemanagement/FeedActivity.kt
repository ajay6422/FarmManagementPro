package com.ajaykumar.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var tvCartCount: TextView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var feedList: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val backBtn = findViewById<ImageView>(R.id.feedBackBtn)
        val cartBtn = findViewById<ImageView>(R.id.feedCartBtn)
        tvCartCount = findViewById(R.id.tvCartBadge)
        recycler = findViewById(R.id.feedRecycler)

        backBtn.setOnClickListener { finish() }

        cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        recycler.layoutManager = GridLayoutManager(this, 2)

        feedList = listOf(
            Product(
                name = "Ultimate Dry Care",
                price = "₹1,804",
                oldPrice = "₹2,391",
                discount = "25% OFF",
                weight = "50 Kg",
                image = R.drawable.feed_1,
                imageUrl = "https://m.media-amazon.com/images/I/71k6XlMlMTL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=cattle+dry+care+feed+50kg",
                description = "A balanced dry cattle feed formula designed to support body condition, digestion, and daily nutrition needs during non-lactation periods.",
                benefits = "Supports body strength, improves nutrition balance, and helps maintain healthy cattle condition.",
                usage = "Feed according to animal age, body weight, and veterinarian or nutritionist guidance."
            ),
            Product(
                name = "Transition Wellness (Pre-20)",
                price = "₹2,438",
                oldPrice = "₹2,981",
                discount = "18% OFF",
                weight = "50 Kg",
                image = R.drawable.feed_2,
                imageUrl = "https://m.media-amazon.com/images/I/71k6XlMlMTL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=transition+wellness+cattle+feed",
                description = "Transition feed support for cattle before and around important production stages, helping nutritional readiness.",
                benefits = "Helps transition feeding, improves stamina, and supports digestive adjustment.",
                usage = "Use in planned transition feeding schedule with proper water access."
            ),
            Product(
                name = "Xtra Milk Prime",
                price = "₹2,145",
                oldPrice = "₹2,500",
                discount = "14% OFF",
                weight = "50 Kg",
                image = R.drawable.feed_3,
                imageUrl = "https://m.media-amazon.com/images/I/71k6XlMlMTL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=xtra+milk+prime+cattle+feed",
                description = "A milk-support nutrition blend intended for dairy animals requiring energy and balanced feed support.",
                benefits = "Supports milk performance, energy balance, and nutritional consistency.",
                usage = "Serve in measured feed portions with green fodder and clean water."
            ),
            Product(
                name = "VetMantra Salt Lick 2 Kg",
                price = "₹430",
                oldPrice = "₹720",
                discount = "40% OFF",
                weight = "2 Kg",
                image = R.drawable.feed_4,
                imageUrl = "https://m.media-amazon.com/images/I/61CQ5YNYGEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=vetmantra+salt+lick+cattle",
                description = "Mineral salt lick block for cattle and other farm animals to support mineral intake in daily management.",
                benefits = "Helps mineral supplementation, supports licking behavior, and improves routine nutrition.",
                usage = "Place near feeding area and allow animals free access as needed."
            ),
            Product(
                name = "Dairy Silver Calves Feed",
                price = "₹1,455",
                oldPrice = "₹1,670",
                discount = "13% OFF",
                weight = "50 Kg",
                image = R.drawable.feed_5,
                imageUrl = "https://m.media-amazon.com/images/I/71k6XlMlMTL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=dairy+silver+calves+feed",
                description = "Feed blend intended for growing calves with focus on daily nourishment and structured feeding support.",
                benefits = "Supports growth-stage feeding, balanced nourishment, and healthy development.",
                usage = "Use in calf feeding routine in suitable quantity as per age and farm plan."
            )
        )

        productAdapter = ProductAdapter(
            products = feedList,
            onProductClick = { product ->
                val intent = Intent(this, ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onAddToCart = { product ->
                CartManager.addToCart(product)
                updateCartCount()
                Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
            }
        )

        recycler.adapter = productAdapter
    }

    override fun onResume() {
        super.onResume()
        updateCartCount()
    }

    private fun updateCartCount() {
        val count = CartManager.getCount()
        tvCartCount.text = count.toString()
        tvCartCount.visibility = if (count > 0) TextView.VISIBLE else TextView.GONE
    }
}                        image = R.drawable.feed_2,
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
