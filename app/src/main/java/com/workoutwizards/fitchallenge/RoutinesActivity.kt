package com.workoutwizards.fitchallenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.workoutwizards.fitchallenge.databinding.ActivityChallengesBinding
import com.workoutwizards.fitchallenge.databinding.ActivityRoutinesBinding
class RoutinesActivity  : AppCompatActivity(){
    private lateinit var binding: ActivityRoutinesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutinesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fab = binding.returnToMenuR
        fab.setOnClickListener {
            finish()
        }

    }
}