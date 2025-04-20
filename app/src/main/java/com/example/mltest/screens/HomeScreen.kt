package com.example.mltest.alert

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mltest.MainActivity
import com.example.mltest.R

class CounterNotification(context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @SuppressLint("NotificationPermission")
    fun showNotification(counter: Int) {
        // 🔧 Ensure the notification channel is created
        createNotificationChannel()

        // 👉 Intent to open MainActivity and navigate to the Threat Form
        val formIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_form", true) // Pass extra data to MainActivity for navigation
        }

        // Creating a PendingIntent for when the notification is clicked
        val formPendingIntent = PendingIntent.getActivity(
            context,
            1,
            formIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 🔔 Notification building
        val notification = NotificationCompat.Builder(context, COUNTER_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Alert 🚨")
            .setContentText("Wow! You have discovered a new species 🤩.")
            .setContentIntent(formPendingIntent) // Navigates when the notification is clicked
            .addAction(
                R.drawable.baseline_notifications_24,
                "Go and connect to Researcher/NGOs",
                formPendingIntent // Reuse the same PendingIntent for the action button
            )
            .setAutoCancel(true) // Notification will be removed once clicked
            .build()

        // Notify with a unique ID (using counter as ID here)
        notificationManager.notify(counter, notification)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Species Discovery Alerts"
            val description = "Channel for alert notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(COUNTER_CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        // Unique identifier for the notification channel
        const val COUNTER_CHANNEL_ID = "counter_channel"
    }
}
