package com.workoutwizards.fitchallenge


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.workoutwizards.fitchallenge.model.SetsAndRepsItem

import java.text.SimpleDateFormat
import java.util.TimeZone

class RoutineRecyclerAdapter(private val dataSet: List<SetsAndRepsItem>) : RecyclerView
.Adapter<RoutineRecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewExerciseName: TextView
        var textViewSets: TextView
        var textViewReps: TextView

        init {
            textViewExerciseName = view.findViewById(R.id.textViewExerciseName)
            textViewSets = view.findViewById(R.id.textViewReps)
            textViewReps = view.findViewById(R.id.textViewSets)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view =
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.recycler_routine_item,
                viewGroup, false
            )

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (dataSet.isNotEmpty()) {

            viewHolder.textViewExerciseName.text = dataSet[position].exercise_name
            viewHolder.textViewSets.text = "Sets: ${dataSet[position].sets}"
            viewHolder.textViewReps.text = "Reps: ${dataSet[position].reps}"
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataSet.size
    }
    fun updateData(newData: List<SetsAndRepsItem>) {
        // Update the dataset and notify the adapter about the changes
        (dataSet as MutableList).clear()
        (dataSet as MutableList).addAll(newData)
        notifyDataSetChanged()
    }

}