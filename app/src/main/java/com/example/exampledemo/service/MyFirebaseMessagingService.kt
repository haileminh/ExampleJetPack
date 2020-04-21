package com.example.exampledemo.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.exampledemo.R
import com.example.exampledemo.common.Constants.NOTIFY_PUSH_ID
import com.example.exampledemo.common.Constants.OREO_CHANNEL_ID
import com.example.exampledemo.common.Constants.OREO_CHANNEL_NAME
import com.example.exampledemo.ui.demo.fcm.sendNotification
import com.example.exampledemo.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO Step 3.5 check messages for data
        // Check if message contains a data payload.
        remoteMessage.data.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // TODO Step 3.6 check messages for notification and call sendNotification
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.body!!)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val msgId = remoteMessage.messageId ?: "0"
        val title = remoteMessage.notification?.title ?: getString(R.string.app_name)
        val body = remoteMessage.notification?.body ?: ""

        intent.putExtra(NOTIFY_PUSH_ID, msgId)

        val id = try {
            SimpleDateFormat("HHmmss").format(Calendar.getInstance().time).toInt()
        } catch (e: Exception) {
            0
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = PendingIntent.getActivity(
            this, id, intent, PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(
                    OREO_CHANNEL_ID, OREO_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
                )
                mChannel.enableLights(true)
                mChannel.enableVibration(true)
                mChannel.lightColor = Color.GREEN
                mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(mChannel)

                NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.nt))
                    .setChannelId(OREO_CHANNEL_ID)
            } else {
                NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.nt))
            }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            val color = ContextCompat.getColor(this, R.color.colorPrimary)
            notificationBuilder.color = color
        }
        notificationManager.notify(id, notificationBuilder.build())
    }

    override fun onNewToken(p0: String) {
        Log.d(TAG, "Refreshed token: $p0")

        sendRegistrationToServer(p0)
    }

    /**
     * Persist token to third-party servers.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(messageBody, applicationContext)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}