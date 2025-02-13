package com.example.proj5

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_NOTIFICATION_ACCESS = 1
    private val REQUEST_CALENDAR_ACCESS = 2

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter // You need to create this adapter
    private lateinit var googleCalendarClient: Calendar

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    //private var account: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("CalendarIntegration", "OnCreate first function, should work")
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter()
        recyclerView.adapter = eventAdapter

        val appUsageTrackingBtn: Button = findViewById(R.id.appUsageTrackingBtn)
        appUsageTrackingBtn.setOnClickListener {
            startActivity(Intent(this, AppTrackUsage::class.java))
        }

        val addLocationsScreen: Button = findViewById(R.id.addLocationsScreenBtn)

        addLocationsScreen.setOnClickListener {
            startActivity(Intent(this, AddLocationActivity::class.java))}

        // Run the coroutine on the Main dispatcher
        coroutineScope.launch(Dispatchers.Main) {
            // Check if the app has notification listener permission
            if (!isNotificationListenerEnabled()) {
                // If not, request the user to grant permission
                Log.d("CalendarIntegration", "No Notification Permission yet")
                requestNotificationListenerPermission()
            }

            // Wait for the notification permission result
            while (!isNotificationListenerEnabled()) {
                delay(1000) // Delay for 1 second before checking again
            }

            // Request Google Calendar permission after notification permission is granted
            if (!hasCalendarPermission()) {
                // If not, request the user to grant calendar permission
                Log.d("CalendarIntegration", "No calendar permission, will request one")
                requestCalendarPermission()

                // Wait for the calendar permission result
                while (!hasCalendarPermission()) {
                    delay(1000) // Delay for 1 second before checking again
                }
            }

            // Authenticate with Google Sign-In
            authenticateWithGoogleSignIn()

            // Button for fetching and displaying calendar events
            val calendarButton: Button = findViewById(R.id.calendarButton)
            val notificationsButton: Button = findViewById(R.id.notificationsButton)

            calendarButton.setOnClickListener {
                // Launch CalendarOptionsActivity when Calendar button is clicked
                startActivity(Intent(this@MainActivity, CalendarOptionsActivity::class.java))

            }

            notificationsButton.setOnClickListener {
                coroutineScope.launch(Dispatchers.Main) {
//                    // Now, calendar permission is granted
//                    // You can start your NotificationListenerService and fetch/display events
                    startService(Intent(this@MainActivity, CustomNotificationListener::class.java))
//                    Log.d("CalendarIntegration", "Have Calendar permission, fetching and displaying events")
                }
                // Handle click on Notifications button to navigate to NotificationsActivity
                val intent = Intent(this@MainActivity, NotificationsActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun authenticateWithGoogleSignIn() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            signIn()
        } else {
            authenticateWithCalendarAPI(account)
        }
    }

    private fun signIn() {
        val webClientId = getString(R.string.web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        Log.d("CalendarIntegration", "Trying to Google SignIn")
        try {
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            Log.d("CalendarIntegration", "Trying to Google SignIn2")
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: ApiException) {
            Log.e("CalendarIntegration", "Error with Google Play services API: ${e.statusCode}")
            // Handle the error based on the status code
        }
        //fetchAndDisplayCalendarEvents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            authenticateWithCalendarAPI(account!!)

            val email = account.email
            val displayName = account.displayName
            val uid = account.id
            val photoUrl = account.photoUrl

            // Now you can use email, displayName, uid, and photoUrl as needed

            // Now you can use email, displayName, uid, and photoUrl as needed
            Log.d("User info", "Email: $email")
            Log.d("User info", "Display Name: $displayName")
            Log.d("User info", "UID: $uid")

            if (photoUrl != null) {
                Log.d("User info", "Photo URL: $photoUrl")
            }
        } catch (e: ApiException) {
            Log.e("CalendarIntegration", "Google Sign-In failed: $e")
            // Handle sign-in failure
        }
    }

    private fun authenticateWithCalendarAPI(account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            applicationContext, setOf(CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR)
            //applicationContext, setOf( CalendarScopes.CALENDAR)
        )

        credential.selectedAccount = account.account

        Log.d("CalendarIntegration", "google calender in authenticateWithCalendarAPI")
        _googleCalendarClient = Calendar.Builder(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("com.example.proj5")
            .build()

    }



    private fun isNotificationListenerEnabled(): Boolean {
        val packageName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains("$packageName/${CustomNotificationListener::class.java.name}")
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun requestNotificationListenerPermission() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        } else {
            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        }
        startActivityForResult(intent, REQUEST_NOTIFICATION_ACCESS)
    }

    private fun hasCalendarPermission(): Boolean {
        // Check if the app has both read and write permissions for Google Calendar
        val readPermission = checkSelfPermission(android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        val writePermission = checkSelfPermission(android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
        Log.d("CalendarIntegration", "readPermission=$readPermission and writePermission=$writePermission")
        return readPermission && writePermission
    }

    private fun requestCalendarPermission() {
        // Request both read and write permissions for Google Calendar
        requestPermissions(arrayOf(android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR), REQUEST_CALENDAR_ACCESS)
        //requestPermissions(arrayOf(android.Manifest.permission.READ_CALENDAR),REQUEST_CALENDAR_ACCESS)
    }

    companion object {
        private const val RC_SIGN_IN = 113
        const val REQUEST_AUTHORIZATION = 126// Any integer constant
        private lateinit var _googleCalendarClient: Calendar

        val googleCalendarClient: Calendar
            get() = _googleCalendarClient
    }

}
