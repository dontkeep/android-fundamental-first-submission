package com.nicelydone.androidfundamentalfirstsubmission.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.ListEventsItem
import com.nicelydone.androidfundamentalfirstsubmission.databinding.ItemHorizontalBinding
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.detail.DetailActivity

class UpcomingAdapter: ListAdapter<ListEventsItem, UpcomingAdapter.ViewHolder>(DIFF_CALLBACK) {
   class ViewHolder(private val binding: ItemHorizontalBinding) : RecyclerView.ViewHolder(binding.root){
      fun bind(eventItem: ListEventsItem){
         binding.apply {
            itemTitle.text = eventItem.name
            Glide.with(itemImage.context).load(eventItem.imageLogo).into(itemImage)

            root.setOnClickListener {
               val intent = Intent(root.context, DetailActivity::class.java)
               intent.putExtra("id", eventItem.id)
               root.context.startActivity(intent)
            }
         }
      }
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val binding = ItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return ViewHolder(binding)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val item = getItem(position)
      holder.bind(item)
   }

   companion object {
      val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>(){
         override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem == newItem
         }

         override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem == newItem
         }
      }
   }
}