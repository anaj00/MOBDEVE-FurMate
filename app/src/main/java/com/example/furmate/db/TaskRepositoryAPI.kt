package com.example.furmate.db

import android.util.Log
import com.example.furmate.models.Task
import com.google.firebase.firestore.CollectionReference

class TaskRepositoryAPI (private val collection: CollectionReference) {
    // Add task to database
    fun addTask(task: Task) {
        Log.d("TaskRepositoryAPI", "Attempting to add task to Firestore")
        val document = collection.document()
        val taskWithId = task.copy(id = document.id)
        collection
            .add(taskWithId)
            .addOnSuccessListener { documentReference ->
                Log.d("FireStore,", "Task added with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->
                Log.e("FireStore", "Error adding task", e)
            }
    }

    fun getTasksByDate(date: String, callback: (List<Task>?, Exception?) -> Unit) {
        collection
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.toObjects(Task::class.java)
                callback(tasks, null) // Pass tasks to callback
            }
            .addOnFailureListener { exception ->
                callback(null, exception) // Pass exception to callback
            }
    }
}

