package com.example.furmate.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.HomeActivity
import com.example.furmate.R
import com.example.furmate.models.Task
import com.example.furmate.adapter.TaskAdapter
import com.example.furmate.utils.MarginItemDecoration
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var todayTasks: ArrayList<Task>
    private lateinit var upcomingTasks: ArrayList<Task>
    private val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Firestore Collections
    private lateinit var scheduleCollection: CollectionReference
    private lateinit var recordCollection: CollectionReference

    // Snapshot listener registration
    private var scheduleUpcomingSnapshotListener : ListenerRegistration? = null
    private var scheduleTodaySnapshotListener : ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_home, container, false)
        (requireActivity() as? HomeActivity)?.changeToolbarTitle("Home")

        // Initialize API collection
        val firestore = FirebaseFirestore.getInstance()
        scheduleCollection = firestore.collection("Schedule")
        recordCollection = firestore.collection("Record")

        // Set up RecyclerView for "Today" pane
        val todayRecyclerView = rootView.findViewById<RecyclerView>(R.id.today_recycler_view)
        todayTasks = ArrayList()
        todayRecyclerView.layoutManager = LinearLayoutManager(context)
        todayRecyclerView.adapter = TaskAdapter(todayTasks){task -> openTaskDetail(task)}
        todayRecyclerView.addItemDecoration(MarginItemDecoration(16))

        // Set up RecyclerView for "Upcoming" pane
        val upcomingRecyclerView = rootView.findViewById<RecyclerView>(R.id.upcoming_recycler_view)
        upcomingTasks = ArrayList()
        upcomingRecyclerView.layoutManager = LinearLayoutManager(context)
        upcomingRecyclerView.adapter = TaskAdapter(upcomingTasks){task -> openTaskDetail(task)}
        upcomingRecyclerView.addItemDecoration(MarginItemDecoration(16))

        // Fetch tasks from FireStore
        observeTodayTasks(todayRecyclerView)
        observeUpcomingTasks(upcomingRecyclerView)

        parentFragmentManager.setFragmentResultListener("KEY_NEW_SCHEDULE", this) { requestKey, bundle ->
            for (key in bundle.keySet()) {
                Log.d(key, bundle.getString(key)!!)
            }

            val pet = bundle.getString("KEY_PET")!!
            val date = bundle.getString("KEY_DATE")!!
            val notes = bundle.getString("KEY_NOTES")!!
            val title = bundle.getString("KEY_TITLE")!!
            val where = bundle.getString("KEY_WHERE")!!

            // TODO: add the rest of the bundle values
            todayTasks.add(Task(null, title, date, pet, notes))
            (todayRecyclerView.adapter as RecyclerView.Adapter)
                .notifyItemInserted(todayTasks.size - 1)
        }

        return rootView
    }

    private fun observeTodayTasks(todayRecyclerView: RecyclerView) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(dateFormat.parse(todayDate)!!)
        val startOfDay = "$formattedDate 00:00"
        val endOfDay = "$formattedDate 23:59"
        Log.d("HomeFragment", "Start of day: $startOfDay, End of day: $endOfDay")

        scheduleTodaySnapshotListener?.remove() // Remove previous listener if exists

        scheduleTodaySnapshotListener = scheduleCollection
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThanOrEqualTo("date", endOfDay)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("HomeFragment", "Error listening for today tasks.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                    todayTasks.clear()
                    todayTasks.addAll(tasks)
                    todayRecyclerView.adapter?.notifyDataSetChanged()
                    Log.d("HomeFragment", "Today tasks: $tasks")
                } else {
                    Log.d("HomeFragment", "No tasks found for today.")
                }
            }
    }

    private fun observeUpcomingTasks(upcomingRecyclerView: RecyclerView) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(dateFormat.parse(todayDate)!!)
        val startOfDay = "$formattedDate 00:00"
        val endOfDay = "$formattedDate 23:59"
        scheduleUpcomingSnapshotListener?.remove() // Remove previous listener if exists

        scheduleUpcomingSnapshotListener = scheduleCollection
            .whereGreaterThan("date", endOfDay) // Filter tasks after today
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HomeFragment", "Error listening for upcoming tasks.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                    upcomingTasks.clear()
                    upcomingTasks.addAll(tasks)
                    upcomingRecyclerView.adapter?.notifyDataSetChanged()
                    Log.d("HomeFragment", "Upcoming tasks: $tasks")
                } else {
                    Log.d("HomeFragment", "No upcoming tasks found.")
                }
            }
    }

    // Function to open a task in detail (or open a form with pre-filled data)
    private fun openTaskDetail(task: Task) {
        // Ensure the parent activity is HomeActivity and hide FABs
        (activity as? HomeActivity)?.hideFABs()

        Log.d("HomeFragment", "Task ID: ${task.id}")

        // Handle task click and open the form with pre-filled task details
        val fragment = FormScheduleFragment.Companion.newInstance(
            isSchedule = true,
            title = task.name,
            date = task.date,
            where = null,
            pet = task.petName,
            notes = task.notes,
            documentId = task.id
        )
        parentFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(HomeActivity.Companion.FragmentName.HOME.name)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scheduleTodaySnapshotListener?.remove()
        scheduleUpcomingSnapshotListener?.remove()
    }
}