package com.nicelydone.androidfundamentalfirstsubmission.ui.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.MainActivity

object NotificationHelper {
   private const val CHANNEL_ID = "daily_reminder_channel"

   fun showNotification(context: Context, eventName: String?, eventTime: String?) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
         val channel = NotificationChannel(
            CHANNEL_ID,
            "Daily Reminder Notifications",
            NotificationManager.IMPORTANCE_HIGH
         )
         notificationManager.createNotificationChannel(channel)
      }

      val intent = Intent(context, MainActivity::class.java).apply {
         flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

      val notification = NotificationCompat.Builder(context, CHANNEL_ID)
         .setSmallIcon(R.drawable.icon_upcoming_state)
         .setContentTitle(eventName)
         .setContentText("Upcoming event at $eventTime")
         .setPriority(NotificationCompat.PRIORITY_HIGH)
         .setContentIntent(pendingIntent)
         .setAutoCancel(true)
         .build()

      notificationManager.notify(0, notification)
   }
}