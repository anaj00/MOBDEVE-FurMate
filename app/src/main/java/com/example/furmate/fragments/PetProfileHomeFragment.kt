package com.example.furmate


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.db.PetRepositoryAPI
import com.example.furmate.utils.MarginItemDecoration
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class PetProfileHomeFragment : Fragment() {
    private var petName: String? = null
    private var petBreed: String? = null
    private var petSex: String? = null
    private var petBirthday: String? = null
    private var petWeight: String? = null
    private var petNotes: String? = null

    private lateinit var recyclerView: RecyclerView

    // Firestore Collections
    private lateinit var petCollection: CollectionReference
    // APIs
    private lateinit var petRepositoryAPI: PetRepositoryAPI

    companion object {
        fun newInstance(): PetProfileHomeFragment {
            return PetProfileHomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.screen_pet_profile_main, container, false)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.pet_edit_form_wrapper)
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

        val adapter = ComposableInputAdapter(composableInputs(), inputValues(), requireContext())
        recyclerView.adapter = adapter

        // TODO: Make it save right away.
        //  Maybe when user presses back or changes screen or after a specfic amount of time?
//        submitButton.setOnClickListener {
//            val petData = mutableMapOf<String, String>()
//
//            for ( child in recyclerView.children) {
//                val holder = recyclerView.getChildViewHolder(child)
//                val key = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div).hint.toString()
//                val value = holder.itemView.findViewById<TextInputEditText>(R.id.input_field).text.toString()
//
//                petData[key] = value
//            }
//
//            Log.d("FormAddPetFragment", "Pet data: $petData")
//
//            val pet = Pet(
//                name = petData["Name"]!!,
//                animal = petData["Animal"]!!,
//                birthday = petData["Birthday"]!!,
//                notes = petData["Notes"]!!
//
//            )
//
//            petRepositoryAPI.addPet(pet)
//        }
        return rootView
    }
}
