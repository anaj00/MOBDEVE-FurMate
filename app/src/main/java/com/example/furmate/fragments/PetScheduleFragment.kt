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
import com.example.furmate.FormScheduleFragment
import com.example.furmate.R
import com.example.furmate.adapter.TaskAdapter
import com.example.furmate.db.TaskRepositoryAPI
import com.example.furmate.models.Task
import com.example.furmate.utils.MarginItemDecoration
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PetScheduleFragment : Fragment() {
    companion object {
        fun newInstance(): PetScheduleFragment {
            return PetScheduleFragment()
        }
    }
    private  lateinit var taskRepositoryAPI: TaskRepositoryAPI
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
        val scheduleCollection = firestore.collection("Schedule")
        taskRepositoryAPI = TaskRepositoryAPI(scheduleCollection)

        // Set the initial date in the header
        val initialDate = Date()
        dateHeader.text = getFormattedDate(initialDate)

        // Set up the MaterialDatePicker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePickerTrigger.setOnClickListener {
            datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
        }

        // Set up RecyclerView for tasks
        val dateRecyclerView = rootView.findViewById<RecyclerView>(R.id.day_listview)
        dateRecyclerView.layoutManager = LinearLayoutManager(context)
        dateRecyclerView.addItemDecoration(MarginItemDecoration(16))

        // Load initial tasks for the date
        populateTasks(initialDate, dateRecyclerView)

        // Update header when a date is selected
        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            dateHeader.text = getFormattedDate(date)
            populateTasks(date, dateRecyclerView)
        }

        return rootView
    }

    // Helper function to format date
    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("E | MMMM d, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    // Sample tasks
    private fun getSampleTasks(): List<Task> {
        return listOf(
            Task("Walk the Dog", "8:30 AM", "Park", "boopie"),
            Task("Feed the Cat", "9:00 AM", "Home", "baabaa"),
            Task("Take a Bath", "10:00 AM", "Home", "booboo")
        )
    }

    private fun populateTasks(date: Date, recyclerView: RecyclerView) {
        val queryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

        getAllTasks(queryDate) { tasks, error ->
            if (error != null) {
                Log.e("CalendarFragment", "Error fetching tasks: $error")
                return@getAllTasks
            }
            recyclerView.adapter = TaskAdapter(tasks ?: emptyList()) { task ->
                val fragment = FormScheduleFragment.newInstance(
                    isSchedule = true,
                    title = task.name,
                    date = task.date,
                    where = "Unknown",
                    pet = task.petName,
                    notes = task.notes
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun getAllTasks(date: String, callback: (List<Task>?, Exception?) -> Unit) {
        // Implement fetching tasks from the database
        taskRepositoryAPI.getTasksByDate(date) { tasks, error ->
            if (error != null) {
                // Handle error
                Log.e("CalendarFragment", "Error fetching tasks: $error")
                return@getTasksByDate
            }
            // Handle tasks
            callback(tasks, null)
        }
    }
}
