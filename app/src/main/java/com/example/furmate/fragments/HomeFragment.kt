package com.example.furmate.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.FormScheduleFragment
import com.example.furmate.HomeActivity
import com.example.furmate.R
import com.example.furmate.models.Task
import com.example.furmate.adapter.TaskAdapter
import com.example.furmate.utils.MarginItemDecoration
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {
    private lateinit var todayTasks: ArrayList<Task>
    private lateinit var upcomingTasks: ArrayList<Task>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_home, container, false)

        // Set up RecyclerView for "Today" pane
        val todayRecyclerView = rootView.findViewById<RecyclerView>(R.id.today_recycler_view)
        todayTasks = ArrayList(getSampleTasks())
        todayRecyclerView.layoutManager = LinearLayoutManager(context)
        todayRecyclerView.adapter = TaskAdapter(todayTasks){task -> openTaskDetail(task)}
        todayRecyclerView.addItemDecoration(MarginItemDecoration(16))

        // Set up RecyclerView for "Upcoming" pane
        val upcomingRecyclerView = rootView.findViewById<RecyclerView>(R.id.upcoming_recycler_view)
        upcomingTasks = ArrayList(getSampleTasks())
        upcomingRecyclerView.layoutManager = LinearLayoutManager(context)
        upcomingRecyclerView.adapter = TaskAdapter(upcomingTasks){task -> openTaskDetail(task)}
        upcomingRecyclerView.addItemDecoration(MarginItemDecoration(16))

        // Fetch tasks from FireStore
        fetchTodayTasks(todayRecyclerView)
        fetchUpcomingTasks(upcomingRecyclerView)

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
            todayTasks.add(Task(title, date, where, pet, notes))
            (todayRecyclerView.adapter as RecyclerView.Adapter)
                .notifyItemInserted(todayTasks.size - 1)
        }

        return rootView
    }
    private fun fetchTodayTasks(todayRecyclerView: RecyclerView) {
        FirebaseFirestore.getInstance().collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("HomeFragment", "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) { // Ensure snapshot is not empty
                    val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                    todayTasks.clear()
                    todayTasks.addAll(tasks) // Add all tasks to the list
                    todayRecyclerView.adapter?.notifyDataSetChanged() // Notify the adapter of data change
                } else {
                    Log.d("HomeFragment", "No tasks found in the collection.")
                }
            }
    }

    private fun fetchUpcomingTasks(recyclerView: RecyclerView) {
        FirebaseFirestore.getInstance().collection("tasks")
            .whereGreaterThan("date", "today") // Adjust query for "Upcoming"
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HomeFragment", "Error fetching upcoming tasks", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                    upcomingTasks.clear()
                    upcomingTasks.addAll(tasks)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
    }

    // Function to open a task in detail (or open a form with pre-filled data)
    private fun openTaskDetail(task: Task) {
        // Ensure the parent activity is HomeActivity and hide FABs
        (activity as? HomeActivity)?.hideFABs()


        // Handle task click and open the form with pre-filled task details
        val fragment = FormScheduleFragment.Companion.newInstance(
            isSchedule = true,
            title = task.name,
            date = task.time,
            where = "Location",  // Add relevant data here
            pet = "Pet Name",     // Add relevant data here
            notes = "Notes"       // Add relevant data here
        )
        parentFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(HomeActivity.Companion.FragmentName.HOME.name)
            .commit()
    }

    // Function to get a list of sample tasks
    private fun getSampleTasks(): List<Task> {
        return listOf(
            Task("Walk the Dog", "8:30 AM", "Park", "boopie"),
            Task("Feed the Cat", "9:00 AM", "Home", "baabaa"),
            Task("Take a Bath", "10:00 AM", "Home", "booboo"),
            Task("Walk the Dog", "8:30 AM", "Park", "boopie"),
            Task("Feed the Cat", "9:00 AM", "Home", "baabaa"),
            Task("Take a Bath", "10:00 AM", "Home", "booboo"),
            Task("Walk the Dog", "8:30 AM", "Park", "boopie"),
            Task("Feed the Cat", "9:00 AM", "Home", "baabaa"),
            Task("Take a Bath", "10:00 AM", "Home", "booboo"),
            Task("Walk the Dog", "8:30 AM", "Park", "boopie"),
            Task("Feed the Cat", "9:00 AM", "Home", "baabaa"),
            Task("Take a Bath", "10:00 AM", "Home", "booboo"),
        )
    }

    // Function to add a task view to the layout
    private fun addTasksToLayout(parentLayout: ConstraintLayout, tasks: List<Task>, inflater: LayoutInflater) {
        var previousViewId: Int? = null // Initialize the ID of the previous view to null

        for (task in tasks) {
            val taskView = inflateTaskView(inflater, parentLayout) // Inflate the task view
            setTaskViewContent(taskView, task) // Set the content of the task view
            addTaskViewToLayout(parentLayout, taskView, previousViewId) // Add the task view to the layout
            previousViewId = taskView.id // Update the ID of the previous view
        }
    }

    // Function to inflate the reusable task view
    private fun inflateTaskView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.composable_schedule_card, parent, false).apply {
            id = View.generateViewId() // Generate a unique ID for each inflated view
        }
    }

    // Function to set the content of the task view
    private fun setTaskViewContent(view: View, task: Task) {
        val taskTitleView = view.findViewById<TextView>(R.id.task_title)
        val taskTimeView = view.findViewById<TextView>(R.id.task_time)
        taskTitleView.text = task.name
        taskTimeView.text = task.time
    }

    // Function to add the task view to the parent layout with appropriate constraints
    private fun addTaskViewToLayout(parentLayout: ConstraintLayout, taskView: View, previousViewId: Int?) {
        // Add the view to the parent ConstraintLayout
        parentLayout.addView(taskView)

        // Apply constraints to position the view within the ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(parentLayout)  // Clone the existing constraints

        if (previousViewId == null) {
            // If this is the first view, connect it to the parent top
            constraintSet.connect(
                taskView.id, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16
            )
        } else {
            // If this is not the first view, connect it below the previous view
            constraintSet.connect(
                taskView.id, ConstraintSet.TOP,
                previousViewId, ConstraintSet.BOTTOM, 16
            )
        }

        // Connect the start of the view to the start of the parent layout
        constraintSet.connect(
            taskView.id, ConstraintSet.START,
            ConstraintSet.PARENT_ID, ConstraintSet.START, 0
        )

        // Apply the new constraints to the ConstraintLayout
        constraintSet.applyTo(parentLayout)
    }
}