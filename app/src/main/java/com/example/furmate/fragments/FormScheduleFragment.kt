package com.example.furmate

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.db.TaskRepositoryAPI
import com.example.furmate.models.Task
import com.example.furmate.utils.MarginItemDecoration
import com.example.furmate.utils.URIToBlob.Companion.uriToBlob
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URI
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
            taskRepositoryAPI = TaskRepositoryAPI(recordCollection)
            header.text = "Add a new record"
        }

        val composableInputs = if (isSchedule!!) {
            listOf("Title", "Date", "Pet", "Notes")
        } else {
            listOf("Title", "Pet", "Notes", "Image")
        }

        // Pre-fill the fields if task data exists
        formEntries = ArrayList();
        val defaultInputValues = if (taskTitle != null) {
            if (isSchedule!!) {
                listOf(
                    taskTitle ?: "",
                    taskDate ?: "",
                    taskPet ?: "",
                    taskNotes ?: ""
                )
            } else {
                listOf(taskTitle ?: "",
                    taskPet ?: "",
                    taskNotes ?: "")
            }
        } else {
            List(composableInputs.size) { "" } // Empty strings for new entries
        }

        for (value in defaultInputValues) {
            formEntries.add(value)
        }

        if (formEntries.any { it.isNotEmpty() }) {
            header.visibility = View.GONE
        }

        val adapter = ComposableInputAdapter(composableInputs, formEntries, requireContext(), filePickerLauncher)
        recyclerView.adapter = adapter

        submitButton = rootView.findViewById(R.id.submit_btn);
        submitButton.setOnClickListener {
            val taskData = mutableMapOf<String, String>()

            for (child in recyclerView.children) {
                val holder = recyclerView.getChildViewHolder(child)
                val hint = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div).hint.toString()
                val key = hintToFieldMap[hint] ?: hint.lowercase(Locale.getDefault())
                val value = holder.itemView.findViewById<TextInputEditText>(R.id.input_field).text.toString()

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
                if (isSchedule!!) {
                    // Add the task to the Firestore database
                    val task = Task(
                        name = taskData["name"] ?: "",
                        date = taskData["date"] ?: "1970-01-01 00:00", // Default date value
                        petName = taskData["petName"] ?: "",
                        notes = taskData["notes"]
                    )
                    taskRepositoryAPI.addTask(task)
                } else {
                    // Add the record to the Firestore database
                    var imageBlob: Blob? = null;
                    taskData["image"]?.let {
                        imageBlob = uriToBlob(Uri.parse(it), requireContext())
                    }

                    val record = Task(
                        name = taskData["name"] ?: "",
                        petName = taskData["petName"] ?: "",
                        notes = taskData["notes"] ?: "",
                        imageURI = taskData["image"] ?: "",
                        image = imageBlob
                    )
                    taskRepositoryAPI.addTask(record)
                    Log.d("FormScheduleFragment", "Record submitted")
                }
            }
            // Test for differentiating the return value
            Log.d("FormScheduleFragment", "onCreateView: $taskData")
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        for (child in recyclerView.children) {
            val holder = recyclerView.getChildViewHolder(child)
            val key = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div).hint.toString()
            val value = holder.itemView.findViewById<TextInputEditText>(R.id.input_field).text.toString()

            outState.putString(key, value)
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
