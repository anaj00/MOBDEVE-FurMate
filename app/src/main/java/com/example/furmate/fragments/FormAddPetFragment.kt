package com.example.furmate.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.db.PetRepositoryAPI
import com.example.furmate.models.Pet
import com.example.furmate.models.Task
import com.example.furmate.utils.MarginItemDecoration
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.any

class FormAddPetFragment : Fragment() {
    private var petName: String? = null
    private var petImage: String? = null
    private var petBreed: String? = null
    private var petSex: String? = null
    private var petBirthday: String? = null
    private var petWeight: String? = null
    private var petNotes: String? = null

    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView

    // Firestore Collections
    private lateinit var petCollection: CollectionReference
    // APIs
    private lateinit var petRepositoryAPI: PetRepositoryAPI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.form_createpet, container, false)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.pet_form_wrapper)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(MarginItemDecoration(16))

        // Initialize the Firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Initilize the Firestore collection
        petCollection = firestore.collection("Pet")
        petRepositoryAPI = PetRepositoryAPI(petCollection)

        val composableInputs = {
            listOf("Name", "Breed", "Sex", "Birthday", "Weight", "Notes")
        }

        val inputValues = {
            listOf(
                petName ?: "",
                petBreed ?: "",
                petSex ?: "",
                petBirthday ?: "",
                petWeight ?: "",
                petNotes ?: ""
            )
        }

        // Log the data being passed to the adapter
        Log.d("FormAddPetFragment", "Composable Inputs: ${composableInputs()}")
        Log.d("FormAddPetFragment", "Input Values: ${inputValues()}")

        val adapter = ComposableInputAdapter(composableInputs(), inputValues(), requireContext())
        recyclerView.adapter = adapter

        submitButton = rootView.findViewById<Button>(R.id.addpet_submit_btn)
        submitButton.setOnClickListener {
            val petData = mutableMapOf<String, String>()

            recyclerView.post {
                for (child in recyclerView.children) {
                    val holder = recyclerView.getChildViewHolder(child)
                    val key = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div)?.hint.toString()
                    val value = holder.itemView.findViewById<TextInputEditText>(R.id.input_field)?.text.toString()

                    Log.d("FormAddPetFragment", "Key: $key, Value: $value")
                    petData[key] = value
                }

                Log.d("FormAddPetFragment", "Final Pet Data: $petData")

                val name = petData["Name"]
                if (name.isNullOrEmpty()) {
                    Log.e("FormAddPetFragment", "Name is missing or empty!")
                    Toast.makeText(requireContext(), "Name is required.", Toast.LENGTH_SHORT).show()
                    return@post
                }

                val uid = Firebase.auth.currentUser?.uid ?: ""
                val pet = Pet(
                    name = name, // Safe access
                    animal = petData["Breed"] ?: "Unknown", // Provide defaults for optional fields
                    birthday = petData["Birthday"] ?: "Unknown",
                    weight = petData["Weight"] ?: "Unknown",
                    notes = petData["Notes"] ?: "",
                    userID = uid
                )

                petRepositoryAPI.addPet(pet)
                Log.d("FormAddPetFragment", "Pet added successfully: $pet")
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        return rootView
    }
}
