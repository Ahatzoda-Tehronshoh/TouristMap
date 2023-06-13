package com.tehronshoh.touristmap.ui.tool

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.utils.MapKitUtil
import com.yandex.mapkit.geometry.Point


val LocalUserCurrentPosition = compositionLocalOf<Point?> { null }

val LocalFilteredPlaces = compositionLocalOf<NetworkResult<List<Place>>> {
    error("No Composition Found!")
}
val LocalStaticPlaces = staticCompositionLocalOf<List<Place>> { listOf() }
