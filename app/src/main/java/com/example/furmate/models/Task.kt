package com.example.furmate.models

import com.google.firebase.firestore.Blob

// Data class representing each schedule
data class Task(
    val id: String? = null,
    var name: String = "",
    var date: String? = null,
    var petName: String = "",
    var notes: String? = null,
    var userID: String? = null,
    var imageURI: String? =null,
    var image: Blob? = null
)
