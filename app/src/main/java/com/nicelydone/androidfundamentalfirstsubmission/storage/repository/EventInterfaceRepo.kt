package com.nicelydone.androidfundamentalfirstsubmission.storage.repository

import com.nicelydone.androidfundamentalfirstsubmission.storage.entity.FavEventEntity
import kotlinx.coroutines.flow.Flow

interface EventInterfaceRepo {
   suspend fun insert(favEvent: FavEventEntity)
   suspend fun deleteById(eventId: Int)
   fun getAll(): Flow<List<FavEventEntity>>
}
