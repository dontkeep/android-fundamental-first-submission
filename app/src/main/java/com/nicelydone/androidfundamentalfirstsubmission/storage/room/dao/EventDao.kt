package com.nicelydone.androidfundamentalfirstsubmission.storage.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nicelydone.androidfundamentalfirstsubmission.storage.entity.FavEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   fun insert(event: FavEventEntity)

   @Query("SELECT * FROM FavEventEntity ORDER BY id ASC")
   fun getAllEvents(): Flow<List<FavEventEntity>>

   @Query("SELECT * FROM FavEventEntity WHERE id = :id")
   fun getEventById(id: Int): Flow<FavEventEntity>

   @Query("SELECT EXISTS(SELECT 1 FROM FavEventEntity WHERE id = :eventId)")
   fun isEventFavorite(eventId: Int): Flow<Boolean>

   @Query("UPDATE favevententity SET isFavorite = :isFavorite WHERE id = :eventId")
   suspend fun updateFavoriteState(eventId: Int, isFavorite: Boolean)

   @Query("DELETE FROM FavEventEntity WHERE id = :id")
   fun deleteById(id: Int)
}