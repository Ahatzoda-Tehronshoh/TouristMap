package com.tehronshoh.touristmap.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val category: String = "",
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    val images: List<String>,
): Parcelable