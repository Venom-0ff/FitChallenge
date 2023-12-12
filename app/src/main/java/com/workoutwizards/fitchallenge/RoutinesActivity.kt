package com.workoutwizards.fitchallenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.workoutwizards.fitchallenge.databinding.ActivityRoutinesBinding
import com.workoutwizards.fitchallenge.model.SetsAndRepsItem
import com.workoutwizards.fitchallenge.databinding.DialogAddRoutineItemBinding
import java.util.Date

class RoutinesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutinesBinding
    private lateinit var routineList: MutableList<MutableList<SetsAndRepsItem>>
    private lateinit var setsAndRepsList: MutableList<SetsAndRepsItem>
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager
    private lateinit var routineRecyclerAdapter: RoutineRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutinesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerViewManager = LinearLayoutManager(applicationContext)


        routineList = emptyList<MutableList<SetsAndRepsItem>>().toMutableList()
        setsAndRepsList = emptyList<SetsAndRepsItem>().toMutableList()

        // set recyclerView
        binding.recyclerViewRoutine.layoutManager = recyclerViewManager
        routineRecyclerAdapter = RoutineRecyclerAdapter(emptyList()) // Initialize the adapter with an empty list
        binding.recyclerViewRoutine.adapter = routineRecyclerAdapter


        // Fetch and display data from Firebase Firestore on activity startup
        fetchDataFromFirestore()

        setupSpinner()

        binding.addRoutine.setOnClickListener {
            // Create a new list of SetsAndRepsItem
            val newRoutineList = mutableListOf<SetsAndRepsItem>()
            routineList.add(newRoutineList)
            // TODO: Add logic to populate the newRoutineList based on user input or spinner selection

            //Re-setup the spinner
            setupSpinner()
        }

        binding.addRoutineItem.setOnClickListener {
            showAddRoutineItemDialog()
        }



        val fab = binding.returnToMenuR
        fab.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinner() {
        val spinnerItems: MutableList<String> = mutableListOf()

        routineList?.let {
            // Add incrementing numbers based on the size of routineList
            for (i in 1..it.size) {
                spinnerItems.add("Routine $i")
            }
        }

        val spinner = binding.spinnerRoutine
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set a listener to handle spinner item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Update the RecyclerViewRoutine accordingly
                binding.recyclerViewRoutine.adapter = RoutineRecyclerAdapter(routineList[binding.spinnerRoutine.selectedItemPosition])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle when nothing is selected, if needed
            }
        }
    }

    private fun showAddRoutineItemDialog() {
        // Inflate the dialogue_add_routine_item.xml layout
        val dialogBinding = DialogAddRoutineItemBinding.inflate(LayoutInflater.from(this))

        // Build the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                // Implement logic to add the item to the current list
                // Update the RecyclerViewRoutine accordingly
                // Add the logic to save the updated list to Firebase Firestore

                var typeString = "Sets and Reps"
                var dateString = ""
                var exerciseNameString = dialogBinding.editTextExerciseName.text.toString()
                var setsString = dialogBinding.editTextSets.text.toString()
                var repsString = dialogBinding.editTextReps.text.toString()
                var setsAndRepsItem = SetsAndRepsItem(typeString, dateString, exerciseNameString, setsString, repsString )

                // add to the list inside the routine list
                routineList[binding.spinnerRoutine.selectedItemPosition].add(setsAndRepsItem)

                saveRoutineToFirestore(routineList[binding.spinnerRoutine.selectedItemPosition])
                binding.recyclerViewRoutine.adapter = RoutineRecyclerAdapter(routineList[binding.spinnerRoutine.selectedItemPosition])

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Show the dialog
        dialog.show()
    }

    private fun fetchDataFromFirestore() {
        // Get the user's document reference in Firebase Firestore
        val userDocRef = db.collection("users").document(MainActivity.user.uid)

        // Fetch data from the "routines" collection under the user's document
        userDocRef.collection("routines").get()
            .addOnSuccessListener { querySnapshot ->
                // Clear the existing routineList
                routineList.clear()

                // Iterate through the documents in the "routines" collection
                for (document in querySnapshot.documents) {
                    // Retrieve the "routineList" field from each document
                    val routineItemList = document.get("routineList") as List<Map<String, Any>>?

                    // Convert the routineItemList to SetsAndRepsItem list and add it to the routineList
                    routineItemList?.let {
                        val routineItems = it.map { item ->
                            SetsAndRepsItem(
                                item["type"].toString(),
                                item["date_time"].toString(),
                                item["exercise_name"].toString(),
                                item["sets"].toString(),
                                item["reps"].toString()
                            )
                        }.toMutableList()

                        routineList.add(routineItems)
                    }
                }

                // Update the spinner and RecyclerView accordingly
                if (routineList.isNotEmpty()){
                    setupSpinner()
                    binding.recyclerViewRoutine.adapter = RoutineRecyclerAdapter(routineList[binding.spinnerRoutine.selectedItemPosition])

                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                routineList = emptyList<MutableList<SetsAndRepsItem>>().toMutableList()
                // Update the spinner and RecyclerView accordingly
                setupSpinner()
                binding.recyclerViewRoutine.adapter = RoutineRecyclerAdapter(routineList[binding.spinnerRoutine.selectedItemPosition])
            }
    }

    private fun saveRoutineToFirestore(updatedRoutineList: List<SetsAndRepsItem>) {
        // Get the user's document reference in Firebase Firestore
        val userDocRef = db.collection("users").document(MainActivity.user.uid)

        val documentName = "Routine ${binding.spinnerRoutine.selectedItemPosition + 1}"

        // Get the currently selected routine's document reference
        val selectedRoutineDocRef = userDocRef.collection("routines")
            .document("Routine ${binding.spinnerRoutine.selectedItemPosition + 1}")

        // Check if the routine already exists in Firestore
        selectedRoutineDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Update the existing routine
                    selectedRoutineDocRef.update("routineList", updatedRoutineList)
                        .addOnSuccessListener {
                            // Handle success, if needed
                        }
                        .addOnFailureListener { e ->
                            // Handle failure, if needed
                            Toast.makeText(this, "Failed to update routine in Firestore", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Routine doesn't exist, add a new routine
                    userDocRef.collection("routines").document(documentName)
                        .set(mapOf("routineList" to updatedRoutineList))
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
                Toast.makeText(this, "Failed to check routine existence in Firestore", Toast.LENGTH_SHORT).show()
            }
    }
}
