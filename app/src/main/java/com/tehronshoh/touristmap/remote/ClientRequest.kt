package com.tehronshoh.touristmap.remote

import retrofit2.Response
import retrofit2.http.GET

interface ClientRequest {
    @GET("/tehron")
    suspend fun test(): Response<String>
}