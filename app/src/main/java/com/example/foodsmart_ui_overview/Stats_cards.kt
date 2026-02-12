package com.example.foodsmart_ui_overview

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    private var itemsList = ItemsStore.items      // ← NEW LINE
    private val addItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val item = (result.data?.getSerializableExtra("new_item") as? FoodItem)
                ?: (result.data?.getSerializableExtra("updated_item") as? FoodItem)
            if (item != null) {
                ItemsStore.addItem(item)
                itemAdapter.updateItems(ItemsStore.items)
                updateStatsCards()
                showItems()
            } else {
                android.widget.Toast.makeText(this, "No item returned", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val editItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val updated = result.data?.getSerializableExtra("updated_item") as? FoodItem
            if (updated != null) {
                ItemsStore.updateItem(updated)
                itemAdapter.updateItems(ItemsStore.items)
                updateStatsCards()
                showItems()
            }
        }
    }

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

        // Set up search
        setupSearch()

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
            items = ItemsStore.items,
            onItemClick = { item ->
                // Edit existing item
                val intent = Intent(this, edit_item::class.java)
                intent.putExtra("mode", "edit")
                intent.putExtra("item", item as java.io.Serializable)
                editItemLauncher.launch(intent)
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
                ItemsStore.deleteItem(item)

                // TODO: Delete from database when backend is ready
                // backend.deleteItem(item.id)

                // Update stats
                updateStatsCards()

                // Show empty state if no items left
                if (ItemsStore.items.isEmpty()) {
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
            addItemLauncher.launch(intent)
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
        val totalDistinct = ItemsStore.items.size
        val expiringSoon = ItemsStore.items.count { it.getExpiryStatus() == "Expiring Soon" }
        val expired = ItemsStore.items.count { it.getExpiryStatus() == "Expired" }
        totalItemsCard.text = totalDistinct.toString()
        expiringCard.text = expiringSoon.toString()
        expiredCard.text = expired.toString()
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

    override fun onResume() {
        super.onResume()
        ItemsStore.ensureExpiryEvents()
        if (ItemsStore.items.isEmpty()) {
            showEmptyState()
        } else {
            showItems()
        }
        updateStatsCards()
    }

    private fun setupSearch() {
        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performFilter(query)
                recyclerView.scrollToPosition(0)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                performFilter(newText)
                return true
            }
        })
    }

    private fun performFilter(query: String?) {
        val q = query?.trim() ?: ""
        if (q.isEmpty()) {
            itemAdapter.updateItems(ItemsStore.items)
            if (ItemsStore.items.isEmpty()) {
                showEmptyState()
            } else {
                showItems()
                recyclerView.scrollToPosition(0)
            }
            return
        }
        val lc = q.lowercase(Locale.getDefault())
        fun score(item: FoodItem): Int {
            val name = item.name.lowercase(Locale.getDefault())
            val cat = item.category.lowercase(Locale.getDefault())
            return when {
                name == lc -> 4
                name.startsWith(lc) -> 3
                cat.startsWith(lc) -> 2
                name.contains(lc) -> 1
                cat.contains(lc) -> 1
                else -> 0
            }
        }
        val filtered = ItemsStore.items
            .map { it to score(it) }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
        itemAdapter.updateItems(filtered)
        if (filtered.isEmpty()) {
            showEmptyState()
        } else {
            showItems()
            recyclerView.scrollToPosition(0)
        }
    }
}
