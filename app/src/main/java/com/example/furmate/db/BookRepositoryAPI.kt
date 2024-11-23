package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Book
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference

class BookRepositoryAPI (private val collection: CollectionReference) {
    // Add book to database
    fun addBook(book: Book) {
        Log.d("BookRepositoryAPI", "Attempting to add book to Firestore")
        val document = collection.document()
        val bookWithId = book.copy(id = document.id)

        collection
            .add(bookWithId)
            .addOnSuccessListener { documentReference ->
                Log.d("FireStore,", "Book added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FireStore", "Error adding book", e)
            }
    }

    fun getAllBooks(callback: (List<Book>?, Exception?) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid

        if (uid == null) {
            callback(null, Exception("User is not logged in."))
            return
        }

        collection
            .whereEqualTo("userID", uid)
            .get()
            .addOnSuccessListener { result ->
                val books = result.map { document ->
                    Book(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        userID = document.getString("userID") ?: "",
                        petID = document.getString("petID") ?: ""
                    )
                }
                callback(books, null)
            }
            .addOnFailureListener { e ->
                callback(null, e)
            }
    }
}