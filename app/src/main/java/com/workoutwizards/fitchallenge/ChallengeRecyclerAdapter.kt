package com.workoutwizards.fitchallenge


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.workoutwizards.fitchallenge.model.ChallengeItem
import com.workoutwizards.fitchallenge.model.SetsAndRepsItem

import java.text.SimpleDateFormat
import java.util.TimeZone

class ChallengeRecyclerAdapter(private val dataSet: List<ChallengeItem>) : RecyclerView
.Adapter<ChallengeRecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewChallengeName: TextView
        var textViewDistance: TextView
        var textViewStartDate: TextView
        var textViewEndDate: TextView

        init {
            textViewChallengeName = view.findViewById(R.id.textViewChallengeName)
            textViewDistance = view.findViewById(R.id.textViewDistance)
            textViewStartDate = view.findViewById(R.id.textViewStartDate)
            textViewEndDate = view.findViewById(R.id.textViewEndDate)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view =
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.recycler_challenge_item,
                viewGroup, false
            )

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (dataSet.isNotEmpty()) {

            viewHolder.textViewChallengeName.text = dataSet[position].challenge_name
            viewHolder.textViewDistance.text = "${dataSet[position].distance}KM left!"
            viewHolder.textViewStartDate.text = "From: ${dataSet[position].start_date}"
            viewHolder.textViewEndDate.text = "To: ${dataSet[position].end_date}"
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataSet.size
    }
    fun updateData(newData: List<ChallengeItem>) {
        // Update the dataset and notify the adapter about the changes
        (dataSet as MutableList).clear()
        (dataSet as MutableList).addAll(newData)
        notifyDataSetChanged()
    }

}