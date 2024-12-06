package com.example.furmate.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class FormScheduleViewModel : ViewModel() {
    // A LiveData for the dropdown values
    private val _petOptions = MutableLiveData<List<String>>()
    private val _bookOptions = MutableLiveData<List<String>>()
    val petOptions: LiveData<List<String>> get() = _petOptions
    val bookOptions: LiveData<List<String>> get() = _bookOptions

    // Initialize the pet options - this could come from a repository or hardcoded for now
    fun loadPetOptions() {
        // Simulate loading pet data (this can be fetched from Firestore or a repository)
        val pets = listOf("Dog", "Cat", "Bird") // Example pet options
        _petOptions.value = pets
    }

    // Function to gather task data (if needed for saving)
    fun getTaskData(formEntries: List<String>): Map<String, String> {
        val taskData = mutableMapOf<String, String>()

        // Example task data extraction
        if (formEntries.isNotEmpty()) {
            taskData["name"] = formEntries[0]
            taskData["date"] = formEntries[1]
            taskData["petName"] = formEntries[2]
            taskData["notes"] = formEntries[3]
        }

        return taskData
    }

    fun setPetOptions(petOptions: List<String>) {
        _petOptions.value = petOptions
    }

    fun setBookOptions(bookOptions: List<String>) {
        _bookOptions.value = bookOptions
    }
}

