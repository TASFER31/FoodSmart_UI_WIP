package com.example.foodsmart_ui_overview

import java.util.UUID

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
) {
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
        // TODO: Implement date calculation
        // Parse expiryDate string and compare with current date
        return 0
    }

    /**
     * Get expiry status as a string
     */
    fun getExpiryStatus(): String {
        val days = getDaysUntilExpiry()
        return when {
            days < 0 -> "Expired"
            days <= 3 -> "Expiring Soon"
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
