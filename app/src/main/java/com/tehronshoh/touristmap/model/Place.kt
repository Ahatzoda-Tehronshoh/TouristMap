package com.tehronshoh.touristmap.model

data class Place(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val images: List<String>,
    val category: String = "",
)