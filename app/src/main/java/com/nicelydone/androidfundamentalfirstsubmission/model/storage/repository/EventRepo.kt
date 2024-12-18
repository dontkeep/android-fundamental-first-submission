package com.nicelydone.androidfundamentalfirstsubmission.model.storage.repository

import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import com.nicelydone.androidfundamentalfirstsubmission.model.connection.response.DetailEventResponse
import com.nicelydone.androidfundamentalfirstsubmission.model.connection.response.EventResponse
import com.nicelydone.androidfundamentalfirstsubmission.model.connection.retrofit.ApiService
import com.nicelydone.androidfundamentalfirstsubmission.model.storage.entity.FavEventEntity
import com.nicelydone.androidfundamentalfirstsubmission.model.storage.room.dao.EventDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class EventRepo @Inject constructor(
   private val eventDao: EventDao,
   private val eventApi: ApiService,
   private val application: Application
) : EventInterfaceRepo {
   private val coroutineScope = CoroutineScope(Dispatchers.IO)
   override suspend fun insert(favEvent: FavEventEntity) {
      coroutineScope.launch {
         eventDao.insert(favEvent)
      }
   }

   fun saveBitmapToFile(bitmap: Bitmap, fileName: String): String? {
      val directory = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
      if (!directory!!.exists()) {
         directory.mkdirs()
      }

      val file = File(directory, "$fileName.png")
      var outputStream: FileOutputStream? = null
      return try {
         outputStream = FileOutputStream(file)
         bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
         outputStream.flush()
         file.absolutePath
      } catch (e: IOException) {
         e.printStackTrace()
         null
      } finally {
         outputStream?.close()
      }
   }

   override suspend fun deleteById(eventId: Int) {
      eventDao.deleteById(eventId)
   }

   fun isEventFavorite(eventId: Int): Flow<Boolean> {
      return eventDao.isEventFavorite(eventId)
         .flowOn(Dispatchers.IO)
   }

   fun getEventDetails(id: Int, isFavorited: Boolean): Flow<Any> {
      return if (isFavorited) {
         eventDao.getEventById(id)
            .map { it }
            .flowOn(Dispatchers.IO)
      } else {
         flow {
            try {
               val response = eventApi.getEventById(id.toString()).awaitResponse()
               if (response.isSuccessful) {
                  emit(response.body()!!)
               } else {
                  throw Exception("API call failed with code ${response.code()}")
               }
            } catch (e: Exception) {
               emit(DetailEventResponse(error = true, message = e.message))
            }
         }.flowOn(Dispatchers.IO)
      }
   }

   fun searchEventsFromApi(query: String): Flow<EventResponse> {
      return flow {
         val response = eventApi.searchEvents(query).awaitResponse()
         if (response.isSuccessful) {
            emit(response.body()!!)
         } else {
            throw Exception("API call failed with code ${response.code()}")
         }
      }.flowOn(Dispatchers.IO)
   }

   override fun getAll(): Flow<List<FavEventEntity>> {
      return eventDao.getAllEvents().flowOn(Dispatchers.IO)
   }

   fun getAllEventsFromApi(active: Int, limit: Int): Flow<EventResponse> {
      return try{
      flow {
         val response = eventApi.getEvents(active, limit).awaitResponse()
         if (response.isSuccessful) {
            emit(response.body()!!)
         } else {
            throw Exception("API call failed with code ${response.code()}")
         }
      }.flowOn(Dispatchers.IO)
      } catch (e: Exception) {
         flow {
            emit(EventResponse(error = true, message = e.message))
         }.flowOn(Dispatchers.IO)
      }
   }
}
