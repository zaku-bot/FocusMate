package com.example.proj5

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class EventAdapter : ListAdapter<EventModel, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        try {
            val event = getItem(position)
            holder.bindEvent(event)
        } catch (e: ClassCastException) {
            Log.e("EventAdapter", "ClassCastException: ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) {
            Log.e("EventAdapter", "Exception: ${e.message}")
            e.printStackTrace()
        }
    }


    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventTitle: TextView = itemView.findViewById(R.id.summaryTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bindEvent(event: EventModel) {
            eventTitle.text = event.summary
            dateTextView.text = event.startDate
        }
    }
}
