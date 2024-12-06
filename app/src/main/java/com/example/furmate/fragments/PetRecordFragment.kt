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
import com.example.furmate.fragments.FormScheduleFragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class PetRecordFragment : Fragment() {
    private lateinit var bookRepositoryAPI: BookRepositoryAPI

    companion object {
        private const val ARG_BOOK_ID = "book_id"

        fun newInstance(bookID: String): PetRecordFragment {
            val fragment = PetRecordFragment()
            val args = Bundle()
            args.putString(ARG_BOOK_ID, bookID)
            fragment.arguments = args
            return PetRecordFragment()
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
        val bookCollection = firestore.collection("Books")
        val recordCollection = firestore.collection("Record")
        bookRepositoryAPI = BookRepositoryAPI(bookCollection)


        getAllRecords(arguments?.getString(ARG_BOOK_ID) ?: "Unknown", recordCollection) { records, error ->
            if (error != null) {
                // Handle error
                Log.e("PetBookRecordFragment", "Error fetching records: $error")
                return@getAllRecords
            }

            // Add record items to the GridLayout
            records?.let { recordList ->
                for (record in recordList) {
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
            }
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
    private fun getAllRecords(bookID: String, collection: CollectionReference,callback: (records: List<com.example.furmate.models.Record>?, error: Exception?) -> Unit) {
        bookRepositoryAPI.getAllRecords(bookID, collection) { records, error ->
            if (error != null) {
                callback(null, error) // Pass the error to the callback
            } else {
                callback(records, null) // Pass the fetched
            }
        }
    }
}
