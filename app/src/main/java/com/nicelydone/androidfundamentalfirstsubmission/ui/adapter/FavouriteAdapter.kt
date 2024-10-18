package com.nicelydone.androidfundamentalfirstsubmission.ui.adapter

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nicelydone.androidfundamentalfirstsubmission.connection.response.ListEventsItem
import com.nicelydone.androidfundamentalfirstsubmission.databinding.ItemVerticalBinding
import com.nicelydone.androidfundamentalfirstsubmission.storage.entity.FavEventEntity
import com.nicelydone.androidfundamentalfirstsubmission.ui.activity.detail.DetailActivity

class FavouriteAdapter: ListAdapter<FavEventEntity, FavouriteAdapter.ViewHolder>(DIFF_CALLBACK) {
   class ViewHolder(private val binding: ItemVerticalBinding): RecyclerView.ViewHolder(binding.root) {
      fun bind(eventItem: FavEventEntity){
         binding.apply {
            val title = eventItem.title?.split(" ")?.take(10)?.joinToString(" ")
            itemTitle.text = buildString {
               append(title)
               append("...")
            }
            itemSummary.text = eventItem.summary
            itemSummary.ellipsize = TextUtils.TruncateAt.END
            itemSummary.maxLines = 1
            itemCategory.text = eventItem.category
            Glide.with(itemImage.context).load(eventItem.image).into(itemImage)

            root.setOnClickListener {
               val intent = Intent(root.context, DetailActivity::class.java)
               intent.putExtra("id", eventItem.id)
               root.context.startActivity(intent)
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
      holder.bind(item)
   }

   companion object {
      val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavEventEntity>(){
         override fun areItemsTheSame(oldItem: FavEventEntity, newItem: FavEventEntity): Boolean {
            return oldItem == newItem
         }

         override fun areContentsTheSame(oldItem: FavEventEntity, newItem: FavEventEntity): Boolean {
            return oldItem == newItem
         }
      }
   }
}