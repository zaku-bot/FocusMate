package com.example.proj5

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast

class NotificationsActivity : AppCompatActivity() {

    private lateinit var textViewTimerRemaining: TextView
    private lateinit var textViewBlockedApps: TextView
    private var timer: CountDownTimer? = null
    private var remainingTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        val editTextTimer = findViewById<EditText>(R.id.editTextTimer)
        val appNamesInput = findViewById<MultiAutoCompleteTextView>(R.id.appNamesInput)
        val buttonBlockNotifications = findViewById<Button>(R.id.buttonBlockNotifications)
        textViewTimerRemaining = findViewById(R.id.textViewTimerRemaining)
        textViewBlockedApps = findViewById(R.id.textViewBlockedApps)

        // Update TextViews with initial values
        updateUI()

        // Set up the app names autocomplete (you need to provide the app names)
        val appNames = arrayOf("WhatsApp", "Gmail", "Snapchat", "Instagram", "Messages", "YouTube", "Facebook", "Twitter", "Messenger", "Bumble",
            "Zoom", "TikTok", "CapCut", "Uber", "Amazon", "Spotify", "WeChat")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, appNames)
        appNamesInput.setAdapter(adapter)
        appNamesInput.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        buttonBlockNotifications.setOnClickListener {
            Log.d("NotificationListener", "Block Notification button clicked")
            val timerText = editTextTimer.text.toString()

            val timerDuration: Long = try {
                timerText.toLong()
            } catch (e: NumberFormatException) {
                // If not a valid Long, set a default value and show an error message
                Toast.makeText(this, "Timer should be an integer.", Toast.LENGTH_SHORT).show()
                0L // Set a default value, you may change it to another appropriate default
            }
            //val timerDuration = editTextTimer.text.toString().toLong()
            val selectedAppNames = appNamesInput.text.toString().split(",").map { it.trim() }

            // Save the user preferences (you can use SharedPreferences)
            saveNotificationSettings(timerDuration, selectedAppNames.joinToString(","))

            // Set the selected app names in CustomNotificationListener
            CustomNotificationListener.selectedAppNames = selectedAppNames

            // Save the current time when the timer is started
            val currentTime = System.currentTimeMillis()
            saveStartTime(currentTime)

            // Start the service
            startService(Intent(this, CustomNotificationListener::class.java))

            // Update TextViews after blocking notifications
            updateUI()
            // Check if there is an active timer, and start it
            val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            val savedTimerDuration = sharedPreferences.getLong("TimerDuration", 0)
            if (savedTimerDuration > 0) {
                startTimer(savedTimerDuration)
            }
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            // Navigate back to the main activity
            onBackPressed()
        }
    }

    private fun saveNotificationSettings(timerDuration: Long, appNames: String) {
        // Save these settings using SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save the timer duration
        editor.putLong("TimerDuration", timerDuration)

        // Save the selected app names
        editor.putString("SelectedAppNames", appNames)

        editor.apply()
    }

    private fun updateUI() {
        // Retrieve saved timer duration and blocked apps
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val timerDuration = sharedPreferences.getLong("TimerDuration", 0)

        // Calculate remaining time
        val currentTime = System.currentTimeMillis()
        val savedTime = sharedPreferences.getLong("SavedTime", 0)
        val elapsedTime = currentTime - savedTime
        val remainingTimeInMillis = timerDuration * 60 * 60 * 1000 - elapsedTime

        // Update TextViews
        if (remainingTimeInMillis > 0) {
            val secondsRemaining = remainingTimeInMillis / 1000
            val hours = secondsRemaining / 3600
            val minutes = (secondsRemaining % 3600) / 60
            val remainingTimeText = String.format("%02d:%02d", hours, minutes)
            textViewTimerRemaining.text = "Timer Remaining: $remainingTimeText"
        } else {
            // Timer has expired
            textViewTimerRemaining.text = "Timer Expired"
        }

        val blockedApps = sharedPreferences.getString("SelectedAppNames", "") ?: ""
        textViewBlockedApps.text = "Blocked Apps: $blockedApps"
    }

    private fun startTimer(timerDuration: Long) {
        timer?.cancel() // Cancel the previous timer if running

        timer = object : CountDownTimer(timerDuration * 60 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                val secondsRemaining = millisUntilFinished / 1000
                val hours = secondsRemaining / 3600
                val minutes = (secondsRemaining % 3600) / 60
                val remainingTimeText = String.format("%02d:%02d", hours, minutes)
                // Update the UI to display the remaining time
                updateRemainingTime(remainingTimeText)
            }

            override fun onFinish() {
                // Reset the UI when the timer finishes
                updateRemainingTime("00:00")
            }
        }.start()
    }

    private fun updateRemainingTime(remainingTimeText: String) {
        textViewTimerRemaining.text = "Timer Remaining: $remainingTimeText"
    }

    private fun saveStartTime(startTime: Long) {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("SavedTime", startTime)
        editor.apply()
    }
}
