package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.upcoming

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicelydone.androidfundamentalfirstsubmission.databinding.FragmentUpcomingBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.detail.DetailActivity
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.MultiAdapter
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.DetailViewModel
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpcomingFragment : Fragment() {
   private var _binding: FragmentUpcomingBinding? = null
   private val binding get() = _binding!!
   private lateinit var viewModel: HomeViewModel
   private lateinit var detailViewModel: DetailViewModel

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
      detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

      setupRecyclerView()

      viewModel.getEventList(1)
      viewModel.activeEventList.observe(viewLifecycleOwner){
         (binding.upcomingRv2.adapter as MultiAdapter).submitList(it.listEvents)
      }

      viewModel.error.observe(viewLifecycleOwner){ errorMessage ->
         errorMessage?.let {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
         }
      }

      viewModel.loading.observe(viewLifecycleOwner){
         if (it){
            showLoading(true)
         }else{
            showLoading(false)
         }
      }
   }

   private fun handleEventClick(eventId: Int) {
      lifecycleScope.launch {
         val isFavorited = detailViewModel.isEventFavorited(eventId).first()
         val isNetworkAvailable = isNetworkAvailable()

         if (!isNetworkAvailable && !isFavorited) {
            Toast.makeText(requireContext(), "No Internet Connection and Event Not Favorited", Toast.LENGTH_SHORT).show()
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
      val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
      return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
          activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
          activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
   }

   private fun showLoading(isLoading: Boolean) {
      binding.loading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
   }

   private fun setupRecyclerView() {
      binding.upcomingRv2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      binding.upcomingRv2.adapter = MultiAdapter(){ eventId ->
         handleEventClick(eventId)
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}