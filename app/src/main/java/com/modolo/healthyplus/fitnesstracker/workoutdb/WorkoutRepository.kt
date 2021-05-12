package com.modolo.healthyplus.fitnesstracker.workoutdb

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.fitnesstracker.DButilFitnessTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutRepository(app: Application) {

    var workoutData = MutableLiveData<List<Workout>>()
    private val workoutDAO = WorkoutDatabase.getDatabase(app).workoutDAO()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val data: List<Workout> = workoutDAO.getAll()
            if (data.isNullOrEmpty()) {
                val mAuth = FirebaseAuth.getInstance()
                val workoutsFromGoogle = DButilFitnessTracker(mAuth, Firebase.firestore).getAll()
                val workoutsTmp = mutableListOf<Workout>()
                workoutsFromGoogle.get().addOnSuccessListener { doc ->
                    doc.forEach { me ->
                        Log.i("devdebug", "WorkoutRepo: $me")
                        val workoutTmp = Workout(
                            id = (me.data["id"] as Long).toInt(),
                            name = me.data["name"] as String,
                            exerciseList = me.data["exerciseList"] as String,
                            date = (me.data["date"] as String),
                            ispreset = me.data["ispreset"] as Boolean,
                            isdone = me.data["isdone"] as Boolean,
                        )
                        workoutsTmp.add(workoutTmp)
                        insertWorkout(workoutTmp)
                    }
                }
                workoutData.postValue(workoutDAO.getAll())
                //insertMeals(mealsTmp)
            } else {
                //se il database di allenamenti è già popolato recupero le entry
                workoutData.postValue(data)
            }
        }
    }

    //recupero tutti gli ingredienti nel database
    fun getAll() {
        CoroutineScope(Dispatchers.IO).launch {
            workoutData.postValue(workoutDAO.getAll())
        }
    }

    //inserisco un nuovo ingrediente e aggiorno la lista richiedendola
    fun insertWorkout(workout: Workout) {
        CoroutineScope(Dispatchers.IO).launch {
            workoutDAO.insertWorkout(workout)
            workoutData.postValue(workoutDAO.getAll())
        }
    }

    //elimino un ingrediente e aggiorno la lista richiedendola
    fun deleteWorkout(workout: Workout) {
        CoroutineScope(Dispatchers.IO).launch {
            workoutDAO.deleteWorkout(workout)
            workoutData.postValue(workoutDAO.getAll())
        }
    }

}