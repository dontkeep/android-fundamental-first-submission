package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.finished

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nicelydone.androidfundamentalfirstsubmission.databinding.FragmentFinishedBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.detail.DetailActivity
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.MultiAdapter
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.DetailViewModel
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FinishedFragment : Fragment() {
   private var _binding: FragmentFinishedBinding? = null
   private val binding get() = _binding!!
   private lateinit var viewModel: HomeViewModel
   private lateinit var detailViewModel: DetailViewModel

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFinishedBinding.inflate(inflater, container, false)
      changeLottieTheme()
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
      detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

      setupRecyclerView(viewModel)
      viewModel.loading.observe(viewLifecycleOwner) {
         if (it) {
            showLoading(true)
         } else {
            showLoading(false)
         }
      }
   }

   private fun changeLottieTheme() {
      val isDarkMode =
         resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
      val animationFile = if (isDarkMode) {
         "loading_white.json"
      } else {
         "loading_primary.json"
      }
      binding.loading.setAnimation(animationFile)
   }

   private fun handleEventClick(eventId: Int) {
      lifecycleScope.launch {
         val isFavorited = detailViewModel.isEventFavorited(eventId).first()
         val isNetworkAvailable = isNetworkAvailable()

         if (!isNetworkAvailable && !isFavorited) {
            Snackbar.make(
               binding.root,
               "No Internet Connection & Event not favourited",
               Snackbar.LENGTH_SHORT
            ).show()
         } else {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("id", eventId)
            startActivity(intent)
         }
      }
   }

   private fun isNetworkAvailable(): Boolean {
      val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
      val networkCapabilities = connectivityManager.activeNetwork ?: return false
      val activeNetwork =
         connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
      return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
          activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
          activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
   }

   private fun showLoading(isLoading: Boolean) {
      binding.loading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
   }

   private fun setupRecyclerView(viewModel: HomeViewModel) {
      binding.finishedRv2.layoutManager =
         LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      val adapter = MultiAdapter { eventId ->
         handleEventClick(eventId)
      }
      binding.finishedRv2.adapter = adapter

      viewModel.getEventList(0)
      viewModel.finishEventList.observe(viewLifecycleOwner) {
         adapter.submitList(it.listEvents)
      }

      viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
         errorMessage?.let {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}
