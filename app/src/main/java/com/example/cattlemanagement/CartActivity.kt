package com.ajaykumar.cattlemanagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var tvItemCount: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDelivery: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var emptyLayout: LinearLayout
    private lateinit var cartLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val backBtn = findViewById<ImageView>(R.id.cartBackBtn)
        recycler = findViewById(R.id.cartRecycler)
        tvItemCount = findViewById(R.id.tvItemCount)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvDelivery = findViewById(R.id.tvDelivery)
        tvTotal = findViewById(R.id.tvTotal)
        btnCheckout = findViewById(R.id.btnCheckout)
        emptyLayout = findViewById(R.id.emptyCartLayout)
        cartLayout = findViewById(R.id.cartContentLayout)

        backBtn.setOnClickListener { finish() }

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = CartAdapter(
            onQuantityChanged = { refreshSummary() },
            onRemove = { refreshSummary() }
        )
        recycler.adapter = adapter

        btnCheckout.setOnClickListener {
            val items = CartManager.getItems()
            val firstLink = items.firstOrNull()?.product?.link ?: "https://www.amazon.in/"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(firstLink)))
        }

        refreshSummary()
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
        refreshSummary()
    }

    private fun refreshSummary() {
        val items = CartManager.getItems()
        adapter.refresh()

        if (items.isEmpty()) {
            emptyLayout.visibility = View.VISIBLE
            cartLayout.visibility = View.GONE
            btnCheckout.isEnabled = false
            tvItemCount.text = "0 items"
            tvSubtotal.text = "₹0"
            tvDelivery.text = "₹0"
            tvTotal.text = "₹0"
        } else {
            emptyLayout.visibility = View.GONE
            cartLayout.visibility = View.VISIBLE
            btnCheckout.isEnabled = true

            val count = CartManager.getCount()
            val subtotal = CartManager.getTotal()
            val delivery = if (subtotal >= 500) 0 else 49
            val total = subtotal + delivery

            tvItemCount.text = "$count item${if (count > 1) "s" else ""} in cart"
            tvSubtotal.text = "₹$subtotal"
            tvDelivery.text = if (delivery == 0) "FREE" else "₹$delivery"
            tvTotal.text = "₹$total"
        }
    }
}
