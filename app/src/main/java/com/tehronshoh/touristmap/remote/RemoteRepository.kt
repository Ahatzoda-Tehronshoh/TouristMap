package com.tehronshoh.touristmap.remote

import com.tehronshoh.touristmap.model.User

class RemoteRepository {
    private val clientAPI = RetrofitClient.getRetrofitClient()

    suspend fun registration(user: User) = clientAPI.registration(user)

    suspend fun logIn(user: User) = clientAPI.logIn(user.login,
        "{ \"password\": \"${user.password}\"}")

}