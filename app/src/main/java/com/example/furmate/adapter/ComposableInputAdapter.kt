package com.example.furmate.adapter

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class ComposableInputAdapter(
    private val hints: List<String>,
    private val prefilledValues: List<String>,
    private val context: Context,
    private val filePickerLauncher: ActivityResultLauncher<Intent>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inputState = mutableMapOf<Int, Boolean>() // Track input enabled/disabled state

    companion object {
        const val TYPE_TEXT_INPUT = 0
        const val TYPE_DROPDOWN = 1
        const val REQUEST_FILE_PICKER = 1001
    }

    // View holder for regular text input
    class TextInputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputLayout: TextInputLayout = itemView.findViewById(R.id.enter_hint_div)
        val inputText: TextInputEditText = itemView.findViewById(R.id.input_field)
    }

    // View holder for dropdown (spinner) input
    class DropdownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputLayout: TextInputLayout = itemView.findViewById(R.id.enter_hint_div)
        val spinner: Spinner = itemView.findViewById(R.id.input_field_spinner)
    }

    init {
        // Initialize all inputs as enabled by default
        for (i in hints.indices) {
            inputState[i] = true
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Determine if we should use a dropdown or text input based on the hint
        return if (hints[position] == "Pet" || hints[position] == "Book") {
            TYPE_DROPDOWN
        } else {
            TYPE_TEXT_INPUT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DROPDOWN -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.composable_input_dropdown, parent, false)
                DropdownViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.composable_input, parent, false)
                TextInputViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hint = hints[position]
        val prefilledValue = prefilledValues[position]

        // Set the hint text and populate the input field
        when (holder) {
            is TextInputViewHolder -> {
                holder.inputLayout.hint = hint
                if (prefilledValue.isNotEmpty()) {
                    holder.inputText.setText(prefilledValue)
                }
                toggleInput(holder.inputText, inputState[position] ?: true)

                // Handle specific input types like DatePicker for "Birthday" and "Date"
                when (hint) {
                    "Birthday", "Date" -> {
                        holder.inputText.inputType = android.text.InputType.TYPE_NULL
                        holder.inputText.setOnClickListener {
                            showDatePicker(holder.inputText)
                        }
                    }
                    "File", "Image", "Profile Picture" -> {
                        holder.inputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                        holder.inputLayout.setEndIconDrawable(android.R.drawable.ic_menu_gallery)

                        // Handle click on the end icon
                        holder.inputLayout.setEndIconOnClickListener {
                            openFileChooser(holder.inputText)
                        }
                    }
                    else -> {
                        // Default behavior for other fields (like "Name")
                        holder.inputText.inputType = android.text.InputType.TYPE_CLASS_TEXT // Ensure it's set as text
                        holder.inputText.isEnabled = true  // Ensure the field is enabled
                    }
                }
            }
            is DropdownViewHolder -> {
                holder.inputLayout.hint = hint

                // Define different sets of options based on the field (hint)
                val options: List<String> = when (hint) {
                    "Pet" -> listOf("Pet", "Booboo", "Bird", "Rabbit") // TODO: Add the options
                    "Book" -> listOf("Book", "Non-fiction", "Science Fiction", "Fantasy") // TODO: Add the options
                    else -> listOf("Option 1", "Option 2", "Option 3") // Default options
                }

                // Set up the spinner with the appropriate options
                val spinnerAdapter = ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_item,
                    options
                )

                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                holder.spinner.adapter = spinnerAdapter

                // Set previously selected value, if any
                // If the prefilled value doesn't match any of the options, select the first option.
                val selectedPosition = spinnerAdapter.getPosition(prefilledValue)
                if (selectedPosition >= 0) {
                    holder.spinner.setSelection(selectedPosition)
                } else {
                    // Set a default selection if prefilledValue is empty or invalid
                    holder.spinner.setSelection(0) // Select the first option by default
                }

                toggleInput(holder.spinner, inputState[position] ?: true)
            }
        }
    }

    private fun openFileChooser(inputField: TextInputEditText) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*" // Use "image/*" for images only
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        filePickerLauncher?.launch(intent)
    }

    override fun getItemCount(): Int = hints.size

    private fun showDatePicker(inputField: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(
            (context as AppCompatActivity).supportFragmentManager,
            "MATERIAL_DATE_PICKER"
        )

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            inputField.setText(dateFormat.format(calendar.time))
        }
    }

    fun setInputEnabled(isEnabled: Boolean) {
        // Update the state for all inputs
        for (i in inputState.keys) {
            inputState[i] = isEnabled
            notifyItemChanged(i) // Refresh only the affected items
        }
    }

    private fun toggleInput(view: View, isEnabled: Boolean) {
        view.isEnabled = isEnabled
        view.alpha = if (isEnabled) 1.0f else 0.5f
    }
}