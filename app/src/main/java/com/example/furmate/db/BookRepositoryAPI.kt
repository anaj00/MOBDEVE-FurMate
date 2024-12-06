package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Book
import com.example.furmate.models.Task
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

    fun getAllBooks(petID: String, callback: (List<Book>?, Exception?) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid

        if (uid == null) {
            callback(null, Exception("User is not logged in."))
            return
        }
        Log.d("FormAddBookFragment", "petID in API call: $petID")
        collection
            .whereEqualTo("userID", uid)
            .whereEqualTo("petID", petID)
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
                Log.d("BookRepositoryAPI", "Books: $books")
                callback(books, null)
            }
            .addOnFailureListener { e ->
                callback(null, e)
            }
    }

    fun getAllRecords(bookID: String, collection: CollectionReference, callback: (List<com.example.furmate.models.Record>?, Exception?) -> Unit) {
        val uid = Firebase.auth.currentUser?.uid

        if (uid == null) {
            callback(null, Exception("User is not logged in."))
            return
        }
        Log.d("FormAddBookFragment", "bookID in API call: $bookID")

        collection
            .whereEqualTo("userID", uid)
            .whereEqualTo("bookID", bookID)
            .get()
            .addOnSuccessListener { result ->
                val records = result.map { document ->
                    com.example.furmate.models.Record(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        petName = document.getString("petName") ?: "",
                        notes = document.getString("notes") ?: "",
                        userID = document.getString("userID") ?: "",
                    )
                }
                Log.d("BookRepositoryAPI", "Books: $records")
                callback(records, null)
            }
    }
}