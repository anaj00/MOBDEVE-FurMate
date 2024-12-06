package com.example.furmate

import PetsFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.db.PetRepositoryAPI
import com.example.furmate.models.Pet
import com.example.furmate.utils.MarginItemDecoration
import com.example.furmate.viewmodels.PetViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class PetProfileHomeFragment : Fragment() {
    private var petName: String? = null
    private var petBreed: String? = null
    private var petSex: String? = null
    private var petBirthday: String? = null
    private var petWeight: String? = null
    private var petNotes: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var editButton: ImageButton
    private lateinit var deleteButton: Button
    private lateinit var adapter: ComposableInputAdapter // Adapter instance for toggling inputs

    // Firestore Collections
    private lateinit var petRepositoryAPI: PetRepositoryAPI
    private lateinit var petCollection: CollectionReference

    private lateinit var inputValues: MutableList<String> // Store input values
    private lateinit var composableInputs: List<String> // Store labels for input field
    val petViewModel: PetViewModel by activityViewModels()
    companion object {
        private const val ARG_PET_ID = "pet_id"
        fun newInstance(petID: String): PetProfileHomeFragment {
            val fragment = PetProfileHomeFragment()
            val args = Bundle()
            args.putString(ARG_PET_ID, petID)
            fragment.arguments = args
            Log.d("PetProfileHomeFragment", "${fragment.arguments} in pet profile home fragment")
            return fragment
        }
    }

    private var areInputsEnabled = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_pet_profile_main, container, false)

        // Initialize Firestore
        val firestore = FirebaseFirestore.getInstance()
        petCollection = firestore.collection("Pet")
        petRepositoryAPI = PetRepositoryAPI(petCollection)

        recyclerView = rootView.findViewById(R.id.pet_edit_form_wrapper)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(MarginItemDecoration(16))

        // Function calls to load pet details using the API
        loadPetDetails()
        Log.d("PetProfileHomeFragment", "Pet details $petName, $petBreed, $petSex, $petBirthday, $petWeight, $petNotes")

        editButton = rootView.findViewById(R.id.edit_btn)
        editButton.setOnClickListener {
            areInputsEnabled = !areInputsEnabled // Toggle state
            adapter.setInputEnabled(areInputsEnabled)

            // Update the button's appearance
            val color = if (areInputsEnabled) {
                ContextCompat.getColor(requireContext(), R.color.success)
            } else {
                ContextCompat.getColor(requireContext(), R.color.primary)
            }
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.circle)
            drawable?.mutate()?.setTint(color) // Apply tint to change color dynamically
            editButton.background = drawable
            editButton.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (areInputsEnabled) R.drawable.success else R.drawable.edit
                )
            )
            if (!areInputsEnabled) {
                // Extract values from the input fields in the RecyclerView
                for (i in 0 until recyclerView.childCount) {
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
                    val inputField = viewHolder?.itemView?.findViewById<EditText>(R.id.input_field)

                    // Update the inputValues list with the text from each field
                    inputField?.let {
                        inputValues[i] = it.text.toString()
                    }
                }

                // Now update the Firestore database with the new values
                val petID = arguments?.getString(ARG_PET_ID)
                if (petID != null) {
                    val petData = mutableMapOf<String, Any>()
                    val userID = Firebase.auth.currentUser?.uid ?: ""
                    petData["name"] = inputValues[0]
                    petData["breed"] = inputValues[1]
                    petData["sex"] = inputValues[2]
                    petData["birthday"] = inputValues[3]
                    petData["weight"] = inputValues[4]
                    petData["notes"] = inputValues[5]
                    petData["userID"] = userID
                    petData["id"] = petID

                    // Call the API to update the pet data in Firestore
                    petRepositoryAPI.updatePetData(petID, petData) { success, error ->
                        if (success) {
                            Log.d("PetProfileHomeFragment", "Pet data updated successfully.")
                        } else {
                            Log.e("PetProfileHomeFragment", "Failed to update pet data: $error")
                        }
                    }
                    val updatedPet = Pet(
                        name = inputValues[0],   // Pet's name
                        breed = inputValues[1],  // Pet's breed
                        sex = inputValues[2],    // Pet's sex
                        birthday = inputValues[3], // Pet's birthday
                        weight = inputValues[4], // Pet's weight
                        notes = inputValues[5],  // Pet's notes
                        userID = userID,         // User ID
                        id = petID               // Pet ID
                    )
                    petViewModel.updatePet(updatedPet)
                    // Refresh the RecyclerView by notifying the adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }

        deleteButton = rootView.findViewById(R.id.submit_btn)
        deleteButton.setOnClickListener {
            val petID = arguments?.getString(ARG_PET_ID)
            if (petID != null) {
                petRepositoryAPI.deletePet(petID) { success, error ->
                    if (success) {
                        Log.d("PetProfileHomeFragment", "Pet deleted successfully.")
                    } else {
                        Log.e("PetProfileHomeFragment", "Failed to delete pet: $error")
                    }
                }
            }
            adapter.notifyDataSetChanged()
            val petListFragment = PetsFragment()

            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, petListFragment) // Replace current fragment with PetListFragment
            fragmentTransaction.commit()
        }

        return rootView
    }

    private fun loadPetDetails() {
        val petID = arguments?.getString(ARG_PET_ID)
        if (petID != null) {
            petRepositoryAPI.getPetByID(petCollection, petID) { pet, error ->
                if (error != null) {
                    Log.e("PetProfileHomeFragment", "Error fetching pet: $error")
                    return@getPetByID
                }

                if (pet != null) {
                    // Update pet details
                    petName = pet.name
                    petBreed = pet.breed
                    petSex = pet.sex
                    petBirthday = pet.birthday
                    petWeight = pet.weight
                    petNotes = pet.notes

                    // Update the input values list
                    inputValues = mutableListOf(
                        petName ?: "",
                        petBreed ?: "",
                        petSex ?: "",
                        petBirthday ?: "",
                        petWeight ?: "",
                        petNotes ?: ""
                    )

                    // Labels for input fields
                    composableInputs = listOf("Name", "Breed", "Sex", "Birthday", "Weight", "Notes")

                    // Set the adapter with updated data
                    adapter = ComposableInputAdapter(composableInputs, inputValues, requireContext())
                    recyclerView.adapter = adapter // Set the adapter to RecyclerView

                    // Notify the adapter to refresh the data
                    adapter.notifyDataSetChanged()

                    // Update toolbar title with pet's name
                    (requireActivity() as? HomeActivity)?.changeToolbarTitle("$petName's Records")
                }
            }
        }
    }
}
