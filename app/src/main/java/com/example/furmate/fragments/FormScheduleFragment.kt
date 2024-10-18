package com.example.furmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.adapter.ComposableInputAdapter
import com.example.furmate.R

class FormScheduleFragment(private val isSchedule: Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.form_schedule, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val backButton = rootView.findViewById<Button>(R.id.back_btn)
        backButton.setOnClickListener {
            activity?.onBackPressed() // Go back to the previous fragment
        }

        val composableInputs = if (isSchedule) {
            // Schedule form hints
            listOf("Title", "Date", "Where", "Pet", "Notes")
        } else {
            // Record form hints
            listOf("Title", "Pet", "Notes")
        }

        val adapter = ComposableInputAdapter(composableInputs)
        recyclerView.adapter = adapter

        return rootView
    }
}
