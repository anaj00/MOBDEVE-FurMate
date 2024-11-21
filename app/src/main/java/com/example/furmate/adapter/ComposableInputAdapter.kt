package com.example.furmate.adapter

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import java.text.SimpleDateFormat
import java.util.*

class ComposableInputAdapter(
    private val hints: List<String>,   // List of field hints (e.g., "Title", "Date")
    private val prefilledValues: List<String>,  // List of prefilled values (e.g., "Task 1", "2023-01-01")
    private val context: Context
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
        val hint = hints[position]
        holder.inputLayout.hint = hint

        // Set the prefilled value if provided
        if (prefilledValues[position].isNotEmpty()) {
            holder.inputText.setText(prefilledValues[position])
        }

        if (hint == "Date" || hint == "Birthday") {
            holder.inputText.inputType = android.text.InputType.TYPE_NULL // Disable keyboard input
            holder.inputText.setOnClickListener {
                showDatePicker(holder.inputText)
            }
        }
    }

    override fun getItemCount(): Int {
        return hints.size  // Size based on the number of fields
    }

    private fun showDatePicker(inputField: TextInputEditText) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                // Format date and set it to the input field
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                inputField.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
