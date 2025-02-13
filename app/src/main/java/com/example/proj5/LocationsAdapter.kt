package com.example.proj5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationsAdapter(private val locationsList: List<String>) :
    RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locationsList[position])
    }

    override fun getItemCount(): Int {
        return locationsList.size
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationName: TextView = itemView.findViewById(R.id.txtLocationName)

        fun bind(location: String) {
            locationName.text = location
        }
    }
}
