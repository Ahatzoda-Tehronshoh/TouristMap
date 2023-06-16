package com.tehronshoh.touristmap.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopPagerBarItem(
    val route: String,
    var labelId: Int
): Parcelable