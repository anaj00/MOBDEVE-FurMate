package com.example.furmate.models

data class Pet (
    val id: Int? = null,
    val name: String,
    val breed: String,
    val age: Int,
    val weight: Double,
    val notes: String?= null
) {
    constructor(
        name: String,
        breed: String,
        age: Int,
        weight: Double,
    ) : this(
        null,
        name,
        breed,
        age,
        weight,
        null
    )
}