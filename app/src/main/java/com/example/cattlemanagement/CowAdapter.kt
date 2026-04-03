package com.example.cattlemanagement

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.Locale

class CowAdapter(private val cowList: ArrayList<CowModel>) :
    RecyclerView.Adapter<CowAdapter.CowViewHolder>() {

    private val displayList = ArrayList<CowModel>()

    init {
        displayList.addAll(cowList)
    }

    class CowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCow: ImageView = itemView.findViewById(R.id.imgCow)
        val cowNo: TextView = itemView.findViewById(R.id.tvCowNo)
        val dob: TextView = itemView.findViewById(R.id.tvDob)
        val yield: TextView = itemView.findViewById(R.id.tvYield)
        val bornType: TextView = itemView.findViewById(R.id.tvLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cow, parent, false)
        return CowViewHolder(view)
    }

    override fun onBindViewHolder(holder: CowViewHolder, position: Int) {
        val cow = displayList[position]

        holder.cowNo.text = "Cow No: ${cow.cowNo}"
        holder.dob.text = "DOB: ${cow.dob}"
        holder.yield.text = "Milk Yield: ${cow.yield} L"
        holder.bornType.text = "Born Type: ${cow.bornType}"

        if (cow.imagePath.isNotEmpty()) {
            val file = File(cow.imagePath)
            if (file.exists()) {
                holder.imgCow.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            } else {
                holder.imgCow.setImageResource(R.drawable.home)
            }
        } else {
            holder.imgCow.setImageResource(R.drawable.home)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CowDetailsActivity::class.java)
            intent.putExtra("COW_KEY", cow.firebaseKey)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = displayList.size

    fun filter(query: String) {
        displayList.clear()

        if (query.isEmpty()) {
            displayList.addAll(cowList)
        } else {
            val searchText = query.lowercase(Locale.getDefault())
            for (cow in cowList) {
                if (cow.cowNo.lowercase(Locale.getDefault()).contains(searchText) ||
                    cow.bornType.lowercase(Locale.getDefault()).contains(searchText)
                ) {
                    displayList.add(cow)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun refreshData() {
        displayList.clear()
        displayList.addAll(cowList)
        notifyDataSetChanged()
    }
}