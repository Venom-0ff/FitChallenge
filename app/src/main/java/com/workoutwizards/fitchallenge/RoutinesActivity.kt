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

        /*
        *
        * TODO: create a way to add several SetsAndRepsItem(s) into a neat recyclerview
        *
        * ideas:
        * - could create a spinner that is incrementable ('select workout:' spinnerHere) and then
        * display a recyclerview of SetsAndRepsItem(s) based on the selected spinner value
        *
        * - find a way to nest dialogues into creating more and more items
        *
        * - display all of the info into a condensed card item
        *
        * guys im so tired, its 3am rn
        * sorry ill work on it more when i wake up
        *
        * */
    }
}