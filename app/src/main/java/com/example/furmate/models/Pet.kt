package com.example.furmate.models

data class Pet (
    val id: Int?=null,
    val name: String,
    val animal: String,
    val birthday: String,
    val notes: String?= null
)
