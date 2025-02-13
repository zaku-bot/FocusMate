package com.example.proj5

class RecommendationRepository {

    fun getRecommendations(
        userId: String, latitude: Double, longitude: Double,
        localTime: String, dateFilter: String, distanceFilter: String,
        callback: (ApiResponse?) -> Unit
    ) {
        RetrofitInstance.api.getRecommendations(userId, latitude, longitude, localTime, dateFilter, distanceFilter).enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}