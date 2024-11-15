package com.example.furmate.models

data class User (
    val id: String? = null,
    val username: String,
    val email: String,
) {
    constructor(username: String, email: String) : this(null, username, email)
}