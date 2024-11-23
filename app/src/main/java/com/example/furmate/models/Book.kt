package com.example.furmate.models

data class Book(
    val id: String?=null,
    val name: String,
    val notes: String?=null,
    val userID: String,
    val petID: String,
)
