package com.example.furmate.models

import com.google.firebase.firestore.Blob

data class Record (
    val id: String? = null,
    var name: String,
    var petName: String,
    var notes: String,
    var imageURI: String? =null,
    var image:  Blob? = null,
    var userID: String,
    var bookID: String,
)