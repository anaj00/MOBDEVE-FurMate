package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Pet
import com.google.firebase.firestore.CollectionReference

class PetRepositoryAPI (private val collection: CollectionReference) {
    // Add pet to database
    fun addPet(pet: Pet) {
        Log.d("PetRepositoryAPI", "Attempting to add pet to Firestore")
        val document = collection.document()
        val petWithId = pet.copy(id = document.id)
        document
            .set(petWithId)
            .addOnSuccessListener {
                Log.d("FireStore,", "Pet added with ID: ${petWithId.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FireStore", "Error adding pet", e)
            }
    }

    // Query all schedules for a specific pet
    fun getPetSchedules(petId: Int, taskCollection: CollectionReference) {
        Log.d("PetRepositoryAPI", "Attempting to get all schedules of pet from Firestore")

        taskCollection
            .whereEqualTo("pet", petId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("FireStore", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FireStore", "Error getting documents: ", exception)
            }
    }

    fun getAllPets(callback: (List<Pet>?, Exception?) -> Unit) {
        collection
            .get()
            .addOnSuccessListener { result ->
                val pets = result.map { document ->
                    Pet(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        animal = document.getString("animal") ?: "",
                        birthday = document.getString("birthday") ?: "",
                        notes = document.getString("notes")
                    )
                }
                callback(pets, null)
            }
            .addOnFailureListener { e ->
                callback(null, e)
            }
    }

    fun getPetByID(collection: CollectionReference, petId: String, callback: (Pet?, Exception?) -> Unit) {
        collection
            .whereEqualTo("id", petId)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                val pet = document?.let {
                    Pet(
                        id = it.getString("id"),
                        name = it.getString("name") ?: "Unknown",
                        animal = it.getString("animal") ?: "Unknown",
                        birthday = it.getString("birthday") ?: "Unkown",
                        notes = it.getString("notes")
                    )
                }
                callback(pet, null)
            }
            .addOnFailureListener { e ->
                callback(null, e)
            }
    }

}