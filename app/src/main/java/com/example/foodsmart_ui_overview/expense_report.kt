package com.example.foodsmart_ui_overview

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.foodsmart_ui_overview.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class expense_report : AppCompatActivity() {

    // ========================================
    // DECLARE ALL VARIABLES
    // ========================================

    private lateinit var btnBack: ImageButton
    private lateinit var spinnerMonth: Spinner
    private lateinit var tvMoneyWasted: TextView
    private lateinit var tvMoneySaved: TextView
    private lateinit var tvTotalItems: TextView
    private lateinit var tvExpiredItems: TextView
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyEmpty: TextView
    private lateinit var expiredItemsContainer: LinearLayout
    private lateinit var activeItemsContainer: LinearLayout

    // ========================================
    // MAIN FUNCTION
    // ========================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_report)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        initializeViews()

        // Set up month selector
        setupMonthSpinner()

        // Set up button listeners
        setupClickListeners()

        // Load initial data
        loadForMonth("January 2026")
    }

    override fun onResume() {
        super.onResume()
        val selected = spinnerMonth.selectedItem?.toString() ?: "January 2026"
        loadForMonth(selected)
    }

    // ========================================
    // CONNECT XML TO KOTLIN
    // ========================================

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back)
        spinnerMonth = findViewById(R.id.spinner_month)
        tvMoneyWasted = findViewById(R.id.tv_money_wasted)
        tvMoneySaved = findViewById(R.id.tv_money_saved)
        tvTotalItems = findViewById(R.id.tv_total_items)
        tvExpiredItems = findViewById(R.id.tv_expired_items)
        historyContainer = findViewById(R.id.history_container)
        historyEmpty = findViewById(R.id.history_empty)
        expiredItemsContainer = findViewById(R.id.expired_items_container)
        activeItemsContainer = findViewById(R.id.active_items_container)
    }

    // ========================================
    // SET UP MONTH SELECTOR DROPDOWN
    // ========================================

    private fun setupMonthSpinner() {
        val months = buildMonthOptions()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerMonth.adapter = adapter

        spinnerMonth.setSelection(0)

        spinnerMonth.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selected = months[position]
                loadForMonth(selected)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
            }
        })
    }

    // ========================================
    // SET UP BUTTON LISTENERS
    // ========================================

    private fun setupClickListeners() {
        // Back button - close screen
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadForMonth(monthLabel: String) {
        ItemsStore.ensureExpiryEvents()
        val filtered = if (monthLabel == "All Activity") {
            ItemsStore.items
        } else {
            val monthYear = parseMonthLabel(monthLabel)
            ItemsStore.items.filter { item ->
                val created = parseCreatedDate(item.createdDate)
                created != null &&
                        created.get(Calendar.MONTH) == monthYear.get(Calendar.MONTH) &&
                        created.get(Calendar.YEAR) == monthYear.get(Calendar.YEAR)
            }
        }
        val wasted = filtered.filter { it.getExpiryStatus() == "Expired" }
            .sumOf { it.price * it.quantity }
        val saved = filtered.filter { it.getExpiryStatus() != "Expired" }
            .sumOf { it.price * it.quantity }
        tvMoneyWasted.text = "$${"%.2f".format(wasted)}"
        tvMoneySaved.text = "$${"%.2f".format(saved)}"
        tvTotalItems.text = filtered.size.toString()
        tvExpiredItems.text = filtered.count { it.getExpiryStatus() == "Expired" }.toString()

        val monthHistory = if (monthLabel == "All Activity") {
            ItemsStore.history
        } else {
            val monthYear = parseMonthLabel(monthLabel)
            ItemsStore.history.filter { h ->
                val ts = parseCreatedDate(h.timestamp)
                ts != null &&
                        ts.get(Calendar.MONTH) == monthYear.get(Calendar.MONTH) &&
                        ts.get(Calendar.YEAR) == monthYear.get(Calendar.YEAR)
            }
        }
        renderHistory(monthHistory.reversed())

        val expiredItems = filtered.filter { it.getExpiryStatus() == "Expired" }
        val activeItems = filtered.filter { it.getExpiryStatus() != "Expired" }
        renderExpiredItems(expiredItems)
        renderActiveItems(activeItems)
    }

    private fun renderHistory(events: List<HistoryEvent>) {
        historyContainer.removeAllViews()
        if (events.isEmpty()) {
            historyEmpty.visibility = TextView.VISIBLE
            return
        } else {
            historyEmpty.visibility = TextView.GONE
        }
        val pad = (12 * resources.displayMetrics.density).toInt()
        val iconSize = (24 * resources.displayMetrics.density).toInt()
        val marginStart = (12 * resources.displayMetrics.density).toInt()
        val subtitleTop = (2 * resources.displayMetrics.density).toInt()
        for (e in events) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(pad, pad, pad, pad)
            }
            val icon = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
                gravity = android.view.Gravity.CENTER
                text = when (e.type) {
                    "ADD" -> "➕"
                    "DELETE" -> "🗑️"
                    "EXPIRE" -> "❌"
                    else -> "ℹ️"
                }
                textSize = 18f
            }
            val content = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(marginStart, 0, 0, 0)
                }
                orientation = LinearLayout.VERTICAL
            }
            val title = TextView(this).apply {
                text = when (e.type) {
                    "ADD" -> "Added ${e.itemName}"
                    "DELETE" -> "Deleted ${e.itemName}"
                    "EXPIRE" -> "Expired ${e.itemName}"
                    else -> e.itemName
                }
                setTextColor(android.graphics.Color.parseColor("#000000"))
                textSize = 16f
                setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val subtitle = TextView(this).apply {
                text = e.timestamp
                setTextColor(android.graphics.Color.parseColor("#999999"))
                textSize = 12f
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(0, subtitleTop, 0, 0)
                layoutParams = lp
            }
            content.addView(title)
            content.addView(subtitle)
            row.addView(icon)
            row.addView(content)
            historyContainer.addView(row)
        }
    }

    private fun renderExpiredItems(items: List<FoodItem>) {
        expiredItemsContainer.removeAllViews()
        if (items.isEmpty()) {
            val empty = TextView(this)
            empty.text = "No expired items"
            empty.textSize = 14f
            empty.setTextColor(android.graphics.Color.parseColor("#999999"))
            expiredItemsContainer.addView(empty)
            return
        }
        val pad = (12 * resources.displayMetrics.density).toInt()
        for (item in items) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(pad, pad, pad, pad)
            }
            val left = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "${item.emoji} ${item.name}\n${item.category} • ${item.quantity} item${if (item.quantity != 1) "s" else ""}"
                setTextColor(android.graphics.Color.parseColor("#000000"))
                textSize = 16f
            }
            val right = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                orientation = LinearLayout.VERTICAL
            }
            val price = TextView(this).apply {
                text = "$${"%.2f".format(item.getTotalCost())}"
                setTextColor(android.graphics.Color.parseColor("#F44336"))
                textSize = 18f
                setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val meta = TextView(this).apply {
                text = "($${"%.2f".format(item.price)} × ${item.quantity})"
                setTextColor(android.graphics.Color.parseColor("#999999"))
                textSize = 12f
            }
            val status = TextView(this).apply {
                text = "Expired"
                setTextColor(android.graphics.Color.parseColor("#D63031"))
                textSize = 14f
                setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            right.addView(price)
            right.addView(meta)
            right.addView(status)
            row.addView(left)
            row.addView(right)
            expiredItemsContainer.addView(row)
        }
    }

    private fun renderActiveItems(items: List<FoodItem>) {
        activeItemsContainer.removeAllViews()
        if (items.isEmpty()) {
            val empty = TextView(this)
            empty.text = "No active items"
            empty.textSize = 14f
            empty.setTextColor(android.graphics.Color.parseColor("#999999"))
            activeItemsContainer.addView(empty)
            return
        }
        val pad = (12 * resources.displayMetrics.density).toInt()
        for (item in items) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(pad, pad, pad, pad)
            }
            val left = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "${item.emoji} ${item.name}\n${item.category} • ${item.quantity} item${if (item.quantity != 1) "s" else ""}"
                setTextColor(android.graphics.Color.parseColor("#000000"))
                textSize = 16f
            }
            val right = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                orientation = LinearLayout.VERTICAL
            }
            val price = TextView(this).apply {
                text = "$${"%.2f".format(item.getTotalCost())}"
                val priceColor = when (item.getExpiryStatus()) {
                    "Expiring Soon" -> android.graphics.Color.parseColor("#FF9800")
                    else -> android.graphics.Color.parseColor("#4CAF50")
                }
                setTextColor(priceColor)
                textSize = 18f
                setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            val meta = TextView(this).apply {
                text = "($${"%.2f".format(item.price)} × ${item.quantity})"
                setTextColor(android.graphics.Color.parseColor("#999999"))
                textSize = 12f
            }
            val status = TextView(this).apply {
                text = item.getExpiryStatus()
                val statusColor = android.graphics.Color.parseColor(item.getStatusColor())
                setTextColor(statusColor)
                textSize = 14f
                setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            right.addView(price)
            right.addView(meta)
            right.addView(status)
            row.addView(left)
            row.addView(right)
            activeItemsContainer.addView(row)
        }
    }

    private fun parseMonthLabel(label: String): Calendar {
        val fmt = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
        val d = fmt.parse(label) ?: Date()
        return Calendar.getInstance().apply { time = d }
    }

    private fun parseCreatedDate(created: String): Calendar? {
        return try {
            val fmt = SimpleDateFormat("MMMM d, yyyy • hh:mm a", Locale.ENGLISH)
            val d = fmt.parse(created) ?: return null
            Calendar.getInstance().apply { time = d }
        } catch (_: Exception) {
            null
        }
    }

    private fun buildMonthOptions(): List<String> {
        val set = linkedSetOf<String>()
        set.add("All Activity")
        val currentLabel = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date())
        set.add(currentLabel)
        ItemsStore.items.mapNotNull { parseCreatedDate(it.createdDate) }.forEach {
            set.add(SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(it.time))
        }
        ItemsStore.history.mapNotNull { parseCreatedDate(it.timestamp) }.forEach {
            set.add(SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(it.time))
        }
        return set.toList()
    }
}

// ========================================
// HELPER FUNCTIONS
// ========================================

// TODO: Add functions to:
// - Load expense data from database
// - Calculate totals for selected month
// - Load expired items list dynamically
// - Load history timeline dynamically
// - Track when items are added/deleted/expired
// - Store item prices for expense calculations
