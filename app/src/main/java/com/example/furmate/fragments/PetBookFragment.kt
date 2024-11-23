package com.example.furmate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import com.example.furmate.db.BookRepositoryAPI
import com.example.furmate.fragments.FormAddBookFragment
import com.example.furmate.fragments.PetProfileFragment
import com.example.furmate.models.Book
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class PetBookFragment : Fragment() {
    private lateinit var bookRepositoryAPI: BookRepositoryAPI

    companion object {
        private const val ARG_PET_ID = "pet_id"
        fun newInstance(petID: String): PetBookFragment {
            val fragment = PetBookFragment()
            val args = Bundle()
            args.putString(ARG_PET_ID, petID)
            fragment.arguments = args // Set arguments before logging
            Log.d("FormAddBookFragment", "Pet ID set in arguments: ${fragment.arguments?.getString(ARG_PET_ID)}")
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_pets, container, false)
        val gridLayout = rootView.findViewById<GridLayout>(R.id.grid_layout_pets)
        gridLayout.columnCount = 2

        // Initialize firestore
        val firestore = FirebaseFirestore.getInstance()
        val bookCollection = firestore.collection("Book")
        bookRepositoryAPI = BookRepositoryAPI(bookCollection)

        getAllBooks(arguments?.getString(ARG_PET_ID) ?: "Unkown"){ books, error ->
            if (error != null) {
                // Handle error
                Log.e("PetBookFragment", "Error fetching books: $error")
                return@getAllBooks
            }

            val bookAdd = inflater.inflate(R.layout.button_addpet, gridLayout, false)
            bookAdd.post {
                val width = bookAdd.width
                val layoutParams = bookAdd.layoutParams
                layoutParams.height = width
                bookAdd.layoutParams = layoutParams
            }

            val bookAddTitle = bookAdd.findViewById<TextView>(R.id.pet_name1)
            bookAddTitle.text = "Add Book"

            bookAdd.setOnClickListener {
                openAddBookForm()
            }
            gridLayout.addView(bookAdd)

            // Dynamically add book items to GridLayout
            books?.let { bookList ->
                for (book in bookList) {
                    val bookItemView =
                        inflater.inflate(R.layout.composable_pet_button, gridLayout, false)

                    // Set the book name
                    val bookNameView = bookItemView.findViewById<TextView>(R.id.pet_name)
                    bookNameView.text = book.name

                    // Measure the width of the card to set the height equal to the width (for a square appearance)
                    bookItemView.post {
                        val width = bookItemView.width
                        val layoutParams = bookItemView.layoutParams
                        layoutParams.height = width
                        bookItemView.layoutParams = layoutParams
                    }

                    bookItemView.setOnClickListener {
                        openBookProfile(book.id?: "unkown")
                    }

                    // Add the book item to the GridLayout
                    gridLayout.addView(bookItemView)
                }
            }
        }

        return rootView
    }

    private fun openBookProfile(bookID: String) {
        val fragment = PetBookRecordFragment.newInstance(bookID)
        (requireActivity() as FragmentNavigator).navigateToFragment(fragment)
    }

    private fun openAddBookForm() {
        val fragment = FormAddBookFragment.newInstance(arguments?.getString(ARG_PET_ID) ?: "null")
        (requireActivity() as FragmentNavigator).navigateToFragment(fragment)
    }

    private fun getAllBooks (petID: String, callback: (List<Book>?, Exception?) -> Unit) {
        bookRepositoryAPI.getAllBooks(petID) { books, error ->
            if (error != null) {
                callback(null, error) // Pass the error to the callback
            } else {
                callback(books, null) // Pass the fetched
            }
        }
    }
}
