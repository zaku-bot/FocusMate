package com.example.proj5

import java.io.Serializable

data class EventModel(
    val id: String,
    val summary: String,
    val startDate: String
) : Serializable



