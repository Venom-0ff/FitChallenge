package com.workoutwizards.fitchallenge

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.workoutwizards.fitchallenge.databinding.ActivityRoutinesBinding
import com.workoutwizards.fitchallenge.model.SetsAndRepsItem
import com.workoutwizards.fitchallenge.databinding.DialogAddRoutineItemBinding

class RoutinesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutinesBinding
    private lateinit var setsAndRepsList: MutableList<SetsAndRepsItem>
    private val db = Firebase.firestore
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutinesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerViewManager = LinearLayoutManager(applicationContext)

        //set recyclerView
        binding.recyclerViewRoutine.layoutManager = recyclerViewManager


        setupSpinner()

        binding.addRoutine.setOnClickListener {
            // Implement logic to increment the spinner or create a new list
            // Add the logic to save the list to Firebase Firestore


        }

        binding.addRoutineItem.setOnClickListener {
            showAddRoutineItemDialog()
        }

        // Fetch and display data from Firebase Firestore on activity startup
        fetchDataFromFirestore()

        val fab = binding.returnToMenuR
        fab.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinner() {
        // Implement spinner setup logic
        // Populate spinner with routine names or provide options to create a new routine
        // Update the RecyclerViewRoutine accordingly
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
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Show the dialog
        dialog.show()
    }

    private fun fetchDataFromFirestore() {
        // Implement logic to fetch data from Firebase Firestore
        // Update the RecyclerViewRoutine accordingly
    }
}
