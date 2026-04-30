package com.emobilis.app

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Handles incoming Firebase Cloud Messaging push notifications.
 * Extend this class to show custom notifications when the app is in the background.
 */
class EmobilisMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle incoming FCM messages here
        // remoteMessage.notification?.title
        // remoteMessage.notification?.body
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send the new token to your server if needed
    }
}
