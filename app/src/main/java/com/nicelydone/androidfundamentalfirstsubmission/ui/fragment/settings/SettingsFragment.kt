package com.nicelydone.androidfundamentalfirstsubmission.ui.fragment.settings

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {
   private var _binding: FragmentSettingsBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSettingsBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }

   override fun onDismiss(dialog: DialogInterface) {
      super.onDismiss(dialog)
      findNavController().popBackStack()
   }

   companion object {
      const val TAG = "SettingsBottomSheet"
   }
}