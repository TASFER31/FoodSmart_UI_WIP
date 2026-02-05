package com.example.foodsmart_ui_overview

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // Load initial data (example data for now)
        loadSampleData()
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
    }

    // ========================================
    // SET UP MONTH SELECTOR DROPDOWN
    // ========================================

    private fun setupMonthSpinner() {
        // Create list of months
        val months = arrayOf(
            "January 2026",
            "December 2025",
            "November 2025",
            "October 2025",
            "September 2025",
            "August 2025",
            "July 2025",
            "June 2025"
        )

        // Create adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Connect to spinner
        spinnerMonth.adapter = adapter

        // Set default to current month (January 2026)
        spinnerMonth.setSelection(0)

        // Listen for month changes
        spinnerMonth.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // When month changes, update the data
                // TODO: Load data for selected month from database
                loadSampleData()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                // Do nothing
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

    // ========================================
    // LOAD SAMPLE DATA (Matches Panel 2)
    // ========================================

    private fun loadSampleData() {
        // This data matches what's shown in Panel 2
        // TODO: Replace with real data from database

        // EXPIRED ITEMS (Only Hotdog)
        // 9 hotdogs × $5.99 each = $53.91
        val hotdogPrice = 5.99
        val hotdogQuantity = 9
        val wastedAmount = hotdogPrice * hotdogQuantity
        tvMoneyWasted.text = "$${"%.2f".format(wastedAmount)}"

        // ACTIVE ITEMS (Money saved by using before expiry)
        // Total value of items still fresh and being used
        val savedAmount = 42.48  // Example total from all active items
        tvMoneySaved.text = "${"%.2f".format(savedAmount)}"

        // TOTAL ITEMS
        // Milk (1) + Hotdog (9) + Bread (2) + Eggs (12) + Cheese (1) = 25 total
        // But we're showing 5 different items in Panel 2
        tvTotalItems.text = "5"

        // EXPIRED ITEMS COUNT
        // Only 1 item expired (Hotdog)
        tvExpiredItems.text = "1"
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