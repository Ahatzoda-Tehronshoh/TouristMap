package com.tehronshoh.touristmap.viewmodel

import androidx.lifecycle.ViewModel
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.User
import com.tehronshoh.touristmap.remote.RemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SignInViewModel : ViewModel() {
    fun signIn(user: User): Flow<NetworkResult<List<User>>> = flow {
        try {
            emit(NetworkResult.Loading())

            val result = RemoteRepository.logIn(user)

            if (result.isSuccessful && result.code() == 200 && result.body() != null)
                emit(NetworkResult.Success(data = result.body()!!))
            else emit(
                NetworkResult.Error<List<User>>(
                    result.message() /*+ result.errorBody()*/ + result.code()
                )
            )
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Try again!"))
        }
    }.flowOn(Dispatchers.IO)
}