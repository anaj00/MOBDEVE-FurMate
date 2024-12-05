package com.example.furmate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.fragments.FormScheduleFragment
import com.example.furmate.adapter.TaskAdapter
import com.example.furmate.db.TaskRepositoryAPI
import com.example.furmate.models.Task
import com.example.furmate.utils.MarginItemDecoration
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PetScheduleFragment : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var scheduleCollection: CollectionReference
    private lateinit var taskRepositoryAPI: TaskRepositoryAPI
    private var snapshotListenerRegistration: ListenerRegistration? = null
    private var petName: String? = null

    companion object {
        fun newInstance(petName: String): PetScheduleFragment {
            val fragment = PetScheduleFragment()
            val args = Bundle()
            args.putString("petName", petName) // Pass petName in the Bundle
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            petName = it.getString("petName")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_pet_profile_schedule, container, false)

        // Date Picker trigger view
        val datePickerTrigger = rootView.findViewById<TextView>(R.id.date_picker_trigger)
        val dateHeader = rootView.findViewById<TextView>(R.id.date_header)
        // Initialize API collection
        val firestore = FirebaseFirestore.getInstance()
        scheduleCollection = firestore.collection("Schedule")
        taskRepositoryAPI = TaskRepositoryAPI(scheduleCollection)


        adapter = TaskAdapter(mutableListOf()) { task ->
            val fragment = FormScheduleFragment.newInstance(
                isSchedule = true,
                title = task.name,
                date = task.date,
                where = "Unknown",
                pet = task.petName,
                notes = task.notes,
                documentId = task.id
            )
            (requireActivity() as FragmentNavigator).navigateToFragment(fragment)
        }
        // Set up RecyclerView for tasks
        val dateRecyclerView = rootView.findViewById<RecyclerView>(R.id.day_listview)
        dateRecyclerView.layoutManager = LinearLayoutManager(context)
        dateRecyclerView.addItemDecoration(MarginItemDecoration(16))
        dateRecyclerView.adapter = adapter

        // Set the initial date in the header
        val initialDate = Date()
        dateHeader.text = getFormattedDate(initialDate)

        observeTasksByDateAndPet(initialDate, petName ?: "")

        // Set up the MaterialDatePicker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePickerTrigger.setOnClickListener {
            datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
        }

        // Update header when a date is selected
        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            dateHeader.text = getFormattedDate(date)
            observeTasksByDateAndPet(date, petName ?: "")
        }

        return rootView
    }

    // Helper function to format date
    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("E | MMMM d, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun observeTasksByDateAndPet(date: Date, petName: String) {
        Log.d("PetScheduleFragment", "PetName: $petName")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val startOfDay = "$formattedDate 00:00"
        val endOfDay = "$formattedDate 23:59"

        snapshotListenerRegistration?.remove()

        snapshotListenerRegistration = scheduleCollection
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThanOrEqualTo("date", endOfDay)
            .whereEqualTo("petName", petName) // Filter by pet name
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("PetScheduleFragment", "Error listening for tasks", error)
                    return@addSnapshotListener
                }

                val tasks = snapshots?.documents?.mapNotNull { document ->
                    document.toObject(Task::class.java)
                } ?: emptyList()

                adapter.updateTasks(tasks.toMutableList())
                Log.d("PetScheduleFragment", "Fetched tasks: $tasks")
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the snapshot listener when the fragment view is destroyed
        snapshotListenerRegistration?.remove()
    }
}
