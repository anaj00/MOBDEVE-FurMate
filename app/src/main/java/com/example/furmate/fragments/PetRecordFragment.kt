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
import com.example.furmate.db.RecordRepositoryAPI
import com.example.furmate.fragments.FormScheduleFragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class PetRecordFragment : Fragment() {
    private lateinit var recordRepositoryAPI: RecordRepositoryAPI

    companion object {
        private const val ARG_PET_ID = "pet_id"

        fun newInstance(petID: String): PetRecordFragment {
            val fragment = PetRecordFragment()
            val args = Bundle()
            args.putString(ARG_PET_ID, petID)
            fragment.arguments = args
            Log.d("PetRecordFragment", "${args} in pet record fragment")
            return fragment
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("PetRecordFragment", "Activity created, arguments: ${arguments}")
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
        val recordCollection = firestore.collection("Record")
        recordRepositoryAPI = RecordRepositoryAPI(recordCollection)

        Log.d("PetRecordFragment", "arguments:  ${arguments?.keySet()}")
        val petID = arguments?.getString(ARG_PET_ID) ?: ""
        getAllRecords(petID) { records, error ->
            if (error != null) {
                // Handle error
                Log.e("PetRecordFragment", "Error fetching records: $error")
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
                        openRecordProfile(record)
                    }

                    // Add the record item to the GridLayout
                    gridLayout.addView(recordItemView)
                }
            }
        }


        return rootView
    }

    private fun openRecordProfile(record: com.example.furmate.models.Record) {
        // Pass required arguments to the FormScheduleFragment
        (activity as? HomeActivity)?.hideFABs()
        val fragment = FormScheduleFragment.Companion.newInstance(
            isSchedule = false,
            title = record.name,
            date = null,
            where = null,
            pet = record.petName,
            taskImage = record.imageURI,
            notes = record.notes,
            documentId = record.id,
        )
        (activity as? HomeActivity)?.supportFragmentManager?.beginTransaction()
            ?.setReorderingAllowed(true)
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(HomeActivity.Companion.FragmentName.HOME.name)
            ?.commit()
    }
    private fun getAllRecords(petID: String, callback: (records: List<com.example.furmate.models.Record>?, error: Exception?) -> Unit) {
        recordRepositoryAPI.getAllRecordsByPetID(petID) { records, error ->
            callback(records, error)
        }
    }
}
