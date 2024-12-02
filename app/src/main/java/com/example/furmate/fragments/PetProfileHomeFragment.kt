package com.example.furmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.utils.MarginItemDecoration

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

    companion object {
        fun newInstance(): PetProfileHomeFragment {
            return PetProfileHomeFragment()
        }
    }

    private var areInputsEnabled = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_pet_profile_main, container, false)

        recyclerView = rootView.findViewById(R.id.pet_edit_form_wrapper)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(MarginItemDecoration(16))

        editButton = rootView.findViewById(R.id.edit_btn)

        val composableInputs = listOf("Name", "Breed", "Sex", "Birthday", "Weight", "Notes")
        (requireActivity() as? HomeActivity)?.changeToolbarTitle(petName + "'s Records")
        val inputValues = listOf(
            petName ?: "",
            petBreed ?: "",
            petSex ?: "",
            petBirthday ?: "",
            petWeight ?: "",
            petNotes ?: ""
        )

        adapter = ComposableInputAdapter(composableInputs, inputValues, requireContext())
        recyclerView.adapter = adapter
        adapter.setInputEnabled(areInputsEnabled) // Set initial state

        // Toggle all inputs when the edit button is clicked
        editButton.setOnClickListener {
            areInputsEnabled = !areInputsEnabled // Toggle state
            adapter.setInputEnabled(areInputsEnabled) // Apply the new state to all inputs
        }

        return rootView
    }
}
