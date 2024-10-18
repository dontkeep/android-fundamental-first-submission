package com.nicelydone.androidfundamentalfirstsubmission.storage.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nicelydone.androidfundamentalfirstsubmission.storage.entity.FavEventEntity
import com.nicelydone.androidfundamentalfirstsubmission.storage.room.dao.EventDao

@Database(entities = [FavEventEntity::class], version = 1, exportSchema = false)
abstract class EventDb: RoomDatabase() {
   abstract fun getEventDao(): EventDao
   companion object{
      @Volatile
      private var INSTANCE: EventDb? = null

      @JvmStatic
      fun getInstance(context: Context): EventDb{
         if (INSTANCE == null) {
            synchronized(EventDb::class.java) {
               INSTANCE =
                  Room.databaseBuilder(context.applicationContext, EventDb::class.java, "event_db")
                     .build()
            }
         }
         return INSTANCE as EventDb
      }
   }
}