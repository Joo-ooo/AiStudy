package com.example.aistudy.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.aistudy.MainActivity
import com.example.aistudy.R

    /**
     * NotificationService is a utility object designed to facilitate the creation of notification channels and
     * the posting of notifications within an application. It supports Android O and above by ensuring the
     * proper creation and management of notification channels, which are required for notifications to be
     * displayed to the user. Additionally, it incorporates a permission check for posting notifications
     * that is necessary for applications targeting Android T (API level 33) and above.
     *
     * Features:
     * - Creates two distinct notification channels for reminders and transcripts, allowing for categorized notifications.
     * - Provides a method to check for notification posting permissions, accommodating newer Android versions' requirements.
     * - Offers a flexible method to post notifications, allowing the choice of channel based on the notification context.
     * - Supports deep linking through the use of a PendingIntent, enabling users to navigate to specific parts of the application directly from the notification.
     *
     * Usage:
     * - Call `createNotificationChannel(context)` upon application start to ensure the necessary notification channels are registered.
     * - Use `showNotification(context, title, message, useTranscriptChannel)` to display notifications, specifying whether to use the transcript channel based on the notification content.
     */
object NotificationService {
    // Define IDs for each notification channel
    private const val reminderChannelId = "notes_reminder_channel_id"
    private const val transcriptChannelId = "transcript_notification_channel_id"
    private const val notificationId =
        1 // You might want to manage notification IDs more dynamically

    // Method to create both notification channels
    fun createNotificationChannel(context: Context) {
        // Create the Reminder Notification Channel
        val reminderChannel = NotificationChannel(
            reminderChannelId,
            "Notes Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notes reminder description"
        }

        // Create the Transcript Notification Channel
        val transcriptChannel = NotificationChannel(
            transcriptChannelId,
            "Transcript Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for Transcript Creation Notifications"
        }

        // Register the channels with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(reminderChannel)
        notificationManager.createNotificationChannel(transcriptChannel)
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // Method to show a notification
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        useTranscriptChannel: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission(context)) {
            // Display a toast message to the user indicating the need for notification permission
            Toast.makeText(context, "Permission to post notifications is required. Please enable it in the app settings.", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.img)
        val channelId = if (useTranscriptChannel) transcriptChannelId else reminderChannelId

        try {
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Log the exception
            Log.e("NotificationService", "Failed to post notification due to security restrictions", e)
        }
    }

}