package com.example.proj5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarOptionsActivity : AppCompatActivity() {

    private lateinit var editTextEventSummary: EditText
    private lateinit var editTextEventStartDate: EditText

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_options)

        val buttonRecentEvents = findViewById<Button>(R.id.buttonRecentEvents)
        val buttonAddEvent = findViewById<Button>(R.id.buttonAddEvent)
        editTextEventSummary = findViewById(R.id.editTextEventSummary)
        editTextEventStartDate = findViewById(R.id.editTextEventStartDate)

        Log.d("CalendarIntegration", "Inside Calendar Options")
        buttonRecentEvents.setOnClickListener {
            // Launch RecentEventsActivity when Recent Events button is clicked
            Log.d("CalendarIntegration", "Will trigger Recent Evnets")
            startActivity(Intent(this@CalendarOptionsActivity, RecentEventsActivity::class.java))
        }

        buttonAddEvent.setOnClickListener {
            // Read the values from EditText
            Log.d("CalendarIntegration", "button add")
            val summary = editTextEventSummary.text.toString()
            val startDate = editTextEventStartDate.text.toString()

            Log.d("CalendarIntegration", "button add events clicked, starting AddEvents")
            addEvent(summary,startDate)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun addEvent(summary: String, startDate: String) {
        // Call the function to add the event to the calendar
        Log.d("CalendarIntegration", "Inside AddEvents, lets start coroutine")
        coroutineScope.launch(Dispatchers.IO) {
            try {
                Log.d("CalendarIntegration", "addEventToCalendar is called")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                var validStartDate = false
                val textViewResult = findViewById<TextView>(R.id.textViewResult)

                try {
                    dateFormat.parse(startDate)
                    validStartDate = true
                } catch (e: ParseException) {
                    //Log.e("CalendarIntegration", "Invalid startDate format: $startDate")
                    validStartDate = false
                    textViewResult.text = "Invalid startDate format please enter a valid one"
                }

                // Check if summary is empty
                val validSummary = summary.isNotEmpty()
                if (validStartDate && validSummary) {
                    val success =
                        addEventToCalendar(MainActivity.googleCalendarClient, summary, startDate)
                    // You can handle the result as needed
                    if (success) {
                        // Event added successfully
                        //runOnUiThread {
                            // Update UI or show a message
                            // For example, you can finish the activity or show a success message
                            textViewResult.text = "Successfully Added Event"
                            //finish()
                        }
                    } else {
                        textViewResult.text = "Invalid input(s)"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle exceptions here
                }

            }
        }
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


