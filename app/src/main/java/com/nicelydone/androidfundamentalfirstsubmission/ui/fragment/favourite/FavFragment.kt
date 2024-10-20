package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.databinding.FragmentFavBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.FavouriteAdapter
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.FavouriteViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavFragment : Fragment() {
   private var _binding: FragmentFavBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFavBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      val viewModel = ViewModelProvider(this)[FavouriteViewModel::class.java]
      val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      binding.favouriteRv.layoutManager = layoutManager
      binding.favouriteRv.adapter = FavouriteAdapter()

      viewModel.favoritedEvents.observe(viewLifecycleOwner) {
         if (it.isNullOrEmpty()) {
            binding.emptyText.visibility = View.VISIBLE
            binding.emptyText.setText(R.string.empty_fav)
            binding.favouriteRv.visibility = View.GONE
         } else {
            binding.emptyText.visibility = View.GONE
            (binding.favouriteRv.adapter as FavouriteAdapter).submitList(it)
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}
