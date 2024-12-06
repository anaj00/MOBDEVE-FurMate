package com.example.furmate.adapter

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.HomeActivity
import com.example.furmate.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ComposableInputAdapter(
    private val hints: List<String>,
    private val prefilledValues: List<String>,
    private val context: Context,
    private val filePickerLauncher: ActivityResultLauncher<Intent>? = null
) : RecyclerView.Adapter<ComposableInputAdapter.InputViewHolder>() {

    private val inputState = mutableMapOf<Int, Boolean>() // Track input enabled/disabled state

    class InputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputLayout: TextInputLayout = itemView.findViewById(R.id.enter_hint_div)
        val inputText: TextInputEditText = itemView.findViewById(R.id.input_field)
    }

    init {
        // Initialize all inputs as enabled by default
        for (i in hints.indices) {
            inputState[i] = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.composable_input, parent, false)
        return InputViewHolder(view)
    }

    override fun onBindViewHolder(holder: InputViewHolder, position: Int) {
        val hint = hints[position]
        holder.inputLayout.hint = hint

        Log.d("ComposableInputAdapter", "Binding item at position: $position, hint: $hint")

        if (prefilledValues[position].isNotEmpty()) {
            holder.inputText.setText(prefilledValues[position])
        }

        // Apply the enabled/disabled state to the input field
        toggleInput(holder, inputState[position] ?: true)

        // Handle specific input types based on the hint
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

        Log.d("ComposableInputAdapter", "onBindViewHolder: $hint")
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

    fun toggleInput(holder: InputViewHolder, isEnabled: Boolean) {
        holder.inputText.isEnabled = isEnabled
        holder.inputLayout.isEnabled = isEnabled
        holder.inputText.alpha = if (isEnabled) 1.0f else 0.5f // Adjust opacity to reflect state
    }

    companion object {
        const val REQUEST_FILE_PICKER = 1001
    }
}
