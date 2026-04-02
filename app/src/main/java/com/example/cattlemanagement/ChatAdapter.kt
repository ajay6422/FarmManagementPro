package com.example.cattlemanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: MutableList<ChatMessage>,
    private val onTranslateClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_BOT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) TYPE_USER else TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_message, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bot_message, parent, false)
            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is UserViewHolder -> {
                holder.tvMessage.text = message.text
            }

            is BotViewHolder -> {
                holder.tvMessage.text = message.text

                if (!message.hindiText.isNullOrEmpty()) {
                    holder.tvHindiMessage.visibility = View.VISIBLE
                    holder.tvHindiMessage.text = message.hindiText
                    holder.btnTranslateHindi.text = "Translated"
                    holder.btnTranslateHindi.isEnabled = false
                } else {
                    holder.tvHindiMessage.visibility = View.GONE
                    holder.btnTranslateHindi.text = "Translate to Hindi"
                    holder.btnTranslateHindi.isEnabled = true
                    holder.btnTranslateHindi.setOnClickListener {
                        onTranslateClick(position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    fun submitList(newMessages: MutableList<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val btnTranslateHindi: Button = itemView.findViewById(R.id.btnTranslateHindi)
        val tvHindiMessage: TextView = itemView.findViewById(R.id.tvHindiMessage)
    }
}