package com.tehronshoh.touristmap.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tehronshoh.touristmap.model.User
import com.tehronshoh.touristmap.remote.RemoteRepository
import com.tehronshoh.touristmap.ui.navigation.currentUser_SS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ActivityViewModel: ViewModel() {
    val userFlow = MutableStateFlow<User?>(null)

    fun getUserById(userId: Int) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = RemoteRepository.getUserById(userId)

            if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                currentUser_SS = response.body()!!.firstOrNull()
                userFlow.emit(response.body()!!.firstOrNull())
            } else
                userFlow.emit(null)

        } catch (e: Exception) {
            userFlow.emit(null)
        }
    }
}