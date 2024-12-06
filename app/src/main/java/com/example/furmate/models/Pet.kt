package com.example.furmate.models

import com.google.firebase.firestore.Blob

data class Pet (
    val id: String?=null,
    val name: String,
    val image: Blob?= null,
    val breed: String,
    val sex: String,
    val birthday: String,
    val weight: String?= "Unknown",
    val notes: String?= null,
    val userID: String
)
