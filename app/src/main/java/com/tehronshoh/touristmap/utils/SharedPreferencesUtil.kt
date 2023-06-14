package com.tehronshoh.touristmap.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.mutableStateOf

class SharedPreferencesUtil private constructor(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("tajik_guide", MODE_PRIVATE)

    var userId = mutableStateOf(-1)

    init {
        if(sharedPreferences.contains(USER_ID_KEY))
            userId.value = sharedPreferences.getInt(USER_ID_KEY, userId.value)
        else
            sharedPreferences
                .edit()
                .putInt(USER_ID_KEY, userId.value)
                .apply()
    }

    fun deleteUserId() = updateUserId(-1)

    fun updateUserId(id: Int) {
        if (userId.value != id) {
            sharedPreferences.edit().putInt(USER_ID_KEY, id).apply()
            userId.value = sharedPreferences.getInt(USER_ID_KEY, userId.value)
        }
    }

    companion object {
        const val USER_ID_KEY = "registered_user_id_key"
        var INSTANCE: SharedPreferencesUtil? = null

        fun getInstance(context: Context): SharedPreferencesUtil {
            return synchronized(this) {
                INSTANCE ?: SharedPreferencesUtil(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}