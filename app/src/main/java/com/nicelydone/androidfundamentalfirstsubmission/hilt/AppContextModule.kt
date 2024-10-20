package com.nicelydone.androidfundamentalfirstsubmission.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppContextModule {
   @Provides
   @Singleton
   fun provideContext(@ApplicationContext context: Context): Context {
      return context
   }
}
