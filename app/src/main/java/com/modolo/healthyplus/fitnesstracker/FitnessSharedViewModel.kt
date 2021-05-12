package com.modolo.healthyplus.fitnesstracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout
import com.modolo.healthyplus.fitnesstracker.workoutdb.WorkoutRepository

class FitnessSharedViewModel(val app: Application) : AndroidViewModel(app) {
    private val mAuth = FirebaseAuth.getInstance()
    private val workoutsdb = WorkoutRepository(app)
    var workouts = MutableLiveData<List<Workout>>()
    var workoutToEdit: Workout? = null

    init {
        workoutsdb.getAll()
        workouts = workoutsdb.workoutData
        Log.i("devdebug", "FitnessViewModel init: $workouts")
    }

    fun setWorkouttoEdit(workout: Workout){
        workoutToEdit = workout
    }

    fun getWorkouttoEdit():Workout{
        return workoutToEdit!!
    }

    fun insertWorkout(workout: Workout) {
        workoutsdb.insertWorkout(workout)
        DButilFitnessTracker(mAuth, Firebase.firestore).addWorkout(workout)
    }

    fun deleteWorkout(workout: Workout) {
        workoutsdb.deleteWorkout(workout)
        DButilFitnessTracker(mAuth, Firebase.firestore).deleteWorkout(workout)
    }
    fun getLastId(): Int{
        var maxId = 0
        workouts.value!!.forEach {
            maxId = if(it.id > maxId) it.id else maxId
        }
        return maxId
    }


    fun getPresets(): ArrayList<Workout>{
        val presets = ArrayList<Workout>()
        workouts.value!!.forEach {
            if(it.ispreset)
                presets.add(it)
        }
        return presets
    }

}

