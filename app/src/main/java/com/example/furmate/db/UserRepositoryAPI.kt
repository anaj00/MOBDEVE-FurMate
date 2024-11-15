package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.User
import com.google.firebase.firestore.CollectionReference

class UserRepositoryAPI(private val collection: CollectionReference) {
    // Add user to database
    fun addUser(user: User) {
        collection
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("FireStore,", "User added with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->
                Log.e("FireStore", "Error adding user", e)
            }
    }
}