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
                // Clear the existing list
                setsAndRepsList.clear()

                // Iterate through the documents in the "routines" collection
                for (document in querySnapshot.documents) {
                    // Retrieve the "routineList" field from each document
                    val routineList = document.get("routineList") as List<Map<String, Any>>?

                    // Convert the routineList to SetsAndRepsItem and add it to the setsAndRepsList
                    routineList?.let {
                        for (item in it) {
                            val exercise = SetsAndRepsItem(
                                item["exerciseName"].toString(),
                                item["sets"].toString(),
                                item["reps"].toString(),
                            )
                            setsAndRepsList.add(exercise)
                        }
                    }
                }

            }
            .addOnFailureListener { e ->
                // Handle failure
                setsAndRepsList = emptyList<SetsAndRepsItem>().toMutableList()
            }
    }

    private fun saveRoutineToFirestore(newRoutineList: List<SetsAndRepsItem>) {
        // Get the user's document reference in Firebase Firestore
        val userDocRef = db.collection("users").document(MainActivity.user.uid)

        // Add the newRoutineList to the "routines" collection under the user's document
        userDocRef.collection("routines").add(mapOf("routineList" to newRoutineList))
            .addOnSuccessListener { documentReference ->
                // Handle success, if needed
            }
            .addOnFailureListener { e ->
                // Handle failure, if needed
                Toast.makeText(this, "Failed to save routine to Firestore", Toast.LENGTH_SHORT).show()
            }
    }
}
