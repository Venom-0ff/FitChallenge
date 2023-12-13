package com.workoutwizards.fitchallenge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.workoutwizards.fitchallenge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore
    private var stepString = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = Firebase.auth.currentUser!!

        binding.textViewUser.text = "Welcome " + user.email

        val fab = binding.logoff
        fab.setOnClickListener {
            showLogoffDialog()
        }

        db.collection("users")
            .document(user.uid)
            .collection("steps")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    binding.textView3.text = document.data.get("steps").toString()
                }
            }
            .addOnFailureListener {
                binding.textView3.text = R.string.default_goal.toString()
            }
    }

    companion object {
        lateinit var user: FirebaseUser
    }

    fun onWorkoutButtonClick(view: View) {
        val intent = Intent(this,WorkoutActivity::class.java)
        startActivity(intent)
    }

    fun onChallengesButtonClick(view: View) {
        val intent = Intent(this,ChallengesActivity::class.java)
        startActivity(intent)
    }

    fun onRoutinesButtonClick(view: View) {
        val intent = Intent(this,RoutinesActivity::class.java)
        startActivity(intent)
    }

    fun onGoalsButtonClick(view: View) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_goals, null)

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.set_goals)
            .setView(dialogView)
                .setPositiveButton("Set"){ _, _ ->
                    this.stepString = dialogView.findViewById<EditText>(R.id.edittext).text.toString().toInt()
                    var stepsString = dialogView.findViewById<EditText>(R.id.edittext).text.toString() +
                            " steps"
                    val data = hashMapOf(
                        "steps" to stepsString,
                    )
                    db.collection("users")
                        .document(MainActivity.user.uid)
                        .collection("steps")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {

                                db.collection("users")
                                    .document(MainActivity.user.uid)
                                    .collection("steps").document(document.id)
                                    .delete()
                            }

                            db.collection("users")
                                .document(MainActivity.user.uid)
                                .collection("steps")
                                .add(data)
                        }
                        .addOnFailureListener {

                        }
                        binding.textView3.text = "${stepsString} to go!"
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    fun onUpdateGoalsButtonClick(view: View) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_goals, null)

        val dialog = AlertDialog.Builder(this)
            .setTitle("How many steps have you taken?")
            .setView(dialogView)
            .setPositiveButton("Set"){ _, _ ->
                var stepsString = (stepString - dialogView.findViewById<EditText>(R.id.edittext).text.toString().toInt()).toString() +
                        " steps"
                val data = hashMapOf(
                    "steps" to stepsString,
                )
                db.collection("users")
                    .document(MainActivity.user.uid)
                    .collection("steps")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {

                            db.collection("users")
                                .document(MainActivity.user.uid)
                                .collection("steps").document(document.id)
                                .delete()
                        }

                        db.collection("users")
                            .document(user.uid)
                            .collection("steps")
                            .add(data)
                    }
                    .addOnFailureListener {

                    }
                Toast.makeText(this, "Great work!", Toast.LENGTH_SHORT).show()
                binding.textView3.text = "${stepsString} to go!"
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

    }
    private fun showLogoffDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Logout")
            .setView(dialogView)
            .setPositiveButton("Yes") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this,R.string.logged_out, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()
    }
}