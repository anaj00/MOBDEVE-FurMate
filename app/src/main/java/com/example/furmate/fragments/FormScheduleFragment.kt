package com.example.furmate.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.HomeActivity
import com.example.furmate.R
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.db.RecordRepositoryAPI
import com.example.furmate.db.TaskRepositoryAPI
import com.example.furmate.models.Record
import com.example.furmate.models.Task
import com.example.furmate.utils.MarginItemDecoration
import com.example.furmate.utils.URIToBlob.Companion.uriToBlob
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale


class FormScheduleFragment() : Fragment() {
    private var isSchedule: Boolean? = null;
    private var taskTitle: String? = null
    private var taskDate: String? = null
    private var taskWhere: String? = null
    private var taskPet: String? = null
    private var taskNotes: String? = null
    private var isEditMode: Boolean = false
    private var documentId: String? = null

    private lateinit var submitButton: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var formEntries: ArrayList<String>

    // Firestore Collections
    private lateinit var scheduleCollection: CollectionReference
    private lateinit var recordCollection: CollectionReference

    // APIs
    private lateinit var taskRepositoryAPI: TaskRepositoryAPI
    private lateinit var recordRepositoryAPI: RecordRepositoryAPI

    private val hintToFieldMap = mapOf(
        "Title" to "name",
        "Date" to "date",
        "Pet" to "petName",
        "Notes" to "notes",
        "Image" to "image"
    )

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                formEntries[3] = selectedImageUri.toString()
                recyclerView.adapter?.notifyItemChanged(3) // Update UI
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            isSchedule = it.getBoolean("is_schedule")
            taskTitle = it.getString("task_title")
            taskDate = it.getString("task_date")
            taskWhere = it.getString("task_where")
            taskPet = it.getString("task_pet")
            taskNotes = it.getString("task_notes")
            documentId = it.getString("document_id")
        }

        // restore data from when screen was unloaded
        if (savedInstanceState != null) {
            taskTitle = savedInstanceState.getString("Title")
            taskDate = savedInstanceState.getString("Date")
            taskWhere = savedInstanceState.getString("Where")
            taskPet = savedInstanceState.getString("Pet")
            taskNotes = savedInstanceState.getString("Notes")
            documentId = savedInstanceState.getString("document_id")
        }

        isEditMode = documentId != null
//        Log.d("FormScheduleFragment", "isSchedule: $isSchedule")
//        Log.d("FormScheduleFragment", "taskTitle: $taskTitle")
//        Log.d("FormScheduleFragment", "taskDate: $taskDate")
//        Log.d("FormScheduleFragment", "taskWhere: $taskWhere")
//        Log.d("FormScheduleFragment", "taskNotes: $taskNotes")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.form_schedule, container, false)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.input_wrapper)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(MarginItemDecoration(16))

        // Initialize the Firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Change the header depending on the form type
        val header = rootView.findViewById<TextView>(R.id.form_header)
        if (isSchedule!!) {
            // Initialize the Firestore collection
            scheduleCollection = firestore.collection("Schedule")
            taskRepositoryAPI = TaskRepositoryAPI(scheduleCollection)
            header.text = "Add a new schedule"
        } else {
            recordCollection = firestore.collection("Record")
            recordRepositoryAPI = RecordRepositoryAPI(recordCollection)
            header.text = "Add a new record"
        }

        val composableInputs = if (isSchedule!!) {
            listOf("Title", "Date", "Pet", "Notes")
        } else {
            listOf("Title", "Pet", "Book", "Image", "Notes")
        }

        // Pre-fill the fields if task data exists
        formEntries = ArrayList();
        if (taskTitle == null) {
            for (i in 1..composableInputs.size) {
                formEntries.add("")
            }
        } else if (isSchedule!!) {
            formEntries.add(taskTitle ?: "")
            formEntries.add(taskDate ?: "")
            formEntries.add(taskPet ?: "")
            formEntries.add(taskNotes ?: "")
        } else {
            formEntries.add(taskTitle ?: "")
            formEntries.add(taskPet ?: "")
            formEntries.add(taskNotes ?: "")
        }

        if (formEntries.any { it.isNotEmpty() }) {
            header.visibility = View.GONE
        }
        Log.d("FormScheduleFragment", "Form Entries: $formEntries")
        val adapter = ComposableInputAdapter(composableInputs, formEntries, requireContext(), filePickerLauncher)
        Log.d("FormScheduleFragment", "composableInputs: $composableInputs")
        recyclerView.adapter = adapter

        submitButton = rootView.findViewById(R.id.submit_btn);
        submitButton.setOnClickListener {
            val taskData = mutableMapOf<String, String>()

            for (child in recyclerView.children) {
                val holder = recyclerView.getChildViewHolder(child)
                val hint = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div).hint.toString()
                val key = hintToFieldMap[hint] ?: hint.lowercase(Locale.getDefault())

                // Safely access the TextInputEditText and check for null
                val inputField = holder.itemView.findViewById<TextInputEditText>(R.id.input_field)

                // Only proceed if the input field is not null
                if (inputField != null) {
                    val value = inputField.text.toString()
                    Log.d("FormScheduleFragment", "Hint: $hint, FieldName: $key, Value: $value")

                    if (key.isNotEmpty() && value.isNotEmpty()) {
                        taskData[key] = if (key == "date") {
                            // Ensure the date is always in a consistent format
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val parsedDate = inputFormat.parse(value)
                            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            parsedDate?.let { outputFormat.format(it) } ?: value
                        } else {
                            value
                        }
                    }
                } else {
                    Log.e("FormScheduleFragment", "TextInputEditText is null for field: $key")
                }
            }

            Log.d("FormScheduleFragment", "is editmode Value: $isEditMode")
            if (isEditMode) {
                // Update existing document
                Log.d("FormScheduleFragment", "before api call")
                val updatedTask = taskData.mapValues { it.value as Any }
                documentId?.let { id ->
                    taskRepositoryAPI.updateTask(id, updatedTask) { success, exception ->
                        if (success) {
                            Log.d("FormScheduleFragment", "Task successfully updated")
                        } else {
                            Log.e("FormScheduleFragment", "Error updating task", exception)
                        }
                    }
                }
            } else {
                Log.d("FormScheduleFragment","isSchedule: $isSchedule")
                val userID = Firebase.auth.currentUser?.uid ?: ""
                if (isSchedule!!) {
                    // Add the task to the Firestore database
                    val task = Task(
                        name = taskData["name"] ?: "",
                        date = taskData["date"] ?: "1970-01-01", // Default date value
                        petName = taskData["petName"] ?: "",
                        notes = taskData["notes"],
                        userID = userID,
                    )
                    taskRepositoryAPI.addTask(task)
                } else {
                    // Add the record to the Firestore database
                    var imageBlob: Blob? = null
                    taskData["image"]?.let {
                        imageBlob = uriToBlob(Uri.parse(it), requireContext())
                    }
                    val record = Record(
                        name = taskData["name"] ?: "",
                        petName = taskData["petName"] ?: "",
                        notes = taskData["notes"] ?: "",
                        imageURI = taskData["image"] ?: "",
                        image = imageBlob,
                        userID = userID,
                        bookID = "",
                    )
                    recordRepositoryAPI.addRecord(record)
                    Log.d("FormScheduleFragment", "Record submitted")
                }
            }

            // Test for differentiating the return value
            // Log.d("FormScheduleFragment", "onCreateView: $taskData")
            (requireActivity() as? HomeActivity)?.showFABs()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }



        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        for (child in recyclerView.children) {
            val holder = recyclerView.getChildViewHolder(child)
            val hint = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div).hint.toString()

            // Check if the input is a TextInputEditText or a dropdown
            val textField = holder.itemView.findViewById<TextInputEditText>(R.id.input_field)
            val spinner = holder.itemView.findViewById<Spinner>(R.id.input_field_spinner) // Assuming this is the dropdown

            // For TextInputEditText
            if (textField != null) {
                val value = textField.text.toString()
                outState.putString(hint, value)
            }
            // For Spinner (dropdown)
            else if (spinner != null) {
                val selectedItem = spinner.selectedItem?.toString() ?: ""
                outState.putString(hint, selectedItem)
            }
        }
    }


    companion object {
        // Function to create a new instance with pre-filled data
        fun newInstance(
            isSchedule: Boolean, title: String?, date: String?, where: String?,
            pet: String?, notes: String?, documentId: String? = null
        ): FormScheduleFragment {
            val fragment = FormScheduleFragment()
            val args = Bundle()
            args.putBoolean("is_schedule", isSchedule)
            args.putString("task_title", title)
            args.putString("task_date", date)
            args.putString("task_where", where)
            args.putString("task_pet", pet)
            args.putString("task_notes", notes)
            args.putString("document_id", documentId)
            fragment.arguments = args
            return fragment
        }
    }
}
