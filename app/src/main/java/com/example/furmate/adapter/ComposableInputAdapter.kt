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
import java.text.SimpleDateFormat
import java.util.*

class ComposableInputAdapter(
    private val hints: List<String>,
    private val prefilledValues: List<String>,
    private val context: Context
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

        if (prefilledValues[position].isNotEmpty()) {
            holder.inputText.setText(prefilledValues[position])
        }

        // Apply the enabled/disabled state to the input field
        toggleInput(holder, inputState[position] ?: true)

        when (hint) {
            "Date", "Birthday" -> {
                holder.inputText.inputType = android.text.InputType.TYPE_NULL
                holder.inputText.setOnClickListener {
                    showDatePicker(holder.inputText)
                }
            }
            "File", "Image", "Profile Picture" -> {
                holder.inputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                holder.inputLayout.setEndIconDrawable(android.R.drawable.ic_menu_gallery)
                holder.inputLayout.setEndIconOnClickListener {
                    openFileChooser(holder.inputText)
                }
            }
        }
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

    private fun openFileChooser(inputField: TextInputEditText) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        (context as AppCompatActivity).startActivityForResult(intent, REQUEST_FILE_PICKER)
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
