package com.nicelydone.androidfundamentalfirstsubmission.ui.activity.searchresult

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.databinding.ActivitySearchResultBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.adapter.MultiAdapter
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.SearchViewModel

class SearchResultActivity : AppCompatActivity() {
   private lateinit var binding: ActivitySearchResultBinding
   private lateinit var viewModel: SearchViewModel

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
      setupRecyclerView()

      val query = intent.getStringExtra("query")
      if (query != null) {
         viewModel.getResult(query)
      }

      viewModel.error.observe(this){ errorMessage ->
         errorMessage?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
         }
      }

      viewModel.searchResult.observe(this){
         (binding.searchRv.adapter as MultiAdapter).submitList(it.listEvents)
      }
      viewModel.loading.observe(this){
         binding.loading.visibility = if (it) View.VISIBLE else View.INVISIBLE
      }
   }

   private fun setupRecyclerView(){
      binding.searchRv.adapter = MultiAdapter()
      binding.searchRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
   }
}