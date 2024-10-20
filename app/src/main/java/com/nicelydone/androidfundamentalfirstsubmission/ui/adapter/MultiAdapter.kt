package com.nicelydone.androidfundamentalfirstsubmission.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nicelydone.androidfundamentalfirstsubmission.R
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.ListEventsItem
import com.nicelydone.androidfundamentalfirstsubmission.databinding.ItemVerticalBinding

class MultiAdapter(private val onEventClick: (Int) -> Unit) :
   ListAdapter<ListEventsItem, MultiAdapter.ViewHolder>(DIFF_CALLBACK) {
   class ViewHolder(private val binding: ItemVerticalBinding) :
      RecyclerView.ViewHolder(binding.root) {
      fun bind(eventItem: ListEventsItem, onEventClick: (Int) -> Unit) {
         binding.apply {
            val title = eventItem.name?.split(" ")?.take(10)?.joinToString(" ")
            itemTitle.text = buildString {
               append(title)
               append("...")
            }
            summaryVertical.text = eventItem.summary
            summaryVertical.ellipsize = TextUtils.TruncateAt.END
            summaryVertical.maxLines = 1
            itemCategory.text = eventItem.category
            Glide.with(itemImage.context).load(eventItem.imageLogo).apply(
               RequestOptions.placeholderOf(
                  R.drawable.placeholder).error(R.drawable.placeholder)).centerCrop()
               .into(itemImage)

            root.setOnClickListener {
               eventItem.id?.let { it1 -> onEventClick(it1) }
            }
         }
      }
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val binding = ItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return ViewHolder(binding)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val item = getItem(position)
      holder.bind(item, onEventClick)
   }

   companion object {
      val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
         override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem == newItem
         }

         override fun areContentsTheSame(
            oldItem: ListEventsItem,
            newItem: ListEventsItem
         ): Boolean {
            return oldItem == newItem
         }
      }
   }
}
