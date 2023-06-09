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

class SignInViewModel : ViewModel() {
    private val remoteRepository = RemoteRepository()

    private var _logInResult = MutableLiveData<NetworkResult<List<User>>>()
    val logInResult: LiveData<NetworkResult<List<User>>>
        get() = _logInResult

    fun signIn(user: User) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _logInResult.postValue(NetworkResult.Loading<List<User>>())

            val result = remoteRepository.logIn(user)

            if (result.isSuccessful && result.code() == 200 && result.body() != null)
                _logInResult.postValue(NetworkResult.Success<List<User>>(data = result.body()!! as List<User>))
            else _logInResult.postValue(
                NetworkResult.Error<List<User>>(
                    result.message() /*+ result.errorBody()*/ + result.code()
                )
            )
        } catch (e: Exception) {
            _logInResult.postValue(NetworkResult.Error(e.message ?: "Try again!"))
        }
    }
}