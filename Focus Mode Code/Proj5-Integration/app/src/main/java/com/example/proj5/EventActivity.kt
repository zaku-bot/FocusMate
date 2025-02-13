// EventActivity.kt
package com.example.proj5

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EventActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        recyclerView = findViewById(R.id.eventRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter()
        recyclerView.adapter = eventAdapter

        // Retrieve the events from the intent
        val events = intent.getSerializableExtra("events") as List<EventModel>
        eventAdapter.submitList(events)

        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle back button click
        if (item.itemId == android.R.id.home) {
            finish() // Close the current activity
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
