package com.tehronshoh.touristmap.remote

import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ClientRequest {
    @Headers("Content-Type: application/json")
    @POST("/place/list")
    suspend fun getListOfPlaces(
        @Query("filter") filter: String,
        @Body userId: String
    ): Response<List<Place>>

    @Headers("Content-Type: application/json")
    @POST("/auth/user")
    suspend fun getUserById(@Body userId: String): Response<List<User>>

    @POST("/auth/register")
    suspend fun registration(@Body user: User): Response<Int>

    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    suspend fun logIn(
        @Query("nickName") nickName: String,
        @Body password: String
    ): Response<List<User>>
}