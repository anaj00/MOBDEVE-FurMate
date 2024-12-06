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
) {
    // No-argument constructor required for Firestore deserialization
    constructor() : this(
        id = null,
        name = "",
        petName = "",
        notes = "",
        imageURI = null,
        image = null,
        userID = ""
    )
}