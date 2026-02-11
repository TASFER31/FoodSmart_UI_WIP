package com.example.foodsmart_ui_overview

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.LinearLayout
import android.widget.TextView

class Stats_cards : AppCompatActivity() {

    // Declare views
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var totalItemsCard: TextView
    private lateinit var expiringCard: TextView
    private lateinit var expiredCard: TextView
    private lateinit var itemAdapter: ItemAdapter          // ← NEW LINE
    private var itemsList = mutableListOf<FoodItem>()      // ← NEW LINE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats_cards)

        // Initialize views
        initializeViews()

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up RecyclerView
        setupRecyclerView()

        // Set up FAB click listener
        setupFabButton()

        // Set up bottom navigation
        setupBottomNavigation()

        // Update stats cards with sample data
        updateStatsCards()

        // Show empty state since we have no items yet
        showEmptyState()
    }

    private fun initializeViews() {
        fabAdd = findViewById(R.id.fab_add)
        bottomNav = findViewById(R.id.bottom_navigation)
        recyclerView = findViewById(R.id.items_recycler_view)
        emptyState = findViewById(R.id.empty_state)
        totalItemsCard = findViewById(R.id.total_items_card)
        expiringCard = findViewById(R.id.expiring_card)
        expiredCard = findViewById(R.id.expired_card)
    }
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        itemAdapter = ItemAdapter(
            items = itemsList,
            onItemClick = { item ->
                // When item clicked, edit it
                val intent = Intent(this, edit_item::class.java)
                startActivity(intent)
            },
            onDeleteClick = { item, position ->
                // When delete button clicked
                deleteItem(item, position)
            }
        )

        recyclerView.adapter = itemAdapter
    }

    private fun deleteItem(item: FoodItem, position: Int) {
        // Show confirmation dialog
        android.app.AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete ${item.name}?")
            .setPositiveButton("Delete") { _, _ ->
                // Remove from list
                itemAdapter.removeItem(position)

                // TODO: Delete from database when backend is ready
                // backend.deleteItem(item.id)

                // Update stats
                updateStatsCards()

                // Show empty state if no items left
                if (itemsList.isEmpty()) {
                    showEmptyState()
                }

                // Show success message
                android.widget.Toast.makeText(this, "${item.name} deleted", android.widget.Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupFabButton() {
        fabAdd.setOnClickListener {
            val intent = Intent(this, edit_item::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inventory -> {
                    // Already on inventory screen
                    true
                }
                R.id.nav_reports -> {
                    // Navigate to expense report
                    val intent = Intent(this, expense_report::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Set inventory as selected by default
        bottomNav.selectedItemId = R.id.nav_inventory
    }

    private fun updateStatsCards() {
        // TODO: Replace with real data from database
        // For now, showing 0 items
        totalItemsCard.text = "0"
        expiringCard.text = "0"
        expiredCard.text = "0"
    }

    private fun showEmptyState() {
        // Show empty state, hide RecyclerView
        emptyState.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showItems() {
        // Hide empty state, show RecyclerView
        emptyState.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}