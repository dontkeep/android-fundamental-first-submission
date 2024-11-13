package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.home

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
import com.nicelydone.androidfundamentalfirstsubmission.databinding.FragmentHomeBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.detail.DetailActivity
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.searchresult.SearchResultActivity
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.MultiAdapter
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.UpcomingAdapter
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.DetailViewModel
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
   private var _binding: FragmentHomeBinding? = null
   private val binding get() = _binding!!
   private lateinit var viewModel: HomeViewModel
   private lateinit var detailViewModel: DetailViewModel

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentHomeBinding.inflate(inflater, container, false)
      changeLottieTheme()
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      setupRecyclerView()
      viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
      detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

      viewModel.fetchTwoEvents()

      viewModel.activeEventListSpecificNum.observe(viewLifecycleOwner) {
         (binding.upcomingRv.adapter as UpcomingAdapter).submitList(it.listEvents)
      }
      viewModel.finishEventListSpecificNum.observe(viewLifecycleOwner) {
         (binding.finishedRv.adapter as MultiAdapter).submitList(it.listEvents)
      }

      setupLoading()

      viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
         if (errorMessage != null) {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
         }
      }
      setupSearch()
   }

   private fun setupLoading(){
      viewModel.loading.observe(viewLifecycleOwner) {
         if (it) {
            showLoadingUpcoming(true)
         } else {
            showLoadingUpcoming(false)
         }
      }

      viewModel.loading2.observe(viewLifecycleOwner) {
         if (it) {
            showLoadingFinished(true)
         } else {
            showLoadingFinished(false)
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

   private fun isNetworkAvailable(): Boolean {
      val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
      val networkCapabilities = connectivityManager.activeNetwork ?: return false
      val activeNetwork =
         connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
      return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
          activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
          activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
   }

   private fun setupRecyclerView() {
      binding.finishedRv.layoutManager =
         LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      binding.finishedRv.adapter = MultiAdapter { eventId ->
         handleEventClick(eventId)
      }
      binding.upcomingRv.layoutManager =
         LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      binding.upcomingRv.adapter = UpcomingAdapter { eventId ->
         handleEventClick(eventId)
      }
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

   private fun setupSearch() {
      binding.searchView.setupWithSearchBar(binding.searchBar)
      binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
         binding.searchBar.setText(binding.searchView.text)
         binding.searchView.hide()

         val intent = Intent(requireContext(), SearchResultActivity::class.java)
         intent.putExtra("query", binding.searchView.text.toString())
         requireContext().startActivity(intent)
         false
      }
   }

   private fun showLoadingUpcoming(isLoading: Boolean) {
      binding.upcomingRv.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
      binding.loading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
   }

   private fun showLoadingFinished(isLoading: Boolean) {
      binding.finishedRv.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
      binding.loading2.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }

}
