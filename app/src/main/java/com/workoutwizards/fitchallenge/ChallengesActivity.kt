package com.workoutwizards.fitchallenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.workoutwizards.fitchallenge.databinding.ActivityChallengesBinding

class ChallengesActivity : AppCompatActivity(){
    private lateinit var binding: ActivityChallengesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fab = binding.returnToMenuC
        fab.setOnClickListener {
            finish()
        }

    }
}