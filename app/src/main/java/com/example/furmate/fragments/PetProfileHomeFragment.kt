package com.example.furmate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.db.PetRepositoryAPI
import com.example.furmate.utils.MarginItemDecoration
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
    private lateinit var adapter: ComposableInputAdapter // Adapter instance for toggling inputs

    // Firestore Collections
    private lateinit var petRepositoryAPI: PetRepositoryAPI
    private lateinit var petCollection: CollectionReference
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

                    // Once pet data is fetched, update input values
                    val inputValues = listOf(
                        petName ?: "",
                        petBreed ?: "",
                        petSex ?: "",
                        petBirthday ?: "",
                        petWeight ?: "",
                        petNotes ?: ""
                    )

                    // Recreate the adapter with updated data
                    val composableInputs = listOf("Name", "Breed", "Sex", "Birthday", "Weight", "Notes")
                    adapter = ComposableInputAdapter(composableInputs, inputValues, requireContext())
                    recyclerView.adapter = adapter // Reset the adapter

                    // Notify the adapter to refresh the data
                    adapter.notifyDataSetChanged()

                    // Update toolbar title with pet's name
                    (requireActivity() as? HomeActivity)?.changeToolbarTitle("$petName's Records")
                }
            }
        }
    }
}
