package com.example.furmate.models

// Data class representing each schedule
data class Task(
    val id: Int? = null,
    var name: String = "",
    var time: String = "",
    var location: String = " ",
    var petName: String = "",
    var notes: String?= null
) {
    constructor(
        name: String,
        time: String,
        location: String,
        petName: String,
        notes: String? = null
    ) : this(
        null,
        name,
        time,
        location,
        petName,
        notes
    )
}