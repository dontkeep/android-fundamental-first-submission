package com.nicelydone.androidfundamentalfirstsubmission.hilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
   override fun onCreate() {
      super.onCreate()
      // Add any additional initialization code here
   }
}