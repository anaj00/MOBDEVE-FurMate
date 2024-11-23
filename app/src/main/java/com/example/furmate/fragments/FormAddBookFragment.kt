package com.example.furmate.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.db.BookRepositoryAPI
import com.example.furmate.db.PetRepositoryAPI
import com.example.furmate.models.Book
import com.example.furmate.models.Pet
import com.example.furmate.utils.MarginItemDecoration
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FormAddBookFragment : Fragment() {

    private var bookName: String? = null
    private var bookNotes: String? = null
    private lateinit var submitButton: Button
    private lateinit var recyclerView: RecyclerView

    // Firestore Collections
    private lateinit var bookCollection: CollectionReference
    // APIs
    private lateinit var bookRepositoryAPI: BookRepositoryAPI


    companion object {
        private const val ARG_PET_ID = "petID"
        fun newInstance(petID: String): FormAddBookFragment {
            val fragment = FormAddBookFragment()
            val args = Bundle()
            args.putString(ARG_PET_ID, petID) // Pass petID in the Bundle
            fragment.arguments = args
            return fragment
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

        // Initialize the Firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Initilize the Firestore collection
        bookCollection = firestore.collection("Book")
        bookRepositoryAPI = BookRepositoryAPI(bookCollection)

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
            val bookData = mutableMapOf<String, Any>()

            recyclerView.post {
                for (child in recyclerView.children) {
                    val holder = recyclerView.getChildViewHolder(child)
                    val key = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div)?.hint.toString()
                    val value = holder.itemView.findViewById<TextInputEditText>(R.id.input_field)?.text.toString()

                    Log.d("FormAddBookFragment", "Key: $key, Value: $value")
                    bookData[key] = value
                }
                val book = Book(
                    name = bookData["Name"] as? String ?: "unknown",
                    notes = bookData["Notes"] as? String ?: "unknown",
                    userID = Firebase.auth.currentUser?.uid ?: "unknown",
                    petID = ARG_PET_ID ?: "unknown",
                )

                Log.d("FormAddBookFragment", "Final Pet Data: $book")

                bookRepositoryAPI.addBook(book)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        return rootView
    }
}