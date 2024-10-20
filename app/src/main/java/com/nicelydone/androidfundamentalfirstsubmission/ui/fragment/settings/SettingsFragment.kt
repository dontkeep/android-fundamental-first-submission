package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.settings

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.DrawableCompat.applyTheme
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.databinding.FragmentSettingsBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.helper.DailyReminderWorker
import com.nicelydone.androidfundamentalfirstsubmission.ui.helper.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {
   private var _binding: FragmentSettingsBinding? = null
   private val binding get() = _binding!!

   @OptIn(DelicateCoroutinesApi::class)
   @Inject
   lateinit var themeDataStore: DataStoreManager

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSettingsBinding.inflate(inflater, container, false)
      setupThemeToggle()
      setupReminderToggle()
      return binding.root
   }

   @OptIn(DelicateCoroutinesApi::class)
   private fun setupThemeToggle() {
      val themeSwitch = binding.root.findViewById<MaterialSwitch>(R.id.theme_switch)

      lifecycleScope.launch {
         themeDataStore.themeFlow.collect { isDarkMode ->
            if (isDarkMode) {
               themeSwitch.setText(R.string.dark_mode)
            } else {
               themeSwitch.setText(R.string.light_mode)
            }
            themeSwitch.isChecked = isDarkMode
         }
      }

      themeSwitch.setOnCheckedChangeListener { _, isChecked ->
         lifecycleScope.launch {
            themeDataStore.saveThemePreference(isChecked)
            applyTheme(isChecked)
         }
      }
   }

   @OptIn(DelicateCoroutinesApi::class)
   private fun setupReminderToggle() {
      val reminderSwitch = binding.root.findViewById<MaterialSwitch>(R.id.notification_switch)

      lifecycleScope.launch {
         themeDataStore.reminderFlow.collect { isReminderActive ->
            reminderSwitch.isChecked = isReminderActive
         }
      }

      reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
         lifecycleScope.launch {
            themeDataStore.saveReminderPreference(isChecked)
            if (isChecked) {
               Log.d("Schedule Running", " is Checked")
               scheduleDailyReminder()
            } else {
               Log.d("Schedule Running", " is Not Checked")
               cancelDailyReminder()
            }
         }
      }
   }

   private fun scheduleDailyReminder(){
      val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
         .build()
      WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
         "DailyReminder",
         ExistingPeriodicWorkPolicy.UPDATE,
         workRequest
      )
      Log.d("Schedule Running", "Daily Reminder Scheduled")
   }

   private fun cancelDailyReminder(){
      WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminder")
   }

   private fun applyTheme(isDarkMode: Boolean) {
      val themeMode = if (isDarkMode) {
         AppCompatDelegate.MODE_NIGHT_YES
      } else {
         AppCompatDelegate.MODE_NIGHT_NO
      }
      AppCompatDelegate.setDefaultNightMode(themeMode)
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }

   companion object {
      const val TAG = "SettingsBottomSheet"
   }
}