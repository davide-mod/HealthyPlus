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
    /*lista di allenamenti che verrà presa dal database*/
    var workoutData = MutableLiveData<List<Workout>>()
    /*oggetto per comunicare con il database locale*/
    private val workoutDAO = WorkoutDatabase.getDatabase(app).workoutDAO()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val data: List<Workout> = workoutDAO.getAll()
            if (data.isNullOrEmpty()) {
                /*se il database locale è vuoto controllo se ci sono elementi online*/
                val mAuth = FirebaseAuth.getInstance()
                val workoutsFromGoogle = DButilFitnessTracker(mAuth, Firebase.firestore).getAll()
                val workoutsTmp = mutableListOf<Workout>()
                /*se vengono trovati allenamenti online li salvo anche nel database locale*/
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
            } else {
                /*se il database di allenamenti è già popolato recupero le entry*/
                workoutData.postValue(data)
            }
        }
    }

    /*recupero tutti gli allenamenti nel database con una coroutine per non appesantire il thread principale*/
    fun getAll() {
        CoroutineScope(Dispatchers.IO).launch {
            workoutData.postValue(workoutDAO.getAll())
        }
    }

    /*inserisco un nuovo allenamento nel database con una coroutine*/
    fun insertWorkout(workout: Workout) {
        CoroutineScope(Dispatchers.IO).launch {
            workoutDAO.insertWorkout(workout)
            workoutData.postValue(workoutDAO.getAll())
        }
    }

    /*elimino un allenamento dal database con una coroutine*/
    fun deleteWorkout(workout: Workout) {
        CoroutineScope(Dispatchers.IO).launch {
            workoutDAO.deleteWorkout(workout)
            workoutData.postValue(workoutDAO.getAll())
        }
    }

}