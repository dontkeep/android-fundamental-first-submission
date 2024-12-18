package com.nicelydone.androidfundamentalfirstsubmission.ui.activity.detail

import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.model.connection.response.Event
import com.nicelydone.androidfundamentalfirstsubmission.databinding.ActivityDetailBinding
import com.nicelydone.androidfundamentalfirstsubmission.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
   private lateinit var binding: ActivityDetailBinding
   private lateinit var viewModel: DetailViewModel
   private var currentEvent: Event? = null
   private var isFavorited: Boolean = false

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      binding = ActivityDetailBinding.inflate(layoutInflater)
      setContentView(binding.root)

      changeLottieTheme()

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

      lifecycleScope.launch {
         isFavorited = viewModel.isEventFavorited(id).first()
         updateFavoriteButtonIcon(isFavorited)
      }


      viewModel.getDetail(id)
      viewModel.detailResult.observe(this) { item ->
         currentEvent = item.event!!
         binding.detailTitle.text = item.event.name ?: "No Name"
         binding.detailOwner.text = item.event.ownerName ?: "No Owner"

         binding.detailCategory.text = buildString {
            append("● ")
            append(item.event.category)
         }

         binding.cityName.text = buildString {
            append("● ")
            append(item.event.cityName)
         }

         binding.detailQuota.text =
            buildString {
               append("● ")
               append(with(item) {
                  (event?.quota ?: 0) - (event?.registrants ?: 0)
               }.toString())
            }

         binding.detailTime.text = item.event.beginTime ?: "No Time"

         binding.detailDescription.text = HtmlCompat.fromHtml(
            item.event.description ?: "No Description",
            HtmlCompat.FROM_HTML_MODE_LEGACY
         ).toString()

         Glide.with(binding.imageDetail.context).load(item.event.imageLogo).apply(RequestOptions.placeholderOf(R.drawable.placeholder).error(R.drawable.placeholder)).centerCrop()
            .into(binding.imageDetail)

         binding.registerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.event.link))
            startActivity(intent)
         }

      }

      binding.favouriteButton.setOnClickListener {
         isFavorited = !isFavorited
         updateFavoriteButtonIcon(isFavorited)
         currentEvent?.let {
            viewModel.toggleFavorite(it, isFavorited)
         }
      }

      viewModel.error.observe(this) { errorMessage ->
         errorMessage?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
         }
      }

      viewModel.loading.observe(this) { loading ->
         if (loading) {
            binding.loading.visibility = View.VISIBLE
         } else {
            binding.loading.visibility = View.GONE
         }
      }
   }

   private fun updateFavoriteButtonIcon(isFavorited: Boolean) {
      if (isFavorited) {
         binding.favouriteButton.setBackgroundColor(
            ContextCompat.getColor(
               this,
               R.color.colorFavourite
            )
         )
      } else {
         binding.favouriteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
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
