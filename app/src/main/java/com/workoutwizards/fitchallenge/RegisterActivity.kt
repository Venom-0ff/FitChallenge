package com.workoutwizards.fitchallenge

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.workoutwizards.fitchallenge.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth
        binding.buttonRegister.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.username.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(binding.password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val email: String = binding.username.text.toString().trim { it <= ' ' }
                    val password: String = binding.password.text.toString().trim { it <= ' ' }
                    // signs in current user
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success")
                                val user = auth.currentUser
                                val intent = Intent(this, LoginActivity::class.java)
                                // gets rid of any login or register activities that are left open
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("userid", auth.currentUser?.uid)
                                intent.putExtra("email", user)
                                startActivity(intent)
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext,
                                    "Registration failed\nPassword must be more than 6 characters.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                println("Authentication failed.")
                            }
                        }

                }
            }
        }

    }
}