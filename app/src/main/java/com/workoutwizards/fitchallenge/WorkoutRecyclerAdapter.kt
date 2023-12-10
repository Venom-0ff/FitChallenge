package com.workoutwizards.fitchallenge


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.workoutwizards.fitchallenge.model.WorkoutItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class WorkoutRecyclerAdapter(private val dataSet: List<WorkoutItem>) : RecyclerView
.Adapter<WorkoutRecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewDateTime: TextView
        var textViewType: TextView

        init {
            textViewType = view.findViewById(R.id.textViewTypeWorkout)
            textViewDateTime = view.findViewById(R.id.textViewDateTimeWorkout)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view =
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.recycler_workout_item,
                viewGroup, false
            )

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (dataSet.isNotEmpty()) {
            if (dataSet[position].date_time!!.isNotBlank()) {
                val est = TimeZone.getTimeZone("EST")
                val sourceFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
                val destFormat = SimpleDateFormat("dd-MMM-YYYY HH:mm aa")
                sourceFormat.timeZone = est
                val convertedDate = sourceFormat.parse(dataSet[position].date_time)
                val formattedDate = SimpleDateFormat("HH:mm, dd MMM, yyyy").format(convertedDate)
                viewHolder.textViewDateTime.text = formattedDate
            }

            viewHolder.textViewType.text = dataSet[position].type
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataSet.size
    }
}