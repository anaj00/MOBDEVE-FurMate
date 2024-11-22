package com.example.furmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.gridlayout.widget.GridLayout
import com.example.furmate.fragments.FormAddPetFragment
import com.example.furmate.fragments.PetProfileFragment
import com.example.furmate.models.Book
import com.example.furmate.models.Pet

class PetRecordsFragment : Fragment() {

    companion object {
        fun newInstance(): PetRecordsFragment {
            return PetRecordsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_pets, container, false)
        val gridLayout = rootView.findViewById<GridLayout>(R.id.grid_layout_pets)
        gridLayout.columnCount = 2

        // Replace backend call with dummy data
        val books = generateDummyBooks()

        // Add a button to add a new book
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
//            openAddBookForm()
        }
        gridLayout.addView(bookAdd)

        // Dynamically add book items to GridLayout
        for (book in books) {
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
//                openBookProfile(book)
            }

            // Add the book item to the GridLayout
            gridLayout.addView(bookItemView)
        }

        return rootView
    }

//    private fun openBookProfile(book: Book) {
//        val fragment = PetProfileFragment.newInstance(book.id ?: "null")
//        parentFragmentManager.commit {
//            setReorderingAllowed(true)
//            replace(R.id.fragment_container, fragment)
//            addToBackStack(null)
//        }
//    }

//    private fun openAddBookForm() {
//        val fragment = FormAddBookFragment()
//        parentFragmentManager.commit {
//            setReorderingAllowed(true)
//            replace(R.id.fragment_container, fragment)
//            addToBackStack(null)
//        }
//    }

    private fun generateDummyBooks(): List<Book> {
        // Generate a list of dummy books
        return listOf(
            Book(id = "1", name = "Book 1"),
            Book(id = "2", name = "Book 2"),
            Book(id = "3", name = "Book 3"),
            Book(id = "4", name = "Book 4"),
            Book(id = "5", name = "Book 5")
        )
    }
}
