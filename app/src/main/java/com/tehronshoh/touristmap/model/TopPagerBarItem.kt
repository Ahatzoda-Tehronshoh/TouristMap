package com.tehronshoh.touristmap.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopPagerBarItem(
    val route: String,
    var labelId: Int,
    var place: Place? = null,
    var routeSettings: RouteSettings? = null
): Parcelable