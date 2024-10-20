package com.nicelydone.androidfundamentalfirstsubmission.hilt

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.nicelydone.androidfundamentalfirstsubmission.ui.helper.DataStoreManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application() {
   @OptIn(DelicateCoroutinesApi::class)
   @Inject
   lateinit var dataStoreManager: DataStoreManager

   override fun onCreate() {
      super.onCreate()
      applyTheme()
   }

   @OptIn(DelicateCoroutinesApi::class)
   private fun applyTheme() {
      CoroutineScope(Dispatchers.Main).launch {
         val isDarkMode = dataStoreManager.themeFlow.first() // Get the current theme preference
         val themeMode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
         } else {
            AppCompatDelegate.MODE_NIGHT_NO
         }
         AppCompatDelegate.setDefaultNightMode(themeMode)
      }
   }
}