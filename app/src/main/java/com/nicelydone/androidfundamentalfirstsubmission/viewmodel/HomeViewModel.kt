package com.nicelydone.androidfundamentalfirstsubmission.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.EventResponse
import com.nicelydone.androidfundamentalfirstsubmission.connection.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class HomeViewModel: ViewModel() {
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
      viewModelScope.launch {
         launch { getEventList(1, 5) }
         launch { getEventList(0, 5) }
      }
   }

   // 1 = active or upcoming, 0 = finished, -1 = all
   fun getEventList(active: Int, limit: Int = 40){
      _loading.value = true
      if (active == 1) {
         val client = ApiConfig.getApiService().getEvents(active, limit)
         client.enqueue(object: retrofit2.Callback<EventResponse>{
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
               if(response.isSuccessful){
                  _loading.value = false
                  _error.value = null
                  if (limit != 40) {
                     _activeEventListSpecificNum.value = response.body()
                  }else {
                     _activeEventList.value = response.body()
                  }
               } else{
                  _loading.value = false
                  _error.value = "Failed to fetch data. Please try again."
               }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
               _loading.value = false
               _error.value = "Network error. Please check your connection."
            }

         })
      }else if (active == 0){
         val client = ApiConfig.getApiService().getEvents(active, limit)
         client.enqueue(object: retrofit2.Callback<EventResponse>{
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
               if(response.isSuccessful){
                  _loading.value = false
                  if (limit != 40) {
                     _finishEventListSpecificNum.value = response.body()
                  }else {
                     _finishEventList.value = response.body()
                  }
               } else{
                  _loading.value = false
                  Log.d("MainViewModel", "onFailure: ${response.message()}")
               }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
               _loading.value = false
               Log.d("TAG", "onFailure: ${t.message}")
            }
         })
      }
   }
}