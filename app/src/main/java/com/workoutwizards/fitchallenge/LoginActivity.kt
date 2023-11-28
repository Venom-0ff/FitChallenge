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
import com.workoutwizards.fitchallenge.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val user = auth.currentUser
        if (user != null) {
            // User is signed in
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // No user is signed in
            binding.buttonLogin.setOnClickListener {
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
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithEmail:success")
                                    val user = auth.currentUser?.email
                                    val intent = Intent(this, MainActivity::class.java)
                                    // gets rid of any login or register activities that are left open
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("userid", auth.currentUser?.uid)
                                    intent.putExtra("email", user)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                                    Toast.makeText(
                                        baseContext,
                                        "Authentication failed.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                    }
                }
            }
            binding.textViewHere.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }
}