package com.tehronshoh.touristmap.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val images: List<String>,
    val category: String = "",
): Parcelable