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
