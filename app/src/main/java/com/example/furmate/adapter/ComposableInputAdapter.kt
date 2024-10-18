package com.example.furmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R

class ComposableInputAdapter(private val hints: List<String>) :
    RecyclerView.Adapter<ComposableInputAdapter.InputViewHolder>() {

    class InputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inputField: EditText = itemView.findViewById(R.id.input_username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.composable_input, parent, false)
        return InputViewHolder(view)
    }

    override fun onBindViewHolder(holder: InputViewHolder, position: Int) {
        holder.inputField.hint = hints[position]
    }

    override fun getItemCount(): Int {
        return hints.size
    }
}
