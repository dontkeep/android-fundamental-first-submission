package com.nicelydone.androidfundamentalfirstsubmission.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.DetailEventResponse
import com.nicelydone.androidfundamentalfirstsubmission.connection.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Response

class DetailViewModel: ViewModel() {
   private val _detailResult = MutableLiveData<DetailEventResponse>()
   var detailResult: LiveData<DetailEventResponse> = _detailResult

   private val _loading = MutableLiveData<Boolean>()
   var loading: LiveData<Boolean> = _loading

   private val _error = MutableLiveData<String?>()
   val error: LiveData<String?> = _error

   init {
      _loading.value = false
   }

   fun getDetail(id: Int){
      _loading.value = true
      val client = ApiConfig.getApiService().getEventById(id.toString())
      client.enqueue(object: retrofit2.Callback<DetailEventResponse>{
         override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
            if(response.isSuccessful){
               _loading.value = false
               _error.value = null
               _detailResult.value = response.body()
            } else {
               _loading.value = false
               _error.value = "Failed to fetch data. Please try again."
            }
         }
         override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
            _loading.value = false
            _error.value = "Failed to fetch data. Please try again."
         }
      })
   }
}