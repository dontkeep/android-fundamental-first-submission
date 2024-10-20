package com.nicelydone.androidfundamentalfirstsubmission.ui.helper

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@DelicateCoroutinesApi
class DataStoreManager @Inject constructor(@ApplicationContext context: Context) {

   private val Context.dataStore by preferencesDataStore("user_pref")
   private val dataStore = context.dataStore
   private val _themeFlow = MutableStateFlow(false)
   val themeFlow: StateFlow<Boolean> get() = _themeFlow

   private val _reminderFlow = MutableStateFlow(false)
   val reminderFlow: StateFlow<Boolean> get() = _reminderFlow

   init {
      dataStore.data.map { preferences ->
         preferences[THEME_KEY] ?: false
      }.distinctUntilChanged()
         .onEach { isDarkMode -> _themeFlow.value = isDarkMode }
         .launchIn(GlobalScope)

      dataStore.data.map { preferences ->
         preferences[REMINDER_KEY] ?: false
      }.distinctUntilChanged()
         .onEach { isReminderActive -> _reminderFlow.value = isReminderActive }
         .launchIn(GlobalScope)
   }

   suspend fun saveThemePreference(isDarkMode: Boolean) {
      dataStore.edit { preferences ->
         preferences[THEME_KEY] = isDarkMode
      }
   }

   suspend fun saveReminderPreference(isReminderActive: Boolean) {
      dataStore.edit { preferences ->
         preferences[REMINDER_KEY] = isReminderActive
      }
   }

   companion object {
      val THEME_KEY = booleanPreferencesKey("theme_key")
      val REMINDER_KEY = booleanPreferencesKey("reminder_key")
   }
}
