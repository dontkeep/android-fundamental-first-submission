package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.upcoming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicelydone.androidfundamentalfirstsubmission.databinding.FragmentUpcomingBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.MultiAdapter
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.HomeViewModel

class UpcomingFragment : Fragment() {
   private var _binding: FragmentUpcomingBinding? = null
   private val binding get() = _binding!!
   private lateinit var viewModel: HomeViewModel

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
      setupRecyclerView()

      viewModel.getEventList(1)
      viewModel.activeEventList.observe(viewLifecycleOwner){
         (binding.upcomingRv2.adapter as MultiAdapter).submitList(it.listEvents)
      }

      viewModel.error.observe(viewLifecycleOwner){ errorMessage ->
         errorMessage?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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

   private fun showLoading(isLoading: Boolean) {
      binding.loading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
   }

   private fun setupRecyclerView() {
      binding.upcomingRv2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      binding.upcomingRv2.adapter = MultiAdapter()
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}