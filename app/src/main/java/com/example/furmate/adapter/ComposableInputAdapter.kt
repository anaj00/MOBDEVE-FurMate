package com.example.furmate.adapter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
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
        Log.d("ComposableInputAdapter", "Hint: $hint, Prefilled Value: ${prefilledValues.getOrNull(position)}")

        when (hint) {
            "Date", "Birthday" -> {
                holder.inputText.inputType = android.text.InputType.TYPE_NULL // Disable keyboard input
                holder.inputText.setOnClickListener {
                    showDatePicker(holder.inputText)
                }
            }

            "File", "Image", "Profile Picture" -> {
                // Enable custom end icon
                holder.inputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                holder.inputLayout.setEndIconDrawable(com.google.firebase.appcheck.interop.R.drawable.common_google_signin_btn_text_light_normal) // TODO: Change image

                // Handle click on the end icon
                holder.inputLayout.setEndIconOnClickListener {
                    openFileChooser(holder.inputText)
                }
            }
        }
    }

    private fun openFileChooser(inputField: TextInputEditText) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*" // Use "image/*" for images only
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        // Start the file chooser intent
        (context as AppCompatActivity).startActivityForResult(intent, REQUEST_FILE_PICKER)
    }

    override fun getItemCount(): Int {
        return hints.size  // Size based on the number of fields
    }

    private fun showDatePicker(inputField: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Show the date picker
        datePicker.show(
            (context as AppCompatActivity).supportFragmentManager,
            "MATERIAL_DATE_PICKER"
        )

        // Handle date selection
        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection

            // Step 2: Create MaterialTimePicker after selecting date
            val timePicker = MaterialTimePicker.Builder()
                .setTitleText("Select Time")
                .setTimeFormat(TimeFormat.CLOCK_24H) // Use TimeFormat.CLOCK_12H for 12-hour format
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .build()

            // Show the time picker
            timePicker.show(
                (context as AppCompatActivity).supportFragmentManager,
                "MATERIAL_TIME_PICKER"
            )

            // Handle time selection
            timePicker.addOnPositiveButtonClickListener {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)

                // Format and display the selected date and time
                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                inputField.setText(dateTimeFormat.format(calendar.time))
            }
        }
    }

    companion object {
        const val REQUEST_FILE_PICKER = 1001
    }
}
