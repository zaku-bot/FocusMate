package com.example.proj5

data class ApiResponse(
    val recommendations: List<Recommendation>
)

data class Recommendation(
    val event: Event,
    val weatherData: WeatherData,
    val trafficData: TrafficData
)

data class Event(
    val eventName: String,
    val eventType: String,
    val eventDate: String,
    val eventTime: String,
    val eventLocationName: String,
    val eventLocation: EventLocation,
    val additionalDetails: AdditionalDetails
)

data class EventLocation(
    val latitude: String,
    val longitude: String
)

data class AdditionalDetails(
    val bookingLink: String,
    val cost: String
)

data class WeatherData(
    val weather: String,
    val temperature: Double,
    val humidity: Int
)

data class TrafficData(
    val distance: Double,
    val duration: Int,
    val durationInTraffic: Int,
    val trafficDetails: String
)