package com.example.furmate.models

// Data class representing each schedule
data class Task(
    val id: String? = null,
    var name: String = "",
    var date: String ?= null,
    var petName: String = "",
    var notes: String?= null,
    var userID: String?= null,
    var bookID: String?= null
)
