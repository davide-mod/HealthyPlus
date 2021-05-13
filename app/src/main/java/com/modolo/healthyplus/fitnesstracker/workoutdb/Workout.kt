package com.modolo.healthyplus.fitnesstracker.workoutdb

import androidx.room.Entity
import androidx.room.PrimaryKey

/*oggetto Workout che è anche il tipo di entità nel database locale*/
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var exerciseList: String,
    var date: String,
    var ispreset: Boolean,
    var isdone: Boolean
)