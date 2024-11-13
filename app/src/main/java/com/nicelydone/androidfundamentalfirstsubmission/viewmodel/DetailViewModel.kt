package com.nicelydone.androidfundamentalfirstsubmission.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.nicelydone.androidfundamentalfirstsubmission.model.connection.response.DetailEventResponse
import com.nicelydone.androidfundamentalfirstsubmission.model.connection.response.Event
import com.nicelydone.androidfundamentalfirstsubmission.model.storage.entity.FavEventEntity
import com.nicelydone.androidfundamentalfirstsubmission.model.storage.repository.EventRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
   private val eventRepo: EventRepo,
   private val application: Application
) : AndroidViewModel(application) {
   private val _detailResult = MutableLiveData<DetailEventResponse>()
   var detailResult: LiveData<DetailEventResponse> = _detailResult

   private val _loading = MutableLiveData<Boolean>()
   var loading: LiveData<Boolean> = _loading

   private val _error = MutableLiveData<String?>()
   val error: LiveData<String?> = _error

   init {
      _loading.value = false
   }

   fun isEventFavorited(eventId: Int): Flow<Boolean> {
      return eventRepo.isEventFavorite(eventId)
   }

   private fun deleteEventById(eventId: Int) {
      viewModelScope.launch(Dispatchers.IO) {
         eventRepo.deleteById(eventId)
      }
   }

   fun toggleFavorite(event: Event?, isFavorited: Boolean) {
      viewModelScope.launch(Dispatchers.IO) {
         if (isFavorited && event != null) {
            saveEvent(event)
         } else if (event != null) {
            deleteEventById(event.id!!)
         }
      }
   }

   private fun saveEvent(currentEvent: Event) {
      viewModelScope.launch(Dispatchers.IO) {
         currentEvent.let {
            try {
               val bitmap = Glide.with(application)
                  .asBitmap()
                  .load(it.imageLogo)
                  .submit()
                  .get()

               val imagePath = eventRepo.saveBitmapToFile(bitmap, "event_image_${it.id}")

               val favEventEntity = FavEventEntity(
                  id = it.id,
                  summary = it.summary,
                  description = it.description,
                  image = imagePath,
                  link = it.link,
                  title = it.name,
                  category = it.category,
                  city = it.cityName,
                  owner = it.ownerName,
                  date = it.beginTime,
                  quota = it.quota,
                  registerant = it.registrants,
                  isFavorite = true
               )

               eventRepo.insert(favEventEntity)
            } catch (_: Exception) {
            }
         }
      }
   }

   fun getDetail(id: Int) {
      viewModelScope.launch {
         _loading.value = true
         val isFavorited = eventRepo.isEventFavorite(id).first()
         eventRepo.getEventDetails(id, isFavorited).collect { eventDetails ->
            when (eventDetails) {
               is FavEventEntity -> {
                  val detailEvent = DetailEventResponse(
                     error = false,
                     message = null,
                     event = Event(
                        id = eventDetails.id,
                        summary = eventDetails.summary,
                        description = eventDetails.description,
                        imageLogo = eventDetails.image,
                        link = eventDetails.link,
                        name = eventDetails.title,
                        category = eventDetails.category,
                        cityName = eventDetails.city,
                        ownerName = eventDetails.owner,
                        beginTime = eventDetails.date,
                        quota = eventDetails.quota,
                        registrants = eventDetails.registerant
                     )
                  )
                  _detailResult.value = detailEvent
               }

               is DetailEventResponse -> {
                  _detailResult.value = eventDetails
               }
            }
            _loading.value = false
         }
      }
   }
}
