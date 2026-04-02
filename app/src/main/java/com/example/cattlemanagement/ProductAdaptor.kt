package com.example.cattlemanagement
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class ProductAdaptor(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdaptor.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.productImage)
        val name: TextView = itemView.findViewById(R.id.productName)
        val price: TextView = itemView.findViewById(R.id.productPrice)
        val oldPrice: TextView = itemView.findViewById(R.id.productOldPrice)
        val discount: TextView = itemView.findViewById(R.id.discountBadge)
        val weight: TextView = itemView.findViewById(R.id.productWeight)
        val buyNowBtn: Button = itemView.findViewById(R.id.buyNowBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.name
        holder.price.text = product.price
        holder.discount.text = product.discount
        holder.weight.text = product.weight

        // Old price with strikethrough
        holder.oldPrice.text = product.oldPrice
        holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        // Load image: URL first, fallback to drawable
        if (product.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .placeholder(product.image)
                .error(product.image)
                .into(holder.image)
        } else {
            holder.image.setImageResource(product.image)
        }

        // Open link on card click OR Buy Now button click
        val openLink = View.OnClickListener {
            if (product.link.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.link))
                holder.itemView.context.startActivity(intent)
            }
        }

        holder.itemView.setOnClickListener(openLink)
        holder.buyNowBtn.setOnClickListener(openLink)
    }
}
