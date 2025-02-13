package com.example.proj5

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/api/recommend")
    fun getRecommendations(
        @Query("userId") userId: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localTime") localTime: String,
        @Query("dateFilter") dateFilter: String,
        @Query("distanceFilter") distanceFilter: String
    ): Call<ApiResponse>
}