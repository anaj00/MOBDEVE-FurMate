package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Task
import com.google.firebase.firestore.CollectionReference

class TaskRepositoryAPI (private val collection: CollectionReference) {
    // Add task to database
    fun addTask(task: Task) {
        Log.d("TaskRepositoryAPI", "Attempting to add task to Firestore")

        collection
            .add(task)
            .addOnSuccessListener { documentReference ->
                Log.d("FireStore,", "Task added with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->
                Log.e("FireStore", "Error adding task", e)
            }
    }
}

