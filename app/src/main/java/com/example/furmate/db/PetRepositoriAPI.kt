package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Pet
import com.google.firebase.firestore.CollectionReference

class PetRepositoryAPI (private val collection: CollectionReference) {
    // Add pet to database
    fun addPet(pet: Pet) {
        Log.d("PetRepositoryAPI", "Attempting to add pet to Firestore")

        collection
            .add(pet)
            .addOnSuccessListener { documentReference ->
                Log.d("FireStore,", "Pet added with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->
                Log.e("FireStore", "Error adding pet", e)
            }
    }
}