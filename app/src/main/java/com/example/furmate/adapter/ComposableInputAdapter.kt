package com.example.furmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R

class ComposableInputAdapter(
    private val hints: List<String>,   // List of field hints (e.g., "Title", "Date")
    private val prefilledValues: List<String>  // List of prefilled values (e.g., "Task 1", "2023-01-01")
) : RecyclerView.Adapter<ComposableInputAdapter.InputViewHolder>() {

    class InputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputLayout: TextInputLayout = itemView.findViewById(R.id.enter_hint_div)  // TextInputLayout
        val inputText: TextInputEditText = itemView.findViewById(R.id.input_field)  // TextInputEditText within the layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.composable_input, parent, false)
        return InputViewHolder(view)
    }

    override fun onBindViewHolder(holder: InputViewHolder, position: Int) {
        // Set the hint
        holder.inputLayout.hint = hints[position]

        // Set the prefilled value if provided
        if (prefilledValues[position].isNotEmpty()) {
            holder.inputText.setText(prefilledValues[position])
        }
    }

    override fun getItemCount(): Int {
        return hints.size  // Size based on the number of fields
    }
}
