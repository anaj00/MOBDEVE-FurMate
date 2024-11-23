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
import com.example.furmate.models.Book
import com.example.furmate.models.Task
import com.google.firebase.firestore.FirebaseFirestore

class PetBookRecordFragment : Fragment() {
    private lateinit var bookRepositoryAPI: BookRepositoryAPI

    companion object {
        fun newInstance(): PetBookRecordFragment {
            return PetBookRecordFragment()
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
        val records = generateDummyRecords()


        // Initialize firestore
        val firestore = FirebaseFirestore.getInstance()
        val bookCollection = firestore.collection("Books")
        bookRepositoryAPI = BookRepositoryAPI(bookCollection)


        getAllBooks { books, error ->
            if (error != null) {
                // Handle error
                Log.e("PetBookRecordFragment", "Error fetching books: $error")
                return@getAllBooks
            }

            // Add a button to add a new record
            val recordAdd = inflater.inflate(R.layout.button_addpet, gridLayout, false)
            recordAdd.post {
                val width = recordAdd.width
                val layoutParams = recordAdd.layoutParams
                layoutParams.height = width
                recordAdd.layoutParams = layoutParams
            }

        }









        // Dynamically add record items to GridLayout
        for (record in records) {
            val recordItemView =
                inflater.inflate(R.layout.composable_pet_button, gridLayout, false)

            // Set the record name
            val recordNameView = recordItemView.findViewById<TextView>(R.id.pet_name)
            recordNameView.text = record.name

            // Measure the width of the card to set the height equal to the width (for a square appearance)
            recordItemView.post {
                val width = recordItemView.width
                val layoutParams = recordItemView.layoutParams
                layoutParams.height = width
                recordItemView.layoutParams = layoutParams
            }

            recordItemView.setOnClickListener {
                openRecordProfile()
            }

            // Add the record item to the GridLayout
            gridLayout.addView(recordItemView)
        }

        return rootView
    }

    private fun openRecordProfile() {
        // Pass required arguments to the FormScheduleFragment
        val fragment = FormScheduleFragment.newInstance(
            isSchedule = false,
            title = null,
            date = null,
            where = null,
            pet = null,
            notes = null
        )
        (requireActivity() as FragmentNavigator).navigateToFragment(fragment)
    }

    private fun openAddRecordForm() {
        // Pass required arguments to the FormScheduleFragment
        val fragment = FormScheduleFragment.newInstance(
            isSchedule = false,
            title = null,
            date = null,
            where = null,
            pet = null,
            notes = null
        )
        (requireActivity() as FragmentNavigator).navigateToFragment(fragment)
    }

    private fun generateDummyRecords(): List<Task> {
        // Generate a list of dummy recordRs
        return listOf(
            Task("Record 1"),
            Task("Record 2"),
            Task("Record 3"),
            Task("Record 4"),
            Task("Record 5")
        )
    }

    private fun getAllBooks (callback: (List<Book>?, Exception?) -> Unit) {
        bookRepositoryAPI.getAllBooks { books, error ->
            if (error != null) {
                callback(null, error) // Pass the error to the callback
            } else {
                callback(books, null) // Pass the fetched
            }
        }
    }
}
