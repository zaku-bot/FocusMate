package com.example.proj5

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

import android.Manifest
import com.google.android.gms.common.api.Status
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.example.proj5.repository.ChatRepository

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "GeofenceBroadcastReceiver"

class GeofencingBroadcastReceiver : BroadcastReceiver() {
    private val chatRepository =  ChatRepository()
    private fun notificationDialog(context: Context, message: String) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "mcproject4"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_MAX
            )
            // Configure the notification channel.
            notificationChannel.description = "Sample Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setTicker("Project4") //.setPriority(Notification.PRIORITY_MAX)
            .setContentTitle("Project 4 Geofence")
            .setContentText(message)
            .setContentInfo("Information")
        notificationManager.notify(1, notificationBuilder.build())
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Intents action is ${intent.action}")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition
        Log.d(TAG, geofenceTransition.toString())
        Log.d(TAG, "Geofence event is ${geofencingEvent}")
//        Toast.makeText(context, "Toast message from broadcast ${geofenceTransition.toString()}", Toast.LENGTH_SHORT).show()
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER  || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            var  message = ""

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)  {
                var msg = "You have entered the geofence"
                val placesClient = Places.createClient(context) // Initialize Places API client

                val geofenceRequestId = geofencingEvent?.triggeringGeofences?.get(0)?.requestId
                // Here, geofenceRequestId should contain your placeId (or a unique identifier)

                val placeFields = listOf(
                    Place.Field.NAME,
                    Place.Field.TYPES
                )

                val placeRequest = FetchPlaceRequest.newInstance(geofenceRequestId!!, placeFields)

                placesClient.fetchPlace(placeRequest)
                    .addOnSuccessListener { response: FetchPlaceResponse ->
                        val place = response.place
                        val placeName = place.name // Retrieve the place name
                        val placeTypes = place.placeTypes // Retrieve place types
                        var newList: List<String> = listOf("default")
                        Log.d(TAG, "Place types is $placeTypes and Place Name is $placeName")
                        if (place.placeTypes != null) {
                            place.placeTypes?.let {
                                newList = if (it.size >= 2) {
                                    it.dropLast(2)
                                } else {
                                    listOf("default")
                                }
                                Log.d(TAG, newList.toString())
                            }
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            val responseMsg =
                                chatRepository.createChatCompletion("Give me the message for the location: $placeName, category list is ${newList.toString()}", context.getString(R.string.open_ai_api_key))
                            msg = responseMsg ?:  message
                            Log.d(TAG, "Message success is $msg")
                            message = msg

                            withContext(Dispatchers.Main) {
                                notificationDialog(context, msg)
                            }
                        }
                    }
                    .addOnFailureListener { exception: Exception ->
                        Log.e(TAG, "Place not found: ${exception.message}")
                    }

            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                message = "You have been in the geofence for more than 3 seconds"
//                    notificationDialog(context, message)
            } else {
                message = "You have exited the geofence"
//                    notificationDialog(context, message)
            }

            // Creating and sending Notification

        } else {
            Log.e(TAG, "Invalid type transition $geofenceTransition")
        }
    }
}