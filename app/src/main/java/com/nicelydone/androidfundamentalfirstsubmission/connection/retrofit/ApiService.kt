package com.nicelydone.androidfundamentalfirstsubmission.connection.retrofit

import com.nicelydone.androidfundamentalfirstsubmission.connection.response.DetailEventResponse
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
   @GET("/events")
   fun getEvents(@Query("active")active: Int, @Query("limit")limit: Int = 40): Call<EventResponse>

   @GET("/events")
   fun searchEvents(@Query("q")query: String): Call<EventResponse>

   @GET("/events/{id}")
   fun getEventById(@Path("id") id: String): Call<DetailEventResponse>
}
