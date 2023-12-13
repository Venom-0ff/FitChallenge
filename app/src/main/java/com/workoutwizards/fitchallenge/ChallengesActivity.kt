package com.workoutwizards.fitchallenge

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.workoutwizards.fitchallenge.databinding.ActivityChallengesBinding
import com.workoutwizards.fitchallenge.databinding.DialogAddChallengeItemBinding
import com.workoutwizards.fitchallenge.databinding.DialogAddRoutineItemBinding
import com.workoutwizards.fitchallenge.model.ChallengeItem
import com.workoutwizards.fitchallenge.model.SetsAndRepsItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChallengesActivity : AppCompatActivity(){
    private lateinit var binding: ActivityChallengesBinding
    private var selectedDateStart = Calendar.getInstance()
    private var selectedTimeStart = Calendar.getInstance()

    private var selectedDateEnd = Calendar.getInstance()
    private var selectedTimeEnd = Calendar.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var challengeItemList = emptyList<ChallengeItem>().toMutableList()
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager
    private lateinit var challengeRecyclerAdapter: ChallengeRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewManager = LinearLayoutManager(applicationContext)

        val fab = binding.returnToMenuC
        fab.setOnClickListener {
            finish()
        }


        // set recyclerView
        binding.recyclerViewChallenges.layoutManager = recyclerViewManager
        challengeRecyclerAdapter = ChallengeRecyclerAdapter(emptyList()) // Initialize the adapter with an empty list
        binding.recyclerViewChallenges.adapter = challengeRecyclerAdapter


        //Fetch and display firestore data
        fetchDataFromFirestore()

        //button binding
        binding.addChallenge.setOnClickListener {
            showAddChallengeItemDialog()
        }

    }

    private fun showAddChallengeItemDialog() {
        // Inflate the dialogue_add_routine_item.xml layout
        val dialogBinding = DialogAddChallengeItemBinding.inflate(LayoutInflater.from(this))
        val textViewPickDateTimeStart = dialogBinding.textViewPickDateTimeStart
        val textViewPickDateTimeEnd = dialogBinding.textViewPickDateTimeEnd


        textViewPickDateTimeStart.setOnClickListener {
            showDateTimePickerDialog(textViewPickDateTimeStart, selectedDateStart, selectedTimeStart)
        }
        textViewPickDateTimeEnd.setOnClickListener {
            showDateTimePickerDialog(textViewPickDateTimeEnd, selectedDateEnd, selectedTimeEnd)
        }

        // Build the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setTitle("Add new challenge")
            .setPositiveButton("Add") { _, _ ->
                // Implement logic to add the item to the current list
                // Update the RecyclerViewRoutine accordingly
                // Add the logic to save the updated list to Firebase Firestore

                val combinedDateTimeStart = Calendar.getInstance()
                combinedDateTimeStart.set(
                    selectedDateStart.get(Calendar.YEAR),
                    selectedDateStart.get(Calendar.MONTH),
                    selectedDateStart.get(Calendar.DAY_OF_MONTH),
                    selectedTimeStart.get(Calendar.HOUR_OF_DAY),
                    selectedTimeStart.get(Calendar.MINUTE)
                )

                val combinedDateTimeEnd = Calendar.getInstance()
                combinedDateTimeEnd.set(
                    selectedDateEnd.get(Calendar.YEAR),
                    selectedDateEnd.get(Calendar.MONTH),
                    selectedDateEnd.get(Calendar.DAY_OF_MONTH),
                    selectedTimeEnd.get(Calendar.HOUR_OF_DAY),
                    selectedTimeEnd.get(Calendar.MINUTE)
                )

                var challengeNameString = dialogBinding.editTextChallengeName.text.toString()
                var distanceString = dialogBinding.editTextChallengeDistance.text.toString()
                var startDateString = Date(combinedDateTimeStart.timeInMillis).toString()
                var endDateString = Date(combinedDateTimeEnd.timeInMillis).toString()

                var challengeItem = ChallengeItem(challengeNameString,distanceString, startDateString, endDateString)

                // add to the list inside the routine list
                challengeItemList.add(challengeItem)

                saveChallengeToFirestore(challengeItem)
                binding.recyclerViewChallenges.adapter = ChallengeRecyclerAdapter(challengeItemList)

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Show the dialog
        dialog.show()
    }

    private fun showDateTimePickerDialog(field: TextView, selectedDate: Calendar, selectedTime: Calendar) {
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

    private fun saveChallengeToFirestore(updatedChallenge: ChallengeItem) {

        val userDocRef = db.collection("users").document(MainActivity.user.uid)

        val documentName = updatedChallenge.challenge_name

        // Get the currently selected routine's document reference
        val selectedChallengeDocRef = userDocRef.collection("chalenges")
            .document(documentName)
        db.collection("users")
            .document(MainActivity.user.uid)
            .collection( "challenges")
            .document(documentName).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Update the existing routine
                    selectedChallengeDocRef.update("distance", updatedChallenge.distance) // for updating distance
                        .addOnSuccessListener {
                            // Handle success, if needed
                        }
                        .addOnFailureListener { e ->
                            // Handle failure, if needed
                            Toast.makeText(this, "Failed to update routine in Firestore", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Routine doesn't exist, add a new routine
                    userDocRef.collection("chalenges").document(documentName)
                        .set(updatedChallenge)
                        .addOnSuccessListener { documentReference ->
                            // Handle success, if needed
                        }
                        .addOnFailureListener { e ->
                            // Handle failure, if needed
                            Toast.makeText(this, "Failed to save routine to Firestore", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure, if needed
                Toast.makeText(this, "Failed to post challenge in Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchDataFromFirestore() {
        // Get the user's document reference in Firebase Firestore
// Grab sets and reps from user
        db.collection("users")
            .document(MainActivity.user.uid)
            .collection("challenges")
            .get()
            .addOnSuccessListener {result ->
                challengeItemList = result.toObjects(ChallengeItem::class.java)
                if (challengeItemList.isNotEmpty()) {
                    binding.recyclerViewChallenges.adapter = ChallengeRecyclerAdapter(challengeItemList)
                }
            }
            .addOnFailureListener {
               challengeItemList = emptyList<ChallengeItem>().toMutableList()
                binding.recyclerViewChallenges.adapter = ChallengeRecyclerAdapter(challengeItemList)

                Toast.makeText(this, "No challenges yet", Toast.LENGTH_SHORT).show()
            }

    }
}