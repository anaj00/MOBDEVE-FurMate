package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Task
import com.google.firebase.firestore.CollectionReference
import java.text.SimpleDateFormat
import java.util.Locale

class TaskRepositoryAPI (private val collection: CollectionReference) {
    // Add task to database
    fun addTask(task: Task) {
        Log.d("TaskRepositoryAPI", "Attempting to add task to Firestore")
        val document = collection.document()
        val taskWithId = task.copy(id = document.id)
        document
            .set(taskWithId)
            .addOnSuccessListener {
                Log.d("FireStore,", "Task added with ID: ${document.id}")

            }
            .addOnFailureListener { e ->
                Log.e("FireStore", "Error adding task", e)
            }
    }

    fun getTasksByDate(date: String, callback: (List<Task>?, Exception?) -> Unit) {
        val startOfDay = "$date 00:00"
        val endOfDay = "$date 23:59"

        collection
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThanOrEqualTo("date", endOfDay)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.toObjects(Task::class.java)
                callback(tasks, null) // Pass tasks to callback
            }
            .addOnFailureListener { exception ->
                callback(null, exception) // Pass exception to callback
            }
    }

    fun updateTask(documentId: String, updatedData: Map<String, Any>, callback: (Boolean, Exception?) -> Unit) {
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
                    callback(false, Exception("No document found with id = $documentId"))
                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception) // Query failed
            }
    }


    fun deleteTask(documentId: String, callback: (Boolean, Exception?) -> Unit) {
        collection.whereEqualTo("id", documentId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
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
                    callback(false, Exception("No document found with id = $documentId"))
                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception) // Query failed
            }
    }
}

