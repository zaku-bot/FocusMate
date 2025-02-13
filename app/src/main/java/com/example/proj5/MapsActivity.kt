package com.example.proj5

import android.Manifest
import android.annotation.SuppressLint
import com.google.android.gms.common.api.Status
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

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
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

private const val TAG = "MapsActivity"
private lateinit var geoClient: GeofencingClient
private  val REQUEST_TURN_DEVICE_LOCATION_ON =20
private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 4
private val REQUEST_BACKGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 5
private val REQUEST_LOCATION_PERMISSION = 10

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val geofenceList =ArrayList<Geofence>()
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var geofenceLatitude: Double = 0.0
    private var geofenceLongitude: Double = 0.0
    private var geofenceRadius: Float = 0f

    private val gadgetQ = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    private val geofenceIntent: PendingIntent by lazy {
        val intent = Intent("com.example.geofence.ACTION_RECEIVE_GEOFENCE")
        intent.setPackage(this.packageName)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        geoClient = LocationServices.getGeofencingClient(this)
        // Initialize Places
        if(!Places.isInitialized()){
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        placesClient = Places.createClient(this)

        // Set up AutocompleteSupportFragment
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment?

        autocompleteFragment?.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES))
        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.d("Place selected", "Yes place selected")
                // Get the selected place's information
                val placeLatLng = place.latLng
                val placeName = place.name
                if (placeLatLng != null) {
                    if(place.placeTypes != null)
                        place.placeTypes?.let { Log.d(TAG, it.toString()) }

                    // Extract latitude and longitude
                    val latitude = placeLatLng.latitude
                    val longitude = placeLatLng.longitude
                    val selectedLatLng = LatLng(latitude, longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f))
                    map.addMarker(MarkerOptions().position(selectedLatLng).title(place.name))
                    // Show confirmation dialog
                    val alertDialog = AlertDialog.Builder(this@MapsActivity)
                    alertDialog.setTitle("Add Geofence")
                    alertDialog.setMessage("Do you want to add a geofence at this location?")
                    alertDialog.setPositiveButton("Yes") { _, _ ->
                        // If user confirms, proceed to create the geofence
                        val radius = 100f // Set your desired radius
                        // Assign values to class-level variables
                        geofenceLatitude = latitude
                        geofenceLongitude = longitude
                        geofenceRadius = radius
                        createGeofence(place.name, latitude, longitude, radius, place.id)
                        //val returnIntent = Intent()
//                        returnIntent.putExtra("SELECTED_LOCATION", placeName)
//                        setResult(Activity.RESULT_OK, returnIntent)
//                        finish()
                    }
                    alertDialog.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()

                }
            }
            override fun onError(status: Status) {
                // Handle error
                Log.e(TAG, "Error: ${status.statusMessage}")
            }
        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)


    }
    private fun drawGeofencePerimeter(latitude: Double, longitude: Double, radius: Float) {
        val latLng = LatLng(latitude, longitude)
        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(radius.toDouble())
            .strokeColor(Color.RED) // Set color for the perimeter
            .fillColor(Color.argb(70, 150, 50, 50)) // Set color for the filled area within the perimeter
        map.addCircle(circleOptions)
    }

    // Function to create a geofence
    private fun createGeofence(placeName: String?, latitude: Double, longitude: Double, radius: Float, placeId: String?) {
        // Create a geofence and add it to the list

        Log.d(TAG, "Place id is $placeId")
        geofenceList.add(Geofence.Builder()
            .setRequestId(placeId ?: "Geofence")
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setLoiteringDelay(20000)
            .build())

        addGeofence()
    }
    private fun addGeofence(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geoClient?.addGeofences(seekGeofencing(), geofenceIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(this@MapsActivity, "Added Geofence", Toast.LENGTH_SHORT).show()
                drawGeofencePerimeter(geofenceLatitude, geofenceLongitude, geofenceRadius)
            }
            addOnFailureListener {
                Toast.makeText(this@MapsActivity, "Failed to add geofences", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startLocation()
        showCurrentLocation()
    }

    private fun showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latlng = LatLng(location.latitude, location.longitude)

                    val circleOptions = CircleOptions()
                        .center(latlng)
                        .radius(20.0)
                        .fillColor(0x40ff0000)
                        .strokeColor(Color.BLUE)
                        .strokeWidth(2f)

                    val zoomLevel = 18f

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel))
                    map.addMarker(MarkerOptions().position(latlng))
                    map.addCircle(circleOptions)
                }
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }

    private fun startLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = true

        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    //specify the geofence to monitor and the initial trigger
    private fun seekGeofencing(): GeofencingRequest {
        Log.d("SEEK GEOFENCING", geofenceList.toString())
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER  or GeofencingRequest.INITIAL_TRIGGER_DWELL )
            addGeofences(geofenceList)
        }.build()
    }

    //removing a geofence
    private fun removeGeofence(){
        geoClient?.removeGeofences(geofenceIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(this@MapsActivity, "Geofences removed", Toast.LENGTH_SHORT).show()

            }
            addOnFailureListener {
                Toast.makeText(this@MapsActivity, "Failed to remove geofences", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun examinePermisionAndinitiatGeofence() {
        if (authorizedLocation()) {
            validateGadgetAreaInitiateGeofence()
        } else {
            askLocationPermission()
        }
    }

    // check if background and foreground permissions are approved
    @TargetApi(29)
    private fun authorizedLocation(): Boolean {

        val formalizeForeground = (
                PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ))
        val formalizeBackground =
            if (gadgetQ) {
                PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                true
            }
        return formalizeForeground && formalizeBackground
    }
    private fun askForegroundLocationPermission() {
        Log.d(TAG, "Ask Foreground Location Permission")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    private fun askBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Display a rationale for needing background location permission
                    showBackgroundLocationRationaleDialog()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        REQUEST_BACKGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                    // Guide the user to enable location in app settings
                    showBackgroundLocationSettingsDialog()
                }
            }
        } else {
            // Handle older versions where background location is requested directly
        }
    }

    private fun showBackgroundLocationRationaleDialog() {
        // Show a dialog explaining the need for background location access
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Background Location Access")
            .setMessage("This app requires background location access to provide certain features. Please enable 'Allow all the time' location access.")
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    REQUEST_BACKGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun showBackgroundLocationSettingsDialog() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    private fun askLocationPermission() {
        askBackgroundLocationPermission()
    }

    private fun validateGadgetAreaInitiateGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val locationResponses =
            client.checkLocationSettings(builder.build())

        locationResponses.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        this,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Toast.makeText(this, "Enable your location", Toast.LENGTH_SHORT).show()
            }
        }
        locationResponses.addOnCompleteListener {
            if (it.isSuccessful) {
                //Toast.makeText(this, "Location is enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                startLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        validateGadgetAreaInitiateGeofence(false)
    }

    override fun onStart() {
        super.onStart()
        examinePermisionAndinitiatGeofence()
    }

}