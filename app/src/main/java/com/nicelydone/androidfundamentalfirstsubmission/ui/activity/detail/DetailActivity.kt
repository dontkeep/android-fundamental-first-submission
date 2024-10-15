package com.nicelydone.androidfundamentalfirstsubmission.ui.activity.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.databinding.ActivityDetailBinding
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.DetailViewModel

class DetailActivity : AppCompatActivity() {
   private lateinit var binding: ActivityDetailBinding
   private lateinit var viewModel: DetailViewModel

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      binding = ActivityDetailBinding.inflate(layoutInflater)
      setContentView(binding.root)

      val actionBar = supportActionBar
      actionBar?.title = "Event Detail"
      actionBar?.setDisplayHomeAsUpEnabled(true)

      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
         v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         insets
      }
      val id = intent.getIntExtra("id", 0)

      viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
      viewModel.getDetail(id)
      viewModel.detailResult.observe(this){ item ->
         binding.detailTitle.text = item.event?.name ?: "No Name"
         binding.detailOwner.text = item.event?.ownerName ?: "No Owner"

         binding.detailCategory.text = buildString {
            append("● ")
            append(item.event?.category)
         }

         binding.detailCity.text = buildString {
            append("● ")
            append(item.event?.cityName)
         }

         binding.detailQuota.text =
            buildString {
               append("● ")
               append(with(item) {
                  (event?.quota ?: 0) - (event?.registrants ?: 0)
               }.toString())
            }

         binding.detailTime.text = item.event?.beginTime ?: "No Time"

         binding.eventDesc.text = HtmlCompat.fromHtml(
            item.event?.description ?: "No Description",
            HtmlCompat.FROM_HTML_MODE_LEGACY
         ).toString()

         Glide.with(binding.detailImage.context).load(item.event?.imageLogo).centerCrop().into(binding.detailImage)

         binding.registerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.event?.link))
            startActivity(intent)
         }
      }

      viewModel.error.observe(this){ errorMessage ->
         errorMessage?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
         }
      }

      viewModel.loading.observe(this){ loading ->
         if(loading){
            binding.loading.visibility = View.VISIBLE
         }else{
            binding.loading.visibility = View.GONE
         }
      }
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return when (item.itemId) {
         android.R.id.home -> {
            onBackPressedDispatcher.onBackPressed()
            true
         }
         else -> super.onOptionsItemSelected(item)
      }
   }
}
