package com.tehronshoh.touristmap.remote

import com.tehronshoh.touristmap.model.Filter
import com.tehronshoh.touristmap.model.User
import retrofit2.Response

object RemoteRepository {
    private val clientAPI = RetrofitClient.getRetrofitClient()

    suspend fun getListOfPlaces(filter: Filter, userId: Int) = clientAPI.getListOfPlaces(
        filter = filter.name,
        userId = "{ \"user_id\": \"$userId\"}"
    )
    suspend fun getUserById(userId: Int): Response<List<User>> =
        clientAPI.getUserById("{ \"userId\": \"$userId\"}")

    suspend fun registration(user: User) = clientAPI.registration(user)

    suspend fun logIn(user: User) = clientAPI.logIn(user.login,
        "{ \"password\": \"${user.password}\"}")
}