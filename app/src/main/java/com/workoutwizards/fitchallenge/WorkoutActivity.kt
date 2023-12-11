package com.workoutwizards.fitchallenge

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.workoutwizards.fitchallenge.databinding.ActivityWorkoutBinding
import com.workoutwizards.fitchallenge.model.WorkoutItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkoutBinding
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager
    private val db = Firebase.firestore
    private lateinit var workouts: MutableList<WorkoutItem>
    private var selectedDate = Calendar.getInstance()
    private var selectedTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerViewManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.layoutManager = recyclerViewManager
        binding.recyclerView.setHasFixedSize(true)

        db.collection("users")
            .document(MainActivity.user.uid)
            .collection("workouts")
            .get()
            .addOnSuccessListener { result ->
                workouts = result.toObjects(WorkoutItem::class.java)
                if (workouts.isNotEmpty()) {
                    binding.recyclerView.adapter = WorkoutRecyclerAdapter(
                        workouts
                    )
                }
            }
            .addOnFailureListener {
                workouts = emptyList<WorkoutItem>().toMutableList()
                binding.recyclerView.adapter = WorkoutRecyclerAdapter(workouts)
            }

        val fab = binding.addWorkout
        fab.setOnClickListener {
            showAddItemDialog()
        }

        val fab2 = binding.returnToMenuW
        fab2.setOnClickListener {
            finish()
        }
    }

    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_workout_item, null)
        val spinnerWorkoutType = dialogView.findViewById<Spinner>(R.id.spinnerWorkoutType)
        val textViewPickDateTimeWorkout = dialogView.findViewById<TextView>(
            R.id
                .textViewPickDateTimeWorkout
        )

        ArrayAdapter.createFromResource(
            this,
            R.array.workout_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinnerWorkoutType.adapter = adapter
        }

        textViewPickDateTimeWorkout.setOnClickListener {
            showDateTimePickerDialog(textViewPickDateTimeWorkout)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Workout Record")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val combinedDateTime = Calendar.getInstance()
                combinedDateTime.set(
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH),
                    selectedTime.get(Calendar.HOUR_OF_DAY),
                    selectedTime.get(Calendar.MINUTE)
                )

                val workout = hashMapOf(
                    "type" to spinnerWorkoutType.selectedItem.toString(),
                    "date_time" to Date(combinedDateTime.timeInMillis).toString()
                )
                db.collection("users")
                    .document(MainActivity.user.uid)
                    .collection("workouts")
                    .add(workout)

                var typeString = spinnerWorkoutType.selectedItem.toString()
                var dateString = Date(combinedDateTime.timeInMillis).toString()
                var workoutListItem = WorkoutItem(typeString, dateString)

                workouts.add(workoutListItem)
                binding.recyclerView.adapter = WorkoutRecyclerAdapter(workouts)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showDateTimePickerDialog(field: TextView) {
        // Date Picker Dialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate.set(year, month, day)

                // Time Picker Dialog
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedTime.set(Calendar.MINUTE, minute)

                        val combinedDateTime = Calendar.getInstance()
                        combinedDateTime.set(
                            selectedDate.get(Calendar.YEAR),
                            selectedDate.get(Calendar.MONTH),
                            selectedDate.get(Calendar.DAY_OF_MONTH),
                            selectedTime.get(Calendar.HOUR_OF_DAY),
                            selectedTime.get(Calendar.MINUTE)
                        )
                        val dateFormat =
                            SimpleDateFormat("HH:mm, dd MMM, yyyy", Locale.getDefault())
                        field.text = dateFormat.format(combinedDateTime.time)
                    },
                    selectedTime.get(Calendar.HOUR_OF_DAY),
                    selectedTime.get(Calendar.MINUTE),
                    false
                )

                // Show Time Picker Dialog after Date Picker Dialog
                timePickerDialog.show()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        // Show Date Picker Dialog
        datePickerDialog.show()
    }
}