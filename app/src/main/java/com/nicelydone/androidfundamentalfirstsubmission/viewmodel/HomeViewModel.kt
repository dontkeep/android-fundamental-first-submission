package com.nicelydone.androidfundamentalfirstsubmission.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.EventResponse
import com.nicelydone.androidfundamentalfirstsubmission.storage.repository.EventRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val eventRepo: EventRepo) : ViewModel() {
   private val _activeEventList = MutableLiveData<EventResponse>()
   var activeEventList: LiveData<EventResponse> = _activeEventList

   private val _finishEventList = MutableLiveData<EventResponse>()
   var finishEventList: LiveData<EventResponse> = _finishEventList

   private val _activeEventListSpecificNum = MutableLiveData<EventResponse>()
   var activeEventListSpecificNum: LiveData<EventResponse> = _activeEventListSpecificNum

   private val _finishEventListSpecificNum = MutableLiveData<EventResponse>()
   var finishEventListSpecificNum: LiveData<EventResponse> = _finishEventListSpecificNum

   private val _loading = MutableLiveData<Boolean>()
   var loading: LiveData<Boolean> = _loading

   private val _error = MutableLiveData<String?>()
   val error: LiveData<String?> = _error

   init {
      _loading.value = false
   }

   fun fetchEvents() {
      getEventList(1, 5)
      getEventList(0, 5)
   }

   fun getEventList(active: Int, limit: Int = 20) {
      _loading.value = true
      viewModelScope.launch {
         try {
            val eventResponse = eventRepo.getAllEventsFromApi(active, limit).first()
            when (active) {
               1 -> {
                  if (limit != 20) {
                     _activeEventListSpecificNum.value = eventResponse
                  } else {
                     _activeEventList.value = eventResponse
                  }
               }

               0 -> {
                  if (limit != 20) {
                     _finishEventListSpecificNum.value = eventResponse
                  } else {
                     _finishEventList.value = eventResponse
                  }
               }
            }
            Log.d("HomeViewModel", "Data is fetched : $eventResponse")
            Log.d("HomeViewModel", "Loading is down : ${_loading.value}")
            _loading.value = false
            _error.value = null
         } catch (e: Exception) {
            _loading.value = false
            _error.value = "Failed to fetch data: ${e.message}"
         }
      }
   }
}