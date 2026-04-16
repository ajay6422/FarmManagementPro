package com.ajaykumar.cattlemanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Health : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var tvCartCount: TextView
    private lateinit var healthAdapter: ProductAdapter
    private lateinit var healthList: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_vendor)

        val backBtn = findViewById<ImageView>(R.id.healthBackBtn)
        val cartBtn = findViewById<ImageView>(R.id.healthCartBtn)
        tvCartCount = findViewById(R.id.tvCartBadge)
        recycler = findViewById(R.id.healthRecycler)

        backBtn.setOnClickListener { finish() }

        cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        recycler.layoutManager = GridLayoutManager(this, 2)

        healthList = listOf(
            Product(
                name = "FOOMASULE No.1 - for CATTLE",
                price = "₹188",
                oldPrice = "₹194",
                discount = "3% OFF",
                weight = "82 grams",
                image = R.drawable.hea_1,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=foomasule+cattle+foot+mouth+disease",
                description = "Foot and mouth disease support formulation for cattle, used in routine veterinary care during outbreaks.",
                benefits = "Supports recovery, reduces clinical impact, and helps farm‑level disease management.",
                usage = "Use only as per veterinarian guidance; follow dosage and withdrawal period."
            ),
            Product(
                name = "PROLAPSGO",
                price = "₹234",
                oldPrice = "₹239",
                discount = "2% OFF",
                weight = "125 grams",
                image = R.drawable.hea_2,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=prolapsgo+cattle+medicine",
                description = "Veterinary product indicated for specific health conditions, under veterinary supervision.",
                benefits = "Supports targeted treatment, works with veterinary protocol.",
                usage = "Administer only as prescribed by a qualified veterinarian."
            ),
            Product(
                name = "HIT-O-GEN",
                price = "₹140",
                oldPrice = "₹141",
                discount = "1% OFF",
                weight = "47 grams",
                image = R.drawable.hea_3,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=hit-o-gen+cattle+heat+inducer",
                description = "Product related to heat‑induction or reproductive‑cycle support in cattle.",
                benefits = "Aids in planned breeding cycles, supports reproductive‑management programs.",
                usage = "Use only under veterinary supervision and as per recommended protocol."
            ),
            Product(
                name = "THUJA WARTNIL KIT",
                price = "₹271",
                oldPrice = "₹271",
                discount = "Full kit",
                weight = "138 grams",
                image = R.drawable.hea_4,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=thuja+wartnil+cattle+warts",
                description = "Topical/support kit intended for wart‑related lesions in cattle.",
                benefits = "Supports lesion management, used in routine farm‑care plans.",
                usage = "Clean area first, apply as directed, and monitor for any adverse reaction."
            ),
            Product(
                name = "FERTISULE",
                price = "₹281",
                oldPrice = "₹284",
                discount = "1% OFF",
                weight = "88 grams",
                image = R.drawable.hea_5,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=fertisule+cattle+repeat+breeding",
                description = "Support product for repeat‑breeding or fertility‑related challenges in cattle.",
                benefits = "Used in fertility‑support protocols, along with nutritional and management measures.",
                usage = "Use only under veterinary guidance and follow prescribed schedule."
            ),
            Product(
                name = "SEPTIGO",
                price = "₹199",
                oldPrice = "₹234",
                discount = "15% OFF",
                weight = "100 ml",
                image = R.drawable.hea_6,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=septigo+antiseptic+cattle",
                description = "Antiseptic or disinfectant‑type product for cattle and farm use.",
                benefits = "Helps in wound‑care and surface‑disinfection, supports hygiene measures.",
                usage = "Follow label instructions, use in well‑ventilated areas, and avoid contact with eyes."
            ),
            Product(
                name = "UTEROGEN",
                price = "₹350",
                oldPrice = "₹427",
                discount = "18% OFF",
                weight = "200 ml",
                image = R.drawable.hea_7,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=uterogen+cattle+uterine+infection",
                description = "Veterinary product indicated for uterine‑health or infection‑related issues.",
                benefits = "Used in uterine‑health protocols under veterinary supervision.",
                usage = "Strictly follow veterinary prescription and safety instructions."
            ),
            Product(
                name = "MILKOGEN KIT",
                price = "₹499",
                oldPrice = "₹601",
                discount = "17% OFF",
                weight = "Full kit",
                image = R.drawable.hea_8,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=milkogen+kit+cattle+milk+booster",
                description = "Support kit for milk‑related health or productivity parameters in dairy cattle.",
                benefits = "Used in milk‑support and well‑being protocols, under veterinary care.",
                usage = "Apply only as directed by veterinarian and in line with treatment plan."
            ),
            Product(
                name = "TEATASULE FIBRO GOLD KIT",
                price = "₹699",
                oldPrice = "₹822",
                discount = "15% OFF",
                weight = "Complete pack",
                image = R.drawable.hea_9,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=teatasule+fibro+gold+mastitis",
                description = "Mastitis‑related or teat‑care kit for dairy cattle.",
                benefits = "Supports teat‑health and mastitis‑management routines.",
                usage = "Use only under veterinary guidance as part of mastitis‑control protocol."
            ),
            Product(
                name = "TEATASULE MASTITIS KIT",
                price = "₹650",
                oldPrice = "₹765",
                discount = "15% OFF",
                weight = "Complete pack",
                image = R.drawable.hea_10,
                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
                link = "https://www.amazon.in/s?k=teatasule+mastitis+kit+cattle",
                description = "Kit designed for mastitis‑related treatment or support in dairy cattle.",
                benefits = "Used in mastitis therapy and prevention, under veterinary command.",
                usage = "Follow prescribed dosage, milk‑withdrawal, and safety instructions."
            )
        )

        healthAdapter = ProductAdapter(
            products = healthList,
            onProductClick = { product ->
                val intent = Intent(this, ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onAddToCart = { product ->
                CartManager.addToCart(product)
                updateCartCount()
            }
        )

        recycler.adapter = healthAdapter
    }

    override fun onResume() {
        super.onResume()
        updateCartCount()
    }

    private fun updateCartCount() {
        val count = CartManager.getCount()
        tvCartCount.text = count.toString()
        tvCartCount.visibility = if (count > 0) View.VISIBLE else View.GONE
    }
}                imageUrl = "https://m.media-amazon.com/images/I/61Q2GM3WSEL._SX679_.jpg",
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
