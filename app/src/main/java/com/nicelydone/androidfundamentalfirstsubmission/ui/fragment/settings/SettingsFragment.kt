package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
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
   private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

   @OptIn(DelicateCoroutinesApi::class)
   @Inject
   lateinit var themeDataStore: DataStoreManager

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      requestPermissionLauncher = registerForActivityResult(
         ActivityResultContracts.RequestPermission()
      ) { isGranted: Boolean ->
         if (isGranted) {
            val isChecked =
               binding.root.findViewById<MaterialSwitch>(R.id.notification_switch).isChecked
            handleReminderToggle(isChecked)
         } else {
            Snackbar.make(
               binding.root,
               "Permission denied to send notifications",
               Snackbar.LENGTH_SHORT
            ).show()
         }
      }
   }

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
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(
                  requireContext(),
                  Manifest.permission.POST_NOTIFICATIONS
               ) != PackageManager.PERMISSION_GRANTED
            ) {
               ActivityCompat.requestPermissions(
                  requireActivity(),
                  arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                  NOTIFICATION_PERMISSION_REQUEST_CODE
               )
            } else {
               handleReminderToggle(isChecked)
            }
         } else {
            handleReminderToggle(isChecked)
         }
      }
   }

   @OptIn(DelicateCoroutinesApi::class)
   private fun handleReminderToggle(isChecked: Boolean) {
      lifecycleScope.launch {
         themeDataStore.saveReminderPreference(isChecked)
         if (isChecked) {
            Snackbar.make(binding.root, "Daily Reminder Activated", Snackbar.LENGTH_SHORT).show()
            scheduleDailyReminder()
         } else {
            Snackbar.make(binding.root, "Daily Reminder Deactivated", Snackbar.LENGTH_SHORT).show()
            cancelDailyReminder()
         }
      }
   }

   private fun scheduleDailyReminder() {
      val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
         .build()
      WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
         "DailyReminder",
         ExistingPeriodicWorkPolicy.UPDATE,
         workRequest
      )
   }

   private fun cancelDailyReminder() {
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
      private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
   }
}