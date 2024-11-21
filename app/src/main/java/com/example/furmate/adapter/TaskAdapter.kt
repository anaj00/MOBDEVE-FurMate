package com.example.furmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.example.furmate.models.Task
import com.google.android.material.card.MaterialCardView

class TaskAdapter(
    private val tasks: List<Task>,
    private val onItemClick: (Task) -> Unit  // Lambda function to handle clicks
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCard: MaterialCardView = itemView.findViewById(R.id.comp_schedule_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.composable_schedule_card, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Set task data here
        holder.itemView.findViewById<TextView>(R.id.task_title).text = task.name
        holder.itemView.findViewById<TextView>(R.id.task_time).text = task.date

        // Handle card click to open the form with pre-filled values
        holder.taskCard.setOnClickListener {
            onItemClick(task)  // Trigger the onClick action with the task data
        }
    }

    override fun getItemCount(): Int = tasks.size
}
