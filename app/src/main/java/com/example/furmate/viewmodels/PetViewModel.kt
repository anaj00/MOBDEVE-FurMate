package com.example.furmate.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.furmate.models.Pet

class PetViewModel : ViewModel() {
    private val _petData = MutableLiveData<Pet>() // Use Pet class to represent the data
    val petData: LiveData<Pet> get() = _petData

    fun updatePet(pet: Pet) {
        _petData.value = pet // Updates the LiveData when pet data changes
    }

    fun updatePetName(newName: String) {
        // Ensure that the pet data is not null, then create a new Pet object with the updated name
        _petData.value?.let {
            val updatedPet = it.copy(name = newName) // Create a new Pet object with the updated name
            _petData.value = updatedPet // Update LiveData with the new Pet object
        }
    }
}
