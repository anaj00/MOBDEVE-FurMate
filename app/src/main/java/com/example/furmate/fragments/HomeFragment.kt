import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.example.furmate.models.Task
import com.example.furmate.adapter.TaskAdapter
import com.example.furmate.utils.MarginItemDecoration

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_home, container, false)

        // Set up RecyclerView for "Today" pane
        val todayRecyclerView = rootView.findViewById<RecyclerView>(R.id.today_recycler_view)
        val todayTasks = getSampleTasks()
        todayRecyclerView.layoutManager = LinearLayoutManager(context)
        todayRecyclerView.adapter = TaskAdapter(todayTasks)
        todayRecyclerView.addItemDecoration(MarginItemDecoration(16))

        // Set up RecyclerView for "Upcoming" pane
        val upcomingRecyclerView = rootView.findViewById<RecyclerView>(R.id.upcoming_recycler_view)
        val upcomingTasks = getSampleTasks()
        upcomingRecyclerView.layoutManager = LinearLayoutManager(context)
        upcomingRecyclerView.adapter = TaskAdapter(upcomingTasks)
        upcomingRecyclerView.addItemDecoration(MarginItemDecoration(16))

        return rootView
    }


    // Function to get a list of sample tasks
    private fun getSampleTasks(): List<Task> {
        return listOf(
            Task("Walk the Dog", "8:30 AM"),
            Task("Feed the Cat", "9:00 AM"),
            Task("Take a Bath", "10:00 AM"),
            Task("Walk the Dog", "8:30 AM"),
            Task("Feed the Cat", "9:00 AM"),
            Task("Take a Bath", "10:00 AM"),
            Task("Walk the Dog", "8:30 AM"),
            Task("Feed the Cat", "9:00 AM"),
            Task("Take a Bath", "10:00 AM"),
            Task("Walk the Dog", "8:30 AM"),
            Task("Feed the Cat", "9:00 AM"),
            Task("Take a Bath", "10:00 AM"),
            Task("Walk the Dog", "8:30 AM"),
            Task("Feed the Cat", "9:00 AM"),
            Task("Take a Bath", "10:00 AM"),
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
