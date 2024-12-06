package com.example.furmate.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class FormScheduleViewModel : ViewModel() {
    // A LiveData for the dropdown values
    private val _petOptions = MutableLiveData<List<Pair<String, String?>>>()
    val petOptions: LiveData<List<Pair<String, String?>>> get() = _petOptions
    private val _selectedPetID = MutableLiveData<String>()
    val selectedPetID: LiveData<String> get() = _selectedPetID

    // Method to update the selected Pet ID
    fun setSelectedPetID(petID: String) {
        _selectedPetID.value = petID
        Log.d("FormScheduleViewModel", "Selected pet ID set: $petID")
    }

    // Initialize the pet options - this could come from a repository or hardcoded for now
    fun setPetOptions(petOptions: List<Pair<String, String?>>) {
        _petOptions.value = petOptions
        Log.d("FormScheduleViewModel", "Pet options set: $petOptions")
    }
}

