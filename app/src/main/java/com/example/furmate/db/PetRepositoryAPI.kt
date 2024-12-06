package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Pet
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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


    fun getAllPets(callback: (List<Pet>?, Exception?) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid
        collection
            .whereEqualTo("userID", uid)
            .get()
            .addOnSuccessListener { result ->
                val pets = result.map { document ->
                    Pet(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        breed = document.getString("breed") ?: "",
                        sex = document.getString("sex") ?: "",
                        birthday = document.getString("birthday") ?: "",
                        weight = document.getString("weight") ?: "",
                        notes = document.getString("notes"),
                        userID = document.getString("userID") ?: "",
                        image = document.getBlob("image")
                    )
                }
                callback(pets, null)
            }
            .addOnFailureListener { e ->
                callback(null, e)
            }
    }

    // Modify the method in PetRepositoryAPI
    suspend fun getPetIDByName(petCollection: CollectionReference, petName: String): String? {
        return suspendCoroutine { continuation ->
            petCollection.whereEqualTo("name", petName).get()
                .addOnSuccessListener { querySnapshot ->
                    val petID = querySnapshot.documents.firstOrNull()?.id
                    continuation.resume(petID)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
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
                        breed = it.getString("breed") ?: "Unknown",
                        sex = it.getString("sex") ?: "Unknown",
                        birthday = it.getString("birthday") ?: "Unkown",
                        weight = it.getString("weight") ?: "Unknown",
                        notes = it.getString("notes"),
                        userID = it.getString("userID") ?: "Unknown",
                        image = document.getBlob("image")
                    )
                }
                callback(pet, null)
            }
            .addOnFailureListener { e ->
                callback(null, e)
            }
    }

    fun updatePetData(petID: String, petData: Map<String, Any>, completion: (Boolean, String?) -> Unit) {
        // Reference to the document with the given petID
        val petRef = collection.document(petID)

        // Update the pet data in Firestore
        petRef.set(petData)
            .addOnSuccessListener {
                // On successful update, invoke the completion handler with success
                completion(true, null)
            }
            .addOnFailureListener { e ->
                // On failure, invoke the completion handler with error message
                completion(false, e.message)
            }
    }

    fun deletePet(petID: String, completion: (Boolean, String?) -> Unit) {
        // Reference to the document with the given petID
        val petRef = collection.document(petID)

        // Delete the pet from Firestore
        petRef.delete()
            .addOnSuccessListener {
                // On successful deletion, invoke the completion handler with success
                completion(true, null)
            }
            .addOnFailureListener { e ->
                // On failure, invoke the completion handler with error message
                completion(false, e.message)
            }
    }
}