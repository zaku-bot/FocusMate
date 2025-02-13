package com.example.proj5

import android.content.Intent
import com.google.android.gms.common.api.Status
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.datatransport.runtime.backends.BackendResponse
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddLocationActivity : AppCompatActivity() {

    private val addedLocationsList = mutableListOf<String>()
    private val REQUEST_ADD_LOCATION = 1001 // Or any unique integer value
    private lateinit var locationsAdapter: LocationsAdapter
    // Button click to add a new location
    private fun onAddLocationClicked() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_LOCATION)
    }

    // Handle the result from MapsActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_LOCATION && resultCode == RESULT_OK) {
            // Retrieve the selected location data from MapsActivity
            val selectedLocation = data?.getStringExtra("SELECTED_LOCATION")

            // Update your list of added locations
            selectedLocation?.let {
                addedLocationsList.add(it)
                locationsAdapter.notifyDataSetChanged()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_locations)


        locationsAdapter = LocationsAdapter(addedLocationsList)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewLocations)
        recyclerView.adapter = locationsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addLocationBtn: Button = findViewById(R.id.btnAddLocation)

        addLocationBtn.setOnClickListener {
            onAddLocationClicked()
        }
    }


}