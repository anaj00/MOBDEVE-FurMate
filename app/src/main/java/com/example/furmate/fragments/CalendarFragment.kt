import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.FormScheduleFragment
import com.example.furmate.R
import com.example.furmate.adapter.TaskAdapter
import com.example.furmate.models.Task
import com.example.furmate.utils.MarginItemDecoration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_calendar, container, false)

        val calendarView = rootView.findViewById<CalendarView>(R.id.calendar_dates)
        val dateHeader = rootView.findViewById<TextView>(R.id.date_header)

        // Set the initial date in the header
        dateHeader.text = getFormattedDate(Calendar.getInstance().time)

        // Set up date picker
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            dateHeader.text = getFormattedDate(calendar.time)
        }

        // Set up RecyclerView
        val dateRecyclerView = rootView.findViewById<RecyclerView>(R.id.day_listview)
        dateRecyclerView.apply {
            adapter = TaskAdapter(getSampleTasks()) { task ->
                // Open the form schedule fragment with pre-filled data
                val fragment = FormScheduleFragment.newInstance(
                    isSchedule = true,
                    title = task.name,
                    date = task.time,
                    where = "Unknown", // Add appropriate data
                    pet = "Unknown",
                    notes = "Some notes"
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(MarginItemDecoration(16))
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
}
