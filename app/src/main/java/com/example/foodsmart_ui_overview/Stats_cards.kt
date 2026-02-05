package com.example.foodsmart_ui_overview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class Stats_cards : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stats_cards)

        val getStartedButton: Button = findViewById(R.id.fab_add)
        getStartedButton.setOnClickListener {
            val intent = Intent(this, edit_item::class.java)
            startActivity(intent)
        }

        val historyButton: Button = findViewById(R.id.btn_history)
        historyButton.setOnClickListener {
            val intent = Intent(this, expense_report::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}