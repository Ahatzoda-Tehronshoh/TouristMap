package com.tehronshoh.touristmap.ui

import androidx.compose.runtime.compositionLocalOf
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.Place
import com.yandex.mapkit.geometry.Point


val LocalUserCurrentPosition = compositionLocalOf<Point?> { null }

val LocalFilteredPlaces = compositionLocalOf<NetworkResult<List<Place>>> {
    error("No Composition Found!")
}