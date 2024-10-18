package com.nicelydone.androidfundamentalfirstsubmission.ui.activity.searchresult

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.databinding.ActivitySearchResultBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.detail.DetailActivity
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.MultiAdapter
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.DetailViewModel
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchResultActivity : AppCompatActivity() {
   private lateinit var binding: ActivitySearchResultBinding
   private lateinit var viewModel: SearchViewModel
   private lateinit var detailViewModel: DetailViewModel

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivitySearchResultBinding.inflate(layoutInflater)
      enableEdgeToEdge()
      setContentView(binding.root)
      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
         v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         insets
      }
      supportActionBar?.hide()
      viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
      detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

      setupRecyclerView()

      val query = intent.getStringExtra("query")
      if (query != null) {
         viewModel.getResult(query)
      }

      viewModel.error.observe(this) { errorMessage ->
         errorMessage?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
         }
      }

      viewModel.searchResult.observe(this) {
         (binding.searchRv.adapter as MultiAdapter).submitList(it.listEvents)
      }
      viewModel.loading.observe(this) {
         binding.loading.visibility = if (it) View.VISIBLE else View.INVISIBLE
      }
   }

   private fun setupRecyclerView() {
      binding.searchRv.adapter = MultiAdapter(){ eventId ->
         handleEventClick(eventId)
      }
      binding.searchRv.layoutManager =
         LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
   }

   private fun handleEventClick(eventId: Int) {
      lifecycleScope.launch {
         val isFavorited = detailViewModel.isEventFavorited(eventId).first()
         val isNetworkAvailable = isNetworkAvailable()

         if (!isNetworkAvailable && !isFavorited) {
            Toast.makeText(this@SearchResultActivity, "No Internet Connection and Event Not Favorited", Toast.LENGTH_SHORT).show()
         } else {
            val intent = Intent(this@SearchResultActivity, DetailActivity::class.java)
            intent.putExtra("id", eventId)
            startActivity(intent)
         }
      }
   }

   private fun isNetworkAvailable(): Boolean {
      val connectivityManager = this.getSystemService(ConnectivityManager::class.java)
      val networkCapabilities = connectivityManager.activeNetwork ?: return false
      val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
      return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
          activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
          activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
   }
}