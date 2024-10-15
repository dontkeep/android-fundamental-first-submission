package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicelydone.androidfundamentalfirstsubmission.databinding.FragmentHomeBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.searchresult.SearchResultActivity
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.MultiAdapter
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.UpcomingAdapter
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
   private var _binding: FragmentHomeBinding? = null
   private val binding get() = _binding!!
   private lateinit var viewModel: HomeViewModel

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentHomeBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupRecyclerView()
      viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

      viewModel.fetchEvents()

      viewModel.activeEventListSpecificNum.observe(viewLifecycleOwner){
         (binding.upcomingRv.adapter as UpcomingAdapter).submitList(it.listEvents)
      }
      viewModel.finishEventListSpecificNum.observe(viewLifecycleOwner){
         (binding.finishedRv.adapter as MultiAdapter).submitList(it.listEvents)
      }

      viewModel.loading.observe(viewLifecycleOwner){
         if (it){
            showLoading(true)
         }else{
            showLoading(false)
         }
      }

      viewModel.error.observe(viewLifecycleOwner){ errorMessage ->
         errorMessage?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
         }
      }

      setupSearch()
   }

   private fun setupRecyclerView(){
      binding.finishedRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      binding.finishedRv.adapter = MultiAdapter()
      binding.upcomingRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      binding.upcomingRv.adapter = UpcomingAdapter()
   }

   private fun setupSearch(){
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

   private fun showLoading(isLoading: Boolean) {
      binding.loading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }

}