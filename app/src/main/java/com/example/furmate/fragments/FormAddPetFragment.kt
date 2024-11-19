package com.example.furmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.utils.MarginItemDecoration
import kotlin.collections.any

class FormAddPetFragment : Fragment() {
    private var petName: String? = null
    private var petAnimal: String? = null
    private var petBirthday: String? = null
    private var petNotes: String? = null

    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.form_createpet, container, false)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.pet_form_wrapper)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(MarginItemDecoration(16))

        val composableInputs = {
            listOf("Name", "Animal", "Birthday", "Notes")
        }

        val inputValues = {
            listOf(petName ?: "", petAnimal?: "", petBirthday?: "", petNotes?: "")

        }

        val adapter = ComposableInputAdapter(composableInputs(), inputValues(), requireContext())
        recyclerView.adapter = adapter

        return rootView
    }
}
