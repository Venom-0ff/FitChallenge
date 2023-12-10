package com.workoutwizards.fitchallenge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.workoutwizards.fitchallenge.databinding.ActivityMainBinding
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = Firebase.auth.currentUser!!

        val fab = binding.logoff
        fab.setOnClickListener {
            showLogoffDialog()
        }
    }

    companion object {
        lateinit var user: FirebaseUser
    }

    fun onButtonClick(view: View) {
        val intent = Intent(this,WorkoutActivity::class.java)
        startActivity(intent)
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