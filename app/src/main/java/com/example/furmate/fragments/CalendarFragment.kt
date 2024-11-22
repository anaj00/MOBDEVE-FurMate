import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentHostCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.FormScheduleFragment
import com.example.furmate.R
import com.example.furmate.adapter.TaskAdapter
import com.example.furmate.db.TaskRepositoryAPI
import com.example.furmate.models.Task
import com.example.furmate.utils.MarginItemDecoration
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.internal.http2.Header
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {
    private lateinit var taskRepositoryAPI: TaskRepositoryAPI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_calendar, container, false)

        val calendarView = rootView.findViewById<CalendarView>(R.id.calendar_dates)
        val dateHeader = rootView.findViewById<TextView>(R.id.date_header)
        val dateRecyclerView = rootView.findViewById<RecyclerView>(R.id.day_listview)

        // Initialize API collection
        val firestore = FirebaseFirestore.getInstance()
        val scheduleCollection = firestore.collection("Schedule")
        taskRepositoryAPI = TaskRepositoryAPI(scheduleCollection)

        // Set the initial date in the header
        dateHeader.text = getFormattedDate(Calendar.getInstance().time)

        dateRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(MarginItemDecoration(16))
        }

        val selectedDate = Calendar.getInstance().time
        populateTasks(selectedDate, dateRecyclerView)


        // Set up date picker
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            dateHeader.text = getFormattedDate(calendar.time)
            val newDate = calendar.time
            populateTasks(newDate, dateRecyclerView)
        }

        return rootView
    }

    // Helper function to format date
    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("E | MMMM d, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    // Populate the RecyclerView with tasks for the selected date
    // TODO: when clicking on the task, the form should open with the task details and should update the task values in DB
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
                    where = "Unkown",
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
        taskRepositoryAPI.getTasksByDate(date) { tasks, error ->
            if (error != null) {
                Log.e("CalendarFragment", "Error fetching tasks: $error")
                return@getTasksByDate
            }
            Log.d("CalendarFragment", "date value = $date")
            Log.d("CalendarFragment", "Fetched tasks: $tasks")
            callback(tasks, null)
        }
    }
}
