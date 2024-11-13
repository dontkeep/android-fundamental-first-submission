package com.nicelydone.androidfundamentalfirstsubmission.hilt

import android.app.Application
import androidx.room.Room
import com.nicelydone.androidfundamentalfirstsubmission.model.storage.room.dao.EventDao
import com.nicelydone.androidfundamentalfirstsubmission.model.storage.room.db.EventDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
   @Provides
   @Singleton
   fun provideDatabase(application: Application): EventDb {
      return Room.databaseBuilder(application, EventDb::class.java, "event_database").build()
   }

   @Provides
   fun provideEventDao(eventDb: EventDb): EventDao {
      return eventDb.getEventDao()
   }
}
