package com.nicelydone.androidfundamentalfirstsubmission.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.EventResponse
import com.nicelydone.androidfundamentalfirstsubmission.connection.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel: ViewModel() {
   private val _searchResult = MutableLiveData<EventResponse>()
   var searchResult: LiveData<EventResponse> = _searchResult

   private val _loading = MutableLiveData<Boolean>()
   var loading: LiveData<Boolean> = _loading

   private val _error = MutableLiveData<String?>()
   val error: LiveData<String?> = _error

   fun getResult(query: String){
      _loading.value = true
      val client = ApiConfig.getApiService().searchEvents(query)
      client.enqueue(object: Callback<EventResponse>{
         override fun onResponse(p0: Call<EventResponse>, p1: Response<EventResponse>) {
            _loading.value = false
            _error.value = null
            if (p1.isSuccessful){
               _searchResult.value = p1.body()
            }else {
               _loading.value = false
               _error.value = "Failed to fetch data. Please try again."
            }
         }

         override fun onFailure(p0: Call<EventResponse>, p1: Throwable) {
            _loading.value = false
            _error.value = "Failed to fetch data. Please try again."
         }
      })
   }
}