package com.example.proj5

import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

// Inside your CustomNotificationListener
class CustomNotificationListener : NotificationListenerService() {

    companion object {
        val appNameMap: Map<String, String> = mapOf(
            "com.whatsapp" to "WhatsApp",
            "com.google.android.gm" to "Gmail",
            "com.snapchat.android" to "Snapchat",
            "com.instagram.android" to "Instagram",
            "com.google.android.apps.messaging" to "Messages",
            "com.google.android.youtube" to "YouTube",
            "com.facebook.katana" to "Facebook",
            "com.twitter.android" to "Twitter",
            "com.facebook.orca" to "Messenger",
            "org.telegram.messenger" to "Telegram",
            "com.tinder" to "Tinder",
            "com.bumble.app" to "Bumble",
            "us.zoom.videomeetings" to "Zoom",
            "com.zhiliaoapp.musically" to "TikTok",
            "com.lemon.lvoverseas" to "CapCut",
            "com.ubercab" to "Uber",
            "com.amazon.mShop.android.shopping" to "Amazon",
            "com.spotify.music" to "Spotify",
            "com.tencent.mm" to "WeChat",

            )

        var selectedAppNames: List<String> = emptyList()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d("NotificationListener", "Received notification from package = ${sbn.packageName}")
        Log.d("NotificationListener", "Received notification $selectedAppNames")

        val appName = getAppName(sbn.packageName)
        Log.d("NotificationListener", "Received notification $appName")
        if (appName in selectedAppNames) {
            // Cancel the notification
            Log.d("NotificationListener", "Cancelled the notification from package = $appName")
            cancelNotification(sbn.key)
        }

    }

    private fun getAppName(userEnteredAppName: String): String {
        // Use the map to get the corresponding package name for the user-entered app name
        return appNameMap[userEnteredAppName] ?: userEnteredAppName
    }


}
