package com.tehronshoh.touristmap.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int = 0,
    @SerializedName("nick_name")
    val nickName: String,
    val login: String,
    val password: String,
    val country: String
): Parcelable