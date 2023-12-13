package com.workoutwizards.fitchallenge.model

data class CardioItem(
    var type: String = "",
    var date_time: String = "",
    var distance: String = "",
    var cardio_time: String = ""
)

data class SetsAndRepsItem(
    var type: String = "",
    var date_time: String = "",
    var exercise_name: String = "",
    var sets: String = "",
    var reps: String = ""

)

data class ChallengeItem(
    var challenge_name: String, //Mostly here for storing in Firebase
    var distance: String,
    var start_date: String,
    var end_date: String,
)
