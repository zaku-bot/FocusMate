package com.example.proj5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RecentEventsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val job = Job()
    private lateinit var eventAdapter: EventAdapter // You need to create this adapter
    private lateinit var googleCalendarClient: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        val recyclerView: RecyclerView = findViewById(R.id.eventRecyclerView) // Replace with your RecyclerView ID
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter()
        recyclerView.adapter = eventAdapter

        fetchRecentEvents()
    }

    private fun fetchRecentEvents() {
        launch {
            try {
                Log.d("CalendarIntegration", "Just before calling fetchEventsFromApi")
                val events = fetchEventsFromApi(10)

                withContext(Dispatchers.Main) {
                    eventAdapter.submitList(events)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private suspend fun fetchEventsFromApi(maxEvents: Int): List<EventModel> = withContext(Dispatchers.IO) {
        try {
            val events: Events = MainActivity.googleCalendarClient.events().list("primary").execute()
            val eventList: List<Event> = events.items ?: emptyList()

            val sortedEvents = eventList.sortedByDescending {
                val startDateTime = it.start?.dateTime
                val startDate = it.start?.date

                when {
                    startDateTime != null -> startDateTime.value
                    startDate != null -> startDate.value
                    else -> 0L
                }
            }

            Log.d("CalendarIntegration", "Inside fetchEventsFromApi")
            val mappedEvents = sortedEvents.map { event ->
                EventModel(
                    event.id,
                    event.summary.orEmpty(),
                    event.start?.dateTime?.toString() ?: event.start?.date?.toString().orEmpty()
                )
            }

            mappedEvents
        } catch (e: UserRecoverableAuthException) {
            Log.e("CalendarIntegration", "Google Calendar API authentication error: $e")
            // Handle the UserRecoverableAuthException here
            Log.d("CalendarIntegration", "Started recovery activity")
            startActivityForResult(e.intent, MainActivity.REQUEST_AUTHORIZATION)

            // Return an empty list in case of an exception
            emptyList()
        } catch (e: UserRecoverableAuthIOException) {
            // Handle UserRecoverableAuthIOException here if needed
            Log.d("CalendarIntegration", "Started recovery activity2")
            startActivityForResult(e.intent, MainActivity.REQUEST_AUTHORIZATION)

            // Return an empty list in case of an exception
            emptyList()
        } catch (e: Exception) {
            Log.e("CalendarIntegration", "Error fetching and processing events: $e")
            // Handle other exceptions if needed.
            e.printStackTrace()
            emptyList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
