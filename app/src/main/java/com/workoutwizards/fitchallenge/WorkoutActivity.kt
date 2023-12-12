package com.workoutwizards.fitchallenge

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.workoutwizards.fitchallenge.databinding.ActivityWorkoutBinding
import com.workoutwizards.fitchallenge.model.CardioItem
import com.workoutwizards.fitchallenge.model.SetsAndRepsItem

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WorkoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkoutBinding
    private lateinit var recyclerViewManager1: RecyclerView.LayoutManager
    private lateinit var recyclerViewManager2: RecyclerView.LayoutManager
    private val db = Firebase.firestore
    private lateinit var setsAndRepsList: MutableList<SetsAndRepsItem>
    private lateinit var cardioList: MutableList<CardioItem>
    private var selectedDate = Calendar.getInstance()
    private var selectedTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerViewManager1 = LinearLayoutManager(applicationContext)
        recyclerViewManager2 = LinearLayoutManager(applicationContext)

        //set recyclerView
        binding.recyclerViewCardio.layoutManager = recyclerViewManager1
        binding.recyclerViewSetsAndReps.layoutManager = recyclerViewManager2

        // Grab sets and reps from user
        db.collection("users")
            .document(MainActivity.user.uid)
            .collection("setsandreps")
            .get()
            .addOnSuccessListener { result ->
                setsAndRepsList = result.toObjects(SetsAndRepsItem::class.java)
                if (setsAndRepsList.isNotEmpty()) {
                    binding.recyclerViewSetsAndReps.adapter = SetsAndRepsRecyclerAdapter(
                        setsAndRepsList
                    )
                }
            }
            .addOnFailureListener {
                setsAndRepsList = emptyList<SetsAndRepsItem>().toMutableList()
                binding.recyclerViewSetsAndReps.adapter = SetsAndRepsRecyclerAdapter(setsAndRepsList)
            }

        // Grab cardio from user
        db.collection("users")
            .document(MainActivity.user.uid)
            .collection("cardio")
            .get()
            .addOnSuccessListener { result ->
                cardioList = result.toObjects(CardioItem::class.java)
                if (cardioList.isNotEmpty()) {
                    binding.recyclerViewCardio.adapter = CardioRecyclerAdapter(
                        cardioList
                    )
                }
            }
            .addOnFailureListener {
                cardioList = emptyList<CardioItem>().toMutableList()
                binding.recyclerViewCardio.adapter = CardioRecyclerAdapter(cardioList)
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

        // Listener to display relevant constraint, whether its cardio or 'sets and reps'
        spinnerWorkoutType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (spinnerWorkoutType.selectedItem == "Cardio")
                {
                    dialogView.findViewById<ConstraintLayout>(R.id.setsAndRepsConstraint).visibility =
                        View.INVISIBLE
                    dialogView.findViewById<ConstraintLayout>(R.id.cardioConstraint).visibility =
                        View.VISIBLE
                } else
                {
                    dialogView.findViewById<ConstraintLayout>(R.id.setsAndRepsConstraint).visibility =
                        View.VISIBLE
                    dialogView.findViewById<ConstraintLayout>(R.id.cardioConstraint).visibility =
                        View.INVISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do nothing
            }
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


                /*  Changed to accomodate either a cardio workout or a 'sets and reps' workout
                *
                *  TODO: change RecyclerAdapter to reflect changes
                * */



                // IF A CARDIO ITEM
                if(spinnerWorkoutType.selectedItem.toString() == "Cardio") {
                    val workout = hashMapOf(
                        "type" to spinnerWorkoutType.selectedItem.toString(),
                        "date_time" to Date(combinedDateTime.timeInMillis).toString(),
                        "distance" to dialogView.findViewById<ConstraintLayout>(R.id.cardioConstraint).findViewById<EditText>(R.id.editTextDistance).text.toString(),
                        "cardio_time" to dialogView.findViewById<ConstraintLayout>(R.id.cardioConstraint).findViewById<EditText>(R.id.editTextTime).text.toString()
                    )
                    db.collection("users")
                        .document(MainActivity.user.uid)
                        .collection( "cardio")
                        .add(workout)

                    var typeString = spinnerWorkoutType.selectedItem.toString()
                    var dateString = Date(combinedDateTime.timeInMillis).toString()
                    var exerciseNameString = dialogView.findViewById<ConstraintLayout>(R.id.cardioConstraint).findViewById<EditText>(R.id.editTextDistance).text.toString()
                    var setsString = dialogView.findViewById<ConstraintLayout>(R.id.cardioConstraint).findViewById<EditText>(R.id.editTextTime).text.toString()
                    var setsAndRepsItem = SetsAndRepsItem(typeString, dateString, exerciseNameString, setsString)

                    setsAndRepsList.add(setsAndRepsItem)
                }
                else
                {
                    val workout = hashMapOf(
                        "type" to spinnerWorkoutType.selectedItem.toString(),
                        "date_time" to Date(combinedDateTime.timeInMillis).toString(),
                        "exercise_name" to dialogView.findViewById<ConstraintLayout>(R.id.setsAndRepsConstraint).findViewById<EditText>(R.id.editTextExerciseName).text.toString(),
                        "sets" to dialogView.findViewById<ConstraintLayout>(R.id.setsAndRepsConstraint).findViewById<EditText>(R.id.editTextSets).text.toString(),
                        "reps" to dialogView.findViewById<ConstraintLayout>(R.id.setsAndRepsConstraint).findViewById<EditText>(R.id.editTextReps).text.toString(),
                    )
                    db.collection("users")
                        .document(MainActivity.user.uid)
                        .collection( "setsandreps")
                        .add(workout)

                    var typeString = spinnerWorkoutType.selectedItem.toString()
                    var dateString = Date(combinedDateTime.timeInMillis).toString()
                    var distanceString = dialogView.findViewById<ConstraintLayout>(R.id.cardioConstraint).findViewById<EditText>(R.id.editTextDistance).text.toString()
                    var cardioTimeString = dialogView.findViewById<ConstraintLayout>(R.id.cardioConstraint).findViewById<EditText>(R.id.editTextTime).text.toString()
                    var cardioListItem = CardioItem(typeString, dateString, distanceString, cardioTimeString)

                    cardioList.add(cardioListItem)
                }







                binding.recyclerViewCardio.adapter = CardioRecyclerAdapter(cardioList)
                binding.recyclerViewSetsAndReps.adapter = SetsAndRepsRecyclerAdapter(setsAndRepsList)
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