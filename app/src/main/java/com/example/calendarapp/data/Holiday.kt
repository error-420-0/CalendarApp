package com.example.calendarapp.data

data class Holiday(
    val month: Int,
    val day: Int,
    val name: String,
    val emoji: String,
    val description: String = ""
)