package com.example.furmate.models

data class Pet (
    val id: Int? = null,
    val name: String,
    val animal: String,
    val birthday: String,
    val notes: String?= null
) {
    constructor(
        name: String,
        animal: String,
        birthday: String,
        notes: String ?= null
    ) : this(
        null,
        name,
        animal,
        birthday,
        notes
    )
}