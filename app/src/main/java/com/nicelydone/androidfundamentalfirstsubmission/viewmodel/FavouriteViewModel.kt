package com.nicelydone.androidfundamentalfirstsubmission.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.androidfundamentalfirstsubmission.storage.entity.FavEventEntity
import com.nicelydone.androidfundamentalfirstsubmission.storage.repository.EventRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(private val eventRepo: EventRepo) : ViewModel() {
   private val _favoritedEvents = MutableLiveData<List<FavEventEntity>>()
   val favoritedEvents: LiveData<List<FavEventEntity>> = _favoritedEvents

   init {
      getFavoritedEvents()
   }

   private fun getFavoritedEvents() {
      viewModelScope.launch {
         eventRepo.getAll().collect { events ->
            _favoritedEvents.value = events
         }
      }
   }
}