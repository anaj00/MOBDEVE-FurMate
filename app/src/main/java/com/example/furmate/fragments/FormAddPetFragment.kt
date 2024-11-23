package com.example.furmate.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
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

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>

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
            listOf("Name", "Profile Picture", "Breed", "Sex", "Birthday", "Weight", "Notes")
        }

        val inputValues = {
            listOf(
                petName ?: "",
                petImage ?: "",
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

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    Log.d("FormAddPetFragment", "Selected image URI: $selectedImageUri")
                    petImage = selectedImageUri.toString()
                    recyclerView.adapter?.notifyItemChanged(1) // Update UI
                }
            }
        }
        submitButton = rootView.findViewById<Button>(R.id.addpet_submit_btn)
        submitButton.setOnClickListener {
            val petData = mutableMapOf<String, Any>()

            recyclerView.post {
                for (child in recyclerView.children) {
                    val holder = recyclerView.getChildViewHolder(child)
                    val key = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div)?.hint.toString()
                    val value = holder.itemView.findViewById<TextInputEditText>(R.id.input_field)?.text.toString()

                    Log.d("FormAddPetFragment", "Key: $key, Value: $value")

                    if (key == "Profile Picture") {
                        if (value.isNotEmpty()) {
                            try {
                                val uri = Uri.parse(value) // Parse the URI string
                                val blob = uriToBlob(uri, requireContext()) // Convert to Blob
                                if (blob != null) {
                                    Log.d("FormAddPetFragment", "Profile Picture is now a blob")
                                    petData[key] = blob
                                } else {
                                    Log.e("FormAddPetFragment", "Failed to convert URI to Blob for Profile Picture")
                                }
                            } catch (e: Exception) {
                                Log.e("FormAddPetFragment", "Invalid URI for Profile Picture: $value", e)
                            }
                        } else {
                            Log.e("FormAddPetFragment", "Profile Picture is empty!")
                            petData[key] = getDefaultImageBlob(requireContext()) ?: return@post
                        }

                    } else{
                        petData[key] = value
                    }
                }

                Log.d("FormAddPetFragment", "Final Pet Data: $petData")

                val name = petData["Name"] as? String
                if (name.isNullOrEmpty()) {
                    Log.e("FormAddPetFragment", "Name is missing or empty!")
                    Toast.makeText(requireContext(), "Name is required.", Toast.LENGTH_SHORT).show()
                    return@post
                }

                val uid = Firebase.auth.currentUser?.uid ?: ""
                val pet = Pet(
                    name = name, // Safe access
                    image = petData["Profile Picture"] as? Blob,
                    animal = petData["Breed"] as? String ?: "Unknown", // Provide defaults for optional fields
                    birthday = petData["Birthday"] as? String ?: "Unknown",
                    weight = petData["Weight"] as? String ?: "Unknown",
                    notes = petData["Notes"] as? String ?: "",
                    userID = uid
                )

                petRepositoryAPI.addPet(pet)
                Log.d("FormAddPetFragment", "Pet added successfully: $pet")
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        return rootView
    }


    private fun uriToBlob(uri: Uri, context: Context): Blob? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Blob.fromBytes(byteArray)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getDefaultImageBlob(context: Context): Blob? {
        return try {
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.home)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Blob.fromBytes(byteArray)
        } catch (e: Exception) {
            Log.e("FormAddPetFragment", "Failed to load default image", e)
            null
        }
    }
}
