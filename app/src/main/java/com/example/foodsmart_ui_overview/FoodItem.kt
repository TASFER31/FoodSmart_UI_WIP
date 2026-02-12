package com.example.foodsmart_ui_overview

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.Calendar

/**
 * Data class representing a food item in the inventory
 */
data class FoodItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val inventory: String,          // Refrigerator, Freezer, Pantry, etc.
    val storage: String,             // Top shelf, bottom drawer, etc.
    val amount: String,              // 500g, 2 packs, etc.
    val quantity: Int,               // Number of items
    val price: Double,               // Price per item
    val expiryDate: String,          // Formatted date string
    val reminder: String,            // Reminder date
    val createdDate: String,         // When item was added
    val isExpired: Boolean = false,  // Whether item has expired
    val emoji: String = "📦"         // Emoji for the item
) : Serializable {
    /**
     * Calculate total cost of this item
     */
    fun getTotalCost(): Double {
        return price * quantity
    }

    /**
     * Get days until expiry
     * Returns negative number if already expired
     */
    fun getDaysUntilExpiry(): Int {
        val raw = expiryDate.trim()
        if (raw.isEmpty() || raw.startsWith("No expiry", ignoreCase = true)) return 9999
        val normalized = raw
            .removePrefix("Expires on")
            .removePrefix("Expires:")
            .trim()
        val patterns = listOf(
            "d MMMM, yyyy",
            "MMMM d, yyyy",
            "d MMM yyyy",
            "MMM d, yyyy"
        )
        var target: Date? = null
        for (p in patterns) {
            try {
                val fmt = SimpleDateFormat(p, Locale.ENGLISH)
                target = fmt.parse(normalized)
                if (target != null) break
            } catch (_: Exception) { }
        }
        if (target == null) return 9999
        val calNow = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calTarget = Calendar.getInstance().apply {
            time = target!!
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffMs = calTarget.timeInMillis - calNow.timeInMillis
        return (diffMs / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * Get expiry status as a string
     */
    fun getExpiryStatus(): String {
        val days = getDaysUntilExpiry()
        return when {
            days < 0 -> "Expired"
            days <= 7 -> "Expiring Soon"
            else -> "Fresh"
        }
    }

    /**
     * Get status color based on expiry
     */
    fun getStatusColor(): String {
        return when (getExpiryStatus()) {
            "Expired" -> "#D63031"       // Red
            "Expiring Soon" -> "#FDCB6E" // Yellow
            else -> "#00B894"            // Green
        }
    }
}
data class HistoryEvent(
    val type: String,
    val itemName: String,
    val timestamp: String
)
object ItemsStore {
    val items: MutableList<FoodItem> = mutableListOf()
    val history: MutableList<HistoryEvent> = mutableListOf()
    fun addItem(item: FoodItem) {
        items.add(item)
        history.add(HistoryEvent("ADD", item.name, item.createdDate))
    }
    fun deleteItem(item: FoodItem) {
        items.removeAll { it.id == item.id }
        val now = SimpleDateFormat("MMMM d, yyyy • hh:mm a", Locale.getDefault()).format(Date())
        history.add(HistoryEvent("DELETE", item.name, now))
    }
    fun updateItem(updated: FoodItem) {
        val idx = items.indexOfFirst { it.id == updated.id }
        if (idx >= 0) {
            items[idx] = updated
            val now = SimpleDateFormat("MMMM d, yyyy • hh:mm a", Locale.getDefault()).format(Date())
            history.add(HistoryEvent("EDIT", updated.name, now))
        }
    }
    fun ensureExpiryEvents() {
        val now = SimpleDateFormat("MMMM d, yyyy • hh:mm a", Locale.getDefault()).format(Date())
        for (item in items) {
            if (item.getExpiryStatus() == "Expired") {
                val already = history.any { it.type == "EXPIRE" && it.itemName == item.name }
                if (!already) {
                    history.add(HistoryEvent("EXPIRE", item.name, now))
                }
            }
        }
    }
}
