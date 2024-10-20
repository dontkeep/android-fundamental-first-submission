package com.nicelydone.androidfundamentalfirstsubmission.ui.helper

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.Event
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.ListEventsItem
import com.nicelydone.androidfundamentalfirstsubmission.connection.retrofit.ApiConfig
import kotlinx.coroutines.runBlocking
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.Locale

class DailyReminderWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
   override fun doWork(): Result {
      val event = runBlocking { fetchUpcomingEvent() }
      event?.let {
         val formattedTime = formatBeginTime(it.beginTime)
         NotificationHelper.showNotification(applicationContext, it.name, formattedTime)
      }
      return Result.success()
   }

   private suspend fun fetchUpcomingEvent(): ListEventsItem? {
      return try {
         val response = ApiConfig.getApiService().getEvents(active = -1, limit = 1).awaitResponse()
         if (response.isSuccessful) {
            response.body()?.listEvents?.firstOrNull()
         } else {
            null
         }
      } catch (e: Exception) {
         null
      }
   }

   private fun formatBeginTime(beginTime: String?): String {
      val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
      val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.getDefault())

      return try {
         val date = beginTime?.let { inputFormat.parse(it) }
         outputFormat.format(date ?: return "Unknown date")
      } catch (e: Exception) {
         "Invalid date format"
      }
   }
}