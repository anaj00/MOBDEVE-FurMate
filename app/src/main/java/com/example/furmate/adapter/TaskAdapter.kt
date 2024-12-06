package com.example.furmate.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furmate.R
import com.example.furmate.models.Task
import com.example.furmate.utils.ImageLoader
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TaskAdapter(
    private val tasks: MutableList<Task>,
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

        // set pet image
        Log.d("TaskAdapter", task.toString())

        val firestore = Firebase.firestore
        firestore.collection("Pet")
            .whereEqualTo("id", task.petName)
            .get()
            .addOnSuccessListener {documents ->

                val pet = documents.firstOrNull()
                Log.d("TaskAdapter", pet.toString())
                val imageView = holder.itemView.findViewById<ImageView>(R.id.task_icon)
                pet?.getBlob("image")?.let {
                    imageView.setImageBitmap(
                        ImageLoader.fromBlobScaled(it, 100, 100)
                    )
                }
            }

        // Handle card click to open the form with pre-filled values
        holder.taskCard.setOnClickListener {
            onItemClick(task)  // Trigger the onClick action with the task data
        }
    }

    override fun getItemCount(): Int = tasks.size

    // Function to update the list of tasks
    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
