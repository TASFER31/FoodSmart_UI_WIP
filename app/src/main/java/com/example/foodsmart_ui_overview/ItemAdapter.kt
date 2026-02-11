package com.example.foodsmart_ui_overview

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private var items: MutableList<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit,
    private val onDeleteClick: (FoodItem, Int) -> Unit  // NEW: Delete callback
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemEmoji: TextView = view.findViewById(R.id.item_emoji)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemDetails: TextView = view.findViewById(R.id.item_details)
        val itemExpiry: TextView = view.findViewById(R.id.item_expiry)
        val statusIndicator: View = view.findViewById(R.id.status_indicator)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete_item)  // NEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemEmoji.text = getCategoryEmoji(item.category)
        holder.itemName.text = item.name
        holder.itemDetails.text = "${item.category} • ${item.quantity} item(s)"
        holder.itemExpiry.text = "Expires: ${item.expiryDate}"

        val expiryStatus = item.getExpiryStatus()
        holder.itemExpiry.setTextColor(Color.parseColor(item.getStatusColor()))

        val statusColor = when (expiryStatus) {
            "Expired" -> "#D63031"
            "Expiring Soon" -> "#FDCB6E"
            else -> "#00B894"
        }

        val drawable = holder.statusIndicator.background as? GradientDrawable
        drawable?.setColor(Color.parseColor(statusColor))

        // Click to edit item
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // Click delete button
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item, position)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<FoodItem>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    // NEW: Remove item from list
    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun filter(query: String?): List<FoodItem> {
        if (query.isNullOrEmpty()) {
            return items
        }

        return items.filter { item ->
            item.name.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)
        }
    }

    private fun getCategoryEmoji(category: String): String {
        return when (category) {
            "Dairy" -> "🥛"
            "Meat" -> "🥩"
            "Frozen Goods" -> "🧊"
            "Bakery" -> "🍞"
            "Vegetables" -> "🥬"
            "Fruits" -> "🍎"
            "Beverages" -> "🥤"
            else -> "📦"
        }
    }
}