package com.tehronshoh.touristmap.remote

import com.tehronshoh.touristmap.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ClientRequest {
    @POST("/auth/register")
    suspend fun registration(@Body user: User): Response<String>

    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    suspend fun logIn(
        @Query("nickName") nickName: String,
        @Body password: String
    ): Response<List<User>>
}