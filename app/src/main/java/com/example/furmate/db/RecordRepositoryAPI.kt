package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Record
import com.google.firebase.firestore.CollectionReference

class RecordRepositoryAPI(private val collection: CollectionReference) {
    // Add record to database
    fun addRecord(record: Record) {
        Log.d("RecordRepositoryAPI", "Attempting to add record to Firestore")
        val document = collection.document()
        val recordWithId = record.copy(id = document.id)
        collection
            .add(recordWithId)
            .addOnSuccessListener { documentReference ->
                // Log success message
                Log.d("FireStore,", "Record added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // Log error message
                Log.e("FireStore", "Error adding record", e)
            }
    }

    fun getRecordsByBookID(bookID: String, callback: (List<Record>?, Exception?) -> Unit) {
        collection
            .whereEqualTo("bookID", bookID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val records = querySnapshot.toObjects(Record::class.java)
                callback(records, null) // Pass records to callback
            }
            .addOnFailureListener { exception ->
                callback(null, exception) // Pass exception to callback
            }
    }

    fun updateRecord(documentId: String, updatedData: Map<String, Any>, callback: (Boolean, Exception?) -> Unit) {
        collection.whereEqualTo("id", documentId) //
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        // Updates each matching document
                        collection.document(document.id)
                            .update(updatedData)
                            .addOnSuccessListener {
                                callback(true, null) // Update succeeded
                            }
                            .addOnFailureListener { exception ->
                                callback(false, exception) // Update failed
                            }
                    }
                } else {
                    // Log error message
                    Log.e("RecordRepositoryAPI", "No document found with id = $documentId")
                }
            }
    }

    fun deleteRecord(documentId: String, callback: (Boolean, Exception?) -> Unit) {
        collection.whereEqualTo("id", documentId) //
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        // Deletes each matching document
                        collection.document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                callback(true, null) // Delete succeeded
                            }
                            .addOnFailureListener { exception ->
                                callback(false, exception) // Delete failed
                            }
                    }
                } else {
                    // Log error message
                    Log.d("RecordRepositoryAPI", "No document found with id = $documentId")
                }
            }
    }
}