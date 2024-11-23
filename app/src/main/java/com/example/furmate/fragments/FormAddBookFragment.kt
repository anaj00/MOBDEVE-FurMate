package com.example.furmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.utils.MarginItemDecoration

class FormAddBookFragment : Fragment() {

    private var bookName: String? = null
    private var bookNotes: String? = null

    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance(): FormAddBookFragment {
            return FormAddBookFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.form_createpet, container, false)
        val header = rootView.findViewById<TextView>(R.id.addpet_header)
        header.text = "Add a new book"

        recyclerView = rootView.findViewById<RecyclerView>(R.id.pet_form_wrapper)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(MarginItemDecoration(16))

        val composableInputs = {
            listOf("Name", "Notes")
        }

        val inputValues = {
            listOf(
                bookName ?: "",
                bookNotes ?: ""
            )
        }

        val adapter = ComposableInputAdapter(composableInputs(), inputValues(), requireContext())
        recyclerView.adapter = adapter

        submitButton = rootView.findViewById<Button>(R.id.addpet_submit_btn)
        submitButton.setOnClickListener {

        }

        return rootView
    }
}