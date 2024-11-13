package com.nicelydone.androidfundamentalfirstsubmission.hilt

import com.nicelydone.androidfundamentalfirstsubmission.model.connection.retrofit.ApiConfig
import com.nicelydone.androidfundamentalfirstsubmission.model.connection.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
   @Provides
   @Singleton
   fun provideApiService(): ApiService {
      return ApiConfig.getApiService()
   }
}
