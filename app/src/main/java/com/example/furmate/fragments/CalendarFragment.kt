import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.fragments.FormScheduleFragment
import com.example.furmate.HomeActivity
import com.example.furmate.R
import com.example.furmate.adapter.TaskAdapter
import com.example.furmate.db.TaskRepositoryAPI
import com.example.furmate.models.Task
import com.example.furmate.utils.MarginItemDecoration
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var scheduleCollection: CollectionReference
    private lateinit var taskRepositoryAPI: TaskRepositoryAPI
    private var snapshotListenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_calendar, container, false)
        (requireActivity() as? HomeActivity)?.changeToolbarTitle("Calendar")

        val calendarView = rootView.findViewById<CalendarView>(R.id.calendar_dates)
        val dateHeader = rootView.findViewById<TextView>(R.id.date_header)
        val dateRecyclerView = rootView.findViewById<RecyclerView>(R.id.day_listview)

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
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        dateRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(MarginItemDecoration(16))
            adapter = this@CalendarFragment.adapter
        }

        // Set the initial date in the header
        val selectedDate = Calendar.getInstance().time
        dateHeader.text = getFormattedDate(selectedDate)
        observeTasksByDate(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate))


        // Set up date picker
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            dateHeader.text = getFormattedDate(calendar.time)
            val newDate = calendar.time
            observeTasksByDate(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newDate))
        }

        return rootView
    }

    // Helper function to format date
    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("E | MMMM d, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    // Populate the RecyclerView with tasks for the selected date
    private fun observeTasksByDate(date: String) {
        Log.d("CalendarFragment", "observeTasksByDate called with date $date")

        // Remove previous listener if it exists
        snapshotListenerRegistration?.remove()
        val userID = Firebase.auth.currentUser?.uid ?: ""
        snapshotListenerRegistration = scheduleCollection
            .whereEqualTo("userID", userID) // Filter tasks by user ID
            .whereEqualTo("date", date)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("CalendarFragment", "Error listening for tasks", error)
                    return@addSnapshotListener
                }

                // Map Firestore documents to Task objects
                val tasks = snapshots?.documents?.mapNotNull { document ->
                    try {
                        Task(
                            id = document.getString("id") ?: "",
                            name = document.getString("name") ?: "",
                            date = document.getString("date"), // Convert to Timestamp or String based on your data
                            petName = document.getString("petName") ?: "",
                            notes = document.getString("notes")
                        )
                    } catch (e: Exception) {
                        Log.e("CalendarFragment", "Error mapping document to Task", e)
                        null
                    }
                } ?: emptyList()

                Log.d("CalendarFragment", "Fetched tasks: $tasks")

                // Update the adapter's dataset
                adapter.updateTasks(tasks)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the snapshot listener when the fragment view is destroyed
        snapshotListenerRegistration?.remove()
    }
}
