package com.nicelydone.androidfundamentalfirstsubmission.storage.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class FavEventEntity (
   @PrimaryKey
   var id: Int?,

   var title: String? = null,

   var summary: String? = null,

   var owner: String? = null,

   var category: String? = null,

   var city: String? = null,

   var quota: Int? = null,

   var description: String? = null,

   var date: String? = null,

   var image: String? = null,

   var link: String? = null,

   var registerant: Int? = null,

   var isFavorite: Boolean
): Parcelable
