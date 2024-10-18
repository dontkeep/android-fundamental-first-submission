package com.nicelydone.androidfundamentalfirstsubmission.ui.activity

import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding
   private lateinit var navController: NavController
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)
      enableEdgeToEdge()

      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
         v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         insets
      }
      val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
      navController = navHostFragment.navController
      supportActionBar?.hide()
      setupActionBarWithNavController(navController)
      setupSmoothBottomMenu()

   }
   private fun setupSmoothBottomMenu() {
      val popupMenu = PopupMenu(this, null)
      popupMenu.inflate(R.menu.menu)
      val menu = popupMenu.menu
      binding.bottomBar.setupWithNavController(menu, navController)
   }
}