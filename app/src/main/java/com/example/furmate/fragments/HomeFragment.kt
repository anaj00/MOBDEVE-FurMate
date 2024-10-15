import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.example.furmate.R

data class Task(val name: String, val time: String) // Data class representing each task

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false) // Inflate the fragment layout

        // Retrieving and displaying tasks for today pane
        val todays_tasks = getSampleTasks() // Sample list of tasks, normally you would retrieve this from your database
        val today_content_pane = rootView.findViewById<ConstraintLayout>(R.id.today_content)
        addTasksToLayout(today_content_pane, todays_tasks, inflater)

        // Retrieving and displaying tasks for upcoming pane
        val upcoming_tasks = getSampleTasks() // Sample list of tasks, normally you would retrieve this from your database
        val upcoming_content_pane = rootView.findViewById<ConstraintLayout>(R.id.upcoming_content)
        addTasksToLayout(upcoming_content_pane, upcoming_tasks, inflater)

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
        return inflater.inflate(R.layout.home_schedulebar, parent, false).apply {
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
