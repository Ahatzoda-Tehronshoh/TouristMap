package com.tehronshoh.touristmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.User
import com.tehronshoh.touristmap.remote.RemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val remoteRepository = RemoteRepository()

    private var _registrationResult = MutableLiveData<NetworkResult<String>>()
    val registrationResult: LiveData<NetworkResult<String>> = _registrationResult

    fun registration(user: User) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _registrationResult.postValue(NetworkResult.Loading())

            val result = remoteRepository.registration(user)

            _registrationResult.postValue(
                if (result.isSuccessful && result.code() == 200 && result.body() != null)
                    NetworkResult.Success(result.body()!!)
                else
                    NetworkResult.Error(result.message() + result.errorBody() + result.code())
            )
        } catch (e: Exception) {
            _registrationResult.postValue(NetworkResult.Error(e.message ?: "Try again!"))
        }
    }
}