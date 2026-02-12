package com.example.foodsmart_ui_overview

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class edit_item : AppCompatActivity() {

    // ========================================
    // DECLARE ALL VARIABLES AT THE TOP
    // ========================================

    // Buttons
    private lateinit var btnBack: ImageButton
    private lateinit var btnEditExpiry: ImageButton
    private lateinit var btnEditReminder: ImageButton
    private lateinit var btnIncrementQuantity: ImageButton
    private lateinit var btnSave: Button

    // Text fields
    private lateinit var editItemName: EditText
    private lateinit var editPrice: EditText
    private lateinit var editAmount: EditText
    private lateinit var editStorage: EditText

    // Display texts
    private lateinit var tvExpiryDate: TextView
    private lateinit var tvReminder: TextView
    private lateinit var tvQuantity: TextView

    // Dropdowns
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerInventory: Spinner

    // Data
    private var quantity = 1

    // ========================================
    // MAIN FUNCTION - RUNS WHEN SCREEN OPENS
    // ========================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_item)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Connect XML elements to Kotlin variables
        initializeViews()

        // Set up both dropdowns
        setupCategorySpinner()
        setupInventorySpinner()

        // Make buttons work when clicked
        setupClickListeners()
    }

    // ========================================
    // CONNECT XML TO KOTLIN
    // ========================================

    private fun initializeViews() {
        // Find all buttons
        btnBack = findViewById(R.id.btn_back)
        btnEditExpiry = findViewById(R.id.btn_edit_expiry)
        btnEditReminder = findViewById(R.id.btn_edit_reminder)
        btnIncrementQuantity = findViewById(R.id.btn_increment_quantity)
        btnSave = findViewById(R.id.btn_save)

        // Find all text input fields
        editItemName = findViewById(R.id.edit_item_name)
        editPrice = findViewById(R.id.edit_price)
        editAmount = findViewById(R.id.edit_amount)
        editStorage = findViewById(R.id.edit_storage)

        // Find all display texts
        tvExpiryDate = findViewById(R.id.tv_expiry_date)
        tvReminder = findViewById(R.id.tv_reminder)
        tvQuantity = findViewById(R.id.tv_quantity)

        // Find dropdowns
        spinnerCategory = findViewById(R.id.spinner_category)
        spinnerInventory = findViewById(R.id.spinner_inventory)
    }

    // ========================================
    // SET UP CATEGORY DROPDOWN
    // ========================================

    private fun setupCategorySpinner() {
        // Create list of food categories
        val categories = arrayOf(
            "Select Category",
            "Meat",
            "Dairy",
            "Frozen Goods",
            "Bakery",
            "Vegetables",
            "Fruits",
            "Beverages",
            "Other"
        )

        // Create adapter (connects data to spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Connect adapter to spinner
        spinnerCategory.adapter = adapter

        // Set default selection
        spinnerCategory.setSelection(0)
    }

    // ========================================
    // SET UP INVENTORY DROPDOWN
    // ========================================

    private fun setupInventorySpinner() {
        // Create list of inventory locations
        val inventoryLocations = arrayOf(
            "Select Inventory",
            "Refrigerator",
            "Freezer",
            "Pantry",
            "Cabinet",
            "Countertop",
            "Other"
        )

        // Create adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, inventoryLocations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Connect adapter to spinner
        spinnerInventory.adapter = adapter

        // Set default selection (Refrigerator)
        spinnerInventory.setSelection(1)
    }

    // ========================================
    // MAKE BUTTONS WORK WHEN CLICKED
    // ========================================

    private fun setupClickListeners() {

        // BACK BUTTON - Close screen and go back
        btnBack.setOnClickListener {
            finish()
        }

        // EXPIRY DATE EDIT BUTTON - Open calendar
        btnEditExpiry.setOnClickListener {
            showDatePicker { year, month, day ->
                // Update the expiry date text
                tvExpiryDate.text = "Expires on $day ${getMonthName(month)}, $year"
            }
        }

        // REMINDER EDIT BUTTON - Open calendar
        btnEditReminder.setOnClickListener {
            showDatePicker { year, month, day ->
                // Update the reminder text
                tvReminder.text = "$day ${getMonthName(month)}, $year"
            }
        }

        // QUANTITY + BUTTON - Increase number
        btnIncrementQuantity.setOnClickListener {
            quantity++  // Add 1 to quantity
            tvQuantity.text = quantity.toString()  // Update display
        }

        // SAVE BUTTON - Save and go back to inventory
        btnSave.setOnClickListener {
            // Get all the data from fields
            val itemName = editItemName.text.toString()
            val price = editPrice.text.toString()
            val amount = editAmount.text.toString()
            val storage = editStorage.text.toString()
            val expiryDate = tvExpiryDate.text.toString()
            val reminder = tvReminder.text.toString()
            val inventory = spinnerInventory.selectedItem.toString()
            val category = spinnerCategory.selectedItem.toString()

            // TODO: Save this data to database/storage
            // For now, just go back to Stats_cards

            val intent = Intent(this, Stats_cards::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    // ========================================
    // SHOW CALENDAR PICKER
    // ========================================

    private fun showDatePicker(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
        // Get today's date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create and show the calendar dialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // When user picks a date, call the function we passed in
                onDateSelected(selectedYear, selectedMonth, selectedDay)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()  // Display the calendar
    }

    // ========================================
    // CONVERT MONTH NUMBER TO NAME
    // ========================================

    private fun getMonthName(month: Int): String {
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return months[month]
    }
}