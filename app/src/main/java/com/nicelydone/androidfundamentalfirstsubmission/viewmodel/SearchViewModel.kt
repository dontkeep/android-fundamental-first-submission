package com.nicelydone.androidfundamentalfirstsubmission.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.EventResponse
import com.nicelydone.androidfundamentalfirstsubmission.connection.retrofit.ApiConfig
import com.nicelydone.androidfundamentalfirstsubmission.storage.repository.EventRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val eventRepo: EventRepo) : ViewModel() {
   private val _searchResult = MutableLiveData<EventResponse>()
   var searchResult: LiveData<EventResponse> = _searchResult

   private val _loading = MutableLiveData<Boolean>()
   var loading: LiveData<Boolean> = _loading

   private val _error = MutableLiveData<String?>()
   val error: LiveData<String?> = _error

   init {
      _loading.value = false
   }

   fun getResult(query: String){
      _loading.value = true
      viewModelScope.launch {
         try {
            val eventResponse = eventRepo.searchEventsFromApi(query).first()
            _searchResult.value = eventResponse
            _loading.value = false
            _error.value = null
         } catch (e: Exception) {
            _loading.value = false
            _error.value = "Failed to search events: ${e.message}"
            Log.e("SearchViewModel", "Error searching events: ${e.message}", e)
         }
      }
   }
}