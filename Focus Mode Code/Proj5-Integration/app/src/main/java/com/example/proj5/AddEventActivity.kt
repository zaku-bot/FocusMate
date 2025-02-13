package com.example.proj5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class AddEventActivity : AppCompatActivity() {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_options)

        // Retrieve summary and startDate from extras
        val summary = intent.getStringExtra("summary")
        val startDate = intent.getStringExtra("startDate")

        if (!summary.isNullOrEmpty() && !startDate.isNullOrEmpty()) {
            addEvent(summary, startDate)
        } else {
            Log.e("CalendarIntegration", "Summary or startDate is null or empty")
            // Handle the case where summary or startDate is null or empty
            // You may want to finish the activity or show an error message
            finish()
        }
    }

    private fun addEvent(summary: String, startDate: String) {
        // Call the function to add the event to the calendar
        coroutineScope.launch(Dispatchers.IO) {
            try {
                Log.d("CalendarIntegration", "addEventToCalendar is called")
                val success = addEventToCalendar(MainActivity.googleCalendarClient, summary, startDate)
                // You can handle the result as needed
                if (success) {
                    // Event added successfully
                    runOnUiThread {
                        // Update UI or show a message
                        // For example, you can finish the activity or show a success message
                        finish()
                    }
                } else {
                    // Handle the case where the event was not added successfully
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exceptions here
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun addEventToCalendar(
        googleCalendarClient: Calendar,
        summary: String,
        startDate: String
    ): Boolean {
        return try {
            val event = Event().apply {
                this.summary = summary
                start = EventDateTime().setDateTime(DateTime(startDate))
                end = EventDateTime().setDateTime(DateTime(startDate))
            }

            googleCalendarClient.events().insert("primary", event).execute()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
