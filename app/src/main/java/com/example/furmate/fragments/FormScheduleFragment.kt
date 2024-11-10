package com.example.furmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.adapter.ComposableInputAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class FormScheduleFragment(private val isSchedule: Boolean) : Fragment() {

    private var taskTitle: String? = null
    private var taskDate: String? = null
    private var taskWhere: String? = null
    private var taskPet: String? = null
    private var taskNotes: String? = null

    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            taskTitle = it.getString("task_title")
            taskDate = it.getString("task_date")
            taskWhere = it.getString("task_where")
            taskPet = it.getString("task_pet")
            taskNotes = it.getString("task_notes")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.form_schedule, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.input_wrapper)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val header = rootView.findViewById<TextView>(R.id.form_header)
        if (isSchedule) {
            header.text = "Add a new schedule"
        } else {
            header.text = "Add a new record"
        }

        val backButton = rootView.findViewById<Button>(R.id.back_btn)
        backButton.setOnClickListener {
            activity?.onBackPressed() // Go back to the previous fragment
        }

        val composableInputs = if (isSchedule) {
            listOf("Title", "Date", "Where", "Pet", "Notes")
        } else {
            listOf("Title", "Pet", "Notes")
        }

        // Pre-fill the fields if task data exists
        val inputValues = if (taskTitle != null) {
            if (isSchedule) {
                listOf(taskTitle ?: "", taskDate ?: "", taskWhere ?: "", taskPet ?: "", taskNotes ?: "")
            } else {
                listOf(taskTitle ?: "", taskPet ?: "", taskNotes ?: "")
            }
        } else {
            List(composableInputs.size) { "" } // Empty strings for new entries
        }

        val adapter = ComposableInputAdapter(composableInputs, inputValues)
        recyclerView.adapter = adapter

        submitButton = rootView.findViewById(R.id.create_account_btn);
        submitButton.setOnClickListener {
            val ret = Bundle()

            for (child in recyclerView.children) {
                val holder = recyclerView.getChildViewHolder(child)
                val key = holder.itemView.findViewById<TextInputLayout>(R.id.enter_hint_div).hint.toString()
                val value = holder.itemView.findViewById<TextInputEditText>(R.id.input_field).text.toString()

                val formattedKey = "KEY_" + key.uppercase()
                ret.putString(formattedKey, value)
            }

            parentFragmentManager.setFragmentResult("KEY_NEW_SCHEDULE", ret)
            activity?.onBackPressed()
        }

        return rootView
    }

    companion object {
        // Function to create a new instance with pre-filled data
        fun newInstance(
            isSchedule: Boolean, title: String?, date: String?, where: String?,
            pet: String?, notes: String?
        ): FormScheduleFragment {
            val fragment = FormScheduleFragment(isSchedule)
            val args = Bundle()
            args.putString("task_title", title)
            args.putString("task_date", date)
            args.putString("task_where", where)
            args.putString("task_pet", pet)
            args.putString("task_notes", notes)
            fragment.arguments = args
            return fragment
        }
    }
}
